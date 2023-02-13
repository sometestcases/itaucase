package testcase.domain.service.exception;

public class DatabaseException extends Exception{

    public DatabaseException(Exception ex){
        super(ex);
    }
}
