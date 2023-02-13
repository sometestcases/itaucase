package testcase.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountBalanceState {

    private String accountId;

    private BigDecimal balance;

    private Boolean blocked;

    private LocalDateTime lastUpadate;

    private Long operationOrder;

    @Builder
    public AccountBalanceState(String accountId, BigDecimal balance, Boolean blocked, LocalDateTime lastUpadate, Long operationOrder){
        this.accountId = Objects.requireNonNull(accountId, "account id cant be null");
        this.balance = Objects.requireNonNull(balance, "balance cant be null");
        this.blocked = Objects.requireNonNull(blocked, "blocked cant be null");
        this.lastUpadate = Objects.requireNonNull(lastUpadate, "lastUpdate cant be null");
        this.operationOrder = Objects.requireNonNull(operationOrder, "operationOrder cant be null");
    }
}
