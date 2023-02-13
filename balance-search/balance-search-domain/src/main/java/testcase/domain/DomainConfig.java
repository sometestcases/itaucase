package testcase.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import testcase.persistence.PersistenceConfig;

@Configuration
@ComponentScan(basePackageClasses = PersistenceConfig.class)
@Slf4j
@EnableAutoConfiguration
public class DomainConfig {


}