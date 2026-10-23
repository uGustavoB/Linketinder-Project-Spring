<h1 align="center">LinkeTinder</h1>

## Descrição

Bem-vindo ao projeto **LinkeTinder**, um sistema inovador idealizado a partir do insight do grande empresário Dr. Antônio Paçoca. O objetivo desta plataforma é revolucionar o processo de recrutamento e seleção, unindo a dinâmica de "match" do Tinder ao mapeamento, nivelamento e validação de competências técnicas do LinkedIn.

O projeto evoluiu de um protótipo monolítico para uma **Arquitetura Distribuída em Microsserviços**, desacoplada, escalável e orientada a contratos RESTful. O backend foi totalmente reescrito em **Groovy** utilizando o ecossistema **Spring Framework (Spring Boot 3, Spring Data JPA e Spring Security)**, dividido em dois microsserviços autônomos com bancos de dados relacionais **PostgreSQL** isolados. A camada de apresentação é uma Single Page Application (SPA) moderna, reativa e tipada construída em **Angular 22** com Tailwind CSS.

> **Microsserviço de Núcleo (`ms-nucleo`)**: Responsável pela autenticação JWT, gestão cadastral de Candidatos e Empresas, CRUD protegido de Vagas, histórico de Formações Acadêmicas e pelo algoritmo bidirecional de **Match**. Roda na porta `8081` conectado ao banco `linketinder_nucleo_db`.

> **Microsserviço de Competências (`ms-competencia`)**: Responsável pelo catálogo global de competências técnicas e pelo gerenciamento dos vínculos e níveis de proficiência (`BASICO`, `MEDIO`, `AVANCADO`) associados a candidatos, vagas e empresas. Roda na porta `8082` conectado ao banco isolado `linketinder_competencia_db`.

> **Frontend Web (`frontend`)**: Interface SPA rica desenvolvida em Angular 22 com signals, interceptors HTTP para gestão de sessão JWT, componentes visuais com biblioteca Zard UI e Sonner Toasts. Roda na porta `4200`.

---

## Funcionalidades

- **Autenticação e Sessão Segura**:
  - Autenticação stateless via tokens JWT emitidos no `ms-nucleo` e validados de forma desacoplada no `ms-competencia`.
  - Controle de acesso baseado em papéis (`ROLE_CANDIDATO` e `ROLE_EMPRESA`) via anotações `@PreAuthorize`.
  - Hidratação de sessão resiliente ao recarregamento de página através do endpoint de checagem de perfil.
- **Anonimização Pré-Match (Garantia de Imparcialidade)**:
  - Candidatos exploram vagas sem visualizar a identificação da empresa contratante até que ocorra interesse recíproco.
  - Empresas buscam talentos de forma anônima, avaliando competências técnicas, formação e localização sem viés pessoal.
- **Sistema Bidirecional de Match**:
  - Registro de curtidas por ambas as partes (`candidato` $\times$ `vaga`).
  - Confirmação instantânea de Match com notificação em tempo real.
  - Liberação de canais de contato direto (Nome Completo, E-mail corporativo/pessoal, Localização e Data do Match) apenas após a reciprocidade.
- **Gerenciamento de Vagas e Candidatos**:
  - Empresas criam, editam e excluem vagas com proteção estrita contra IDOR (autorização de propriedade).
  - Exclusão segura em cascata de curtidas órfãs prevenindo violações de integridade referencial.
  - Painel com filtros de candidatos interessados, sugeridos por compatibilidade de competências e aba dedicada de matches formados.
- **Catálogo e Vínculo de Competências**:
  - Cadastro de novas competências técnicas no catálogo global.
  - Associação de proficiência com garantia de unicidade via `@UniqueConstraint` e operação de *upsert* transacional.
- **Histórico Acadêmico**:
  - CRUD completo de formações acadêmicas (instituição, curso, nível, status e períodos) no perfil do candidato.

---

## Arquitetura e Microsserviços

