package testcase.api.exceptionHandle;

import org.springframework.stereotype.Component;

@Component
public class MessageCodes {

    public static final String INVALID_PATTERN = "invalid.pattern";
    public static final String INVALID_RANGE = "invalid.range";
    public static final String ALREADY_EXIST_ACCOUNT = "already.exist.account";
    public static final String ALREADY_INACTIVE_ACCOUNT = "already.inactive.account";
    public static final String CANT_BLOCK_BALANCE = "cant.block.balance";
    public static final String INACTIVE_CUSTOMER = "inactive.customer";
    public static final String INEXISTENT_CUSTOMER = "inexistent.customer";
    public static final String INEXISTENT_ACCOUNT = "inexistent.account";

    private MessageCodes(){

    }
}
