package testcase.persistence.entities;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "ACCOUNT")
public class Account {

    @Id
    @SequenceGenerator(name = "accountPkGenerator", sequenceName = "SQ_ACCOUNT_IDT", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "accountPkGenerator")
    @Column(name = "IDT_ACCOUNT")
    private Long id;

    @Column(name = "COD_ACCOUNT")
    private String accountId;

    @Column(name = "COD_CUSTOMER")
    private String customerId;

    @Column(name = "DAT_CREATION")
    private LocalDateTime creationDate;

    @Column(name = "DAT_UPDATE")
    private LocalDateTime updateDate;

    @Column(name = "IND_STATUS")
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(name = "NUM_NUMBER")
    private int number;

    @JoinColumn(name = "IDT_AGENCY")
    @ManyToOne(fetch = FetchType.LAZY)
    private Agency agency;
}
