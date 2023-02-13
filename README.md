# Itau Case

Projeto que propõe uma solução, para um cenário hipotético, onde é necessário criar uma estrutura que comporte consulta de saldo de contas, e transferencia de saldo entre essas contas seguindo algumas regras.
<br>
<br>
Segue o diagrama da solução proposta, abaixo: <br>
(pode ser visto mais adequadamente no draw io, ou abrindo a imagem em um visualizador)

![](/arquitetura.drawio.png)

# Observações da Arquitetura

- Foi desconsiderada a questão de autenticação, visto que é só um case de teste, e aumentaria o tempo de desenvolvimento consideravelmente, além de não ter sido solicitado explicitamente nada nesse sentido, e não ser excencial para o funcionamento em sí bem como a apresentação da idéia, porém caso fosse um projeto real seria indispensável.

<br>

- Foi escolhido banco oracle para todas aplicações, tendo em vista que ele possui recurso de DataRedaction que é importante para garantir o permissionamento adequado da informação financeira, além disso, como todos bancos relacionais, tem mecanismos que podem ser utilizados para garantir a integridade da informação e atomicidade das transações, além disso tenho bom histórico desse banco para trabalhar com grande volume de informação.

<br>

- Apesar de solicitado, não contemplei mecanismo de 'rate limit', porque todos esses micro-serviços, estariam em rede interna, não seriam expostos, e cada canal/produto que for utilizar vai ter seu 'BFF' que será exposto, e  nesse caso o rate limit teria que estar nesses serviços que são expostos que terão o IP do cliente.  Até poderia ser implementado um rate limit, porém o IP que esses micro-serviços desse projeto teriam não seria do cliente, e sim das instancias de outras aplicações, ou seja, não seria dos melhores indicadores para ser a chave do 'rate limit', porque quando um IP for limitado ira estar impactando varias requisições que não tem necessáriamente relação com o exceço de chamadas, por isso entendi como uma implementação de benefício contestavel para esse case, que precisaria ser avaliada/discutida com mais calma se fosse um cenário real, e como a principio não impacta na demostração do case e eu tive que selecionar bem o que iria ser feito (por conta do prazo e de a arquitetura ter ficado grande pra implementar), eu descartei isso.

<br>

- Não coloquei uma API Gateway, porque os serviços tem conextos muito diferentes entre sí, além disso seria mais um ponto de falha entre todos eles, mas ainda sim é algo discutivel.

# Observações do Desenvolvimento

- Foram desenvolvidos 7 microsserviços (4 APIS e 3 Event Listeners) demonstrados no diagrama de arquitetura, ficando de fora somente o job de retry de sinc do bacen, porém o retry acontece no consumo do evento também (um número limitado de vezes), então como não daria para desenvolver tudo, acabei descartando isso porque não interfere na demostração.

- Foi utilizado banco H2 ao invés do oracle, porque para fins de demonstração é mais simples, porém como esta usando JPA o que foi implementado serviria para oracle também trocando apenas algumas propriedades.

- Subi tudo que é necessário para rodar no dockerhub, bastando rodar o 'docker-compose up' dentro da pasta para iniciar todos containers do projeto, Kafka, Zookeeper, Schema Registry, os 7 micro-serviços e mocks do bacen e cadastro de cliente.

- Os testes integrados estão em um projeto separado, 'integration-test', visto que são testes que unem funcionalidades de todas aplicações implementadas, foram escritos testes que cobrem os cenários principais, que julguei os mais importantes.

- Não deu tempo de escrever todos testes unitários ideias, escrevi somente alguns no 'account-api' para não ficar sem nada (o case não diz a quantidade de teste unitário que precisa, mas certamente precisaria de mais do que foram escritos, costumo trabalhar com cerca de 70% de cobertura, o que não pude chegar nem perto aqui), como são 7 micro-serviços, ficou inviavel escrever teste para todas classes no prazo estipulado, tentei compensar com os integrados que acho mais relevantes e testam os cenários propostos.

- O Dockerfile de todos projetos estão dentro de cada um, sendo possível buildar a imagem com facilidade novamente se necessário 

- Não subi um Redis para o Balance Search como mostra a arquitetura, como mencionei, tive que escolher algumas coisas para deixar de fazer por conta do prazo e tamanho da arquitetura, e vi isso como secundario pelo menos para demonstrar o funcionamento, afinal ja é um micro-serviço separado e com base separada, o Redis seria uma melhoria.

