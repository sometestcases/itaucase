package testcase.domain.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import testcase.domain.eventPublisher.PublishService;
import testcase.domain.eventPublisher.mapping.BalanceUpdateEventMapping;
import testcase.domain.service.exception.AccountAlreadyInitializedException;
import testcase.domain.service.exception.AccountInitializeInternalException;
import testcase.domain.service.mapper.AccountStateMapper;
import testcase.domain.service.model.AccountState;
import testcase.persistence.entities.Account;
import testcase.persistence.repositories.AccountRepository;
import testcase.persistence.utils.TransactionalComponent;

@Slf4j
@Service
public class AccountInitializerService {

    @Value("${account-initialization.debts.daily-limit}")
    private BigDecimal dailyDebtsLimit;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionalComponent transactionalComponent;

    @Autowired
    private PublishService publishService;

    @Autowired
    private AccountStateMapper accountStateMapper;

    public void initializeAccount(String accountId, int number, int agency) throws AccountAlreadyInitializedException, AccountInitializeInternalException {

        if (this.accountRepository.existsByAccountId(accountId)) {
            log.warn("stage=account-already-initialized, accountId={}", accountId);
            throw new AccountAlreadyInitializedException();
        }

        try {

            LocalDateTime creationDate = LocalDateTime.now();

            AccountState accountState = this.transactionalComponent.require(() -> {
                Account account = new Account();
                account.setAccountId(accountId);
                account.setNumber(number);
                account.setAgency(agency);
                account.setLockNumber(0L);
                account.setLastBalanceUpdateDate(null);
                account.setLastBlockUpdateDate(null);
                account.setCreationDate(creationDate);
                account.setDailyDebtsAccumulator(BigDecimal.ZERO);
                account.setDailyDebtLimit(this.dailyDebtsLimit);
                account.setBlockedBalance(false);
                account.setBalance(BigDecimal.ZERO);
                this.accountRepository.save(account);

                return AccountState.builder()
                        .accountId(account.getAccountId())
                        .balance(account.getBalance())
                        .agency(account.getAgency())
                        .number(account.getNumber())
                        .blocked(account.getBlockedBalance())
                        .operationOrder(account.getLockNumber() - 1)
                        .build();
            });

            this.publishService.publish(this.accountStateMapper
                    .toBalanceUpdateEvent(accountState, creationDate));

            log.info("stage=account-initialized, accountId={}", accountId);

        } catch (Exception ex) {

            log.error("stage=fail-to-initialize-account, accountId={}", accountId, ex);
            throw new AccountInitializeInternalException(ex);
        }
    }
}
