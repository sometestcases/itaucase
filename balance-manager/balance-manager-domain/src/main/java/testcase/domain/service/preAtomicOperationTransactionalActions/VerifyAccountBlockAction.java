package testcase.domain.service.preAtomicOperationTransactionalActions;

import java.time.LocalDateTime;
import java.util.Map;

import com.google.common.collect.ImmutableSet;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import testcase.domain.service.exception.BalanceAtomicOperationException;
import testcase.domain.service.exception.BalanceTransactionalOperationError;
import testcase.domain.service.model.SingleBalanceOperation;
import testcase.persistence.entities.Account;

@Component
@Order(1)
public class VerifyAccountBlockAction implements PreOperationTransactionalAction {

    @Override
    public void execute(Map<String, Account> accountMap, ImmutableSet<SingleBalanceOperation> singleBalanceOperations, LocalDateTime executionDate) throws BalanceAtomicOperationException {
        if (accountMap.values().stream().anyMatch(ac -> Boolean.TRUE.equals(ac.getBlockedBalance()))) {
            throw new BalanceAtomicOperationException(BalanceTransactionalOperationError.BLOCKED_ACCOUNT);
        }
    }
}
