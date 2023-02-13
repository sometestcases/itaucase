package testcase.api.exceptionHandle;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;
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
import testcase.domain.service.exception.AlreadyExecutedOperationException;
import testcase.domain.service.exception.BalanceAtomicOperationException;
import testcase.domain.service.exception.BalanceTransactionalOperationError;

@ControllerAdvice
public class ExceptionMapping {

    private static final ImmutableMap<BalanceTransactionalOperationError, Pair<HttpStatus, String>>
            BALANCE_TRANSACTIONAL_OPERATION_ERROR_MAP =
            ImmutableMap.<BalanceTransactionalOperationError, Pair<HttpStatus, String>>builder()
                    .put(BalanceTransactionalOperationError.DATABASE_ERROR,
                            Pair.of(HttpStatus.INTERNAL_SERVER_ERROR, MessageCodes.DATABASE_ERROR))
                    .put(BalanceTransactionalOperationError.BLOCKED_ACCOUNT,
                            Pair.of(HttpStatus.PRECONDITION_REQUIRED, MessageCodes.BLOCKED_ACCOUNT))
                    .put(BalanceTransactionalOperationError.INSUFICIENT_BALANCE,
                            Pair.of(HttpStatus.PRECONDITION_REQUIRED, MessageCodes.INSUFICIENT_BALANCE))
                    .put(BalanceTransactionalOperationError.OPERATION_DATE_INCONSISTENCY,
                            Pair.of(HttpStatus.INTERNAL_SERVER_ERROR, MessageCodes.DATE_INCONSISTENCY))
                    .put(BalanceTransactionalOperationError.DEBT_LIMIT_REACHED,
                            Pair.of(HttpStatus.PRECONDITION_REQUIRED, MessageCodes.DEBT_LIMIT_REACHED))
                    .put(BalanceTransactionalOperationError.INEXISTENT_ACCOUNT,
                            Pair.of(HttpStatus.NOT_FOUND, MessageCodes.ACCOUNT_NOT_FOUND))
                    .put(BalanceTransactionalOperationError.ALREADY_BLOCKED_ACCOUNT,
                            Pair.of(HttpStatus.CONFLICT, MessageCodes.ALREADY_BLOCKED_ACCOUNT))
                    .build();

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

    @ExceptionHandler(AlreadyExecutedOperationException.class)
    protected ResponseEntity<List<ErrorResponse>> handleAlreadyExecutedOperation(AlreadyExecutedOperationException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Arrays.asList(this.buildErrorResponse(MessageCodes.ALREADY_EXECUTED_OPERATION)));
    }

    @ExceptionHandler(BalanceAtomicOperationException.class)
    protected ResponseEntity<List<ErrorResponse>> handleBalanceAtomicOperationException(BalanceAtomicOperationException ex, WebRequest request) {

        Pair<HttpStatus, String> errorMapping = BALANCE_TRANSACTIONAL_OPERATION_ERROR_MAP.get(ex.getError());

        return ResponseEntity
                .status(errorMapping.getKey())
                .body(Arrays.asList(this.buildErrorResponse(errorMapping.getValue())));
    }

    private ErrorResponse buildErrorResponse(String code, Object... args) {
        return ErrorResponse.builder()
                .code(code)
                .message(this.messageSource.getMessage(code, args, LocaleContextHolder.getLocale()))
                .build();
    }
}
