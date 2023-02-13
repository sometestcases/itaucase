package testcase.domain.integrations.balanceManager.request;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleOperationRequest {

    @JsonProperty("account_id")
    private String accountId;

    private BigDecimal value;
}
