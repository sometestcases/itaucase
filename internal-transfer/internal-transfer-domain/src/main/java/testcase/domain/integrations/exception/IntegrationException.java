package testcase.domain.integrations.exception;

public class IntegrationException extends Exception{

    public IntegrationException(){

    }

    public IntegrationException(Exception exception){
        super(exception);
    }

}
