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
@Table(name = "ACCOUNT")
public class Account {

    @Id
    @SequenceGenerator(name = "accountPkGenerator", sequenceName = "SQ_ACCOUNT_IDT", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "accountPkGenerator")
    @Column(name = "IDT_ACCOUNT")
    private Long id;

    @Column(name = "COD_ACCOUNT")
    private String accountId;

    @Column(name = "NUM_NUMBER")
    private int number;

    @Column(name = "NUM_AGENCY")
    private int agency;

    @Column(name = "NUM_LOCK")
    private Long lockNumber;

    @Column(name = "NUM_BALANCE")
    private BigDecimal balance;

    @Column(name = "DAT_LAST_BALANCE_UPDATE")
    private LocalDateTime lastBalanceUpdateDate;

    @Column(name = "NUM_DAILY_DEBT_LIMIT")
    private BigDecimal dailyDebtLimit;

    @Column(name = "NUM_DAILY_DEBTS_ACCUMULATOR")
    private BigDecimal dailyDebtsAccumulator;

    /*
     * Essa nao seria a data de criacao na account-api, e sim a data da criacao da conta no balance-manager
     * (data do consumo do evento no caso)
     */
    @Column(name = "DAT_CREATION")
    private LocalDateTime creationDate;

    @Column(name = "FLG_BLOCKED_BALANCE")
    private Boolean blockedBalance;

    @Column(name = "DAT_LAST_BLOCK_UPDATE")
    private LocalDateTime lastBlockUpdateDate;
}
