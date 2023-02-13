package testcase.domain.service;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import testcase.domain.service.model.AccountBalance;
import testcase.persistence.repositories.AccountRepository;

@Slf4j
@Service
public class BalanceSearchService {

    @Autowired
    private AccountRepository accountRepository;

    /*
     * Nao e necessario abrir transacao para essa consulta, visto que nenhum dado
     * que sera capturado dela tem relacionamento tipo LAZY
     */

    public Optional<AccountBalance> findByAccountId(String accountId) {
        return this.accountRepository.findByAccountId(accountId)
                .map(ac -> AccountBalance.builder()
                        .accountId(ac.getAccountId())
                        .balance(ac.getBalance())
                        .blocked(ac.getBlockedBalance())
                        .build());
    }
}
