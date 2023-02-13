package testcase.domain.service.exception;

public class InexistentCustomerException extends Exception{
    public InexistentCustomerException(){
        super("inexistent customer");
    }
}
