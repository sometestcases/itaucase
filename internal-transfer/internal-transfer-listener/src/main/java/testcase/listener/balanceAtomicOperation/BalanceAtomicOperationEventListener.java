package testcase.listener.balanceAtomicOperation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import testcase.domain.service.BacenService;
import testcase.domain.service.InternalTransferInitializeService;
import testcase.domain.service.InternalTransferService;
import testcase.domain.service.exception.InternalTransferAlreadyExistsException;
import testcase.domain.service.exception.InternalTransferInitializationException;
import testcase.event.avro.BalanceAtomicOperation;
import testcase.listener.config.KafkaSubscriberConfig;

@Slf4j
@Component
public class BalanceAtomicOperationEventListener {

    @Autowired
    private InternalTransferInitializeService internalTransferInitializeService;

    @Autowired
    private BacenService bacenService;

    @KafkaListener(topics = "${kafka.topics.balance-atomic-operation}", containerFactory = KafkaSubscriberConfig.BALANCE_ATOMIC_OPERATION_CONTAINER_FACTORY, groupId = "${spring.application.name}[${spring.profiles.active}]")
    public void listenAtomicOperation(final ConsumerRecord<String, BalanceAtomicOperation> event) {

        BalanceAtomicOperation atomicOperation = event.value();

        if (!InternalTransferService.BALANCE_MANAGER_OPERATION_TYPE
                .equals(atomicOperation.getOperationType())) {
            return;
        }

        final Long internalTransferId;

        try {
            internalTransferId =
                    this.internalTransferInitializeService.initializeByBalanceAtomicOperationEvent(atomicOperation);
        } catch (InternalTransferAlreadyExistsException e) {
            log.warn("stage=already-existent-transfer, operationId={}", atomicOperation.getOperationId());
            return;
        } catch (InternalTransferInitializationException e) {
            log.warn("stage=transfer-initialization-exception-retry, operationId={}",
                    atomicOperation.getOperationId(), e);
            throw new RuntimeException(e);
        } catch (Exception ex) {
            log.error("stage=unretryable-error, operationId={}", atomicOperation.getOperationId(), ex);
            return;
        }

        this.bacenService.tryEnqueueSincInternalTransferById(internalTransferId);
        log.info("stage=internal-transfer-success-initialized");
    }
}
