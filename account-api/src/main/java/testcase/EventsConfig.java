package testcase;

import java.util.Map;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@EnableKafka
@Configuration
@ComponentScan(basePackageClasses = {EventsConfig.class})
@EnableAutoConfiguration(exclude = KafkaAutoConfiguration.class)
@EnableConfigurationProperties(KafkaProperties.class)
public class EventsConfig {

    private static final String ACCOUNT_API_PRODUCER_CLIENT_ID = "AccountAPI";

    private final KafkaProperties properties;

    private static final int MAX_SUBSCRIBE_ATTEMPTS = 3;


    @Value("${kafka.request.timeout.ms}")
    private Integer kafkaMaxMillisTimeout;

    @Value("${kafka.schema-registry-url}")
    private String schemaRegistryURL;

    public EventsConfig(final KafkaProperties properties) {
        this.properties = properties;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = properties.buildProducerProperties();

        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaMaxMillisTimeout);
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, kafkaMaxMillisTimeout);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, ACCOUNT_API_PRODUCER_CLIENT_ID);
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryURL);

        // Only one in-flight messages per Kafka broker connection
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        // Only retry after three seconds.
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 3_000);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
