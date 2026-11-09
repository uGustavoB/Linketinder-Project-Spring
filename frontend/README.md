# 🎨 LinkeTinder — Frontend Web

Interface Single Page Application (SPA) moderna, responsiva e fortemente tipada do ecossistema **LinkeTinder**, construída com **Angular 22**, **Tailwind CSS 4** e componentes inspirados no design system **Zard UI** (shadcn/ui para Angular).

---

## 🚀 Tecnologias e Bibliotecas

- **Angular 22** (Standalone Components, Signals reativos, novo Control Flow `@if` / `@for` / `@switch`)
- **TypeScript 6** (Tipagem estática estrita em todos os modelos, serviços e formulários reativos)
- **Tailwind CSS 4** + **Tailwind Animate** (Estilização utilitária moderna e responsiva)
- **Zard UI Core** (`class-variance-authority`, `clsx`, `tailwind-merge` para estilização modular de componentes)
- **Lucide Icons** (`@ng-icons/lucide` — ícones vetoriais integrados e leves)
- **Ngx-Sonner** (Notificações toast visuais para feedback instantâneo de ações e erros)
- **Vitest** (Test runner moderno e veloz para testes unitários)

---

## 🏛️ Arquitetura e Estrutura de Pastas

A aplicação segue uma estrutura modular centrada em boas práticas de isolamento de responsabilidades:

```
frontend/src/app/
├── core/                                # Camada central de infraestrutura e regras globais
│   ├── guards/                          # Proteção de rotas
│   │   ├── auth.guard.ts                # Validação de sessão ativa via currentUser Signal
│   │   └── empresa-guard.ts             # Restrição de rotas exclusivas para empresas (ROLE_EMPRESA)
│   ├── interceptors/                    # Interceptores HTTP
│   │   └── auth.interceptor.ts          # Injeção automática de withCredentials (cookies HTTP-only)
│   ├── models/                          # Interfaces e contratos TypeScript alinhados aos DTOs
│   │   ├── candidato.model.ts
│   │   ├── competencia.model.ts
│   │   ├── empresa.model.ts
│   │   ├── formacao.model.ts
│   │   ├── match.model.ts
│   │   └── vaga.model.ts
│   └── services/                        # Comunicação HTTP reativa com os microsserviços
│       ├── auth.service.ts              # Gestão de autenticação, login, logout e signal de usuário
│       ├── candidato.service.ts
│       ├── competencia.service.ts
│       ├── empresa.service.ts
│       ├── formacao.service.ts
│       ├── match.service.ts
│       └── vaga.service.ts
│
├── layouts/                             # Layouts estruturais da aplicação
│   └── main-layout/                     # Shell com Sidebar dinâmica, perfil logado e logout
│
├── pages/                               # Visões e fluxos da aplicação (Roteadas)
│   ├── auth/
│   │   ├── login/                       # Autenticação de candidatos e empresas
│   │   └── register/                    # Cadastro com seleção de perfil (Candidato vs Empresa)
│   ├── dashboard/                       # Painel com visão geral e atalhos rápidos
│   ├── vagas/                           # Painel de vagas (Exploração para Candidato, CRUD para Empresa)
│   ├── candidatos/                      # Busca e visualização anônima de talentos (Exclusivo Empresa)
│   ├── matches/                         # Painel de matches confirmados com canais de contato liberados
│   └── perfil/                          # Gestão de perfil, dados cadastrais e CRUD de formações
│
└── shared/                              # Componentes visuais reutilizáveis (Zard UI)
    ├── components/                      # Botões, Badges, Cards, Modais/Dialogs, Dropdowns
    └── ui/                              # Primitivos de estilização acessíveis
```

---

## 🔒 Autenticação e Segurança no Frontend

1. **Gestão de Sessão via Cookie HTTP-Only:**
   - A aplicação não armazena tokens JWT no `localStorage` ou `sessionStorage`, prevenindo vulnerabilidades de **XSS** (*Cross-Site Scripting*).
   - O `auth.interceptor.ts` configura `withCredentials: true` em todas as requisições HTTP, permitindo que o navegador trafegue o cookie `auth_token` de forma segura e transparente entre as portas `4200`, `8081` (`ms-nucleo`) e `8082` (`ms-competencia`).

2. **Guards de Rota:**
   - **`authGuard`**: Impede o acesso de usuários deslogados às páginas internas (`/dashboard`, `/vagas`, `/candidatos`, `/matches`, `/perfil`), redirecionando para `/login`.
   - **`empresaGuard`**: Garante que candidatos não acessem telas exclusivas corporativas (como a listagem de candidatos `/candidatos`).

3. **Reatividade com Angular Signals:**
   - O `AuthService` gerencia o estado da sessão através de `signal<UserData | null>(null)`, propagando alterações de usuário, perfil e permissões em tempo real por toda a árvore de componentes sem necessidade de recarregamento da página.

---

## 🛠️ Como Executar

### Pré-requisitos
- **Node.js**: versão 20 ou superior
- **npm**: versão 10 ou superior

### Instalação das Dependências
```bash
npm install
```

### Servidor de Desenvolvimento
Inicia a aplicação na porta padrão `4200`:
```bash
npm start
# ou
ng serve
```
Acesse no navegador: **`http://localhost:4200`**

### Build de Produção
Compila os artefatos otimizados na pasta `dist/frontend`:
```bash
npm run build
```

### Execução dos Testes Unitários
Executa a suíte de testes com o Vitest:
```bash
npm test
```
