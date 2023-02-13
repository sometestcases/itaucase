package testcase.domain.service.exception;

public enum BalanceTransactionalOperationError {

    INSUFICIENT_BALANCE,
    DEBT_LIMIT_REACHED,
    INEXISTENT_ACCOUNT,
    BLOCKED_ACCOUNT,

    ALREADY_BLOCKED_ACCOUNT,

    OPERATION_DATE_INCONSISTENCY,
    DATABASE_ERROR
}
