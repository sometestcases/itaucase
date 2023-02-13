package testcase.domain.service.exception;

public class AccountAlreadyInitializedException extends Exception {
    public AccountAlreadyInitializedException(){
        super("account initialize error");
    }
}
