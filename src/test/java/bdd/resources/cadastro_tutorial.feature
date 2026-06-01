# language: pt
Funcionalidade: Cadastro de Tutoriais em Vídeo
  Como um administrador do sistema
  Quero cadastrar um novo tutorial com um link do YouTube
  Para que os usuários possam visualizar as dúvidas frequentes em formato de vídeo

  Contexto:
    Dado que o administrador está autenticado e na tela de gerenciamento de FAQ

  Cenário: Cadastro de tutorial realizado com sucesso
    E que preenche a pergunta com "Como alterar a minha senha de acesso?"
    E preenche a URL do YouTube com "https://www.youtube.com/watch?v=exemplo123"
    E seleciona a categoria "Segurança"
    Quando solicitar a criação do tutorial
    Então o sistema deve salvar o novo tutorial no banco de dados
    E deve retornar os dados confirmando a criação, incluindo o ID gerado e a data de criação

  Esquema do Cenário: Tentativa de cadastro com dados obrigatórios ausentes
    E que preenche a pergunta com "<pergunta>"
    E preenche a URL do YouTube com "<youtubeUrl>"
    E seleciona a categoria "<categoria>"
    Quando solicitar a criação do tutorial
    Então o sistema deve recusar a operação
    E exibir a mensagem de erro apropriada para o campo inválido

    Exemplos:
      | pergunta                            | youtubeUrl                              | categoria |
      |                                     | https://www.youtube.com/watch?v=123kjs | Suporte   |
      | Como redefinir o App?               |                                         | Suporte   |
      | Como entrar em contato com o SAC?  | https://www.youtube.com/watch?v=123kjs |           |