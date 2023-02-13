package testcase.domain.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import testcase.domain.service.exception.InternalTransferAlreadyExistsException;
import testcase.domain.service.exception.InternalTransferInitializationException;
import testcase.event.avro.BalanceAtomicOperation;
import testcase.event.avro.Operation;
import testcase.persistence.entities.InternalTransfer;
import testcase.persistence.repositories.InternalTransferRepository;
import testcase.persistence.utils.TransactionalComponent;

@Slf4j
@Service
public class InternalTransferInitializeService {

    @Value("${date.zone}")
    private String dateZone;

    @Autowired
    private InternalTransferRepository internalTransferRepository;

    @Autowired
    private TransactionalComponent transactionalComponent;

    public Long initializeByBalanceAtomicOperationEvent(
            BalanceAtomicOperation event) throws
            InternalTransferAlreadyExistsException,
            InternalTransferInitializationException {

        if (!event.getOperationType().equals(InternalTransferService.BALANCE_MANAGER_OPERATION_TYPE)) {
            log.warn("stage=event-with-different-transaction-type");
            throw new IllegalArgumentException();
        }

        Collection<Operation> operations = (Collection<Operation>) event.getOperations();

        if (operations.size() != 2) {
            log.error("stage=need-to-have-two-operations, operationId={}", event.getOperationId());
            throw new IllegalArgumentException();
        }

        Operation debitOperation = operations.stream().filter(op -> op.getValue() < 0).findAny().orElse(null);
        Operation creditOperation = operations.stream().filter(op -> op.getValue() > 0).findAny().orElse(null);

        if (debitOperation == null) {
            log.error("stage=debit-operation-is-null, operationId={}", event.getOperationId());
            throw new IllegalArgumentException();
        } else if (creditOperation == null) {
            log.error("stage=credit-operation-is-null, operationId={}", event.getOperationId());
            throw new IllegalArgumentException();
        } else if (
                !Objects.equals((debitOperation.getValue() * -1), creditOperation.getValue())) {
            log.error("stage=debit-and-credit-operation-is-inconsistent, operationId={}, debitValue={}, creditValue={}", event.getOperationId(),
                    debitOperation.getValue(), creditOperation.getValue());
            throw new IllegalArgumentException();
        }

        if (this.internalTransferRepository.existsByOperationId(event.getOperationId())) {
            log.warn("stage=internal-transfer-already-exists, operationId={}", event.getOperationId());
            throw new InternalTransferAlreadyExistsException();
        }

        try {
            Long internalTransferId = this.transactionalComponent.require(() -> {
                InternalTransfer internalTransfer = new InternalTransfer();
                internalTransfer.setOperationId(event.getOperationId());

                internalTransfer.setCreationDate(
                        Instant.ofEpochMilli(event.getOperationDate()).atZone(ZoneId.of(this.dateZone)).toLocalDateTime());
                internalTransfer.setValue(new BigDecimal(creditOperation.getValue()));

                internalTransfer.setCreditorAccountId(creditOperation.getAccountId());
                internalTransfer.setCreditorAccountAgency(creditOperation.getAccountAgency());
                internalTransfer.setCreditorAccountNumber(creditOperation.getAccountNumber());

                internalTransfer.setDebtorAccountId(debitOperation.getAccountId());
                internalTransfer.setDebtorAccountNumber(debitOperation.getAccountNumber());
                internalTransfer.setDebtorAccountAgency(debitOperation.getAccountAgency());

                internalTransfer.setBacenSinc(false);
                internalTransfer.setLastBacenSincAttemptDate(null);

                return this.internalTransferRepository.save(internalTransfer).getId();
            });

            log.warn("stage=internal-transfer-initialized, operationId={}, internalTransferId={}", event.getOperationId(), internalTransferId);
            return internalTransferId;
        } catch (Exception ex) {

            log.warn("stage=internal-transfer-initialization-error, operationId={}", event.getOperationId(), ex);
            throw new InternalTransferInitializationException(ex);
        }
    }
}
