package testcase.domain.service.model;

import java.math.BigDecimal;
import java.util.Objects;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AccountBalance {

    private final String accountId;

    private final BigDecimal balance;

    private final boolean blocked;

    public AccountBalance(String accountId, BigDecimal balance, boolean blocked) {
        this.accountId = Objects.requireNonNull(accountId, "Account id cant be null");
        this.balance = Objects.requireNonNull(balance, "Balance cant be null");
        this.blocked = blocked;
    }
}
