package testcase.domain.service.preAtomicOperationTransactionalActions;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableSet;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import testcase.domain.service.exception.BalanceAtomicOperationException;
import testcase.domain.service.exception.BalanceTransactionalOperationError;
import testcase.domain.service.model.SingleBalanceOperation;
import testcase.persistence.entities.Account;

@Component
@Order(0)
public class VerifyExecutionDateAction implements PreOperationTransactionalAction {

    @Override
    public void execute(Map<String, Account> accountMap, ImmutableSet<SingleBalanceOperation> singleBalanceOperations,
                        LocalDateTime executionDate) throws BalanceAtomicOperationException {
        if (accountMap.values().stream().map(Account::getLastBalanceUpdateDate)
                .filter(Objects::nonNull)
                .anyMatch(dt -> dt.isAfter(executionDate))) {
            throw new BalanceAtomicOperationException(BalanceTransactionalOperationError.OPERATION_DATE_INCONSISTENCY);
        }
    }
}
