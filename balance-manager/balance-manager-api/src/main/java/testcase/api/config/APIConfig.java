package testcase.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import testcase.domain.DomainConfig;
import testcase.persistence.PersistenceConfig;

@Configuration
@ComponentScan(basePackageClasses = {DomainConfig.class, PersistenceConfig.class})
@PropertySource("classpath:application-api.properties")
@PropertySource(value = "classpath:application-api-${spring.profiles.active}.properties")
public class APIConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().version("v1")
                        .title("Balance Manager API")
                                .description("API to manage account balances"));
    }
}
