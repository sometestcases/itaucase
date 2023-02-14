package testcase.domain.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import testcase.domain.service.exception.BalanceTransactionalOperationError;
import testcase.domain.service.exception.BalanceAtomicOperationException;
import testcase.domain.service.model.AccountState;
import testcase.domain.service.model.BalanceOperation;
import testcase.domain.service.model.SingleBalanceOperation;
import testcase.domain.service.preAtomicOperationTransactionalActions.PreOperationTransactionalAction;
import testcase.persistence.entities.Account;
import testcase.persistence.entities.AccountOperationLock;
import testcase.persistence.entities.Operation;
import testcase.persistence.repositories.AccountOperationLockRepository;
import testcase.persistence.repositories.AccountRepository;
import testcase.persistence.repositories.OperationRepository;

@Slf4j
@Component
public class BalanceAtomicOperationComponent {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private AccountOperationLockRepository accountOperationLockRepository;

    private ImmutableList<PreOperationTransactionalAction> orderedTransactionalActions;

    @Autowired
    protected BalanceAtomicOperationComponent(List<PreOperationTransactionalAction> orderedTransactionalActions) {
        this.orderedTransactionalActions = ImmutableList.copyOf(orderedTransactionalActions);
    }


    /*
     * Nesse caso o ideal é ser REQUIRES_NEW, porque é de estrita importancia que a transacao seja finalizada
     * ao fim do metodo, caso esteja na configuracao default de propagation, pode acabar aproveitando uma transacao
     * indevidamente e ao fim do metodo nao terminar a operacao
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ImmutableSet<AccountState> executeAtomicBalanceOperation(BalanceOperation balanceOperation,
                                                                    LocalDateTime executionDate)
            throws BalanceAtomicOperationException {

        Map<String, Account> accounts = this.accountRepository.findByAccountIdIn(balanceOperation.getSingleBalanceOperations()
                        .stream().map(SingleBalanceOperation::getAccountId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Account::getAccountId, a -> a, (a1, a2) -> a1));

        if (balanceOperation.getSingleBalanceOperations().stream().map(SingleBalanceOperation::getAccountId)
                .anyMatch(accountId -> !accounts.containsKey(accountId))) {
            log.info("stage=account-not-found, operationId={}", balanceOperation.getOperationId());
            throw new BalanceAtomicOperationException(BalanceTransactionalOperationError.INEXISTENT_ACCOUNT);
        }

        for (PreOperationTransactionalAction preOperationTransactionalAction : this.orderedTransactionalActions) {
            preOperationTransactionalAction.execute(accounts, balanceOperation.getSingleBalanceOperations(), executionDate);
        }

        Operation operation = new Operation();
        operation.setOperationId(balanceOperation.getOperationId());
        operation = this.operationRepository.save(operation);

        for (SingleBalanceOperation singleBalanceOperation : balanceOperation.getSingleBalanceOperations()) {

            Account account = accounts.get(singleBalanceOperation.getAccountId());

            account.setBalance(account.getBalance().add(singleBalanceOperation.getValue()));
            account.setLastBalanceUpdateDate(executionDate);

            Long lockNumber = account.getLockNumber();

            AccountOperationLock accountOperationLock = new AccountOperationLock();
            accountOperationLock.setAccount(account);
            accountOperationLock.setOperation(operation);
            accountOperationLock.setLockNumber(lockNumber);
            this.accountOperationLockRepository.save(accountOperationLock);

            account.setLockNumber(lockNumber + 1);
        }

        ImmutableSet.Builder<AccountState> resultBuilder = ImmutableSet.builder();

        for (Account account : accounts.values()) {
            resultBuilder.add(AccountState.builder()
                    .accountId(account.getAccountId())
                    .number(account.getNumber())
                    .agency(account.getAgency())
                    .balance(account.getBalance())
                    .operationOrder(account.getLockNumber() - 1)
                    .blocked(account.getBlockedBalance())
                    .build());
        }

        return resultBuilder.build();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public AccountState executeAtomicBalanceBlock(String accountId,
                                                  LocalDateTime executionDate) throws BalanceAtomicOperationException {

        Account account = this.accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> {
                    log.warn("stage=inexistent-account, accountId={}", accountId);
                    return new BalanceAtomicOperationException(BalanceTransactionalOperationError.INEXISTENT_ACCOUNT);
                });

        if (Boolean.TRUE.equals(account.getBlockedBalance())) {
            log.warn("stage=account-already-blocked, accountId={}", accountId);
            throw new BalanceAtomicOperationException(BalanceTransactionalOperationError.ALREADY_BLOCKED_ACCOUNT);
        }

        Operation operation = new Operation();
        operation.setOperationId(UUID.randomUUID().toString().replace("-", "").toUpperCase());
        operation = this.operationRepository.save(operation);

        AccountOperationLock lock = new AccountOperationLock();
        lock.setAccount(account);
        lock.setOperation(operation);
        lock.setLockNumber(account.getLockNumber());
        this.accountOperationLockRepository.save(lock);

        account.setBlockedBalance(true);
        account.setLastBlockUpdateDate(executionDate);
        account.setLockNumber(account.getLockNumber() + 1);

        return AccountState.builder()
                .accountId(account.getAccountId())
                .number(account.getNumber())
                .agency(account.getAgency())
                .balance(account.getBalance())
                .operationOrder(account.getLockNumber() - 1)
                .blocked(account.getBlockedBalance())
                .build();
    }
}