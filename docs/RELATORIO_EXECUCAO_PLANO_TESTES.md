# Relatório de Execução do Plano de Testes – FaqBB Backend

**Projeto:** FaqBB – FAQ multilíngue Banco do Brasil  
**Disciplina:** Requisitos, Projeto de Software e Validação  
**Plano de testes:** versão 02  
**Data da execução:** 25/05/2026  
**Equipe:** Squad FaqBB (4º período – CESAR School)

---

## 1. Objetivo deste relatório

Registrar o que foi **executado** do Plano de Testes: quais testes rodaram, quantos passaram ou falharam, e o que ficou pendente — separando testes **automatizados** e **manuais**.

---

## 2. Ambiente de execução

| Item | Testes automatizados (CI) | Testes manuais (local) |
|------|---------------------------|-------------------------|
| SO | Ubuntu Latest | Windows 10 |
| Java | JDK 21 (Temurin) | JRE 8 na máquina local (falhou); JDK 21 no CI (passou) |
| Ferramenta | Maven Wrapper – `./mvnw test` | Navegador + Postman/Insomnia |
| Pipeline | GitHub Actions – *Java CI with Maven (Backend)* | Backend na porta 8080 |
| Variáveis | `AZURE_STORAGE_CONNECTION_STRING`, `API_SECURITY_TOKEN_SECRET`, `APP_FRONTEND_URL` | `application.properties` + PostgreSQL |

---

## 3. Resumo geral

| Categoria | Planejado / existente | Executado | Passou | Falhou | Pendente |
|-----------|----------------------|-----------|--------|--------|----------|
| Testes unitários – Service | 24 | 24 | 24 | 0 | 0 |
| Testes unitários – Controller | 23 | 23 | 23 | 0 | 0 |
| **Total automatizados** | **47** | **47** | **47** | **0** | **0** |
| Casos manuais (TC02–TC04) | 3 | 3 | 3 | 0 | 0 |
| Execução local `mvnw test` | 47 | 47 | 0 | 1 (ambiente) | — |

**Resultado geral dos testes automatizados (CI):** BUILD SUCCESS (47/47).  
**Execução local:** falhou por JRE 8 (ver Relatório de Falhas – F03).  
**Resultado geral dos testes manuais (TC02–TC04):** 3 passaram.

---

## 4. Execução – testes automatizados

**Comando:** `./mvnw test`  
**Onde rodou:** GitHub Actions, a cada push/PR na branch `main`.

### 4.1 Camada Service

| Classe de teste | Quantidade | Status |
|-----------------|------------|--------|
| AudioServiceTest | 4 | Passou |
| UsuarioServiceTest | 5 | Passou |
| TokenServiceTest | 4 | Passou |
| TutorialServiceTest | 4 | Passou |
| PasswordResetServiceTest | 4 | Passou |
| IdiomaServiceTest | 3 | Passou |
| **Subtotal** | **24** | **Passou** |

### 4.2 Camada Controller

| Classe de teste | Quantidade | Status |
|-----------------|------------|--------|
| AudioControllerTest | 8 | Passou |
| UsuarioControllerTest | 9 | Passou |
| TutorialControllerTest | 3 | Passou |
| IdiomaControllerTest | 3 | Passou |
| **Subtotal** | **23** | **Passou** |

### 4.3 Evidência

- Workflow: **Java CI with Maven (Backend)**  
- Step: *Rodar todos os Testes Automatizados*  
- Saída esperada: todos os testes verdes, build concluído com sucesso.

---

## 5. Execução – testes manuais (casos do plano)

| ID | Cenário | Título | Executado? | Status | Responsável |
|----|---------|--------|------------|--------|-------------|
| TC02 | TS01 | Listar tutoriais pela API | Sim | Passou | Dev backend |
| TC03 | TS01 | Cadastrar novo tutorial | Sim | Passou | Dev backend |
| TC04 | TS02 | Login com sucesso | Sim | Passou | QA / Dev |

### Detalhe dos manuais executados

**TC02 – Passou**  
- GET `/api/tutoriais` retornou lista com id, pergunta, URL e data.

**TC03 – Passou**  
- POST `/api/tutoriais` criou tutorial; item apareceu na listagem.

**TC04 – Passou**  
- Login retornou token JWT, nome do usuário e cargo.

---

## 6. Atividades do plano x execução

| Atividade (plano) | Status |
|-------------------|--------|
| Rodar testes no CI (`mvnw test`) | Concluído – passou (47/47) |
| Rodar testes local (`mvnw test`) | Falhou – JRE 8 (F03) |
| Executar TC02 | Concluído – passou |
| Executar TC03 | Concluído – passou |
| Testes manuais no app (fluxos principais) | Em andamento |

---

## 7. Conclusão

A execução do plano no **CI** foi concluída com **100% de sucesso** (47 testes). Na **máquina local**, os testes não rodaram por uso de **JRE 8** em vez de JDK 21 (falha F03). Os **testes manuais** TC02, TC03 e TC04 passaram.

Problemas registrados (Qodana e JRE 8) estão no **Relatório de Falhas** (documento separado).

---

*Relatório de Execução do Plano de Testes – FaqBB Backend – v1.0 – 25/05/2026*
