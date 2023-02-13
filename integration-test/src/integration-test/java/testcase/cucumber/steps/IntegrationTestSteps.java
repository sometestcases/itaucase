package testcase.cucumber.steps;

import static io.restassured.RestAssured.given;

import java.math.BigDecimal;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import testcase.TestConfig;
import testcase.cucumber.steps.data.TestDataComponent;
import testcase.cucumber.steps.request.BalanceOperationRequest;
import testcase.cucumber.steps.request.CreateAccountRequest;
import testcase.cucumber.steps.request.InternalTransferRequest;
import testcase.cucumber.steps.request.SingleOperationRequest;
import testcase.cucumber.steps.utils.UuidGenerator;

@CucumberContextConfiguration
@SpringBootTest(classes = TestConfig.class)

/*
 * Essa configuracao abaixo só faz sentido se os profiles forem docker-test ou local, caso for outro perfil
 * pode ser por exemplo que o serviço de cliente nao funcione mais com o mock server, por isso defini os perfis abaixo
 */
public class IntegrationTestSteps extends AbstractDefsConfig {

    private static final String INTERNAL_TRANSFER_API_TRANSFER = "/internal-transfers/{accountIdFrom}/to/{accountIdTo}";

    private static final String BALANCE_SEARCH_BALANCES = "/balances/{accountId}";

    private static final String ACCOUNT_API_CREATE_ACCOUNT = "/accounts";

    private static final String ACCOUNT_API_DELETE_ACCOUNT_BY_CUSTOMER_ID = "/accounts/customer/{customerId}";

    private static final String BALANCE_MANAGER_API_OPERATION = "/atomicBalanceOperation";

    private static final String BALANCE_MANAGER_REDUCE_OPERATION_DAY = "/actuator/hawtio/jolokia/exec/testcase.api.jmx:name=testJMX,type=TestJMX/decrementAccountLastUpdateBalanceDate/{accountId}";

    @Value("${account-api.url}")
    private String accountAPIUrl;

    @Value("${internal-transfer-api.url}")
    private String internalTransferAPIUrl;

    @Value("${balance-manager-api.url}")
    private String balanceManagerAPIUrl;

    @Value("${balance-search-api.url}")
    private String balanceSearchAPIUrl;

    @Autowired
    private TestDataComponent testDataComponent;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Quando("efetuar a criacao de conta bancaria para um cliente {word}")
    public void efetuarCriacaoDeContaBancariaParaClienteExistenteAtivo(String testID) throws Throwable {

        String customerId = UuidGenerator.generate("A");
        this.tryCreateAccount(customerId, testID);
    }

    @Quando("efetuar a criacao de uma conta bancaria apelidada de {word} para um cliente {word}")
    public void efetuarCriacaoDeUmaDeterminadaContaBancariaParaUmCliente(String accountAlias, String testID) throws Throwable {

        String customerId = UuidGenerator.generate("B");
        this.tryCreateAccount(customerId, testID + " " + accountAlias);
    }

    @E("a consulta de saldo da conta bancaria apelidada de {word} deve retornar {double} {word}")
    public void aConsultaDeSaldoDaContaBancariaApelidadeDeveRetornar(String accountAlias, double saldoEsperado, String testID) throws Throwable {
        Thread.sleep(1000);

        String accountId = (String) this.testDataComponent.get(testID + " " + accountAlias, TestDataComponent.ContextVariable.ACCOUNT);

        Response response = this.tryGetAccountBalance(accountId, testID);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(saldoEsperado, Double.parseDouble(response.jsonPath().get("balance").toString()));
    }

    @Quando("consultar o saldo da conta bancaria apelidade de {word} {word}")
    public void aConsultaDeSaldoDaContaDoClienteBloqueadoNaoDeveRetornarNada(String accountAlias, String testID) throws Throwable {
        Thread.sleep(1000);

        String accountId = (String) this.testDataComponent.get(testID + " " + accountAlias, TestDataComponent.ContextVariable.ACCOUNT);

        this.tryGetAccountBalance(accountId, testID);
    }

