package testcase.integrations.balanceManager;

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
import testcase.integrations.balanceManager.exception.FailedBlockException;
import testcase.integrations.exception.IntegrationException;
import testcase.integrations.hystrix.HystrixCommandKeys;
import testcase.integrations.hystrix.HystrixGroupKeys;

@Slf4j
@Component
public class BalanceManagerClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${balance-manager-api.url}")
    private String balanceManagerApiUrl;

    @HystrixCommand( //
            groupKey = HystrixGroupKeys.BALANCE_MANAGER, //
            commandKey = HystrixCommandKeys.BALANCE_MANAGER_BLOCK)
    public boolean blockBalance(String accountId) throws FailedBlockException, IntegrationException {
        Objects.requireNonNull(accountId, "Account Id cant be null");

        log.info("stage=block-balance-status-init, accountId={}", accountId);
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        final HttpEntity<?> entity = new HttpEntity<>(headers);

        final ResponseEntity<?> exchange;

        try {
            exchange = restTemplate
                    .exchange(this.balanceManagerApiUrl.concat(Endpoints.BLOCK_BALANCE)
                            , HttpMethod.PATCH, entity, Void.class, accountId);
        } catch (Exception ex) {

            log.error("stage=error-to-block-account, accountId={}", accountId, ex);
            throw new IntegrationException(ex);
        }

        if (exchange.getStatusCode().equals(HttpStatus.CONFLICT)) {
            log.info("stage=account-already-blocked, accountId={}", accountId);
            return true;
        } else if (exchange.getStatusCode().equals(HttpStatus.OK)) {
            log.info("stage=account-blocked, accountId={}", accountId);
            return true;
        } else if (exchange.getStatusCode().is4xxClientError()) {
            log.info("stage=account-blocked, accountId={}", accountId);
            return false;
        }

        log.error("stage=unaccepted-balance-manager-block-status, accountId={}, httpStatus={}", accountId, exchange.getStatusCode());
        throw new FailedBlockException();

    }
}
