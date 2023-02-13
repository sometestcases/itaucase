#language: pt

Funcionalidade: Consulta de saldo

  Cenario: Consultar saldo com sucesso
    Quando efetuar a criacao de uma conta bancaria apelidada de X para um cliente S1
    E efetuar a adicao de saldo 300,40 para conta bancaria apelidada de X S1
    Entao a consulta de saldo da conta bancaria apelidada de X deve retornar 300,40 S1

  Cenario: Consultar saldo de conta inativa
    Quando efetuar a criacao de uma conta bancaria apelidada de X para um cliente S2
    E efetuar a adicao de saldo 15000,00 para conta bancaria apelidada de X S2
    E efetuar a inativacao do cliente da conta bancaria X S2
    Quando consultar o saldo da conta bancaria apelidade de X S2
    Entao o codigo de resposta deve ser 412 S2