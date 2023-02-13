package testcase.domain.service.testOperations;


import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import testcase.persistence.entities.Account;
import testcase.persistence.repositories.AccountRepository;
import testcase.persistence.utils.TransactionalComponent;

@Profile("local | docker")
@Service
public class TestOperationsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionalComponent transactionalComponent;

    public void decrementAccountLastUpdateBalanceDate(String accountId) {
        this.transactionalComponent.require(() -> {
            Account account = this.accountRepository.findByAccountId(accountId)
                    .orElseThrow();
            if (account.getLastBalanceUpdateDate() != null) {
                account.setLastBalanceUpdateDate(account.getLastBalanceUpdateDate().minus(1,
                        ChronoUnit.DAYS));
            }
        });
    }
}
