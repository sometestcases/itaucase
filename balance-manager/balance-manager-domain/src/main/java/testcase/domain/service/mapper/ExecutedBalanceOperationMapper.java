package testcase.domain.service.mapper;

import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import testcase.domain.service.model.ExecutedBalanceOperation;
import testcase.event.avro.BalanceAtomicOperation;
import testcase.event.avro.BalanceUpdate;
import testcase.event.avro.Operation;

@Component
public class ExecutedBalanceOperationMapper {

    @Value("${date.zone}")
    private String dateZone;

    @Autowired
    private AccountStateMapper accountStateMapper;

    public BalanceAtomicOperation toBalanceAtomicOperationEvent(ExecutedBalanceOperation executedBalanceOperation) {

        return BalanceAtomicOperation.newBuilder()
                .setOperationId(executedBalanceOperation.getOperationId())
                .setOperationDate(executedBalanceOperation.getExecutionDate()
                        .atZone(ZoneId.of(this.dateZone)).toInstant().toEpochMilli())
                .setOperationType(executedBalanceOperation.getOperationType())
                .setOperations(executedBalanceOperation.getSingleBalanceOperations().stream().map(op ->
                        Operation.newBuilder()
                                .setAccountId(op.getAccountId())
                                .setAccountNumber(executedBalanceOperation.getAccountStates().get(op.getAccountId()).getNumber())
                                .setAccountAgency(executedBalanceOperation.getAccountStates().get(op.getAccountId()).getAgency())
                                .setValue(op.getValue().doubleValue())
                                .build()
                ).collect(Collectors.toList()))
                .build();
    }

    public Set<BalanceUpdate> toBalanceUpdateEvent(ExecutedBalanceOperation executedBalanceOperation) {
        return executedBalanceOperation.getAccountStates().values().stream().map(st ->
                        this.accountStateMapper.toBalanceUpdateEvent(st,
                                executedBalanceOperation.getExecutionDate()))
                .collect(Collectors.toSet());
    }
}
