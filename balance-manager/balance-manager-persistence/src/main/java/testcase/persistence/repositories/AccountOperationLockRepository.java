package testcase.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import testcase.persistence.entities.AccountOperationLock;

@Repository
public interface AccountOperationLockRepository extends JpaRepository<AccountOperationLock, Long> {

}
