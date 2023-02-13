package testcase.domain.service.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;


@Getter
public class ExecutedBalanceOperation extends BalanceOperation {

    private final LocalDateTime executionDate;

    private final ImmutableMap<String, AccountState> accountStates;

    public ExecutedBalanceOperation(BalanceOperation balanceOperation,
                                    ImmutableSet<AccountState> accountStates,
                                    LocalDateTime executionDate) {
        super(balanceOperation.getOperationId(), balanceOperation.getOperationType(), balanceOperation.getSingleBalanceOperations());

        this.executionDate = Objects.requireNonNull(executionDate, "Execution date cant be null");

        ImmutableMap.Builder<String, AccountState> mapBuilder = ImmutableMap.builder();

        for (AccountState accountState :
                Objects.requireNonNull(accountStates, "Account balance states cant be null")) {
            mapBuilder.put(accountState.getAccountId(), accountState);
        }
        this.accountStates = mapBuilder.build();

        if (balanceOperation.getSingleBalanceOperations().stream()
                .anyMatch(op -> !this.accountStates.containsKey(op.getAccountId()))) {
            throw new IllegalArgumentException("Executed balance operation need have all account states");
        }
    }
}
