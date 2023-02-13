package testcase.cucumber.steps.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;


public class SingleOperationRequest {

    @JsonProperty("account_id")
    private String accountId;

    private BigDecimal value;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
