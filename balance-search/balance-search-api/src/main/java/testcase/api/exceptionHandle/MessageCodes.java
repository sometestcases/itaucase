package testcase.api.exceptionHandle;

import org.springframework.stereotype.Component;

@Component
public class MessageCodes {

    public static final String BALANCE_BLOCKED = "balance.blocked";

    public static final String BALANCE_SEARCH_INTERNAL_ERROR = "balance.search.internal.error";

    private MessageCodes(){

    }
}
