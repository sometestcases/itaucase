package testcase.cucumber.steps.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalTransferRequest {

    @JsonProperty("operation_id")
    private String operationId;

    private BigDecimal value;

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
