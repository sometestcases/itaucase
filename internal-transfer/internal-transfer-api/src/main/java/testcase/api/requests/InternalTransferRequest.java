package testcase.api.requests;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import testcase.api.exceptionHandle.MessageCodes;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternalTransferRequest {

    @JsonProperty("operation_id")
    @NotNull(message = MessageCodes.REQUIRED_FIELD)
    @Pattern(regexp = "[A-F\\d]{32}", message = MessageCodes.INVALID_PATTERN)
    private String operationId;

    @NotNull(message = MessageCodes.REQUIRED_FIELD)
    private BigDecimal value;
}
