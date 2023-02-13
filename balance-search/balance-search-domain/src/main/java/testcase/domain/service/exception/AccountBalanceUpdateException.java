package testcase.domain.service.exception;


public class AccountBalanceUpdateException extends Exception {
    public AccountBalanceUpdateException(Exception ex) {
        super(ex);
    }
}
