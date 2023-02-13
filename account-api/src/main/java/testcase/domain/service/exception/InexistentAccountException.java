package testcase.domain.service.exception;

public class InexistentAccountException extends Exception{
    public InexistentAccountException(){
        super("inexistent account");
    }
}
