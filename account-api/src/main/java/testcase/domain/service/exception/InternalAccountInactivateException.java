package testcase.domain.service.exception;

public class InternalAccountInactivateException extends Exception{

    public InternalAccountInactivateException(Exception exception){
        super(exception);
    }
}
