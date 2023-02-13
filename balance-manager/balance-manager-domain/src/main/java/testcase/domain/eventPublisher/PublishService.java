package testcase.domain.eventPublisher;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import testcase.domain.eventPublisher.mapping.PublishMapping;

@Service
@Slf4j
public class PublishService {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    @Qualifier("publisherTaskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    private ImmutableMap<Class<? extends GenericRecord>, PublishMapping> publishMappings;

    @Autowired
    public PublishService(List<PublishMapping> mappings) {

        ImmutableMap.Builder<Class<? extends GenericRecord>, PublishMapping> mapBuilder =
                ImmutableMap.builder();

        for (PublishMapping mapping : mappings) {
            mapBuilder.put(mapping.getMappedClass(), mapping);
        }
        this.publishMappings = mapBuilder.build();
    }

    public void publish(final GenericRecord genericRecord) {
        CompletableFuture.runAsync(() -> {

                    PublishMapping mapping = this.publishMappings.get(genericRecord.getClass());

                    if (mapping == null) {
                        throw new RuntimeException("Unknown generic record type exception, implement a PublishMapping to class " + genericRecord.getClass());
                    }

                    final String key = mapping.getKey(genericRecord);

                    this.kafkaTemplate.send(mapping.getTopic(), key, genericRecord);
                    log.info("stage=success-send-event, type={}, key={}", genericRecord.getClass().getSimpleName(), key);
                }, this.taskExecutor)
                .exceptionally(ex -> {
                    log.error("stage=error-send-event, type={}", genericRecord.getClass().getSimpleName(), ex);
                    return null;
                });
    }

    public void publishAll(final Set<? extends GenericRecord> genericRecords) {
        CompletableFuture.runAsync(() -> {

                    if (genericRecords.stream().map(GenericRecord::getClass)
                            .anyMatch(gc -> !this.publishMappings.containsKey(gc))) {
                        throw new RuntimeException("Unknown generic record types exception, implement a PublishMapping to any of this classes "
                                + genericRecords.stream().map(Object::getClass)
                                .map(Class::getSimpleName)
                                .collect(Collectors.toList()));
                    }
                    for (GenericRecord genericRecord : genericRecords) {

                        PublishMapping mapping = this.publishMappings.get(genericRecord.getClass());
                        final String key = mapping.getKey(genericRecord);

                        this.kafkaTemplate.send(mapping.getTopic(), key, genericRecord);
                        log.info("stage=success-send-event, type={}, key={}", genericRecord.getClass().getSimpleName(), key);
                    }
                }, this.taskExecutor)
                .exceptionally(ex -> {
                    log.error("stage=error-send-events-batch, type={}", genericRecords.stream()
                            .map(Object::getClass)
                            .map(Class::getSimpleName)
                            .collect(Collectors.toList()), ex);
                    return null;
                });
    }
}
