#language: pt

Funcionalidade: Criação de conta bancaria para o cliente

  Cenario: Criar conta bancaria para cliente com sucesso
    Quando efetuar a criacao de conta bancaria para um cliente A1
    Entao o codigo de resposta deve ser 201 A1
    E uma conta deve ter sido criada A1

  Cenario: Tentar conta bancaria para cliente inativo
    Quando efetuar a criacao de conta bancaria para um cliente existente inativo A2
    Entao o codigo de resposta deve ser 428 A2
    E uma conta nao deve ter sido criada A2

  Cenario: Tentar conta bancaria para cliente inexistente
    Quando efetuar a criacao de conta bancaria para um cliente inexistente A3
    Entao o codigo de resposta deve ser 428 A3
    E uma conta nao deve ter sido criada A3