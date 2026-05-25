# Relatório de Falhas – FaqBB Backend

**Projeto:** FaqBB – FAQ multilíngue Banco do Brasil  
**Disciplina:** Requisitos, Projeto de Software e Validação  
**Data:** 25/05/2026  
**Equipe:** Squad FaqBB (4º período – CESAR School)

---

## 1. Objetivo deste relatório

Documentar **falhas e problemas** encontrados durante a execução do plano de testes: o que deu errado ou ficou incompleto, **como reproduzir**, **ambiente** e **caso de teste relacionado**.

> **Observação:** Os 47 testes unitários **passaram no CI** (JDK 21). As falhas abaixo referem-se à **análise estática (Qodana)** e à **execução local dos testes com JRE 8** (sem JDK 21).

---

## Falha #01

| Campo | Informação |
|-------|------------|
| **ID da falha** | F01 |
| **Data da detecção** | 25/05/2026 |
| **Detectado por** | GitHub Actions – Qodana for JVM |
| **Severidade** | Warning (avisos de qualidade de código) |
| **Caso de teste relacionado** | TC07 (envio/gravação de áudio – TS03) |
| **Teste automatizado relacionado** | `AudioServiceTest` (fluxo de áudio) |
| **Arquivo** | `src/main/java/com/bb/faq/service/AudioService.java` |
| **Linha** | 118 |
| **Método** | `salvarAudio` – bloco `finally` |

### Descrição

A inspeção **Result of method call ignored** apontou que o retorno de `tempInput.delete()` não é verificado. Se a exclusão do arquivo temporário falhar, o sistema não trata o erro.

### Configuração do ambiente de teste

| Item | Valor |
|------|-------|
| Pipeline | GitHub Actions – workflow **Qodana** |
| SO do runner | Ubuntu Latest |
| Trigger | Push ou Pull Request na branch `main` |
| Action | `JetBrains/qodana-action@v2026.1` |
| JDK do projeto | 21 |
| Branch analisada | `main` (ou head do PR) |

### Passos para reproduzir

1. Fazer commit e push na branch `main`, **ou** abrir um Pull Request para `main`.
2. No repositório GitHub, abrir a aba **Actions**.
3. Selecionar o workflow **Qodana** em execução.
4. Aguardar o job **Qodana for JVM** finalizar.
5. Abrir o resumo: *2 new problems were found*.
6. Clicar no annotation **Result of method call ignored** na linha **118** de `AudioService.java`.
7. Confirmar a mensagem: *Result of `File.delete()` is ignored*.

### Resultado esperado

Nenhum warning do tipo *Result of method call ignored* no arquivo `AudioService.java`.

### Resultado obtido

Warning na linha 118 – retorno de `File.delete()` ignorado.

### Status

Aberto  

### Ação corretiva sugerida

Verificar o booleano retornado por `delete()` ou usar `Files.deleteIfExists(path)` com tratamento adequado.

---

## Falha #02

| Campo | Informação |
|-------|------------|
| **ID da falha** | F02 |
| **Data da detecção** | 25/05/2026 |
| **Detectado por** | GitHub Actions – Qodana for JVM |
| **Severidade** | Warning |
| **Caso de teste relacionado** | TC07 (envio/gravação de áudio – TS03) |
| **Teste automatizado relacionado** | `AudioServiceTest` |
| **Arquivo** | `src/main/java/com/bb/faq/service/AudioService.java` |
| **Linha** | 119 |
| **Método** | `salvarAudio` – bloco `finally` |

### Descrição

Mesma inspeção da F01: o retorno de `tempOutput.delete()` não é verificado no `finally`.

### Configuração do ambiente de teste

Igual à **Falha #01** (mesmo job Qodana no CI).

### Passos para reproduzir

1. Repetir os passos 1 a 5 da **Falha #01**.
2. Abrir o segundo annotation na linha **119** de `AudioService.java`.
3. Confirmar a mensagem: *Result of `File.delete()` is ignored*.

### Trecho de código relacionado

```java
} finally {
    if (tempInput.exists()) tempInput.delete();   // linha 118 – F01
    if (tempOutput.exists()) tempOutput.delete(); // linha 119 – F02
}
```

### Resultado esperado

Exclusão de arquivos temporários tratada ou sem warning na análise estática.

### Resultado obtido

Warning na linha 119.

### Status

Aberto  

### Ação corretiva sugerida

Igual à F01 – tratar ambos os `delete()` no `finally`.

---

## Falha #03

| Campo | Informação |
|-------|------------|
| **ID da falha** | F03 |
| **Data da detecção** | 25/05/2026 |
| **Detectado por** | Dev da equipe (execução local) |
| **Severidade** | Média – impede rodar testes na máquina local |
| **Caso de teste relacionado** | Suite automatizada (47 testes unitários – service + controller) |
| **Atividade do plano** | Rodar `mvnw test` no ambiente local |
| **Tipo** | Falha de ambiente (configuração Java) |

### Descrição

Ao executar os testes automatizados localmente no Windows, o Maven falhou na compilação porque a máquina tinha apenas **JRE 8** instalado, e o projeto exige **JDK 21** para compilar e rodar os testes.

### Configuração do ambiente de teste

| Item | Valor |
|------|-------|
| SO | Windows 10 |
| Java instalado | JRE 1.8.0_401 (`java version "1.8.0_401"`) |
| Caminho detectado | `C:\Program Files\Java\jre-1.8` |
| Projeto | FaqBB Backend – `java.version=21` no `pom.xml` |
| Comando executado | `.\mvnw.cmd test` |
| Pasta do projeto | `projetos-4-bb-backend` |

### Passos para reproduzir

1. Clonar o repositório do backend.
2. Ter apenas **JRE 8** no PATH (sem JDK 21 configurado).
3. Abrir terminal na pasta `projetos-4-bb-backend`.
4. Executar: `.\mvnw.cmd test`
5. Aguardar a fase de compilação dos testes (`testCompile`).

### Resultado esperado

Maven compila e executa os 47 testes unitários com sucesso (`BUILD SUCCESS`).

### Resultado obtido

Build falhou com erro de compilação:

```
[ERROR] No compiler is provided in this environment. Perhaps you are running on a JRE rather than a JDK?
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:testCompile
```

Os testes **não chegaram a rodar** na máquina local.

### Status

Resolvido na equipe via **GitHub Actions** (JDK 21 no CI). Local: aberto até instalar JDK 21.

### Ação corretiva sugerida

1. Instalar **JDK 21** (Temurin ou similar).
2. Configurar `JAVA_HOME` apontando para o JDK (não para o JRE 8).
3. Rodar novamente `.\mvnw.cmd test`.
4. Manter o CI como validação principal quando a máquina local não estiver configurada.

---

## 2. Resumo das falhas

| ID | Origem | Severidade | Caso de teste | Status |
|----|--------|------------|---------------|--------|
| F01 | Qodana CI – linha 118 | Warning | TC07 / AudioServiceTest | Aberto |
| F02 | Qodana CI – linha 119 | Warning | TC07 / AudioServiceTest | Aberto |
| F03 | Ambiente local – JRE 8 | Média | Suite automatizada (47 testes) | Aberto (local) / OK no CI |

---

## 3. Evidências (anexar na entrega)

| Falha | Evidência sugerida |
|-------|-------------------|
| F01, F02 | Print da aba Actions → Qodana for JVM → annotations nas linhas 118 e 119 |
| F03 | Print do terminal com erro *JRE rather than a JDK* + `java -version` mostrando 1.8 |

---

*Relatório de Falhas – FaqBB Backend – v1.0 – 25/05/2026*
