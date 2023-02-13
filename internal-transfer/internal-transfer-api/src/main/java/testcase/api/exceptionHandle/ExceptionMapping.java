package testcase.api.exceptionHandle;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
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
import testcase.domain.integrations.balanceManager.exception.BalanceManagerOperationException;
import testcase.domain.service.exception.InternalTransferServerException;

@ControllerAdvice
public class ExceptionMapping {

    private static final ImmutableMap<String, HttpStatus> BALANCE_MANAGER_ERROR_MAP =
            ImmutableMap.<String, HttpStatus>builder()
                    .put("invalid.pattern", HttpStatus.BAD_REQUEST)
                    .put("required.field", HttpStatus.INTERNAL_SERVER_ERROR)
                    .put("invalid.size", HttpStatus.INTERNAL_SERVER_ERROR)
                    .put("already.executed.operation", HttpStatus.CONFLICT)
                    .put("database.error", HttpStatus.INTERNAL_SERVER_ERROR)
                    .put("blocked.account", HttpStatus.PRECONDITION_REQUIRED)
                    .put("insuficient.balance", HttpStatus.PRECONDITION_REQUIRED)
                    .put("date.inconsistency", HttpStatus.INTERNAL_SERVER_ERROR)
                    .put("account.not.found", HttpStatus.NOT_FOUND)
                    .put("not.duplied.accounts", HttpStatus.INTERNAL_SERVER_ERROR)
                    .put("debt.limit.reached", HttpStatus.PRECONDITION_REQUIRED)
                    .put("not.zero.value", HttpStatus.BAD_REQUEST)
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

    @ExceptionHandler(InternalTransferServerException.class)
    protected ResponseEntity<List<ErrorResponse>> handleInternalTransferServerException(InternalTransferServerException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Arrays.asList(this.buildErrorResponse(MessageCodes.INTERNAL_TRANSFER_ERROR)));
    }

    @ExceptionHandler(BalanceManagerOperationException.class)
    protected ResponseEntity<List<ErrorResponse>> handleBalanceManagerOperationException(BalanceManagerOperationException ex, WebRequest request) {

        Optional<testcase.domain.integrations.balanceManager.response.ErrorResponse> errorResponse = ex.getBalanceManagerErrors().stream()
                .findFirst();

        if (errorResponse.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Arrays.asList(this.buildErrorResponse(MessageCodes.BALANCE_MANAGER_ERROR)));
        }

        return ResponseEntity
                .status(BALANCE_MANAGER_ERROR_MAP.getOrDefault(errorResponse.get().getCode(), HttpStatus.INTERNAL_SERVER_ERROR))
                .body(Arrays.asList(
                        ErrorResponse.builder()
                                .code(errorResponse.get().getCode())
                                .message(errorResponse.get().getMessage())
                                .build()
                ));
    }

    private ErrorResponse buildErrorResponse(String code, Object... args) {
        return ErrorResponse.builder()
                .code(code)
                .message(this.messageSource.getMessage(code, args, LocaleContextHolder.getLocale()))
                .build();
    }
}
