package testcase.api.exceptionHandle;

import org.springframework.stereotype.Component;

@Component
public class MessageCodes {

    public static final String INVALID_PATTERN = "invalid.pattern";

    public static final String REQUIRED_FIELD = "required.field";

    public static final String INVALID_SIZE = "invalid.size";

    public static final String ALREADY_EXECUTED_OPERATION = "already.executed.operation";

    public static final String DATABASE_ERROR = "database.error";

    public static final String BLOCKED_ACCOUNT = "blocked.account";

    public static final String INSUFICIENT_BALANCE = "insuficient.balance";

    public static final String DATE_INCONSISTENCY = "date.inconsistency";

    public static final String ACCOUNT_NOT_FOUND = "account.not.found";

    public static final String ALREADY_BLOCKED_ACCOUNT = "already.blocked.account";

    public static final String NOT_DUPLIED_ACCOUNTS = "not.duplied.accounts";

    public static final String DEBT_LIMIT_REACHED = "debt.limit.reached";

    public static final String NOT_ZERO_VALUE = "not.zero.value";

    private MessageCodes(){

    }
}
