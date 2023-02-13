package testcase.domain.service.exception;

public class InternalTransferAlreadyExistsException extends Exception{
    public InternalTransferAlreadyExistsException(){
        super("internal transfer already exists");
    }
}
