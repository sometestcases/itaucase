package testcase.api.jmx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;
import testcase.domain.service.testOperations.TestOperationsService;

@Component
@Profile("local | docker")
@ManagedResource(description = "JMX com operacoes de auxilio nos testes integrados")
public class TestJMX {

    @Autowired
    private TestOperationsService testOperationsService;

    @ManagedOperation(description = "Diminui a data de ultima atualizacao do saldo de uma conta, com a finalidade de permitir com facilidade o teste integrado do limite de debitos diario")
    public String decrementAccountLastUpdateBalanceDate(String accountId) {
        try {
            this.testOperationsService.decrementAccountLastUpdateBalanceDate(accountId);
            return "Success";
        } catch (Exception ex) {
            return "Operation Failed: " + ex;
        }
    }
}
