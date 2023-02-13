package testcase.integrations.hystrix;

public class HystrixCommandKeys {

    public static final String CUSTOMER_GET_STATUS = HystrixGroupKeys.CUSTOMER_SERVICE + ".getStatus";

    public static final String BALANCE_MANAGER_BLOCK = HystrixGroupKeys.BALANCE_MANAGER + ".block";
}
