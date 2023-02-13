package testcase.domain.service.exception;

public class InternalTransferAlreadySincException extends Exception{
    public InternalTransferAlreadySincException(){
        super("internal transfer already exists");
    }
}
