package testcase.api.requests;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import testcase.api.exceptionHandle.MessageCodes;

@Data
@NoArgsConstructor
public class CreateAccountRequest {

    @JsonProperty("customer_id")
    @Pattern(regexp = "[A-F\\d]{32}", message = MessageCodes.INVALID_PATTERN)
    @NotNull
    private String customerId;

    @Max(value = 99, message = MessageCodes.INVALID_RANGE)
    @Min(value = 1, message = MessageCodes.INVALID_RANGE)
    @NotNull
    private int agency;
}
