package testcase.domain.service;

import java.math.BigDecimal;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import testcase.domain.integrations.balanceManager.BalanceManagerClient;
import testcase.domain.integrations.balanceManager.exception.BalanceManagerOperationException;
import testcase.domain.integrations.balanceManager.request.BalanceOperationRequest;
import testcase.domain.integrations.balanceManager.request.SingleOperationRequest;
import testcase.domain.integrations.exception.IntegrationException;
import testcase.domain.service.exception.InternalTransferServerException;

@Slf4j
@Service
public class InternalTransferService {

    public static final String BALANCE_MANAGER_OPERATION_TYPE = "INTERNAL_TRANSFER";

    @Autowired
    private BalanceManagerClient balanceManagerClient;

    public void internalTransfer(
            String operationId,
            String creditorAccountId,
            String debtorAccountId,
            BigDecimal value) throws BalanceManagerOperationException, InternalTransferServerException {

        if(value.signum() <= 0){
            throw new IllegalArgumentException("Value need to be positive");
        }

        log.info("stage=init-internal-transfer, operationId={}", operationId);

        BalanceOperationRequest balanceOperationRequest = new BalanceOperationRequest();
        balanceOperationRequest.setOperationId(operationId);
        balanceOperationRequest.setOperationType(BALANCE_MANAGER_OPERATION_TYPE);

        SingleOperationRequest credit = new SingleOperationRequest();
        credit.setAccountId(creditorAccountId);
        credit.setValue(value);

        SingleOperationRequest debit = new SingleOperationRequest();
        debit.setAccountId(debtorAccountId);
        debit.setValue(value.negate());

        balanceOperationRequest.setOperations(Arrays.asList(credit, debit));

        try {
            this.balanceManagerClient.atomicOperation(balanceOperationRequest);
            log.info("stage=internal-transfer-success, operationId={}", operationId);
        } catch (IntegrationException e) {
            log.info("stage=internal-transfer-error, operationId={}", operationId, e);
            throw new InternalTransferServerException(e);
        }
    }


}
