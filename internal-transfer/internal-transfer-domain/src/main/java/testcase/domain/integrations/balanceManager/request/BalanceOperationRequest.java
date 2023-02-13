package testcase.domain.integrations.balanceManager.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceOperationRequest {

    @JsonProperty("operation_id")
    private String operationId;

    @JsonProperty("operation_type")
    private String operationType;

    private List<SingleOperationRequest> operations;
}
