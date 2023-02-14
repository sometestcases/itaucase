package testcase.persistence.entities;

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
@Table(name = "OPERATION")
public class Operation {

    @Id
    @SequenceGenerator(name = "operationLockPkGenerator", sequenceName = "SQ_OPERATION_IDT", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "operationLockPkGenerator")
    @Column(name = "IDT_OPERATION")
    private Long id;

    @Column(name = "COD_OPERATION")
    private String operationId;
}
