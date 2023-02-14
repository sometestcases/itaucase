package testcase.domain.service;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import testcase.domain.eventPublisher.PublishService;
import testcase.domain.service.exception.AccountAlreadyInitializedException;
import testcase.domain.service.exception.AccountInitializeInternalException;
import testcase.domain.service.mapper.AccountStateMapper;
import testcase.persistence.entities.Account;
import testcase.persistence.repositories.AccountRepository;
import testcase.persistence.utils.TransactionalComponent;

@ExtendWith(MockitoExtension.class)
public class AccountInitializerServiceTest {

    @Spy
    TransactionalComponent transactionalComponent;

    @Mock
    AccountRepository accountRepository;

    @Mock
    PublishService publishService;

    @Mock
    AccountStateMapper accountStateMapper;

    @Spy
    @InjectMocks
    AccountInitializerService accountInitializerService;

    @BeforeEach
    public void beforeAll() throws NoSuchFieldException, IllegalAccessException {

        Field dailyDebtsLimit = AccountInitializerService.class.getDeclaredField("dailyDebtsLimit");
        dailyDebtsLimit.setAccessible(true);
        dailyDebtsLimit.set(this.accountInitializerService, BigDecimal.TEN);
    }

    @Test
    public void initializeAccountTest() throws AccountAlreadyInitializedException, AccountInitializeInternalException {

        String accountId="123421342134312";

        Mockito.when(this.accountRepository.existsByAccountId(Mockito.eq(accountId)))
                .thenReturn(false);

        this.accountInitializerService.initializeAccount(accountId, 1, 2);

        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);

        Mockito.verify(this.accountRepository, Mockito.times(1))
                .save(accountArgumentCaptor.capture());

        Account account = accountArgumentCaptor.getValue();

        Assertions.assertEquals(accountId, account.getAccountId());
        Assertions.assertEquals(1, account.getNumber());
        Assertions.assertEquals(2, account.getAgency());
        Assertions.assertEquals(0, account.getLockNumber());
    }
}
