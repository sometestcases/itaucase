#language: pt

Funcionalidade: Transferencias de saldo entre contas de mesma titularidade

  Cenario: Transferir saldo com sucesso
    Quando efetuar a criacao de uma conta bancaria apelidada de X para um cliente T1
    E efetuar a criacao de uma conta bancaria apelidada de Y para um cliente T1
    E efetuar a adicao de saldo 300,40 para conta bancaria apelidada de X T1
    E efetuar a transferencia de 100,00 reais da conta X para a Y T1
    Entao a consulta de saldo da conta bancaria apelidada de Y deve retornar 100,00 T1
    E a consulta de saldo da conta bancaria apelidada de X deve retornar 200,40 T1

  Cenario: Transferir saldo excedendo limite diario
    Quando efetuar a criacao de uma conta bancaria apelidada de X para um cliente T2
    E efetuar a criacao de uma conta bancaria apelidada de Y para um cliente T2
    E efetuar a adicao de saldo 15000,00 para conta bancaria apelidada de X T2
    E efetuar a transferencia de 1500,00 reais da conta X para a Y T2
    Entao o codigo de resposta deve ser 200 T2
    Quando efetuar a transferencia de 9000,00 reais da conta X para a Y T2
    Entao o codigo de resposta deve ser 428 T2
    E a consulta de saldo da conta bancaria apelidada de X deve retornar 13500,00 T2
    E a consulta de saldo da conta bancaria apelidada de Y deve retornar 1500,00 T2
    Entao suponha que a ultima transacao realizada pela conta X tenha ocorrido um dia atraz T2
    Quando efetuar a transferencia de 9000,00 reais da conta X para a Y T2
    Entao o codigo de resposta deve ser 200 T2
    E a consulta de saldo da conta bancaria apelidada de X deve retornar 4500,00 T2
    E a consulta de saldo da conta bancaria apelidada de Y deve retornar 10500,00 T2

  Cenario: Transferencia de saldo envolvendo conta inativa
    Quando efetuar a criacao de uma conta bancaria apelidada de X para um cliente T3
    E efetuar a criacao de uma conta bancaria apelidada de Y para um cliente T3
    E efetuar a adicao de saldo 7000,00 para conta bancaria apelidada de X T3
    E efetuar a inativacao do cliente da conta bancaria Y T3
    E efetuar a transferencia de 1500,00 reais da conta X para a Y T3
    Entao o codigo de resposta deve ser 428 T3
    E a consulta de saldo da conta bancaria apelidada de X deve retornar 7000,00 T3