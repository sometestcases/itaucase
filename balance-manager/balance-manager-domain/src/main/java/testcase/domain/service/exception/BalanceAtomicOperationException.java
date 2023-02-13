package testcase.domain.service.exception;

import lombok.Getter;

@Getter
public class BalanceAtomicOperationException extends Exception {

    private BalanceTransactionalOperationError error;

    public BalanceAtomicOperationException(BalanceTransactionalOperationError error) {
        super(error.name());
        this.error = error;
    }
}
