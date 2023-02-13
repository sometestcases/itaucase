package testcase.domain;

import static java.util.Collections.singletonMap;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import testcase.persistence.PersistenceConfig;

@Configuration
@ComponentScan(basePackageClasses = PersistenceConfig.class)
@Slf4j
@EnableCircuitBreaker
@EnableAutoConfiguration
public class DomainConfig {

    @Value("${httpclient.timeout}")
    private int httpclientTimeout;

    @Bean("bacenSincRetryTemplate")
    public RetryTemplate bacenSincRetryTemplate() {

        final RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy( //
                new SimpleRetryPolicy(4, singletonMap(Exception.class, true)));

        final ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setMultiplier(2);
        backOffPolicy.setInitialInterval(1000);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .requestFactory(() -> this.httpClientFactory())
                .errorHandler(this.getErrorHandler())
                .additionalMessageConverters(new MappingJackson2HttpMessageConverter(this.objectMapper()))
                .setConnectTimeout(Duration.ofMillis(this.httpclientTimeout))
                .setReadTimeout(Duration.ofMillis(this.httpclientTimeout))
                .build();
    }

    private HttpComponentsClientHttpRequestFactory httpClientFactory() {
        try {
            return new HttpComponentsClientHttpRequestFactory(HttpClients.custom()
                    .setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContexts.custom()
                            .loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> true)
                            .build())
                    ).build());
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            log.error("stage=error-config-client-http-local, message={}", e.getMessage());
            return httpClientFactory();
        }
    }

    private PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager() {
        final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(85);
        connManager.setDefaultMaxPerRoute(20);
        return connManager;
    }

    private DefaultResponseErrorHandler getErrorHandler() {
        return new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(final ClientHttpResponse response) throws IOException {
                //O client decide se é um erro ou não
                return false;
            }
        };
    }
}