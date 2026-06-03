# language: pt
Funcionalidade: Login de Usuário
  Como um usuário do sistema FAQ
  Quero fazer login com meu email e senha
  Para acessar as funcionalidades restritas

  Cenário: Login realizado com sucesso
    Dado que o usuário preenche o email com "admin@darkarts.com" e a senha com "Admin123!"
    Quando o usuário solicitar o login
    Então o sistema deve autenticar o usuário com status 200
    E deve retornar um token JWT válido na resposta

  Cenário: Login recusado por senha incorreta
    Dado que o usuário preenche o email com "admin@darkarts.com" e a senha com "SenhaErrada!"
    Quando o usuário solicitar o login
    Então o sistema deve recusar a autenticação com status 401
    E exibir a mensagem de erro "Credenciais inválidas"