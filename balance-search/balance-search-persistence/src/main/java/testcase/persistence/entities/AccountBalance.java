package testcase.persistence.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "ACCOUNT_BALANCE")
public class AccountBalance {

    @Id
    @SequenceGenerator(name = "accountBalancePkGenerator", sequenceName = "SQ_ACCOUNT_BALANCE_IDT", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "accountBalancePkGenerator")
    @Column(name = "IDT_ACCOUNT_BALANCE")
    private Long id;

    @Column(name = "COD_ACCOUNT")
    private String accountId;

    @Column(name = "NUM_OPERATION_ORDER")
    private Long operationOrder;

    @Column(name = "NUM_BALANCE")
    private BigDecimal balance;

    @Column(name = "FLG_BLOCKED")
    private Boolean blocked;

    @Column(name = "DAT_UPDATE")
    private LocalDateTime updateDate;
}
