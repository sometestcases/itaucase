package testcase.api.requests.mapper;

import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import testcase.api.requests.BalanceOperationRequest;
import testcase.api.requests.SingleOperationRequest;
import testcase.domain.service.model.BalanceOperation;
import testcase.domain.service.model.SingleBalanceOperation;

public class RequestToDomainMapper {

    private RequestToDomainMapper() {

    }

    public static SingleBalanceOperation map(SingleOperationRequest request) {
        return SingleBalanceOperation.builder()
                .accountId(request.getAccountId())
                .value(request.getValue())
                .build();
    }

    public static BalanceOperation map(BalanceOperationRequest request) {
        return BalanceOperation.builder()
                .operationId(request.getOperationId())
                .operationType(request.getOperationType())
                .singleBalanceOperations(
                        ImmutableSet.copyOf(request.getOperations().stream()
                                .map(RequestToDomainMapper::map).collect(Collectors.toSet())))
                .build();
    }
}
