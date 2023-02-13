package testcase.domain.service;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import testcase.domain.eventPublisher.PublishService;
import testcase.domain.service.exception.BalanceAtomicOperationException;
import testcase.domain.service.mapper.AccountStateMapper;
import testcase.domain.service.model.AccountState;

@Slf4j
@Service
public class BalanceBlockService {

    @Autowired
    private BalanceAtomicOperationComponent balanceAtomicOperationComponent;

    @Autowired
    private AccountStateMapper accountStateMapper;

    @Autowired
    private PublishService publishService;

    public void blockAccount(String accountId) throws BalanceAtomicOperationException {

        log.info("stage=init-account-block, accountId={}", accountId);

        LocalDateTime executionDate = LocalDateTime.now();

        AccountState accountState = this.balanceAtomicOperationComponent
                .executeAtomicBalanceBlock(accountId, executionDate);

        this.publishService.publish(this.accountStateMapper
                .toBalanceUpdateEvent(accountState, executionDate));

    }
}
