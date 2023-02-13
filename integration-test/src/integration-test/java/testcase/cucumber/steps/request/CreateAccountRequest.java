package testcase.cucumber.steps.request;

import com.fasterxml.jackson.annotation.JsonProperty;


public class CreateAccountRequest {

    @JsonProperty("customer_id")
    private String customerId;

    private int agency;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public int getAgency() {
        return agency;
    }

    public void setAgency(int agency) {
        this.agency = agency;
    }
}
