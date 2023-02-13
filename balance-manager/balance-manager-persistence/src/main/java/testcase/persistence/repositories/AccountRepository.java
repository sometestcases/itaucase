package testcase.persistence.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import testcase.persistence.entities.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByAccountId(String accountId);

    Optional<Account> findByAccountId(String accountId);

    List<Account> findByAccountIdIn(List<String> accountId);
}
