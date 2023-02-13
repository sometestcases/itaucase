package testcase.domain.integrations.balanceManager;

import java.util.List;
import java.util.Objects;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import testcase.domain.integrations.balanceManager.exception.BalanceManagerOperationException;
import testcase.domain.integrations.balanceManager.request.BalanceOperationRequest;
import testcase.domain.integrations.balanceManager.response.ErrorResponse;
import testcase.domain.integrations.exception.IntegrationException;
import testcase.domain.integrations.hystrix.HystrixCommandKeys;
import testcase.domain.integrations.hystrix.HystrixGroupKeys;

@Slf4j
@Component
public class BalanceManagerClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${balance-manager-api.url}")
    private String balanceManagerApiUrl;

    @HystrixCommand( //
            groupKey = HystrixGroupKeys.BALANCE_MANAGER, //
            commandKey = HystrixCommandKeys.BALANCE_MANAGER_ATOMIC_OPERATION)
    public void atomicOperation(BalanceOperationRequest balanceOperationRequest)
            throws IntegrationException, BalanceManagerOperationException {
        Objects.requireNonNull(balanceOperationRequest, "balance operation request cant be null");

        log.info("stage=operation-init, operationId={}", balanceOperationRequest.getOperationId());
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);


        final HttpEntity<BalanceOperationRequest> entity = new HttpEntity<>(balanceOperationRequest, headers);

        final ResponseEntity<List<ErrorResponse>> exchange;

        try {
            exchange = restTemplate
                    .exchange(this.balanceManagerApiUrl.concat(Endpoints.ATOMIC_BALANCE_OPERATION),
                            HttpMethod.POST, entity, new ParameterizedTypeReference<List<ErrorResponse>>() {
                            });
        } catch (Exception ex) {

            log.error("stage=error-to-execute-operation, operationId={}", balanceOperationRequest.getOperationId(), ex);
            throw new IntegrationException(ex);
        }

        if (exchange.getStatusCode().is2xxSuccessful()) {
            log.info("stage=operation-execution-success, operationId={}", balanceOperationRequest.getOperationId());
            return;
        } else if (exchange.getStatusCode().is4xxClientError()) {
            log.warn("stage=operation-execution-business-rule-error, operationId={}", balanceOperationRequest.getOperationId());
            throw new BalanceManagerOperationException(exchange.getBody());
        }

        log.error("stage=operation-execute-internal-error, operationId={}", balanceOperationRequest.getOperationId());
        throw new IntegrationException();
    }
}
