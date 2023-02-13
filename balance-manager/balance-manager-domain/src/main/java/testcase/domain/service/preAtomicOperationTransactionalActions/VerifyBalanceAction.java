package testcase.domain.service.preAtomicOperationTransactionalActions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import testcase.domain.service.exception.BalanceAtomicOperationException;
import testcase.domain.service.exception.BalanceTransactionalOperationError;
import testcase.domain.service.model.SingleBalanceOperation;
import testcase.persistence.entities.Account;

@Component
@Order(2)
public class VerifyBalanceAction implements PreOperationTransactionalAction {

    @Override
    public void execute(Map<String, Account> accountMap, ImmutableSet<SingleBalanceOperation> singleBalanceOperations, LocalDateTime executionDate) throws BalanceAtomicOperationException {
        if (singleBalanceOperations.stream()
                .filter(o -> o.getValue().signum() == -1)
                .collect(Collectors.toMap(o -> o.getAccountId(), o -> o.getValue().abs(), BigDecimal::add))
                .entrySet().stream().anyMatch(accountAndDebt ->
                        accountMap.get(accountAndDebt.getKey()).getBalance().compareTo(accountAndDebt.getValue()) < 0)) {
            throw new BalanceAtomicOperationException(BalanceTransactionalOperationError.INSUFICIENT_BALANCE);
        }
    }
}
