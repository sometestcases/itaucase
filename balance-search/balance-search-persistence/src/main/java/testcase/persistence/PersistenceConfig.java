package testcase.persistence;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableAutoConfiguration
@PropertySource("classpath:application-database.properties")
@PropertySource(value = "classpath:application-database-${spring.profiles.active}.properties")
public class PersistenceConfig {
}
