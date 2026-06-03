# language: pt
Funcionalidade: Cadastro de Tutorial

  Cenário: Sistema deve recusar pergunta em branco
    Dado que eu tenho os dados de um tutorial com a pergunta em branco
    Quando eu enviar a requisição para salvar
    Então o sistema deve retornar o status 400