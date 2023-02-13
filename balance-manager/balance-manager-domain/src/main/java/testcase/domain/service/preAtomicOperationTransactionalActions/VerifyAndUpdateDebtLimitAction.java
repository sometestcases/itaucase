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
@Order(3)
public class VerifyAndUpdateDebtLimitAction implements PreOperationTransactionalAction {

    @Override
    public void execute(Map<String, Account> accountMap, ImmutableSet<SingleBalanceOperation> singleBalanceOperations, LocalDateTime executionDate) throws BalanceAtomicOperationException {

        for (Account account : accountMap.values()) {
            if (account.getLastBalanceUpdateDate() == null) {
                account.setDailyDebtsAccumulator(BigDecimal.ZERO);
            } else if (!account.getLastBalanceUpdateDate().toLocalDate().equals(executionDate.toLocalDate())) {
                account.setDailyDebtsAccumulator(BigDecimal.ZERO);
            }
        }

        if (singleBalanceOperations.stream()
                .filter(o -> o.getValue().signum() == -1)
                .collect(Collectors.toMap(o -> o.getAccountId(), o -> o.getValue().abs(), BigDecimal::add))
                .entrySet().stream().anyMatch(accountAndDebt ->
                        accountMap.get(accountAndDebt.getKey()).getDailyDebtsAccumulator().add(accountAndDebt.getValue())
                                .compareTo(accountMap.get(accountAndDebt.getKey()).getDailyDebtLimit()) > 0)) {
            throw new BalanceAtomicOperationException(BalanceTransactionalOperationError.DEBT_LIMIT_REACHED);
        }


        for (SingleBalanceOperation operation : singleBalanceOperations) {
            if (operation.getValue().signum() == -1) {
                Account account = accountMap.get(operation.getAccountId());

                account.setDailyDebtsAccumulator(account.getDailyDebtsAccumulator().add(operation.getValue().abs()));
            }
        }
    }
}
