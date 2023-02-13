package testcase.domain.integrations.bacen;

import java.util.Objects;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import testcase.domain.integrations.bacen.request.InternalTransferNotificationRequest;
import testcase.domain.integrations.exception.IntegrationException;
import testcase.domain.integrations.hystrix.HystrixCommandKeys;
import testcase.domain.integrations.hystrix.HystrixGroupKeys;

@Slf4j
@Component
public class BacenClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${bacen-integration-api.url}")
    private String bacenUrl;

    @HystrixCommand( //
            groupKey = HystrixGroupKeys.BACEN, //
            commandKey = HystrixCommandKeys.BACEN_TRANSFERS)
    public void notifyTransfer(InternalTransferNotificationRequest request) throws IntegrationException {
        Objects.requireNonNull(request, "request cant be null");

        log.info("stage=init-bacen-transfer-notification, identifier={}", request.getUniqueIdentifier());
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        final HttpEntity<InternalTransferNotificationRequest> entity = new HttpEntity<>(request, headers);

        final ResponseEntity<?> exchange;

        try {
            exchange = restTemplate
                    .exchange(this.bacenUrl.concat(Endpoints.NOTIFY_TRANSFER)
                            , HttpMethod.POST, entity, Void.class);
        } catch (Exception ex) {

            log.error("stage=error-to-get-customer-status, identifier={}", request.getUniqueIdentifier(), ex);
            throw new IntegrationException(ex);
        }

        if (exchange.getStatusCode().is2xxSuccessful() ||
                exchange.getStatusCode().equals(HttpStatus.CONFLICT)) {
            log.info("stage=success-transfer-notified, identifier={}", request.getUniqueIdentifier());
            return;
        }

        log.error("stage=error-to-bacen-notification, identifier={}, httpStatus={}", request.getUniqueIdentifier(),
                exchange.getStatusCode());
        throw new IntegrationException();
    }
}
