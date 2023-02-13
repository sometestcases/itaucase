package testcase.api.exceptionHandle;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import testcase.api.responses.ErrorResponse;
import testcase.domain.service.exception.AccountBalanceBlockedException;
import testcase.domain.service.exception.AccountBalanceNotFoundException;
import testcase.domain.service.exception.AccountBalanceSearchException;

@ControllerAdvice
public class ExceptionMapping {

    @Autowired
    private MessageSource messageSource;


    @ExceptionHandler(AccountBalanceNotFoundException.class)
    protected ResponseEntity<?> handleAccountBalanceNotFoundException(AccountBalanceNotFoundException ex, WebRequest request) {
        return ResponseEntity.notFound().build();
    }


    @ExceptionHandler(AccountBalanceSearchException.class)
    protected ResponseEntity<List<ErrorResponse>> handleAccountBalanceSearchException(AccountBalanceSearchException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Arrays.asList(this.buildErrorResponse(MessageCodes.BALANCE_SEARCH_INTERNAL_ERROR)));
    }

    @ExceptionHandler(AccountBalanceBlockedException.class)
    protected ResponseEntity<List<ErrorResponse>> handleAccountBalanceBlockedException(AccountBalanceBlockedException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.PRECONDITION_FAILED)
                .body(Arrays.asList(this.buildErrorResponse(MessageCodes.BALANCE_BLOCKED)));
    }

    private ErrorResponse buildErrorResponse(String code, Object... args) {
        return ErrorResponse.builder()
                .code(code)
                .message(this.messageSource.getMessage(code, args, LocaleContextHolder.getLocale()))
                .build();
    }
}
