package testcase.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "ACCOUNT_OPERATION_LOCK")
public class AccountOperationLock {

    @Id
    @SequenceGenerator(name = "accountOperationLockPkGenerator", sequenceName = "SQ_ACCOUNT_OPERATION_LOCK_IDT", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "accountOperationLockPkGenerator")
    @Column(name = "IDT_ACCOUNT_OPERATION_LOCK")
    private Long id;

    @JoinColumn(name = "IDT_ACCOUNT")
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @Column(name = "NUM_LOCK")
    private Long lockNumber;

    @Column(name = "COD_OPERATION")
    private String operationId;
}
