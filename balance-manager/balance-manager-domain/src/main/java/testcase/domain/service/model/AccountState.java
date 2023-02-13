package testcase.domain.service.model;

import java.math.BigDecimal;
import java.util.Objects;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AccountState {

    private final String accountId;

    private int number;

    private int agency;

    private final BigDecimal balance;

    private final Long operationOrder;

    private final boolean blocked;

    public AccountState(String accountId, int number, int agency,
                        BigDecimal balance, Long operationOrder, boolean blocked) {
        this.accountId = Objects.requireNonNull(accountId, "Account id cant be null");
        this.balance = Objects.requireNonNull(balance, "Balance cant be null");
        this.operationOrder = Objects.requireNonNull(operationOrder, "Operation order cant be null");
        this.agency = agency;
        this.number = number;
        this.blocked = blocked;
    }
}
