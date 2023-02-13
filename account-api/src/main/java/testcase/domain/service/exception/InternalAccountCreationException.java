package testcase.domain.service.exception;

public class InternalAccountCreationException extends Exception{

    public InternalAccountCreationException(Exception exception){
        super(exception);
    }
}
