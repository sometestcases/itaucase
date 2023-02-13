package testcase.api.requests;

import java.util.List;
import java.util.Set;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import testcase.api.exceptionHandle.MessageCodes;
import testcase.api.validators.NotDuplicatedAccounts;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceOperationRequest {

    @JsonProperty("operation_id")
    @NotNull(message = MessageCodes.REQUIRED_FIELD)
    @Pattern(regexp = "[A-F\\d]{32}", message = MessageCodes.INVALID_PATTERN)
    private String operationId;

    @JsonProperty("operation_type")
    @NotNull(message = MessageCodes.REQUIRED_FIELD)
    private String operationType;

    @NotDuplicatedAccounts
    @Size(min = 1, max = 5, message = MessageCodes.INVALID_SIZE)
    private List<SingleOperationRequest> operations;
}