- Utilizei Java 11, com uma versão do Spring Boot que não é a mais recente possível, por questão de velocidade de desenvolvimento mesmo, pois ja testei muito essa stack utilizada é compativel com todas as bibliotecas que coloquei no projeto evitando assim alguns tipos de provaveis falhas de compatibilidade que tomariam mais tempo de desenvolvimento para resolver se eu optasse pelo ideal que seria usar o Spring Boot mais recente com Java 17 pelo menos, porém, como isso não interfere significativamente no resultado final, segui essa linha mesmo.  

# Como Executar

Conforme mencionado acima, basta ter o docker compose instalado, e dentro da pasta do projeto executar o comando 'docker-compose up', os artefatos que serão iniciados, são:
<br>
<br>
- Account API -> localhost:11111
- Balance Manager API -> localhost:11112
- Blance Manager Listener -> localhost:11113
- Internal Transfer API -> localhost:11114
- Internal Transfer Listener -> localhost:11115
- Balance Search API -> localhost:11116
- Balance Search Listener -> localhost:11117

Também serão iniciadas algumas dependencias necessárias para rodar, como Kafka, Zookeeper, Schema Registry, e Mocks

<br>
<br>
Após iniciar o projeto, é possível executar os testes integrados, entrando dentro da pasta do projeto 'integration-test', e executando o comando './gradlew integrationTest'
<br>
<br>
Todas API's desenvolvidas, estão documentadas conforme a especificação OpenAPI 3, e a documentação pode ser visualizada através do swagger:

- Account API -> http://localhost:11111/swagger-ui/index.html
- Balance Manager API -> http://localhost:11112/swagger-ui/index.html
- Internal Transfer API -> http://localhost:11114/swagger-ui/index.html
- Balance Search API -> http://localhost:11116/swagger-ui/index.html



# Pontos Chave:

- As operações são indepotentes, ou seja, sempre tem um código que faz parte da regra de negócio e é gerado pelo client, que faz esse papel de 'Indepotency Key' por baixo dos panos.

- Balance Manager: A idéia dele é fazer operações de saldo, de forma atomica, isto é (entre outras coisas), ser uma operação totalmente 'thread-safe' para a mesma conta, centralizando esse domínio de 'saldo' porque certamente será comum varios processos, não só transferencias para contas de mesma titularidade, precisarem manipular o saldo do cliente, dentro desse projeto foram desenvolvidos mecanismos de lock utilizando a unique key do banco relacional, que garante 100% de assertividade na hora de realizar uma operações no saldo (no pior caso dando rollback da transação e retornando erro pro cliente) independente de haver operações concorrentes para a mesma conta, esse serviço é responsável por publicar os eventos de alteração de saldo e operações que realiza, dividi ele em API e Listener, que compartilham o mesmo core, onde o Listener é responsável somente por ler os eventos de cadastro de conta e replicar a conta para a base do serviço.

- Account API: Esse serviço é responsável principalmente pelo cadastro da conta do cliente, tem um papel bem simples de cadastrar na base (realizando somente as validações de cliente ativo/existente) e publicar o evento de criação de conta.

- Internal Transfer: Essa é a aplicação que de fato realiza a transferencia entre contas orquestrando a operação, no caso faz somente uma chamada ao Balance Manager, para realizar as operações de saldo entre as 2 contas (como se fosse um poxy), e fica aguardando o evento de conclusão da operação, para salvar em base bem como enviar a comunicação ao Bacen, essa aplicação esta dividida em Listener e API também. 

- Balance Search: Essa aplicação replica atualizações de saldo da conta, para disponibilização da consulta, que fica totalmente desacoplada do restante da arquitetura, funcionando na falha de qualquer outro micro-serviço, nesse caso a chave do tópico do kafka é o id da conta, garantindo que não havera concorrencia de informação para a mesma conta, além disso dentro esse evento tem um mecanismo referente a ordem do update, para garantir que os updates sejam feitos na ordem correta nessa aplicação e o saldo nunca permaneça errado, pode ter alguns millisegundos de delay para o consumo do evento do kafka, mas não considero isso um problema para consulta de saldo. 

# Build

Todos projetos usam Gradle e podem ser buildados com o comando "./gradlew build"

