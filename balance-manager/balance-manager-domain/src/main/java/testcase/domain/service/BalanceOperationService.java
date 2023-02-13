package testcase.domain.service;

import java.time.LocalDateTime;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import testcase.domain.eventPublisher.PublishService;
import testcase.domain.service.exception.AlreadyExecutedOperationException;
import testcase.domain.service.exception.BalanceAtomicOperationException;
import testcase.domain.service.exception.BalanceOperationSizeExcededException;
import testcase.domain.service.exception.BalanceTransactionalOperationError;
import testcase.domain.service.mapper.ExecutedBalanceOperationMapper;
import testcase.domain.service.model.AccountState;
import testcase.domain.service.model.BalanceOperation;
import testcase.domain.service.model.ExecutedBalanceOperation;
import testcase.persistence.repositories.AccountOperationLockRepository;
import testcase.persistence.repositories.AccountRepository;

@Slf4j
@Service
public class BalanceOperationService {

    @Value("${balance-operation.limit-per-operation}")
    private int balanceOperationLimit;

    @Autowired
    private BalanceAtomicOperationComponent balanceAtomicOperationComponent;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountOperationLockRepository accountOperationLockRepository;

    @Autowired
    private ExecutedBalanceOperationMapper executedBalanceOperationMapper;

    @Autowired
    private PublishService publishService;

    public ExecutedBalanceOperation executeOperation(BalanceOperation balanceOperation)
            throws AlreadyExecutedOperationException,
            BalanceAtomicOperationException,
            BalanceOperationSizeExcededException {

        if (balanceOperation.getSingleBalanceOperations().size() > this.balanceOperationLimit) {

            log.warn("stage=balance-operations-invalid-size, operationId={}, type={}",
                    balanceOperation.getOperationId(), balanceOperation.getOperationType());
            throw new BalanceOperationSizeExcededException();

        } else if (this.accountOperationLockRepository.existsByOperationId(balanceOperation.getOperationId())) {

            log.warn("stage=operation-already-executed, operationId={}, type={}",
                    balanceOperation.getOperationId(), balanceOperation.getOperationType());
            throw new AlreadyExecutedOperationException();
        }

        LocalDateTime executionDate = LocalDateTime.now();

        final ImmutableSet<AccountState> accountStatesAfterOperation;

        try {
            accountStatesAfterOperation = this.balanceAtomicOperationComponent
                    .executeAtomicBalanceOperation(balanceOperation, executionDate);
        } catch (BalanceAtomicOperationException btoe) {
            log.warn("stage=balance-transaction-operation-exception, operationId={}, type={}, error={}",
                    balanceOperation.getOperationId(), balanceOperation.getOperationType(), btoe.getError());
            throw btoe;
        } catch (Exception ex) {
            log.warn("stage=balance-operation-internal-error, operationId={}, type={}",
                    balanceOperation.getOperationId(), balanceOperation.getOperationType(), ex);
            throw new BalanceAtomicOperationException(BalanceTransactionalOperationError.DATABASE_ERROR);
        }

        ExecutedBalanceOperation executedBalanceOperation = new ExecutedBalanceOperation(
                balanceOperation, accountStatesAfterOperation, executionDate);

        log.info("stage=executed-balance-atomic-operation, operationId={}, operationType={}",
                executedBalanceOperation.getOperationId(), executedBalanceOperation.getOperationType());

        this.publishService.publish(this.executedBalanceOperationMapper
                .toBalanceAtomicOperationEvent(executedBalanceOperation));

        this.publishService.publishAll(this.executedBalanceOperationMapper
                .toBalanceUpdateEvent(executedBalanceOperation));

        return executedBalanceOperation;
    }
}
