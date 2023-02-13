package testcase.domain.service.preAtomicOperationTransactionalActions;

import java.time.LocalDateTime;
import java.util.Map;

import com.google.common.collect.ImmutableSet;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import testcase.domain.service.exception.BalanceAtomicOperationException;
import testcase.domain.service.model.SingleBalanceOperation;
import testcase.persistence.entities.Account;

public interface PreOperationTransactionalAction {

    @Transactional(propagation = Propagation.MANDATORY)
    void execute(Map<String, Account> accountMap,
                 ImmutableSet<SingleBalanceOperation> singleBalanceOperations,
                 LocalDateTime executionDate) throws BalanceAtomicOperationException;
}
