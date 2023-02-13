package testcase.domain.service.model;

import java.util.Objects;

import com.google.common.collect.ImmutableSet;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BalanceOperation {

    private final String operationId;

    private final String operationType;

    private final ImmutableSet<SingleBalanceOperation> singleBalanceOperations;

    public BalanceOperation(String operationId,
                            String operationType,
                            ImmutableSet<SingleBalanceOperation> singleBalanceOperations) {
        this.operationId = Objects.requireNonNull(operationId, "Operation id cant be null");
        this.operationType = Objects.requireNonNull(operationType, "Operation type cant be null");

        Objects.requireNonNull(singleBalanceOperations, "Balance operations cant be null");

        if (singleBalanceOperations.isEmpty()) {
            throw new IllegalArgumentException();
        } else if (singleBalanceOperations.stream()
                .map(SingleBalanceOperation::getAccountId)
                .distinct().count() < singleBalanceOperations.size()) {
            throw new IllegalArgumentException();
        }

        this.singleBalanceOperations = singleBalanceOperations;
    }
}
