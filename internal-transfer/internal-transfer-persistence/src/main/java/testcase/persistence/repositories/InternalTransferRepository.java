package testcase.persistence.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import testcase.persistence.entities.InternalTransfer;

@Repository
public interface InternalTransferRepository extends JpaRepository<InternalTransfer, Long> {

    boolean existsByOperationId(String operationId);
}
