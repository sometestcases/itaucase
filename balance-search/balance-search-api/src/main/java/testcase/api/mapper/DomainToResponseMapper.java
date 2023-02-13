package testcase.api.mapper;

import testcase.api.responses.AccountBalanceResponse;
import testcase.domain.model.AccountBalanceState;

public class DomainToResponseMapper {

    private DomainToResponseMapper() {

    }

    public static AccountBalanceResponse map(AccountBalanceState accountBalanceState) {
        return AccountBalanceResponse.builder()
                .accountId(accountBalanceState.getAccountId())
                .balance(accountBalanceState.getBalance())
                .operationOrder(accountBalanceState.getOperationOrder())
                .blocked(accountBalanceState.getBlocked())
                .lastUpdate(accountBalanceState.getLastUpadate())
                .build();
    }
}
