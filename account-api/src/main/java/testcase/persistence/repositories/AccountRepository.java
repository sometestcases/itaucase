package testcase.persistence.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import testcase.persistence.entities.Account;
import testcase.persistence.repositories.queryEntities.AccountAndStatus;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT new testcase.persistence.repositories.queryEntities.AccountAndStatus(ac.accountId,ac.status) " +
            "FROM Account ac WHERE ac.customerId=:customerId")
    Optional<AccountAndStatus> findAccountAndStatusByCustomerId(@Param("customerId") String customerId);

    Optional<Account> findByAccountId(String accountId);

    boolean existsByCustomerId(String customerId);
}
