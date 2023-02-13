package testcase.api.exceptionHandle;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import testcase.api.responses.ErrorResponse;
import testcase.domain.service.exception.AlreadyExistAccountException;
import testcase.domain.service.exception.AlreadyInactiveAccountException;
import testcase.domain.service.exception.CantBlockBalanceException;
import testcase.domain.service.exception.InactiveCustomerException;
import testcase.domain.service.exception.InexistentAccountException;
import testcase.domain.service.exception.InexistentCustomerException;
import testcase.domain.service.exception.InternalAccountCreationException;
import testcase.domain.service.exception.InternalAccountInactivateException;

@ControllerAdvice
public class ExceptionMapping {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<List<ErrorResponse>> handleFieldError(MethodArgumentNotValidException ex, WebRequest request) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getBindingResult().getFieldErrors().stream()
                        .map(error -> this.buildErrorResponse(error.getDefaultMessage(), error.getField()))
                        .collect(Collectors.toList()));
    }

    @ExceptionHandler(AlreadyExistAccountException.class)
    protected ResponseEntity<List<ErrorResponse>> handleAlreadyExistAccount(AlreadyExistAccountException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Arrays.asList(this.buildErrorResponse(MessageCodes.ALREADY_EXIST_ACCOUNT)));
    }

    @ExceptionHandler(AlreadyInactiveAccountException.class)
    protected ResponseEntity<List<ErrorResponse>> handleAlreadyInactiveAccount(AlreadyInactiveAccountException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Arrays.asList(this.buildErrorResponse(MessageCodes.ALREADY_INACTIVE_ACCOUNT)));
    }

    @ExceptionHandler(CantBlockBalanceException.class)
    protected ResponseEntity<List<ErrorResponse>> handleCantBlockBalance(CantBlockBalanceException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.FAILED_DEPENDENCY)
                .body(Arrays.asList(this.buildErrorResponse(MessageCodes.CANT_BLOCK_BALANCE)));
    }

    @ExceptionHandler(InactiveCustomerException.class)
    protected ResponseEntity<List<ErrorResponse>> handleInactiveCustomer(InactiveCustomerException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.PRECONDITION_REQUIRED)
                .body(Arrays.asList(this.buildErrorResponse(MessageCodes.INACTIVE_CUSTOMER)));
    }

    @ExceptionHandler(InexistentAccountException.class)
    protected ResponseEntity<List<ErrorResponse>> handleInexistentAccount(InexistentAccountException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.PRECONDITION_REQUIRED)
                .body(Arrays.asList(this.buildErrorResponse(MessageCodes.INEXISTENT_ACCOUNT)));
    }

    @ExceptionHandler(InexistentCustomerException.class)
    protected ResponseEntity<List<ErrorResponse>> handleInexistentCustomer(InexistentCustomerException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.PRECONDITION_REQUIRED)
                .body(Arrays.asList(this.buildErrorResponse(MessageCodes.INEXISTENT_CUSTOMER)));
    }

    @ExceptionHandler(value =
            {InternalAccountCreationException.class,
                    InternalAccountInactivateException.class})
    protected ResponseEntity<List<ErrorResponse>> handleInternalErrors(Exception ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }

    private ErrorResponse buildErrorResponse(String code, Object... args) {
        return ErrorResponse.builder()
                .code(code)
                .message(this.messageSource.getMessage(code, args, LocaleContextHolder.getLocale()))
                .build();
    }
}