A plataforma adota **Clean Architecture** em seus serviços backend, garantindo baixo acoplamento entre regras de negócio, frameworks e camadas de transporte de dados:

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
               │ • Formações Acadêmicas       │       │                              │
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
│   ├── build.gradle                         # Dependências Spring Boot, JPA, Security, Spock
│   └── src/
│       ├── main/
│       │   ├── groovy/org/ugustavodev/msnucleo/
│       │   │   ├── MsNucleoApplication.groovy
│       │   │   ├── controller/              # REST Controllers (Vaga, Candidato, Empresa, Auth, Perfil)
│       │   │   ├── domain/                  # Entidades de Segurança e Usuário
│       │   │   ├── dto/                     # Request, Response e Records anonimizados
│       │   │   ├── exception/handler/       # RestControllerAdvice e padronização RFC
│       │   │   ├── mapper/                  # Mappers de conversão Entidade <-> DTO
│       │   │   ├── model/                   # Entidades JPA (Candidato, Empresa, Vaga, Curtida, Formacao)
│       │   │   ├── repositories/            # Spring Data JPA Repositories
│       │   │   ├── security/                # SecurityFilter, SecurityConfigurations, TokenService
│       │   │   └── usecase/                 # Regras de Negócio e Casos de Uso (Clean Architecture)
│       │   └── resources/application.yaml   # Configurações do serviço (porta 8081, Postgres)
│       └── test/groovy/                     # Suíte de Testes BDD com Spock Framework
│
├── ms-competencia/                          # Microsserviço de Competências (Spring Boot + Groovy)
│   ├── build.gradle                         # Dependências Spring Boot, JPA, Security Stateless, Spock
│   └── src/
│       ├── main/
│       │   ├── groovy/org/ugustavodev/mscompetencia/
│       │   │   ├── MsCompetenciaApplication.groovy
│       │   │   ├── controller/              # CompetenciaController, CompetenciaCandidatoController, etc.
│       │   │   ├── dto/                     # ApiResponseDTO, Request e Response DTOs
│       │   │   ├── exception/handler/       # Handler de exceções padronizado
│       │   │   ├── model/                   # Competencia, CompetenciaCandidato, CompetenciaVaga
│       │   │   ├── repositories/            # Repositórios com buscas por unicidade
│       │   │   ├── security/                # Validador stateless de JWT (TokenService, Filter)
│       │   │   └── usecase/                 # Casos de uso de vinculação e upsert
│       │   └── resources/application.yaml   # Configurações do serviço (porta 8082, Postgres)
│       └── test/groovy/                     # Testes unitários com Spock Framework
│
└── frontend/                                # Frontend SPA (Angular 22)
    ├── package.json                         # Dependências Angular, Tailwind, Ng-Icons, Ngx-Sonner
    └── src/
        ├── app/
        │   ├── core/                        # Guards (authGuard), Interceptors (JWT), Services e Models
        │   ├── layouts/                     # MainLayout com barra de navegação dinâmica por perfil
        │   ├── pages/                       # Login, Register, Dashboard, Vagas, Candidatos, Matches, Perfil
        │   └── shared/                      # Componentes Zard UI (Botões, Tabelas, Cards, Dialogs)
        └── environments/                    # Configuração de URLs dos microsserviços
