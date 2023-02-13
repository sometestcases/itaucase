package testcase.listener.config;

import static java.util.Collections.singletonMap;

import java.util.Map;
import java.util.Objects;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import testcase.event.avro.BalanceAtomicOperation;

@Slf4j
@EnableKafka
@EnableRetry
@Configuration
@PropertySource("classpath:application-listener.properties")
@PropertySource(value = "classpath:application-listener-${spring.profiles.active}.properties")
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaSubscriberConfig {

    public static final String BALANCE_ATOMIC_OPERATION_CONTAINER_FACTORY = "balanceAtomicOperationKafkaListenerContainerFactory";

    private final KafkaProperties properties;

    @Value("${kafka.schema-registry-url}")
    private String schemaRegistryURL;

    @Value("${kafka.request.timeout.ms}")
    private Integer kafkaMaxMillisTimeout;

    public KafkaSubscriberConfig(final KafkaProperties properties) {
        this.properties = properties;
    }

    @Bean
    public ConsumerFactory<String, BalanceAtomicOperation> balanceAtomicOperationConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getDefaultProperties());
    }

    @Bean(BALANCE_ATOMIC_OPERATION_CONTAINER_FACTORY)
    public ConcurrentKafkaListenerContainerFactory<String, BalanceAtomicOperation> balanceAtomicOperationListenerContainerFactory() {

        final ConcurrentKafkaListenerContainerFactory<String, BalanceAtomicOperation> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(balanceAtomicOperationConsumerFactory());

        final RetryTemplate retryTemplate = getDefaultRetryTemplate();
        factory.setRetryTemplate(retryTemplate);
        factory.setConcurrency(1);
        return factory;
    }

    @Bean
    public KafkaListenerErrorHandler kafkaErrorHandler() {
        return new KafkaListenerErrorHandler() {

            @Override
            public Object handleError(final Message<?> message, final ListenerExecutionFailedException exception) {

                String topic = Objects.toString(message.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC));
                log.error("stage=error-listen-kafka, topic={}", topic, exception);

                throw exception;

            }

            @Override
            public Object handleError(Message<?> message, ListenerExecutionFailedException exception,
                    Consumer<?, ?> consumer) {

                String topic = Objects.toString(message.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC));
                log.error("stage=error-listen-kafka, topic={}", topic, exception);

                throw exception;
            }
        };
    }

    private RetryTemplate getDefaultRetryTemplate() {

        final RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy( //
                new SimpleRetryPolicy(Integer.MAX_VALUE, singletonMap(Exception.class, true)));

        final ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(2_000);
        backOffPolicy.setMaxInterval(256_000);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return retryTemplate;
    }

    private Map<String, Object> getDefaultProperties() {
        final Map<String, Object> props = properties.buildProducerProperties();

        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaMaxMillisTimeout);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaMaxMillisTimeout / 3);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryURL);

        return props;
    }
}
