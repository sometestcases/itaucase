package testcase.domain.eventPublisher.mapping;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import testcase.event.avro.BalanceAtomicOperation;

@Component
public class BalanceAtomicOperationEventMapping implements PublishMapping<BalanceAtomicOperation>{

    @Value("${kafka.topics.balance-atomic-operation}")
    private String balanceAtomicOperation;

    @Override
    public Class<BalanceAtomicOperation> getMappedClass() {
        return BalanceAtomicOperation.class;
    }

    @Override
    public String getTopic() {
        return this.balanceAtomicOperation;
    }

    @Override
    public String getKey(BalanceAtomicOperation balanceAtomicOperation) {
        return balanceAtomicOperation.getOperationId();
    }
}
