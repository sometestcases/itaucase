# Itau Case

Projeto que apresenta uma possível solução, para um cenário hipotético, onde é necessário criar uma estrutura que comporte consulta de saldo de contas, e transferencia de saldo entre essas contas.

![](/arquitetura.drawio.png)

# Explicação dos Fluxos contemplados no diagrama

1 - Criação de contas: cria uma conta bancaria para um cliente, e nesse processo faz a verificação se o cliente esta ativo na 'customer-api', caso
esteja inativo, ou não exista, o retorno da criação de conta será negativo(4xx), esse endpoint deve ser chamado pelo fluxo que cria contas, após ser criada
será publicada no kafka, e o serviço de saldo ira replicar a conta.

2 - Inativação de conta: o o account-manager inativa uma conta bancaria a partir do id do cliente da mesma, bloqueando o saldo no balance-manager (essa operação pode retornar falha caso exista uma transação concorrente em andamento, ou a conta tenha saldo), toda vez que o cadastro de um cliente for inativado esse endpoint deve ser chamado antes da conclusão da operação pelo sistema que realiza a inativação (presumo que para encerrar a conta do cliente, precise encerrar a conta bancaria antes), e se a chamada falhar a inativação do cliente deve ser negada, porque vai significar que alguma transação esta em andamento, ou ele tem saldo

OBS: Trazendo esse status para dentro da conta, faz com que não precise criar uma integração com o serviço de cadastro em operações criticas como consulta de saldo e transferencia, além disso impede problemas de concorrência, como inativação do cliente ao mesmo tempo que uma transação esta ocorrendo, (considerei um problema grave, o cliente ter uma transação reportada ao BACEN com data posterior ao encerramento da própria conta)

3 - Transferencia entre contas de mesma Titularidade:  transfere um valor de uma conta para outra, nessa operação a internal-transfer-api vai realizar apenas uma chamada para o balance-manager, realizando uma operação de transferencia atômica entre as duas contas (já que nesse caso ambas estão no mesmo banco), e nesse caso o internal-transfer-listener ira consumir o evento do kafka e no consumo ira salvar a transferencia no banco de dados, e ira tentar enviar para o BACEN apenas uma vez, caso não consiga, a retentativa sera feita através de um cronjob que vai ficar rodando de X em X minutos. 

4 - Consulta de saldo:  O balance search api simplesmente consulta o saldo através da conta bancaria, primeiro no redis, e caso não exista ele procura n próprio banco de dados
que é atualizado através do consumidor do tópico de atualização de saldo do balance manager (no consumo do evento  é atualizado tanto o redis quanto o banco)



