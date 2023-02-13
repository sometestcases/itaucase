package testcase.persistence.entities;

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
@Table(name = "AGENCY")
public class Agency {

    @Id
    @SequenceGenerator(name = "agencyPkGenerator", sequenceName = "SQ_AGENCY_IDT", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "agencyPkGenerator")
    @Column(name = "IDT_AGENCY")
    private Long id;

    @Column(name = "NUM_NUMBER")
    private int number;

    @Column(name = "NUM_NEXT_ACCOUNT")
    private int nextAccount;

    @Column(name = "DAT_CREATION")
    private LocalDateTime creationDate;
}
