package testcase.domain.service.model;

import java.math.BigDecimal;
import java.util.Objects;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SingleBalanceOperation {

    private final String accountId;

    private final BigDecimal value;

    public SingleBalanceOperation(String accountId, BigDecimal value){
        this.accountId = Objects.requireNonNull(accountId,"Account id cant be null");
        this.value = Objects.requireNonNull(value, "Account id cant be null");
    }
}
