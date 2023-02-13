package testcase.listener.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import testcase.domain.DomainConfig;
import testcase.persistence.PersistenceConfig;

@Configuration
@PropertySource("classpath:application-listener.properties")
@PropertySource(value = "classpath:application-listener-${spring.profiles.active}.properties")
@ComponentScan(basePackageClasses = {DomainConfig.class, PersistenceConfig.class})
public class ListenerConfig {
}