    @Quando("efetuar a adicao de saldo {double} para conta bancaria apelidada de {word} {word}")
    public void efetuarCriacaoDeUmaDeterminadaContaBancariaParaUmCliente(double saldo, String accountAlias, String testID) throws Throwable {
        Thread.sleep(1000L);

        String accountId = (String) this.testDataComponent.get(testID + " " + accountAlias, TestDataComponent.ContextVariable.ACCOUNT);
        this.addAccountBalance(accountId, saldo);
    }

    @Quando("efetuar a transferencia de {double} reais da conta {word} para a {word} {word}")
    public void efetuarATransferenciaDaContaParaOutra(double saldo, String accountFromAlias, String accountToAlias, String testID) throws Throwable {
        Thread.sleep(1000L);

        String accountIdFrom = (String) this.testDataComponent.get(testID + " " + accountFromAlias, TestDataComponent.ContextVariable.ACCOUNT);
        String accountIdTo = (String) this.testDataComponent.get(testID + " " + accountToAlias, TestDataComponent.ContextVariable.ACCOUNT);


        this.tryInternalTransfer(accountIdFrom, accountIdTo, saldo, testID);
    }

    @Quando("efetuar a criacao de conta bancaria para um cliente inexistente {word}")
    public void efetuarCriacaoDeContaBancariaParaClienteInexistente(String testID) throws Throwable {

        String customerId = UuidGenerator.generate("0");
        this.tryCreateAccount(customerId, testID);
    }

    @Entao("suponha que a ultima transacao realizada pela conta {word} tenha ocorrido um dia atraz {word}")
    public void suponhaQueAUltimaTransacaoRealizadaPelaContaTenhaOcorridoUmDiaAtras(String accountAlias,String testID) throws Throwable {

        String accountId = (String) this.testDataComponent.get(testID + " " + accountAlias, TestDataComponent.ContextVariable.ACCOUNT);

        this.reduceOperationDay(accountId, testID);
    }

    @Quando("efetuar a inativacao do cliente da conta bancaria {word} {word}")
    public void efetuarInativacaoDoClienteDaContaBancaria(String accountAlias,String testID) throws Throwable {

        String customerId = (String) this.testDataComponent.get(testID + " " + accountAlias, TestDataComponent.ContextVariable.CUSTOMER);
        this.inactivateAccountByCustomerId(customerId, testID);
    }


    @Quando("efetuar a criacao de conta bancaria para um cliente existente inativo {word}")
    public void efetuarCriacaoDeContaBancariaParaClienteExistenteInativo(String testID) throws Throwable {

        String customerId = UuidGenerator.generate("1");
        this.tryCreateAccount(customerId, testID);
    }

    @E("uma conta deve ter sido criada {word}")
    public void umaContaDeveTerSidoCriada(String testID) throws Throwable {

        Assertions.assertTrue(this.testDataComponent.exists(testID, TestDataComponent.ContextVariable.ACCOUNT));
    }

    @E("uma conta nao deve ter sido criada {word}")
    public void umaContaNaoDeveTerSidoCriada(String testID) throws Throwable {

        Assertions.assertFalse(this.testDataComponent.exists(testID, TestDataComponent.ContextVariable.ACCOUNT));
    }

    @E("o codigo de resposta deve ser {int} {word}")
    public void oCodigoDeRespostaDeveSer(int statusCode, String testID) throws Throwable {

        Response response = (Response) this.testDataComponent.get(testID, TestDataComponent.ContextVariable.RESPONSE);
        Assertions.assertEquals(statusCode, response.statusCode());
    }

