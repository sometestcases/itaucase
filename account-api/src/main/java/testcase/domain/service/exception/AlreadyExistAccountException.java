package testcase.domain.service.exception;

public class AlreadyExistAccountException extends Exception{
    public AlreadyExistAccountException(){
        super("already exist account");
    }
}
