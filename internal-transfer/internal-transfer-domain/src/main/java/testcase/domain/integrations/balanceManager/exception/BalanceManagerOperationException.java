package testcase.domain.integrations.balanceManager.exception;

import java.util.List;

import lombok.Getter;
import testcase.domain.integrations.balanceManager.response.ErrorResponse;

public class BalanceManagerOperationException extends Exception {

    @Getter
    private List<ErrorResponse> balanceManagerErrors;

    public BalanceManagerOperationException(List<ErrorResponse> errors) {
        this.balanceManagerErrors = errors;
    }
}
