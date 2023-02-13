package testcase.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import testcase.persistence.PersistenceConfig;

@Configuration
@ComponentScan(basePackageClasses = PersistenceConfig.class)
public class DomainConfig {

    @Bean("publisherTaskExecutor")
    public ThreadPoolTaskExecutor publisherTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(20);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setQueueCapacity(200);
        taskExecutor.initialize();
        return taskExecutor;
    }
}
