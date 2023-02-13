package testcase.domain.service.exception;

public class InternalTransferInitializationException extends Exception{
    public InternalTransferInitializationException(Exception exception){
        super(exception);
    }
}
