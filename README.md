# FaqBB – Backend

API REST do **FaqBB**, sistema de FAQ multilíngue do Banco do Brasil desenvolvido no **4º período** do curso de Análise e Desenvolvimento de Sistemas (ADS) da [CESAR School](https://www.cesar.school/), na disciplina de **Requisitos, Projeto de Software e Validação**.
O backend expõe endpoints para tutoriais, áudios traduzidos, idiomas, usuários, autenticação JWT e moderação de conteúdo, com integração ao **Azure Blob Storage** para armazenamento de áudios. Adicionalmente, atua como um servidor **MCP (Model Context Protocol)** usando Spring AI para integrações nativas com modelos de inteligência artificial.
---

## Objetivo

- Oferecer uma API para o frontend do FaqBB;
- Permitir tutoriais em vídeo (YouTube) com traduções em áudio;
- Garantir cadastro, login e perfis de usuário (comunidade, admin, super admin);
- Apoiar moderação de áudios (aprovar, reprovar, votar);
- Manter qualidade com testes automatizados e CI.
- Habilitar capacidades agênticas através da integração com o protocolo MCP.

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
| `src/main/java/com/bb/faq/mcp` | Ferramentas de IA (Model Context Protocol) |
| `src/test/java/com/bb/faq/bdd` | Infraestrutura e passos dos testes BDD (Cucumber) |
| `src/test/resources/features` | Cenários de comportamento em formato Gherkin |
| `.github/workflows` | Pipeline CI (Maven + testes) |
| `src/main/resources/TEMPLATE_application.properties` | Modelo de variáveis de ambiente |

---

## Tecnologias

**Backend & APIs:**
- Java 21
- Spring Boot 3/4
- Spring Security + JWT (Auth0)
- Validações com Jakarta Bean Validation

**Dados & Nuvem:**
- Spring Data JPA + PostgreSQL (Produção)
- H2 Database (Em memória para testes)
- Azure Blob Storage (Armazenamento de áudios)
- Spring Mail

**Testes & Qualidade:**
- JUnit 5 + Mockito (Testes Unitários)
- Cucumber + Gherkin (Testes BDD / Integração)
- Spring MockMvc

**DevOps:**
- Docker
- GitHub Actions (CI/CD Automático)
- Azure Container Apps

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

--

## Model Context Protocol (MCP)

O backend do FaqBB implementa o ecossistema do **Model Context Protocol (MCP)** integrado ao **Spring AI**. Isso transforma a aplicação num *Agentic Backend*, habilitando LLMs externas (como Claude Desktop ou assistentes dedicados) a consumir e gerenciar os dados em tempo real através de linguagem natural de forma segura, mapeando dinamicamente as seguintes ferramentas base:

- `listarTutoriais`: Consulta os vídeos, perguntas e categorias disponíveis na aplicação.
- `criarTutorialMcp`: Cadastra novos tutoriais estruturados interpretando comandos em linguagem humana.
---

## Principais endpoints

| Módulo | Base | Exemplos |
|--------|------|----------|
| Tutoriais | `/api/tutoriais` | GET listar, POST criar, DELETE `/{id}` |
| Áudios | `/api/audio` | POST upload, GET por tutorial/idioma, PATCH voto/aprovar |
| Usuários | `/api/usuarios` | POST cadastro/login, PATCH promover/rebaixar, recuperação de senha |
| Idiomas | `/api/idiomas` | GET, POST, DELETE `/{id}` |

---

## Testes e Qualidade

O projeto possui uma arquitetura de testes robusta, dividida em:

- **Testes Unitários (JUnit + Mockito):** Focados em validar isoladamente as regras de negócio nos `Services` e os retornos nos `Controllers`.
- **Testes BDD / Integração (Cucumber + MockMvc):** Utilizando a linguagem Ubíqua (Gherkin), testamos fluxos completos da API (como Cadastro e Login) em um ambiente isolado utilizando um banco de dados em memória (**H2 Database**).

Para rodar toda a suíte de testes (Unitários e BDD):

```bash
# Windows
.\mvnw.cmd clean test

# Linux / macOS
./mvnw clean test
```

```markdown
## CI/CD e Deploy

O repositório conta com uma esteira configurada no **GitHub Actions** que realiza:
1. **Continuous Integration (CI):** Ao fazer push para a `main`, a esteira sobe a aplicação, roda todos os testes (Unitários e BDD) e valida o build. Utiliza um filtro inteligente para pular execuções caso o código fonte não tenha sido alterado.
2. **Continuous Deployment (CD):** Se os testes passarem, a esteira empacota a aplicação em uma imagem **Docker**, publica no Docker Hub e faz o deploy automático na **Azure Container Apps**.
```
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
