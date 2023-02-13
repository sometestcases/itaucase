package testcase.domain.service.exception;

public class InternalTransferNotExistsException extends Exception{
    public InternalTransferNotExistsException(){
        super("internal transfer not exists");
    }
}
