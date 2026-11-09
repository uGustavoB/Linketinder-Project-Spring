<h1 align="center">LinkeTinder</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange.svg" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring_Boot-4.1.1-brightgreen.svg" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Groovy-4.0.21-blue.svg" alt="Groovy 4" />
  <img src="https://img.shields.io/badge/Angular-22-red.svg" alt="Angular 22" />
  <img src="https://img.shields.io/badge/PostgreSQL-14%2B-blue.svg" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Spock_Framework-2.4-purple.svg" alt="Spock Framework" />
  <img src="https://img.shields.io/badge/CodeNarc-3.5.0-success.svg" alt="CodeNarc" />
  <img src="https://img.shields.io/badge/OpenAPI_3-Swagger-green.svg" alt="Swagger OpenAPI 3" />
</p>

## Descrição

Bem-vindo ao projeto **LinkeTinder**, um sistema inovador idealizado a partir do insight do grande empresário Dr. Antônio Paçoca. O objetivo desta plataforma é revolucionar o processo de recrutamento e seleção, unindo a dinâmica de "match" do Tinder ao mapeamento, nivelamento e validação de competências técnicas do LinkedIn.

O projeto adota uma **Arquitetura Distribuída em Microsserviços**, desacoplada, escalável e orientada a contratos RESTful. O backend foi desenvolvido em **Groovy** utilizando o ecossistema **Spring Framework (Spring Boot, Spring Data JPA e Spring Security)**, dividido em dois microsserviços autônomos com bancos de dados relacionais **PostgreSQL** isolados. A camada de apresentação é uma Single Page Application (SPA) moderna, reativa e tipada construída em **Angular 22** com Tailwind CSS e componentes Zard UI.

> **Microsserviço de Núcleo (`ms-nucleo`)**: Responsável pela autenticação JWT, gestão cadastral de Candidatos e Empresas, CRUD protegido de Vagas, histórico de Formações Acadêmicas e pelo algoritmo bidirecional de **Match**. Roda na porta `8081` conectado ao banco `linketinder_nucleo_db`.

> **Microsserviço de Competências (`ms-competencia`)**: Responsável pelo catálogo global de competências técnicas e pelo gerenciamento dos vínculos e níveis de proficiência (`BASICO`, `MEDIO`, `AVANCADO`) associados a candidatos, vagas e empresas com operação de *upsert* transacional. Roda na porta `8082` conectado ao banco isolado `linketinder_competencia_db`.

> **Frontend Web (`frontend`)**: Interface SPA rica desenvolvida em Angular 22 com signals reativos, interceptors HTTP para gestão de sessão segura via cookie HTTP-only, componentes visuais com biblioteca Zard UI e Sonner Toasts. Roda na porta `4200`.

---

## 📚 Documentação Interativa das APIs (Swagger / OpenAPI 3)

Ambos os microsserviços expõem documentação interativa completa via **SpringDoc OpenAPI 3** com exemplos de requisições, payloads de resposta, esquemas de validação e códigos de erro:

