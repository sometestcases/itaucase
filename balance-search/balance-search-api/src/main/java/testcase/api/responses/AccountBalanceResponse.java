package testcase.api.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountBalanceResponse {

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("last_update")
    private LocalDateTime lastUpdate;

    private Boolean blocked;

    private BigDecimal balance;

    @JsonProperty("operation_order")
    private Long operationOrder;
}
