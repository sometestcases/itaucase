package testcase.integrations.customerService.exception;

import lombok.Getter;

public class CustomerNotFoundException extends Exception {
    @Getter
    private String customerId;

    public CustomerNotFoundException(String customerId) {
        super("Customer with id " + customerId + " not found");
        this.customerId = customerId;
    }
}
