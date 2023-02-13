package testcase.api.exceptionHandle;

import org.springframework.stereotype.Component;

@Component
public class MessageCodes {

    public static final String INVALID_PATTERN = "invalid.pattern";

    public static final String REQUIRED_FIELD = "required.field";

    public static final String INTERNAL_TRANSFER_ERROR = "internal.transfer.error";

    public static final String BALANCE_MANAGER_ERROR = "balance.manager.error";

    private MessageCodes(){

    }
}