```

---

## Estrutura dos Dados e Bancos de Dados

O modelo de dados distribuído garante o isolamento de domínios entre os microsserviços:

### Banco de Dados: `linketinder_nucleo_db` (`ms-nucleo`)


| Tabela / Entidade | Descrição e Propriedades Principais                                                                               |
| :---------------- | :------------------------------------------------------------------------------------------------------------------ |
| **`usuarios`**    | Identidade e credenciais (`email`, `senha` com hash BCrypt, `role`: `CANDIDATO` ou `EMPRESA`).                      |
| **`candidatos`**  | Perfil do candidato (`nome`, `sobrenome`, `cpf`, `data_nascimento`, `pais`, `estado`, `cep`, `descricao`).          |
| **`empresas`**    | Perfil corporativo (`nome`, `cnpj`, `pais`, `estado`, `cep`, `descricao`).                                          |
| **`vagas`**       | Oportunidade publicada por empresa (`nome`, `descricao`, `local`, `nivel`, `empresa_id`).                           |
| **`curtidas`**    | Tabela de interesse mútuo (`candidato_id`, `vaga_id`, `candidato_curtiu_em`, `empresa_curtiu_em`).                 |
| **`formacoes`**   | Histórico acadêmico (`candidato_id`, `instituicao`, `curso`, `nivel`, `status`, `data_inicio`, `data_conclusao`). |

### Banco de Dados: `linketinder_competencia_db` (`ms-competencia`)


| Tabela / Entidade            | Descrição e Propriedades Principais                                                                                        |
| :--------------------------- | :--------------------------------------------------------------------------------------------------------------------------- |
| **`competencias`**           | Catálogo técnico de tecnologias e habilidades (`id`, `nome`).                                                              |
| **`competencias_candidato`** | Vínculo Candidato-Competência (`candidato_id`, `competencia_id`, `nivel`). Unicidade por `(candidato_id, competencia_id)`. |
| **`competencias_empresa`**   | Vínculo Empresa-Competência (`empresa_id`, `competencia_id`). Unicidade por `(empresa_id, competencia_id)`.                |
| **`competencias_vaga`**      | Requisitos técnicos da vaga (`vaga_id`, `competencia_id`). Unicidade por `(vaga_id, competencia_id)`.                       |

---

## Padrões de Projeto e Práticas Arquiteturais

- **Clean Architecture & Use Cases**: Cada ação do sistema é isolada em um caso de uso com interface e implementação independentes (`CadastrarVagaUseCase`, `BuscarVagaPorIdUseCase`, `CandidatoCurteVagaUseCase`), facilitando a manutenção e a testabilidade sem acoplamento a controladores HTTP.
- **Data Transfer Object (DTO) & Envelope Pattern**: Todas as comunicações HTTP utilizam o envelope padrão `ApiResponseDTO` com código de status HTTP, mensagens amigáveis e sanitização rigorosa contra vazamento de metadados internos ou detalhes de banco de dados (`SEC-005`).
- **Segurança Stateless via JWT (JSON Web Tokens)**: O `ms-nucleo` emite os tokens criptografados com assinatura HMAC-256 e claims de identificação (`role`, `profileId`). O `ms-competencia` valida esses tokens de maneira 100% stateless através do seu próprio `SecurityFilter`, dispensando comunicação síncrona entre os backends para autenticação.
- **Repository Pattern & Spring Data JPA**: Abstração do acesso a dados via interfaces com consultas otimizadas e queries JPQL personalizadas para checagem rápida de matches.
- **Upsert Transacional**: Controle atômico com `@Transactional` que insere ou atualiza relacionamentos de competências sem duplicidade.

---

## Tecnologias

### Backend

- **Groovy 4 & Java 21** (Linguagem dinâmica, produtiva e totalmente compatível com a JVM)
- **Spring Boot 3.4+** (Framework principal para microserviços e injeção de dependências)
- **Spring Data JPA & Hibernate** (Mapeamento objeto-relacional e gerenciamento de persistência)
- **Spring Security & Auth0 Java-JWT (4.4.0)** (Autenticação, controle de papéis e segurança stateless)
- **PostgreSQL** (Banco de dados relacional oficial utilizado em ambos os microsserviços)
- **Spock Framework 2.4** (Framework de testes BDD para testes unitários e de integração com asserções expressivas)
- **Gradle** (Automação de compilação, resolução de dependências e testes)

### Frontend

- **Angular 22** (Framework SPA com arquitetura reativa moderna baseada em Signals)
- **TypeScript** (Tipagem estática estrita em todos os modelos e serviços)
- **Tailwind CSS** (Estilização utilitária responsiva e moderna)
- **Lucide Icons (`@ng-icons/lucide`)** (Conjunto de ícones vetoriais integrados)
- **Ngx-Sonner** (Sistema de notificações toast visuais e responsivas)

---

## Como Executar

### 1. Pré-requisitos

Certifique-se de ter instalados em seu ambiente de desenvolvimento:

- **Java JDK 21** ou superior
- **Node.js 20+** e **npm 10+**
- **PostgreSQL 14+** em execução na porta padrão `5432`

---

### 2. Configuração dos Bancos de Dados PostgreSQL

Acesse o cliente do PostgreSQL (`psql` ou pgAdmin) e crie os dois bancos de dados isolados:

```sql
CREATE DATABASE linketinder_nucleo_db;
CREATE DATABASE linketinder_competencia_db;
```

> [!NOTE]
> Por padrão, ambos os serviços conectam-se ao PostgreSQL em `localhost:5432` com usuário `postgres` e senha `postgres`. Caso utilize outras credenciais, ajuste em `ms-nucleo/src/main/resources/application.yaml` e `ms-competencia/src/main/resources/application.yaml` ou defina as variáveis de ambiente equivalentes.

---

### 3. Executando os Microsserviços e o Frontend

Abra três abas de terminal para iniciar os serviços:

#### Aba 1: Iniciar o `ms-nucleo` (Porta 8081)

- **No Linux/Mac:**
  ```bash
  cd ms-nucleo
  ./gradlew bootRun
  ```
- **No Windows:**
  ```bash
  cd ms-nucleo
  gradlew.bat bootRun
  ```

#### Aba 2: Iniciar o `ms-competencia` (Porta 8082)

- **No Linux/Mac:**
  ```bash
  cd ms-competencia
  ./gradlew bootRun
  ```
- **No Windows:**
  ```bash
  cd ms-competencia
  gradlew.bat bootRun
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

### 4. Executando os Testes Automatizados

Para rodar a suíte completa de testes unitários e de integração do projeto:

- **Testes do `ms-nucleo` (Spock):**
  ```bash
  cd ms-nucleo && ./gradlew test
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
