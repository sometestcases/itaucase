package testcase.persistence.repositories.queryEntities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import testcase.persistence.entities.AccountStatus;

@Getter
@AllArgsConstructor
public class AccountAndStatus {

    private String accountId;
    private AccountStatus status;
}
