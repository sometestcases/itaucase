package testcase.domain.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import testcase.domain.customMocks.TestFakeAccountMapper;
import testcase.domain.mapper.AccountMapper;
import testcase.domain.service.exception.AlreadyExistAccountException;
import testcase.domain.service.exception.InactiveCustomerException;
import testcase.domain.service.exception.InexistentCustomerException;
import testcase.domain.service.exception.InternalAccountCreationException;
import testcase.eventPublisher.PublishService;
import testcase.integrations.balanceManager.BalanceManagerClient;
import testcase.integrations.customerService.CustomerServiceClient;
import testcase.integrations.customerService.exception.CustomerNotFoundException;
import testcase.integrations.exception.IntegrationException;
import testcase.persistence.entities.Account;
import testcase.persistence.entities.AccountStatus;
import testcase.persistence.entities.Agency;
import testcase.persistence.repositories.AccountRepository;
import testcase.persistence.repositories.AgencyRepository;
import testcase.persistence.utils.TransactionalComponent;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Spy
    private TransactionalComponent transactionalComponent;

    @Mock
    private CustomerServiceClient customerServiceClient;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AgencyRepository agencyRepository;

    @Mock
    private PublishService publishService;

    @Spy
    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    public void beforeAll() throws NoSuchFieldException, IllegalAccessException {

        Field accountMapper = AccountService.class.getDeclaredField("accountMapper");
        accountMapper.setAccessible(true);
        accountMapper.set(this.accountService, new TestFakeAccountMapper());
    }


    @Test
    public void createSuccessAccountTest() throws
            IntegrationException, CustomerNotFoundException,
            InexistentCustomerException, InactiveCustomerException,
            AlreadyExistAccountException, InternalAccountCreationException {

        String customerId = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
        int agency = 1;

        Mockito.doReturn(true)
                .when(this.customerServiceClient)
                .isActiveCustomer(Mockito.eq(customerId));

        Mockito.doReturn(false)
                .when(this.accountRepository)
                .existsByCustomerId(Mockito.eq(customerId));

        Mockito.doReturn(Optional.empty())
                .when(this.agencyRepository)
                .findByNumber(Mockito.eq(agency));

        Mockito.when(this.agencyRepository.save(Mockito.any(Agency.class)))
                .then(AdditionalAnswers.returnsFirstArg());

        Mockito.when(this.accountRepository.save(Mockito.any(Account.class)))
                .then(AdditionalAnswers.returnsFirstArg());

        this.accountService.create(customerId, agency);

        Mockito.verify(this.publishService, Mockito.times(1))
                .publish(Mockito.any());

        ArgumentCaptor<Account> savedAccountCaptor = ArgumentCaptor.forClass(Account.class);

        Mockito.verify(this.accountRepository, Mockito.times(1))
                .save(savedAccountCaptor.capture());

        Account account = savedAccountCaptor.getValue();

        Assertions.assertEquals(account.getNumber(), 1);
        Assertions.assertEquals(account.getAgency().getNumber(), agency);
        Assertions.assertEquals(account.getCustomerId(), customerId);
        Assertions.assertEquals(account.getStatus(), AccountStatus.ACTIVE);
    }
}
