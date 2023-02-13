package testcase.domain.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import testcase.domain.mapper.AccountMapper;
import testcase.domain.model.Account;
import testcase.domain.service.exception.AlreadyExistAccountException;
import testcase.domain.service.exception.AlreadyInactiveAccountException;
import testcase.domain.service.exception.CantBlockBalanceException;
import testcase.domain.service.exception.InactiveCustomerException;
import testcase.domain.service.exception.InexistentAccountException;
import testcase.domain.service.exception.InexistentCustomerException;
import testcase.domain.service.exception.InternalAccountCreationException;
import testcase.domain.service.exception.InternalAccountInactivateException;
import testcase.eventPublisher.PublishService;
import testcase.integrations.balanceManager.BalanceManagerClient;
import testcase.integrations.balanceManager.exception.FailedBlockException;
import testcase.integrations.customerService.CustomerServiceClient;
import testcase.integrations.customerService.exception.CustomerNotFoundException;
import testcase.integrations.exception.IntegrationException;
import testcase.persistence.entities.AccountStatus;
import testcase.persistence.entities.Agency;
import testcase.persistence.repositories.AccountRepository;
import testcase.persistence.repositories.AgencyRepository;
import testcase.persistence.repositories.queryEntities.AccountAndStatus;
import testcase.persistence.utils.TransactionalComponent;

@Slf4j
@Service
public class AccountService {

    @Autowired
    private TransactionalComponent transactionalComponent;

    @Autowired
    private CustomerServiceClient customerServiceClient;

    @Autowired
    private BalanceManagerClient balanceManagerClient;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private PublishService publishService;

    @Autowired
    private AccountMapper accountMapper;

    public void inactivateAccountByCustomerId(String customerId) throws AlreadyInactiveAccountException, InexistentAccountException,
            CantBlockBalanceException, InternalAccountInactivateException {
        log.info("stage=init-account-deactivation, customerId={}", customerId);

        Optional<AccountAndStatus> accountAndStatus = this.accountRepository
                .findAccountAndStatusByCustomerId(customerId);

        if (accountAndStatus.isEmpty()) {
            log.warn("stage=account-not-found-to-block, customerId={}", customerId);
            throw new InexistentAccountException();
        } else if (accountAndStatus.get().getStatus() == AccountStatus.INACTIVE) {
            log.warn("stage=already-inactive-account, customerId={}", customerId);
            throw new AlreadyInactiveAccountException();
        }

        String accountId = accountAndStatus.get().getAccountId();

        try {
            if (!this.balanceManagerClient.blockBalance(accountId)) {
                log.info("stage=cant-block-balance-exception, accountId={}", accountId);
                throw new CantBlockBalanceException();
            }
        } catch (FailedBlockException e) {
            log.error("stage=failed-to-block-balance, accountId={}", accountId);
            throw new InternalAccountInactivateException(e);
        } catch (IntegrationException | HystrixRuntimeException e) {
            log.error("stage=integration-error-to-block-balance, accountId={}", accountId);
            throw new InternalAccountInactivateException(e);
        }

        try {
            this.inactivateAccountOnDatabase(accountId);
        } catch (Exception ex) {
            log.error("stage=transaction-error-to-inactivate-account, accountId={}", accountId);
            throw new InternalAccountInactivateException(ex);
        }

        log.info("stage=account-succeffully-inactivated, accountId={}", accountId);
    }

    public Account create(String customerId, int agency) throws InternalAccountCreationException, AlreadyExistAccountException,
            InexistentCustomerException, InactiveCustomerException {
        log.info("stage=init-account-creation, customerId={}, agency={}", customerId, agency);

        try {
            if (!this.customerServiceClient.isActiveCustomer(customerId)) {
                log.info("stage=customer-inative, customerId={}", customerId);
                throw new InactiveCustomerException();
            }
        } catch (CustomerNotFoundException e) {
            log.warn("stage=customer-not-found, customerId={}", customerId);
            throw new InexistentCustomerException();
        } catch (IntegrationException | HystrixRuntimeException ex) {
            log.error("stage=account-creation-error, customerId={}", customerId, ex);
            throw new InternalAccountCreationException(ex);
        }

        if (this.accountRepository.existsByCustomerId(customerId)) {
            log.info("stage=already-exist-account-to-same-customer, customerId={}", customerId);
            throw new AlreadyExistAccountException();
        }

        final Account createdAccount;

        try {
            createdAccount = this.createAccountOnDatabase(customerId, agency);
        } catch (Exception ex) {
            log.error("stage=transaction-error-to-create-account, customerId={}, agency={}", customerId, agency);
            throw new InternalAccountCreationException(ex);
        }

        this.publishService.publish(this.accountMapper.mapToAccountCreatedEvent(createdAccount));

        log.info("stage=account-success-created, customerId={}, accountId={}, number={}, agency={}",
                createdAccount.getCustomerId(), createdAccount.getAccountId(),
                createdAccount.getNumber(), createdAccount.getAgency());
        return createdAccount;
    }

    private Account createAccountOnDatabase(final String customerId, final int agencyNumber) {
        return this.transactionalComponent.require(() -> {

            Agency agency = this.agencyRepository.findByNumber(agencyNumber)
                    .orElseGet(() -> {
                        Agency newAgency = new Agency();
                        newAgency.setNextAccount(1);
                        newAgency.setNumber(agencyNumber);
                        newAgency.setCreationDate(LocalDateTime.now());
                        return this.agencyRepository.save(newAgency);
                    });

            testcase.persistence.entities.Account newAccount = new testcase.persistence.entities.Account();
            newAccount.setAgency(agency);
            newAccount.setCustomerId(customerId);
            newAccount.setStatus(testcase.persistence.entities.AccountStatus.ACTIVE);
            newAccount.setCreationDate(LocalDateTime.now());
            newAccount.setNumber(agency.getNextAccount());
            newAccount.setAccountId(UUID.randomUUID().toString().replace("-", "").toUpperCase());

            agency.setNextAccount(agency.getNextAccount() + 1);

            return this.accountMapper.map(
                    this.accountRepository.save(newAccount));
        });
    }

    private void inactivateAccountOnDatabase(final String accountId) {
        this.transactionalComponent.require(() -> {

            testcase.persistence.entities.Account account = this.accountRepository.findByAccountId(accountId)
                    .orElseThrow();

            account.setStatus(AccountStatus.INACTIVE);
            account.setUpdateDate(LocalDateTime.now());
        });
    }
}
