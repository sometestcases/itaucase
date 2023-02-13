package testcase.domain.service.exception;

public class AlreadyExecutedOperationException extends Exception {
    public AlreadyExecutedOperationException(){
        super("already executed operation");
    }
}
