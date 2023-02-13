package testcase.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import testcase.domain.model.AccountBalanceState;
import testcase.domain.service.exception.AccountBalanceBlockedException;
import testcase.domain.service.exception.AccountBalanceNotFoundException;
import testcase.domain.service.exception.AccountBalanceSearchException;
import testcase.persistence.entities.AccountBalance;
import testcase.persistence.repositories.AccountBalanceRepository;

@Slf4j
@Service
public class BalanceSearchService {

    @Autowired
    private AccountBalanceRepository accountBalanceRepository;


    public AccountBalanceState findByAccountId(
            String accountId,
            boolean requireUnblocked) throws AccountBalanceNotFoundException,
            AccountBalanceBlockedException, AccountBalanceSearchException {

        try {
            AccountBalance accountBalance = this.accountBalanceRepository.findByAccountId(accountId)
                    .orElseThrow(() -> new AccountBalanceNotFoundException());

            if (Boolean.TRUE.equals(accountBalance.getBlocked()) && requireUnblocked) {
                throw new AccountBalanceBlockedException();
            }

            return AccountBalanceState.builder()
                    .accountId(accountBalance.getAccountId())
                    .lastUpadate(accountBalance.getUpdateDate())
                    .blocked(accountBalance.getBlocked())
                    .operationOrder(accountBalance.getOperationOrder())
                    .balance(accountBalance.getBalance())
                    .build();
        } catch (AccountBalanceBlockedException ex) {
            throw ex;
        } catch (AccountBalanceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("stage=balance-search-error, accountId={}", accountId, ex);
            throw new AccountBalanceSearchException(ex);
        }
    }
}
