package testcase.domain.service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import testcase.domain.integrations.bacen.BacenClient;
import testcase.domain.integrations.bacen.request.InternalTransferNotificationRequest;
import testcase.domain.integrations.exception.IntegrationException;
import testcase.domain.service.exception.BacenSincException;
import testcase.domain.service.exception.DatabaseException;
import testcase.domain.service.exception.InternalTransferAlreadySincException;
import testcase.domain.service.exception.InternalTransferNotExistsException;
import testcase.persistence.entities.InternalTransfer;
import testcase.persistence.repositories.InternalTransferRepository;
import testcase.persistence.utils.TransactionalComponent;

@Slf4j
@Service
public class BacenService {

    @Autowired
    private InternalTransferRepository internalTransferRepository;

    @Autowired
    private BacenClient bacenClient;

    @Autowired
    @Qualifier("bacenSincRetryTemplate")
    private RetryTemplate retryTemplate;

    @Autowired
    private TransactionalComponent transactionalComponent;

    private Semaphore sincSemaphore;

    private SimpleAsyncTaskExecutor simpleAsyncTaskExecutor;

    public BacenService(
            @Value("${bacen-sinc.max-parallel}")
            int bacenSincMaxParallelSinc) {
        this.sincSemaphore = new Semaphore(bacenSincMaxParallelSinc);

        this.simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
        this.simpleAsyncTaskExecutor.setConcurrencyLimit(bacenSincMaxParallelSinc);
    }

    public void tryEnqueueSincInternalTransferById(Long id) {
        if (!this.sincSemaphore.tryAcquire()) {
            log.warn("stage=transfer-sinc-is-full, id={}", id);
            return;
        }
        CompletableFuture
                .runAsync(() -> {
                    try {
                        this.sincInternalTransactionByOperationId(id);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .thenApply(e -> {
                    this.sincSemaphore.release();
                    return null;
                });

    }

    public void sincInternalTransactionByOperationId(Long id)
            throws
            InternalTransferNotExistsException,
            InternalTransferAlreadySincException,
            DatabaseException,
            BacenSincException {

        InternalTransfer internalTransfer = this.internalTransferRepository.findById(id)
                .orElse(null);

        if (internalTransfer == null) {
            log.warn("stage=internal-transfer-not-exists-to-sinc, id={}", id);
            throw new InternalTransferNotExistsException();
        } else if (Boolean.TRUE.equals(internalTransfer.getBacenSinc())) {
            log.warn("stage=internal-transfer-already-sinc, id={}", id);
            throw new InternalTransferAlreadySincException();
        }

        InternalTransferNotificationRequest bacenRequest = new InternalTransferNotificationRequest();
        bacenRequest.setUniqueIdentifier(internalTransfer.getOperationId());

        bacenRequest.setValue(internalTransfer.getValue());

        bacenRequest.setCreditorAccountNumber(internalTransfer.getCreditorAccountNumber());
        bacenRequest.setCreditorAccountAgency(internalTransfer.getCreditorAccountAgency());

        bacenRequest.setDebtorAccountAgency(internalTransfer.getDebtorAccountAgency());
        bacenRequest.setDebtorAccountNumber(internalTransfer.getDebtorAccountNumber());

        try {
            this.retryTemplate.execute(ctx -> {
                try {
                    log.info("stage=try-bacen-sinc, operationId={}, attempt={}", internalTransfer.getOperationId(),
                            ctx.getRetryCount());
                    this.bacenClient.notifyTransfer(bacenRequest);
                    return true;
                } catch (IntegrationException e) {
                    log.warn("stage=bacen-sinc-fail, operationId={}, attempt={}",
                            internalTransfer.getOperationId(), ctx.getRetryCount());
                    throw new RuntimeException(e);
                }
            });

            log.info("stage=bacen-sinc-success, operationId={}", internalTransfer.getOperationId());
        } catch (Exception ex) {
            log.error("stage=bacen-sinc-fail, operationId={}", internalTransfer.getOperationId(), ex);
            this.updateInternalTransferSincLastAttemptDate(id);
            throw new BacenSincException(ex);
        }

        this.markCompleteInternalTransferSinc(id);
    }

    private void markCompleteInternalTransferSinc(Long id) throws DatabaseException {
        try {
            this.transactionalComponent.require(() -> {
                InternalTransfer internalTransfer = this.internalTransferRepository.findById(id)
                        .orElseThrow();
                internalTransfer.setBacenSinc(true);
                internalTransfer.setLastBacenSincAttemptDate(LocalDateTime.now());
            });
        } catch (Exception ex) {
            throw new DatabaseException(ex);
        }
    }

    private void updateInternalTransferSincLastAttemptDate(Long id) throws DatabaseException {
        try {
            this.transactionalComponent.require(() -> {
                InternalTransfer internalTransfer = this.internalTransferRepository.findById(id)
                        .orElseThrow();
                internalTransfer.setLastBacenSincAttemptDate(LocalDateTime.now());
            });
        } catch (Exception ex) {
            throw new DatabaseException(ex);
        }
    }
}
