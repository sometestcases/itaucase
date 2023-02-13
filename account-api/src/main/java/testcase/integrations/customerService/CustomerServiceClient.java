package testcase.integrations.customerService;

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
import testcase.integrations.customerService.exception.CustomerNotFoundException;
import testcase.integrations.customerService.response.CustomerStatus;
import testcase.integrations.customerService.response.Status;
import testcase.integrations.exception.IntegrationException;
import testcase.integrations.hystrix.HystrixCommandKeys;
import testcase.integrations.hystrix.HystrixGroupKeys;

@Slf4j
@Component
public class CustomerServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${customer-service-api.url}")
    private String customerServiceApiUrl;

    @HystrixCommand( //
            groupKey = HystrixGroupKeys.CUSTOMER_SERVICE, //
            commandKey = HystrixCommandKeys.CUSTOMER_GET_STATUS)
    public boolean isActiveCustomer(String customerId) throws CustomerNotFoundException, IntegrationException {
        Objects.requireNonNull(customerId, "Customer Id cant be null");

        log.info("stage=get-customer-status-init, customerId={}", customerId);
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        final HttpEntity<?> entity = new HttpEntity<>(headers);

        final ResponseEntity<CustomerStatus> exchange;

        try {
            exchange = restTemplate
                    .exchange(this.customerServiceApiUrl.concat(Endpoints.CUSTOMER_STATUS)
                            , HttpMethod.GET, entity, CustomerStatus.class, customerId);
        } catch (Exception ex) {

            log.error("stage=error-to-get-customer-status, customerId={}", customerId, ex);
            throw new IntegrationException(ex);
        }

        if (exchange.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            log.info("stage=customer-not-exists, customerId={}", customerId);
            throw new CustomerNotFoundException(customerId);
        } else if (exchange.getStatusCode().equals(HttpStatus.OK)) {

            Status customerStatus = exchange.getBody().getStatus();
            log.info("stage=customer-status-retrived, customerId={}, status={}", customerId, customerStatus);

            if (Status.ACTIVE.equals(customerStatus)) {
                return true;
            } else if (Status.INACTIVE.equals(customerStatus)) {
                return false;
            }

            log.error("stage=customer-status-unknown, customerId={}, status={}", customerId, customerStatus);
            throw new IntegrationException();
        }

        log.error("stage=unknown-customer-service-response-status, customerId={}, httpStatus={}", customerId, exchange.getStatusCode());
        throw new IntegrationException();
    }
}
