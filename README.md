# FaqBB – Backend

API REST do **FaqBB**, sistema de FAQ multilíngue do Banco do Brasil desenvolvido no **4º período** do curso de Análise e Desenvolvimento de Sistemas (ADS) da [CESAR School](https://www.cesar.school/), na disciplina de **Requisitos, Projeto de Software e Validação**.

O backend expõe endpoints para tutoriais, áudios traduzidos, idiomas, usuários, autenticação JWT e moderação de conteúdo, com integração ao **Azure Blob Storage** para armazenamento de áudios.

---

## Objetivo

- Oferecer uma API para o frontend do FaqBB;
- Permitir tutoriais em vídeo (YouTube) com traduções em áudio;
- Garantir cadastro, login e perfis de usuário (comunidade, admin, super admin);
- Apoiar moderação de áudios (aprovar, reprovar, votar);
- Manter qualidade com testes automatizados e CI.

---

## O que você encontrará aqui

| Pasta / arquivo | Conteúdo |
|-----------------|----------|
| `src/main/java/com/bb/faq/controller` | Controllers REST (`/api/audio`, `/api/usuarios`, `/api/tutoriais`, `/api/idiomas`) |
| `src/main/java/com/bb/faq/service` | Regras de negócio |
| `src/main/java/com/bb/faq/repository` | Acesso ao banco (JPA) |
| `src/main/java/com/bb/faq/model` | Entidades (Usuario, Tutorial, Audio, Idioma…) |
| `src/main/java/com/bb/faq/config` | Segurança, Azure, inicialização de dados |
| `src/test/java/com/bb/faq` | Testes unitários (service e controller) |
| `.github/workflows` | Pipeline CI (Maven + testes) |
| `src/main/resources/TEMPLATE_application.properties` | Modelo de variáveis de ambiente |

---

## Tecnologias

- Java 21
- Spring Boot 4
- Spring Data JPA + PostgreSQL
- Spring Security + JWT (Auth0)
- Azure Storage Blob
- Spring Mail
- JUnit 5 + Mockito
- Maven (`mvnw`)

---

## Como rodar o projeto

### Pré-requisitos

- JDK 21 instalado
- PostgreSQL (ou URL de banco configurada)
- Variáveis de ambiente (copie o template e ajuste)

### Configuração

1. Clone o repositório:
   ```bash
   git clone https://github.com/cesar-ads2402-vsaa/projetos-4-bb-backend.git
   cd projetos-4-bb-backend
   ```

2. Crie `src/main/resources/application.properties` com base em `TEMPLATE_application.properties`.

3. Defina as variáveis principais:

   | Variável | Descrição |
   |----------|-----------|
   | `DB_URL` | URL do PostgreSQL |
   | `DB_USER` / `DB_PASSWORD` | Credenciais do banco |
   | `AZURE_STORAGE_CONNECTION` | Connection string do Azure Blob |
   | `AZURE_CONTAINER` | Nome do container |
   | `SEC_TOKEN` | Segredo do JWT |
   | `MAIL_USERNAME` / `MAIL_PASSWORD` | E-mail (recuperação de senha) |
   | `app.frontend.url` | URL do frontend |

### Executar

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

A API sobe por padrão em `http://localhost:8080`.

### Testes

```bash
.\mvnw.cmd test
```

Os testes unitários cobrem **services** e **controllers** (Mockito). O CI roda automaticamente em push/PR na branch `main`.

---

## Principais endpoints

| Módulo | Base | Exemplos |
|--------|------|----------|
| Tutoriais | `/api/tutoriais` | GET listar, POST criar, DELETE `/{id}` |
| Áudios | `/api/audio` | POST upload, GET por tutorial/idioma, PATCH voto/aprovar |
| Usuários | `/api/usuarios` | POST cadastro/login, PATCH promover/rebaixar, recuperação de senha |
| Idiomas | `/api/idiomas` | GET, POST, DELETE `/{id}` |

---

## Testes

- **Service:** regras de negócio (áudio, usuário, tutorial, idioma, token, senha).
- **Controller:** delegação aos services e status HTTP.
- **CI:** GitHub Actions com JDK 21 e `mvnw test`.

> Planejado para o futuro: testes de repository, model e integração com MockMvc.

---

## Squad – 4º período

| Nome | E-mail (CESAR) |
|------|----------------|
| Matheus Rangel Kirzner | mrk@cesar.school |
| Michelangelo Morais Do Rego | mmr@cesar.school |
| Paulo Cesar Ferreira De Assis | pcfa@cesar.school |
| Rafael Farias Santana | rfs5@cesar.school |
| Ramom De Oliveira Aguiar | roa@cesar.school |
| Robson Sandro Andrade Cunha Filho | rsacf@cesar.school |
| Thyalles Araujo Campos | tac2@cesar.school |
| Victor Gabriel Figueira Dos Santos | vgfs@cesar.school |
| Victor Simas Azevedo De Almeida | vsaa@cesar.school |
| Arthur Borba Lins | abl2@cesar.school |

---

## Para visitantes

Se você é **professor**, **avaliador** ou **profissional de tecnologia**, este repositório faz parte do MVP acadêmico FaqBB. Explore o código, os testes e o histórico de commits para entender a arquitetura e as decisões do grupo.

---

## Licença

Na ausência de licença explícita, considere o conteúdo **apenas para fins educacionais e visualização pública**, conforme orientação da disciplina e da organização acadêmica.