- **Swagger UI do `ms-nucleo`:**  
  👉 [http://localhost:8081/api/docs](http://localhost:8081/api/docs) *(JSON OpenAPI: `/api/v3/api-docs`)*
- **Swagger UI do `ms-competencia`:**  
  👉 [http://localhost:8082/api/docs](http://localhost:8082/api/docs) *(JSON OpenAPI: `/api/v3/api-docs`)*

---

## Funcionalidades

- **Autenticação e Sessão Segura**:
  - Autenticação stateless via tokens JWT emitidos no `ms-nucleo` e validados de forma desacoplada no `ms-competencia`.
  - Sessão transparente via cookie HTTP-Only (`auth_token`), prevenindo vulnerabilidades de XSS.
  - Controle de acesso baseado em papéis (`ROLE_CANDIDATO` e `ROLE_EMPRESA`) via anotações `@PreAuthorize` e rota protegida com `empresaGuard` no frontend.
- **Anonimização Pré-Match (Garantia de Imparcialidade)**:
  - Candidatos exploram vagas sem visualizar a identificação da empresa contratante até que ocorra interesse recíproco.
  - Empresas buscam talentos de forma anônima, avaliando competências técnicas, formação e localização sem viés pessoal.
- **Sistema Bidirecional de Match**:
  - Registro de curtidas por ambas as partes (`candidato` $\times$ `vaga`).
  - Confirmação instantânea de Match com notificação em tempo real.
  - Liberação de canais de contato direto (Nome Completo, E-mail corporativo/pessoal, Localização e Data do Match) apenas após a reciprocidade.
- **Gerenciamento de Vagas e Candidatos**:
  - Empresas criam, editam e excluem vagas com proteção estrita contra IDOR (autorização de propriedade).
  - Exclusão segura em lote de curtidas com queries otimizadas `@Modifying @Query`.
  - Painéis com listagem de vagas sugeridas, candidatos interessados e matches confirmados.
- **Catálogo e Vínculo de Competências**:
  - Cadastro de novas competências técnicas no catálogo global.
  - Associação de proficiência com garantia de unicidade via `@UniqueConstraint` e operação de **upsert transacional padronizado** para candidatos e empresas.
- **Histórico Acadêmico**:
  - CRUD completo de formações acadêmicas (instituição, curso, nível, status e períodos) vinculado ao candidato titular com validação de IDOR.

---

## Arquitetura e Microsserviços

A plataforma adota os princípios de **Clean Architecture** em seus serviços backend, garantindo baixo acoplamento entre regras de negócio, frameworks e camadas de transporte de dados:

```
                              ┌────────────────────────────────────────┐
                              │            Frontend Angular            │
                              │           (Porta 4200 / SPA)           │
                              └───────┬────────────────────────┬───────┘
                                      │                        │
                      HTTP /api/perfil│        HTTP /api/      │
                      HTTP /api/vaga  │        competencias    │
                      HTTP /api/auth  │        (JWT Validated) │
                                      ▼                        ▼
               ┌──────────────────────────────┐       ┌──────────────────────────────┐
               │          ms-nucleo           │       │        ms-competencia        │
               │      (Porta 8081 / Spring)   │       │     (Porta 8082 / Spring)    │
               │                              │       │                              │
               │ • Autenticação & JWT Issuer  │       │ • Catálogo de Competências   │
               │ • Candidatos & Empresas      │       │ • Vínculos & Proficiências   │
               │ • CRUD Vagas & Deleção Segura│       │ • Validador JWT Stateless    │
               │ • Curtidas & Lógica de Match │       │ • Upsert Transacional        │
               │ • Formações Acadêmicas       │       │ • Proteção IDOR por Vaga     │
               └──────────────┬───────────────┘       └──────────────┬───────────────┘
                              │                                      │
                              ▼                                      ▼
               ┌──────────────────────────────┐       ┌──────────────────────────────┐
               │    linketinder_nucleo_db     │       │  linketinder_competencia_db  │
               │         (PostgreSQL)         │       │         (PostgreSQL)         │
               └──────────────────────────────┘       └──────────────────────────────┘
```

---

## Estrutura do Projeto

O repositório está organizado como um monorepo modular contendo os microsserviços e a aplicação cliente:

```
Linketinder-Project-Spring/
├── ms-nucleo/                               # Microsserviço Núcleo (Spring Boot + Groovy)
│   ├── build.gradle                         # Dependências Spring Boot, JPA, Security, Spock, CodeNarc
│   ├── config/codenarc/codenarc.xml         # Regras de Análise Estática (CodeNarc 3.5.0 para Groovy 4)
│   └── src/
│       ├── main/
│       │   ├── groovy/org/ugustavodev/msnucleo/
│       │   │   ├── MsNucleoApplication.groovy
│       │   │   ├── config/                  # OpenApiConfig (Swagger UI /api/docs)
│       │   │   ├── controller/              # REST Controllers (Vaga, Candidato, Empresa, Auth, Perfil, Formacao)
│       │   │   ├── domain/                  # Entidades de Segurança e Usuário (Role, Usuario)
│       │   │   ├── dto/                     # Request, Response e Records anonimizados
│       │   │   ├── handler/                 # ExceptionHandlerController (padronização RFC)
│       │   │   ├── mapper/                  # Mappers de conversão Entidade <-> DTO
│       │   │   ├── model/                   # Entidades JPA (Candidato, Empresa, Vaga, Curtida, Formacao)
│       │   │   ├── repositories/            # Spring Data JPA com @EntityGraph e @Modifying
│       │   │   ├── security/                # SecurityFilter, SecurityConfigurations, TokenService
│       │   │   └── usecase/                 # Clean Architecture dividida por domínio
│       │   │       ├── impl/                # Implementações (candidatos, curtidas, empresas, formacao, matches, vagas)
│       │   │       └── interfaces/          # Contratos dos Use Cases
│       │   └── resources/application.yaml   # Configurações do serviço (porta 8081, Postgres)
│       └── test/groovy/                     # Suíte de 30 Specs Spock (126 testes unitários)
│
├── ms-competencia/                          # Microsserviço de Competências (Spring Boot + Groovy)
│   ├── build.gradle                         # Dependências Spring Boot, JPA, Security, Spock Framework
│   └── src/
│       ├── main/
│       │   ├── groovy/org/ugustavodev/mscompetencia/
│       │   │   ├── MsCompetenciaApplication.groovy
│       │   │   ├── config/                  # OpenApiConfig (Swagger UI /api/docs)
│       │   │   ├── controller/              # CompetenciaController, CompetenciaCandidatoController, etc.
│       │   │   ├── dto/                     # ApiResponseDTO, Request e Response DTOs
│       │   │   ├── exception/handler/       # ExceptionHandlerController padronizado
│       │   │   ├── mapper/                  # CompetenciaMapper, CompetenciaVinculoMapper
│       │   │   ├── model/                   # Competencia, CompetenciaCandidato, CompetenciaEmpresa, CompetenciaVaga
│       │   │   ├── repositories/            # Spring Data JPA Repositories
│       │   │   ├── security/                # Validador stateless de JWT (TokenService, SecurityFilter, UsuarioAutenticado)
│       │   │   └── usecase/                 # Casos de uso de vinculação, upsert e remoção
│       │   │       ├── impl/
│       │   │       └── interfaces/
│       │   └── resources/application.yaml   # Configurações do serviço (porta 8082, Postgres)
│       └── test/groovy/                     # Suíte Spock Framework (57 testes unitários cobrindo UseCases e Mappers)
│
└── frontend/                                # Frontend SPA (Angular 22 + Tailwind CSS)
    ├── package.json                         # Dependências Angular 22, Tailwind 4, Ng-Icons, Ngx-Sonner
    └── src/
        ├── app/
        │   ├── core/                        # Guards (authGuard, empresaGuard), Interceptors, Services e Models
        │   ├── layouts/                     # MainLayout com navegação dinâmica e perfil logado
        │   ├── pages/                       # Login, Register, Dashboard, Vagas, Candidatos, Matches, Perfil
        │   └── shared/                      # Componentes Zard UI (Botões, Tabelas, Cards, Dialogs)
        └── environments/                    # Configuração de URLs dos microsserviços
```

---

## Estrutura dos Dados e Bancos de Dados

O modelo de dados distribuído garante o isolamento de domínios entre os microsserviços:

### Banco de Dados: `linketinder_nucleo_db` (`ms-nucleo`)

| Tabela / Entidade | Descrição e Propriedades Principais                                                                               |
|:------------------|:------------------------------------------------------------------------------------------------------------------|
| **`usuarios`**    | Identidade e credenciais (`email`, `senha` com hash BCrypt, `role`: `CANDIDATO` ou `EMPRESA`).                    |
| **`candidatos`**  | Perfil do candidato (`nome`, `sobrenome`, `cpf`, `data_nascimento`, `pais`, `estado`, `cep`, `descricao`).        |
| **`empresas`**    | Perfil corporativo (`nome`, `cnpj`, `pais`, `estado`, `cep`, `descricao`).                                        |
| **`vagas`**       | Oportunidade publicada por empresa (`nome`, `descricao`, `local`, `nivel`, `empresa_id`).                         |
| **`curtidas`**    | Tabela de interesse mútuo (`candidato_id`, `vaga_id`, `candidato_curtiu_em`, `empresa_curtiu_em`).                |
| **`formacoes`**   | Histórico acadêmico (`candidato_id`, `instituicao`, `curso`, `nivel`, `status`, `data_inicio`, `data_conclusao`). |

### Banco de Dados: `linketinder_competencia_db` (`ms-competencia`)

| Tabela / Entidade             | Descrição e Propriedades Principais                                                                                        |
|:------------------------------|:---------------------------------------------------------------------------------------------------------------------------|
| **`competencias`**            | Catálogo técnico de tecnologias e habilidades (`id`, `nome`).                                                              |
| **`competencias_candidatos`** | Vínculo Candidato-Competência (`candidato_id`, `competencia_id`, `nivel`). Unicidade por `(candidato_id, competencia_id)`. |
| **`competencias_empresas`**   | Vínculo Empresa-Competência (`empresa_id`, `competencia_id`, `nivel`). Unicidade por `(empresa_id, competencia_id)`.       |
| **`competencias_vagas`**      | Requisitos técnicos da vaga (`vaga_id`, `competencia_id`, `empresa_id`). Unicidade por `(vaga_id, competencia_id)`.        |

---

## Padrões de Projeto e Práticas Arquiteturais

- **Clean Architecture & Use Cases**: Cada ação do sistema é isolada em um caso de uso com interface e implementação independentes (`CadastrarVagaUseCase`, `BuscarVagaPorIdUseCase`, `CandidatoCurteVagaUseCase`), facilitando a manutenção e a testabilidade sem acoplamento a controladores HTTP.
- **Data Transfer Object (DTO) & Envelope Pattern**: Todas as comunicações HTTP utilizam o envelope padrão `ApiResponseDTO` com código de status HTTP, mensagens amigáveis e sanitização rigorosa contra vazamento de metadados internos.
- **Segurança Stateless via JWT (JSON Web Tokens)**: O `ms-nucleo` emite os tokens criptografados com assinatura HMAC-256 e claims de identificação (`role`, `perfilId`). O `ms-competencia` valida esses tokens de maneira 100% stateless através do seu próprio `SecurityFilter`, dispensando comunicação síncrona entre os backends para autenticação.
- **Otimização com `@EntityGraph` e `@Modifying`**: Consultas relacionais com `LEFT OUTER JOIN` antecipado para eliminar gargalos de queries N+1, além de deleções em lote executadas diretamente no banco de dados.
- **Upsert Transacional**: Controle atômico com `@Transactional` que insere novos vínculos ou atualiza o nível de proficiência caso a competência já esteja associada, tanto para candidatos quanto para empresas.
- **Análise Estática de Código (CodeNarc)**: Regras ativas de qualidade e boas práticas com `CodeNarc 3.5.0` garantindo 0 violações de código em ambos os microsserviços.

---

## Tecnologias

### Backend
- **Groovy 4.0.21 & Java JDK 21**
- **Spring Boot 4.1.1** (Spring Data JPA, Spring Security, Spring Validation, SpringDoc OpenAPI 3)
- **Auth0 Java-JWT (4.4.0)**
- **PostgreSQL** (Bancos relacionais isolados por microsserviço)
- **Spock Framework 2.4** (Suíte BDD completa em ambos os microsserviços)
- **CodeNarc 3.5.0** (Análise estática de código)
- **Gradle 9**

### Frontend
- **Angular 22** (Standalone Components, Signals reativos)
- **TypeScript 6**
- **Tailwind CSS 4** + **Tailwind Animate**
- **Zard UI Core** (`class-variance-authority`, `clsx`, `tailwind-merge`)
- **Lucide Icons (`@ng-icons/lucide`)**
- **Ngx-Sonner** (Notificações toast)
- **Vitest**

---

## Como Executar

### 1. Pré-requisitos
- **Java JDK 21** ou superior
- **Node.js 20+** e **npm 10+**
- **PostgreSQL 14+** em execução na porta padrão `5432`

---

### 2. Configuração dos Bancos de Dados PostgreSQL

Crie os dois bancos de dados isolados no PostgreSQL:

```sql
CREATE DATABASE linketinder_nucleo_db;
CREATE DATABASE linketinder_competencia_db;
```

---

### 3. Executando os Microsserviços e o Frontend

Abra três abas de terminal para iniciar os serviços:

#### Aba 1: Iniciar o `ms-nucleo` (Porta 8081)
```bash
cd ms-nucleo
./gradlew bootRun
```

#### Aba 2: Iniciar o `ms-competencia` (Porta 8082)
```bash
cd ms-competencia
./gradlew bootRun
```

#### Aba 3: Iniciar o Frontend Angular (Porta 4200)
```bash
cd frontend
npm install
npm start
```

Após a inicialização, acesse a aplicação em seu navegador no endereço:
👉 **`http://localhost:4200`**

---

### 4. Executando os Testes Automatizados e Análise Estática

- **Testes do `ms-nucleo` (Spock):**
  ```bash
  cd ms-nucleo && ./gradlew test
  ```
- **Análise Estática do `ms-nucleo` (CodeNarc):**
  ```bash
  cd ms-nucleo && ./gradlew codenarcMain codenarcTest
  ```
- **Testes do `ms-competencia` (Spock):**
  ```bash
  cd ms-competencia && ./gradlew test
  ```
- **Build e Validação de Tipos do Frontend:**
  ```bash
  cd frontend && npm run build
  ```

---

## Autores

Este projeto foi desenvolvido por:
- **Gustavo Gabriel** - [GitHub](https://github.com/uGustavoB)
