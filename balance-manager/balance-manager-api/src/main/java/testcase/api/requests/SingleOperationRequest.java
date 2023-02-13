package testcase.api.requests;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import testcase.api.exceptionHandle.MessageCodes;
import testcase.api.validators.NotZero;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleOperationRequest {

    @JsonProperty("account_id")
    @NotNull(message = MessageCodes.REQUIRED_FIELD)
    @Pattern(regexp = "[A-F\\d]{32}", message = MessageCodes.INVALID_PATTERN)
    private String accountId;

    @NotNull(message = MessageCodes.REQUIRED_FIELD)
    @NotZero
    private BigDecimal value;
}
