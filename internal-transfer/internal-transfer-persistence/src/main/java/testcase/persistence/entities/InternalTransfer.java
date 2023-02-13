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
@Table(name = "INTERNAL_TRANSFER")
public class InternalTransfer {

    @Id
    @SequenceGenerator(name = "internalTransferPkGenerator", sequenceName = "SQ_INTERNAL_TRANSFER_IDT", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "internalTransferPkGenerator")
    @Column(name = "IDT_INTERNAL_TRANSFER")
    private Long id;

    @Column(name = "COD_ACCOUNT_CREDITOR")
    private String creditorAccountId;

    @Column(name = "NUM_CREDITOR_ACCOUNT_AGENCY")
    private int creditorAccountAgency;

    @Column(name = "NUM_CREDITOR_ACCOUNT_NUMBER")
    private int creditorAccountNumber;

    @Column(name = "COD_ACCOUNT_DEBTOR")
    private String debtorAccountId;

    @Column(name = "NUM_DEBTOR_ACCOUNT_AGENCY")
    private int debtorAccountAgency;

    @Column(name = "NUM_DEBTOR_ACCOUNT_NUMBER")
    private int debtorAccountNumber;

    @Column(name = "DAT_CREATION")
    private LocalDateTime creationDate;

    @Column(name = "NUM_VALUE")
    private BigDecimal value;

    @Column(name = "COD_OPERATION")
    private String operationId;

    @Column(name = "FLG_BACEN_SINC")
    private Boolean bacenSinc;

    @Column(name = "DAT_LAST_BACEN_SINC_ATTEMPT")
    private LocalDateTime lastBacenSincAttemptDate;
}
