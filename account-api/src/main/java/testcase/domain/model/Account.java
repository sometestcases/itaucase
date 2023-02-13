package testcase.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Account {
    private Long id;
    private String accountId;
    private String customerId;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    private AccountStatus status;
    private int number;
    private int agency;

    @Builder
    public Account(Long id, String accountId, String customerId, LocalDateTime creationDate, LocalDateTime updateDate, AccountStatus status,
                   int number, int agency){
        this.id = Objects.requireNonNull(id, "id cant be null");
        this.accountId = Objects.requireNonNull(accountId, "account id cant be null");
        this.customerId = Objects.requireNonNull(customerId, "customer id cant be null");
        this.creationDate = Objects.requireNonNull(creationDate, "creation date cant be null");
        this.updateDate = updateDate;
        this.status = Objects.requireNonNull(status, "status cant be null");
        this.number = number;
        this.agency = agency;
    }
}
