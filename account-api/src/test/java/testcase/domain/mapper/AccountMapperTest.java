package testcase.domain.mapper;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import testcase.persistence.entities.Account;
import testcase.persistence.entities.AccountStatus;
import testcase.persistence.entities.Agency;

@ExtendWith(MockitoExtension.class)
public class AccountMapperTest {

    @Spy
    AccountMapper accountMapper;

    @BeforeEach
    public void beforeAll() throws NoSuchFieldException, IllegalAccessException {

        Field dateZone = AccountMapper.class.getDeclaredField("dateZone");
        dateZone.setAccessible(true);
        dateZone.set(this.accountMapper,"America/Sao_Paulo");
    }


    @Test
    public void mapFromEntityTest(){

        Account account = new Account();
        account.setStatus(AccountStatus.INACTIVE);
        account.setId(1L);
        account.setUpdateDate(LocalDateTime.now());
        account.setCustomerId("1324431243214");
        account.setAccountId("13412341234231");
        account.setCreationDate(LocalDateTime.now());
        account.setAgency(new Agency());
        account.getAgency().setNumber(2);
        account.setNumber(1);


        testcase.domain.model.Account modelAccount = this.accountMapper.map(account);

        Assertions.assertEquals(account.getAccountId(), modelAccount.getAccountId());
        Assertions.assertEquals(account.getId(), modelAccount.getId());
        Assertions.assertEquals(account.getNumber(), modelAccount.getNumber());
        Assertions.assertEquals(account.getCustomerId(), modelAccount.getCustomerId());
        Assertions.assertEquals(account.getCreationDate(), modelAccount.getCreationDate());
        Assertions.assertEquals(account.getUpdateDate(), modelAccount.getUpdateDate());
        Assertions.assertEquals(account.getAgency().getNumber(), modelAccount.getAgency());
    }

}
