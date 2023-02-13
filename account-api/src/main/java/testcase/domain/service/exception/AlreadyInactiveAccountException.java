package testcase.domain.service.exception;

public class AlreadyInactiveAccountException extends Exception{
    public AlreadyInactiveAccountException(){
        super("already inactive account");
    }
}
