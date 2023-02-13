package testcase.domain.service.exception;

public class InactiveCustomerException extends Exception{
    public InactiveCustomerException(){
        super("inactive customer");
    }
}