    private void tryCreateAccount(String customerId, String contextID) throws JsonProcessingException {

        CreateAccountRequest createAccountRequest = new CreateAccountRequest();
        createAccountRequest.setCustomerId(customerId);
        createAccountRequest.setAgency(((int) (Math.random() * 98))+1);

        Response response = given()
                .baseUri(this.accountAPIUrl)
                .contentType("application/json")
                .body(this.objectMapper.writeValueAsString(createAccountRequest))
                .post(ACCOUNT_API_CREATE_ACCOUNT);

        this.testDataComponent.put(contextID, TestDataComponent.ContextVariable.CUSTOMER, customerId);
        this.testDataComponent.put(contextID, TestDataComponent.ContextVariable.RESPONSE, response);
        this.testDataComponent.remove(contextID, TestDataComponent.ContextVariable.ACCOUNT);

        try {
            String accountId = response.header("Location");
            accountId = accountId.split("/")[accountId.split("/").length - 1];

            this.testDataComponent.put(contextID, TestDataComponent.ContextVariable.ACCOUNT, accountId);
        } catch (Exception ex) {

        }
    }

    private void tryInternalTransfer(String accountIdFrom, String accountIdTo, double balance, String contextID) throws JsonProcessingException {

        if (balance <= 0) {
            throw new IllegalArgumentException();
        }

        InternalTransferRequest internalTransferRequest = new InternalTransferRequest();
        internalTransferRequest.setOperationId(UuidGenerator.generate());
        internalTransferRequest.setValue(new BigDecimal(balance));

        Response response = given()
                .baseUri(this.internalTransferAPIUrl)
                .contentType("application/json")
                .body(this.objectMapper.writeValueAsString(internalTransferRequest))
                .post(INTERNAL_TRANSFER_API_TRANSFER, accountIdFrom, accountIdTo);

        this.testDataComponent.put(contextID, TestDataComponent.ContextVariable.RESPONSE, response);
    }

    private Response tryGetAccountBalance(String accountId, String contextID) throws JsonProcessingException {


        Response response = given()
                .baseUri(this.balanceSearchAPIUrl)
                .contentType("application/json")
                .get(BALANCE_SEARCH_BALANCES, accountId);

        this.testDataComponent.put(contextID, TestDataComponent.ContextVariable.RESPONSE, response);
        return response;
    }


    private void addAccountBalance(String accountId, double balance) throws JsonProcessingException {

        if (balance <= 0) {
            throw new IllegalArgumentException();
        }

        BalanceOperationRequest balanceOperationRequest = new BalanceOperationRequest();
        balanceOperationRequest.setOperationId(UuidGenerator.generate());
        balanceOperationRequest.setOperationType("TEST_AUTO");

        SingleOperationRequest singleOperationRequest = new SingleOperationRequest();
        singleOperationRequest.setAccountId(accountId);
        singleOperationRequest.setValue(new BigDecimal(balance));

        balanceOperationRequest.setOperations(Arrays.asList(singleOperationRequest));

        Response response = given()
                .baseUri(this.balanceManagerAPIUrl)
                .contentType("application/json")
                .body(this.objectMapper.writeValueAsString(balanceOperationRequest))
                .post(BALANCE_MANAGER_API_OPERATION);

        Assertions.assertEquals(200, response.statusCode());
    }


    private Response inactivateAccountByCustomerId(String customerId, String contextID) throws JsonProcessingException {


        Response response = given()
                .baseUri(this.accountAPIUrl)
                .contentType("application/json")
                .delete(ACCOUNT_API_DELETE_ACCOUNT_BY_CUSTOMER_ID, customerId);

        this.testDataComponent.put(contextID, TestDataComponent.ContextVariable.RESPONSE, response);

        Assertions.assertEquals(200, response.statusCode());
        return response;
    }

    private Response reduceOperationDay(String accountId, String contextID) throws JsonProcessingException {


        Response response = given()
                .baseUri(this.balanceManagerAPIUrl)
                .contentType("application/json")
                .get(BALANCE_MANAGER_REDUCE_OPERATION_DAY, accountId);

        this.testDataComponent.put(contextID, TestDataComponent.ContextVariable.RESPONSE, response);
        return response;
    }

}




