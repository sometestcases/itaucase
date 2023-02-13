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


# Observações do Desenvolvimento

- Foram desenvolvidos 7 microsserviços demonstrados no diagrama de arquitetura, ficando de fora somente o job de retry de sinc do bacen, porém o retry acontece no consumo do evento também, então como não daria para desenvolver tudo, acabei descartando isso porque não interfere na demostração.

- Foi utilizado banco H2 ao invés do oracle, porque para fins de demonstração é mais simples, porém como esta usando JPA o que foi implementado serviria para oracle também trocando apenas algumas propriedades.

- Subi tudo que é necessário para rodar no dockerhub, bastando rodar o 'docker-compose up' dentro da pasta para iniciar todos containers do projeto, Kafka, Zookeeper, Schema Registry, os 7 micro-serviços e mocks do bacen e cadastro de cliente.

- Os testes integrados estão em um projeto separado, 'integration-test', visto que são testes que unem funcionalidades de todas aplicações implementadas, foram escritos testes que cobrem os cenários principais, que julguei os mais importantes.

- Não deu tempo de escrever todos testes unitários ideias, escrevi somente alguns no 'account-api' para não ficar sem nada (o case não diz a quantidade de teste unitário que precisa, mas certamente precisaria de mais do que foram escritos, costumo trabalhar com cerca de 70% de cobertura, o que não pude chegar nem perto aqui), como são 7 micro-serviços, ficou inviavel escrever teste para todas classes no prazo estipulado, tentei compensar com os integrados que acho mais relevantes e testam os cenários propostos.


# Como Executar

Conforme mencionado acima, basta ter o docker compose instalado, e dentro da pasta do projeto executar o comando 'docker-compose up', os artefatos que serão iniciados, são:
<br>
<br>
- Account API -> http://localhost:11111
- Balance Manager API -> http://localhost:11112
- Blance Manager Listener -> http://localhost:11113
- Internal Transfer API -> http://localhost:11114
- Internal Transfer Listener -> http://localhost:11115
- Balance Search API -> http://localhost:11116
- Balance Search Listener -> http://localhost:11117
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







