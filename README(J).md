# FaqBB — Sistema de FAQ Multilíngue e Infraestrutura Agentic-Ready

> **Projeto de Conclusão de Módulo**
> **Disciplina:** Requisitos, Projeto de Software e Validação
> **Curso:** Análise e Desenvolvimento de Sistemas (ADS) — 4º Período
> **Instituição:** CESAR School / Porto Digital
> **Parceria Corporativa:** Banco do Brasil

---

## 👥 Integrantes do Grupo (Squad FaqBB)
* **Arthur Borba Lins** (Dev / QA)
* **Matheus Rangel Kirzner** (Dev)
* **Michelangelo Morais Do Rego** (QA / Dev)
* **Paulo Cesar Ferreira De Assis** (Dev / QA)
* **Rafael Farias Santana** (QA / Dev)
* **Ramom De Oliveira Aguiar** (Product Owner)
* **Robson Sandro Andrade Cunha Filho** (Dev)
* **Thyalles Araujo Campos** (Dev / QA)
* **Victor Gabriel Figueira Dos Santos** (Dev / QA)
* **Victor Simas Azevedo De Almeida** (QA / Dev)

---

## 🔗 Repositórios do Projeto
* **Repositório do Frontend (React/Next.js):** [cesar-ads2402-vsaa/projetos-4-bb-frontend](https://github.com/cesar-ads2402-vsaa/projetos-4-bb-frontend)
* **Repositório do Backend (Java/Spring Boot):** [cesar-ads2402-vsaa/projetos-4-bb-backend](https://github.com/cesar-ads2402-vsaa/projetos-4-bb-backend)

---

## 📝 Visão Geral do Sistema
O **FaqBB** é uma plataforma distribuída de inclusão bancária e financeira desenvolvida sob medida para mitigar as barreiras linguísticas, de leitura e conceituais enfrentadas por comunidades indígenas no acesso aos serviços do Banco do Brasil. A solução estabelece uma convergência multimídia, conectando tutoriais visuais (vídeos do YouTube) com explicações e traduções em áudio gravadas em línguas nativas pela própria comunidade.

O sistema conta com um ecossistema rigoroso de moderação (votação de áudios e fluxos de aprovação/reprovação por administradores) e adota uma arquitetura inovadora **Agentic-Ready**. Através do **Model Context Protocol (MCP)** via Spring AI, o backend permite que agentes externos de Inteligência Artificial (como Claude Desktop ou Cursor IDE) se conectem nativamente ao sistema, descubram as ferramentas cadastradas e interajam de forma autônoma com a base de dados.

---

## 🏛️ Evidências de Cumprimento dos Requisitos da Disciplina

Este documento serve como mapa de rastreabilidade para a banca avaliadora, detalhando as decisões de design de software, locais exatos de implementação e justificativas de engenharia que sustentam os **3 pontos** da avaliação do módulo.

### 1. Arquitetura Distribuída
O projeto foi integralmente concebido e executado sob o modelo de **Arquitetura Distribuída Cliente-Servidor com Integração Cloud Baseada em Serviços**.

* **Como o sistema está distribuído:** A aplicação divide suas cargas lógicas, de renderização e de persistência em nós fisicamente independentes que se comunicam através da rede:
    * **Nó Cliente (Apresentação):** Camada baseada em `React / Next.js` focada em dispositivos móveis (*Mobile-First*), operando no navegador do usuário de maneira leve e desacoplada.
    * **Nó Servidor (Core Logístico):** API REST em `Java 21` com `Spring Boot 3.x` que centraliza as regras de negócio, autorização e os componentes agênticos de IA.
    * **Nó de Persistência Cloud (Dados Relacionais):** Banco de dados relacional **PostgreSQL** hospedado na infraestrutura em nuvem da **Supabase**.
    * **Nó de Armazenamento Distribuído (Mídia Pesada):** Container de objetos **Azure Blob Storage**, responsável por gerenciar de forma isolada os arquivos binários de áudio (`.mp3`).
* **Justificativa da Escolha:** Adotar um modelo Cliente-Servidor com terceirização de persistência resolve um trade-off crítico de performance: o peso do armazenamento e tráfego de arquivos multimídia é empurrado para a infraestrutura elástica da Azure. Isso mantém o servidor Java leve, operando unicamente com lógica e metadados, reduzindo drasticamente os custos de CPU e permitindo escalabilidade horizontal estável.
* **Adequação ao Tema (Inclusão Indígena):** Comunidades remotas frequentemente utilizam dispositivos de baixo processamento sob conexões de rede instáveis. Centralizar o processamento pesado de segurança, IA e dados no servidor, e entregar ao cliente uma interface limpa em Next.js com áudios reproduzíveis em no máximo **2 cliques**, reduz o atrito técnico e viabiliza a usabilidade real em campo.

---

### 2. Desenho da Arquitetura (Componentes, Tecnologias e Protocolos)
O fluxo completo de dados e a topologia física da rede do ecossistema FaqBB estão mapeados no diagrama de arquitetura abaixo:
![Untitled diagram-2026-06-11-144533.png](Untitled%20diagram-2026-06-11-144533.png)

#### Detalhamento Técnico das Interações e Protocolos:
1. **Frontend -> Core API:** Comunicação síncrona baseada em requisições **HTTP/REST** utilizando verbos padronizados (GET, POST, PUT, DELETE), trafegando dados estruturados em JSON e chaves criptográficas via cabeçalho de autorização (**Token JWT Bearer**).
2. **Automato/Agente de IA -> MCP Server:** Comunicação persistente orientada a eventos utilizando **SSE (Server-Sent Events)** sobre HTTP. Esse protocolo permite que modelos de linguagem em grande escala (LLMs) externos mapeiem semanticamente as capacidades lógicas do backend Java em tempo real.
3. **Core API -> Supabase (PostgreSQL):** Conexão lógica persistente e transacional estruturada sob o protocolo **TCP/IP nativo do PostgreSQL**, gerenciada via JDBC pelo Spring Data JPA / Hibernate.
4. **Core API -> Azure Blob Storage:** Transferência de streams binários de áudio protegida por criptografia de transporte através do protocolo **HTTPS**, encapsulada em formatos de requisição de rede do tipo `multipart/form-data`.
5. **Core API -> Servidor de E-mail:** Disparo assíncrono de chaves temporárias para fluxos de redefinição de credenciais de acesso, operando estritamente através do protocolo **SMTP** sobre canais TLS protegidos (porta 587).

---

### 3. Concorrência e Paralelismo
O gerenciamento de concorrência do FaqBB é estruturado de forma transparente pela infraestrutura de rede, possuindo mapeamento explícito para intervenções sob demandas de alto estresse de I/O de rede.

* **Mecanismo Concorrente Nativo Utilizado:** O backend em Spring Boot opera de forma embutida sobre o servidor web **Apache Tomcat**. O Tomcat implementa de forma automática um **Thread Pool (Pool de Threads)**. Cada nova requisição HTTP originada do Next.js ou de requisições de agentes MCP é interceptada e isolada em uma thread de trabalho (*Worker Thread*) independente e concorrente gerenciada pelo framework.
* **Componente Onde Está Presente:** Presente em toda a camada de controle e roteamento de rede da aplicação (`com.bb.faq.controller`).
* **Problema que Abordagem Resolve (Ganhos):** Evita o bloqueio crônico de concorrência. Se múltiplos usuários da comunidade indígena estiverem realizando o envio síncrono ou votação de áudios concorrentemente, o servidor não estabelece uma fila de gargalo sequencial. As threads rodam de forma concorrente em paralelo na CPU, blindando a porta `8080` de congelamentos sistêmicos e garantindo alta disponibilidade.

#### 🔧 Cenário de Extensão Explícita (Mecanismo Assíncrono Mapeado):
Identificamos que no componente `AudioService.java`, o processamento interno executa duas rotinas com alto potencial bloqueante (*I/O-Bound* e *CPU-Bound*): o encoder de conversão de arquivos de mídia via biblioteca JAVE2 (`encoder.encode`) e a transmissão do arquivo final para a nuvem da Azure (`blobClient.upload`).

Em cenários de escala de produção com arquivos extensos de áudio, a equipe mapeou a refatoração assíncrona do método utilizando a diretiva `@Async` do Spring Boot.
* **Ganho Adicional Esperado:** O método síncrono gravaria um status temporário no banco e liberaria o cliente HTTP (retornando `201 Created` para o celular em milissegundos). A thread pesada rodaria em background sem que o utilizador ficasse a aguardar com o ecrã travado.

---

### 4. Otimização do Sistema
O FaqBB aplica técnicas maduras de engenharia de software para a conservação e otimização de recursos computacionais do servidor e tempo de resposta da rede.

#### Otimizações Já Implementadas:
1. **Desacoplamento e Isolamento de I/O Multimídia (Offloading):** O sistema remove completamente o tráfego e armazenamento de arquivos binários pesados de dentro da aplicação Java ou do PostgreSQL. Salvando apenas a string da URL do local da Azure no atributo `caminhoArquivo` da entidade `Audio`, mantemos os índices de busca do PostgreSQL extremamente enxutos, performáticos e com alocação mínima de memória RAM.
2. **Pooling de Conexões de Rede Estáveis via HikariCP:** O Spring Data JPA utiliza nativamente o HikariCP como gerenciador de conexões com a Supabase. Em vez de desperdiçar ciclos de processamento de CPU e latência de rede abrindo e fechando sessões TCP complexas com o banco de dados na nuvem a cada requisição, um pool de conexões reutilizáveis é mantido permanentemente aberto e aquecido. Isso mitiga gargalos e acelera o tempo de resposta das consultas.
3. **Autenticação Stateless baseada em Tokens JWT:** O barramento de segurança estruturado no `SecurityFilter.java` não consome recursos de memória RAM mantendo sessões ativas do usuário (`HttpSession`). A validação é realizada exclusivamente por criptografia simétrica no token enviado, o que elimina o risco de vazamentos de memória e otimiza a performance horizontal.

#### Otimizações Futuras Mapeadas:
1. **Camada de Caching Distribuído com Redis:** A listagem de tutoriais de ajuda financeira tem baixa frequência de modificação. A introdução do **Redis** em memória RAM agirá como um acelerador: as buscas de tutoriais retornarão em menos de **5ms** diretamente da memória do servidor, poupando recursos e chamadas desnecessárias de rede.
2. **Paginação Inteligente e Lazy Loading de Coleções (Pageable):** Para otimizar o consumo de tráfego de rede e memória, as rotas que atualmente buscam todos os áudios (`.findAll()`) serão reestruturadas para receber parâmetros de paginação (`Pageable`), forçando a busca indexada e controlada sob demanda.

---

## 🧪 Estratégia de Qualidade e Testes (Pass Rate: 92%)
A estabilidade da infraestrutura distribuída do FaqBB é auditada e garantida de forma automatizada por um pipeline robusto de integração contínua (**GitHub Actions**):
* **Testes de Unidade (Caixa Branca):** 47 cenários automatizados implementados com `JUnit 5` e `Mockito`.
* **Testes de Integração (BDD via Cucumber / Gherkin):** 51 cenários de comportamento real que simulam fluxos do usuário de ponta a ponta sobre uma base de dados H2 rodando em memória RAM.
* **Testes End-to-End (E2E):** Automação funcional de interface do usuário executada diretamente no Frontend via **Cypress**.

---

## 🚀 Guia de Implantação e Execução Local

### Variáveis de Ambiente Obrigatórias
Para subir os componentes aplicacionais, configure as seguintes variáveis no seu ambiente ou no arquivo `application.properties`:
* `SPRING_DATASOURCE_URL` = URL JDBC de conexão com a base de dados PostgreSQL na Supabase.
* `SPRING_DATASOURCE_USERNAME` = Nome de utilizador do banco relacional.
* `SPRING_DATASOURCE_PASSWORD` = Senha de acesso ao banco na nuvem.
* `AZURE_STORAGE_CONNECTION_STRING` = String de conexão de segurança contendo as chaves do Azure Blob Storage.
* `JWT_SECRET_KEY` = Hash alfanumérico privado utilizado para assinar os tokens criptográficos de autenticação.
