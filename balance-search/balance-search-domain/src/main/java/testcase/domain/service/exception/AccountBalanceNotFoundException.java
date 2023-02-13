package testcase.domain.service.exception;


public class AccountBalanceNotFoundException extends Exception {
    public AccountBalanceNotFoundException() {
        super("account balance not found");
    }
}
