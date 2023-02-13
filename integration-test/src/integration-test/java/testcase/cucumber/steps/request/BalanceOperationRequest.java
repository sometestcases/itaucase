package testcase.cucumber.steps.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class BalanceOperationRequest {

    @JsonProperty("operation_id")
    private String operationId;

    @JsonProperty("operation_type")
    private String operationType;

    private List<SingleOperationRequest> operations;

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public List<SingleOperationRequest> getOperations() {
        return operations;
    }

    public void setOperations(List<SingleOperationRequest> operations) {
        this.operations = operations;
    }
}
