package testcase.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import testcase.persistence.entities.Operation;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {

    boolean existsByOperationId(String operationId);
}
