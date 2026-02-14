# ERP Cloud - Documentação Arquitetural

> **Última Atualização**: Fevereiro 14, 2026  
> Um documento único, agnóstico de tecnologia, que documenta estratégias, padrões e motivos das decisões do sistema.
> ⚠️ **NOTA**: Este documento descreve a arquitetura ideal. Ver seção [Desvios Identificados](#-desvios-identificados) para divergências com a implementação atual.

---

## 📋 Índice
1. [Visão Geral](#visão-geral)
2. [Princípios Arquiteturais](#princípios-arquiteturais)
3. [Padrões de Design](#padrões-de-design)
4. [Estratégia de Domínio](#estratégia-de-domínio)
5. [Observability & Auditoria](#-observability--auditoria)
6. [Gestão de Acesso](#-gestão-de-acesso)
7. [Multi-Tenancy](#multi-tenancy)
8. [Decisões de Projeto](#decisões-de-projeto)
9. [Desvios Identificados](#-desvios-identificados)
10. [Componentes Principais](#componentes-principais)
11. [Fluxos Críticos](#fluxos-críticos)

---

## 🎯 Visão Geral

ERP Cloud é um sistema de gestão empresarial projetado para ser:
- **Multi-tenant**: Múltiplas empresas operando de forma isolada no mesmo sistema
- **Extensível**: Suporta customizações por tenant
- **Auditável**: Rastreia todas as operações críticas
- **Seguro**: Isolamento de dados, controle de acesso granular

### Objetivo Principal
Fornecer uma plataforma centralizada para gerenciamento de processos empresariais (usuários, produtos, clientes, etc) com suporte a múltiplos tenants.

---

## 🏗️ Princípios Arquiteturais

### 1. **Clean Architecture**
A codebase é organizada em camadas independentes:

```
┌─────────────────────────────────────────┐
│   Presentation (REST API, Controllers)  │
│   Input validation, HTTP mapping        │
├─────────────────────────────────────────┤
│   Application (Handlers, Commands)      │
│   Orquestração de domínio, use cases    │
├─────────────────────────────────────────┤
│   Domain (Aggregates, Value Objects)    │
│   Lógica de negócio pura                │
├─────────────────────────────────────────┤
│   Infrastructure (Persistence, Events)  │
│   Detalhe técnico de persistência       │
└─────────────────────────────────────────┘
```

**Por que?** Mantém a lógica de negócio isolada de detalhes técnicos, facilitando testes e manutenção.

### 2. **Domain-Driven Design (DDD)**
O sistema modela conceitos de negócio como primeira classe:
- **Agregados**: User, Tenant, Product (raízes de coerência transacional)
- **Value Objects**: UserId, UserEmail, UserRole (imutáveis, sem identidade própria)
- **Repositórios**: Persistência abstrata para agregados
- **Domain Events**: Eventos que representam mudanças significativas

**Por que?** Alinha código com linguagem de negócio, tornando mais fácil comunicação entre técnicos e stakeholders.

### 3. **CQRS (Command Query Responsibility Segregation)**
Separação entre operações de escrita (Commands) e leitura (Queries):
- **Commands**: Mudam o estado (CreateUser, ActivateUser)
- **Handlers**: Orquestram a execução de commands
- **Queries**: Recuperam dados (potencial para otimizações futuras)

**Por que?** Permite otimizações independentes de escrita/leitura e auditoria clara.

### 4. **Event Sourcing**
Operações críticas geram domain events que documentam o histórico:
- UserCreatedEvent, UserActivatedEvent, UserRoleAddedEvent
- Permite reconstruir estado, auditoria, e notificações

**Por que?** Auditoria completa, capacidade de recuperação de falhas, e base para sistemas assíncronos.

---

## 🎨 Padrões de Design

### **Pattern: Mapper (Domain ↔ Infrastructure)** - ⚠️ NÃO IMPLEMENTADO
```
Domain Layer      Infrastructure Layer
   User      <---->   UserEntity
  (puro)            (com JPA annotations)
```

**Objetivo**: Mantém o domínio livre de acoplamento com detalhes de persistência. A camada domain não conhece JPA, bancos de dados, ou qualquer framework.

**Padrão idealizado**:
- `User.java` (domínio puro, sem @Entity, sem @Column)
- `UserEntity.java` (mapping JPA completo)
- `UserMapper.java` (converte User ↔ UserEntity bidirecionalmente)

**ℹ️ ATUAL**: Ver seção [Desvios Identificados](#-desvios-identificados) - Implementação atual não segue este padrão.

### **Pattern: Value Object (Imutável)**
```
UserEmail = "joao@example.com"  // Validado no construtor
userId = 123L                    // Dentro de UserId(123)
```

**Motivo**: Encapsula validação, previne estados inválidos, documenta intent.

**Exemplos**: UserId, UserEmail, UserName, UserPassword, UserRole, UserPermission

### **Pattern: Repository (Abstração de Persistência)**
```
Domain:     UserRepository (interface, abstraiona dados)
Infra:      UserRepositoryJpa (implementação concreta)
```

**Motivo**: Domínio não depende de tecnologia de persistência. Fácil de mockear testes.

### **Pattern: Aggregate Root (Consistência Transacional)**
```
User (Aggregate Root)
├── UserId (Value Object)
├── UserEmail (Value Object)
├── Set<UserRole> (Value Objects)
├── Set<UserPermission> (Value Objects)
└── Domain Events (histórico)
```

**Motivo**: Define limites de transação. Operações afetam o agregado como um todo, garantindo consistência.

---

## 🔑 Estratégia de Domínio

### **User Aggregate**

#### **Identidade e Dados Básicos**
- **Identificador Único**: UserId (por sistema global)
- **Email**: Único por tenant (valor object validado)
- **Nome**: String validada (comprimento mínimo/máximo)
- **Senha**: Hash criptografado (nunca armazenado plano)

#### **Estado do Usuário** (UserStatus)
```
INACTIVE → ACTIVE → DELETED
  ↑                   ↑
  │   (não pode      (soft delete,
  └─ voltar)        não pode reverter)
```

**Regras**:
- Novo usuário começa INACTIVE
- Ativação geralmente ocorre após confirmação de email
- Soft delete mantém dados para auditoria

#### **Papéis (Roles)** - Modelo Multi-Role
```
Antes (anti-padrão):
  user.role = "ADMIN"  // Apenas 1 role

Agora (correto):
  user.roles = {ADMIN, MANAGER}  // 0..N roles
```

**Tipos de Role**: ADMIN, MANAGER, USER, GUEST

**Por que múltiplas roles?**
- Cenários reais: Um usuário pode ser simultaneamente ADMIN de um módulo e MANAGER de outro
- Mais flexível que lógica de condicional complexa
- Segue princípio de composição sobre herança

#### **Permissões (Permissions)** - Modelo Independente de Roles
```
user.permissions = {
  "USER_CREATE",      // Pode criar usuários
  "TENANT_EDIT",      // Pode editar tenant
  "REPORT_VIEW"       // Pode ver relatórios
}
```

**Sistema de Código**: `MODULE_ACTION`
- Convenção consistente
- Extraível para filtros (ex: "Todas as permissões do módulo USER")
- Agnóstico a camadas

**Por que independente de roles?**
- Roles definem perfis padrão (ADMIN = todos as permissões)
- Permissões diretas permitem customização (revoke específico, grant exceções)
- Não preso a estrutura de roles

#### **Invariantes (Regras de Negócio)**
1. Todo usuário ativo must ter pelo menos 1 role
2. Email é único por tenant
3. Senha ativa é sempre hash (nunca texto plano)
4. Usuário inativo/deletado não pode ter role/permissão atribuída
5. Não pode remover GUEST se for a única role

---

## 📊 Observability & Auditoria

### **Estratégia de Observability**

Observability é a capacidade de entender o estado interno do sistema observando seus outputs (logs, eventos, métricas). No ERP Cloud, implementamos via camada de **rastreamento de fluxos (Flow Tracking)** com a biblioteca `com.dros.observability`.

#### **Arquitetura**

```
┌─────────────────────────────────────────────┐
│   Method com @TrackFlow                     │
│   @TrackFlow("CREATE_USER")                 │
│   public User criar(CreateUserRequest req)  │
└──────────────────┬──────────────────────────┘
                   │ Intercepta via AOP
                   ↓
┌─────────────────────────────────────────────┐
│   Aspect (Automatic)                        │
│   Captura: entrada, saída, exceção          │
│   Mede: tempo de execução                   │
│   Rastreia: tenantId, userId, traceId       │
└──────────────────┬──────────────────────────┘
                   │ Publica para múltiplos destinos
         ┌─────────┼──────────┬────────────┐
         ↓         ↓          ↓            ↓
    Database   WebSocket    Kafka       Logs
  (auditoria) (real-time) (analytics) (debug)
```

#### **Componentes**

1. **@TrackFlow Annotation** (com.dros.observability)
   - Marca métodos para rastreamento
   - Parâmetro: código único (ex: "CREATE_USER", "SRVC_CREATE_USER")
   - Aplicado em controllers, services, handlers
   - Automático: sem código adicional necessário

2. **Aspect/Interceptor** (automático via pacote dros)
   - Intercepts método anotado antes e depois
   - Cria FlowEvent com detalhes da execução
   - Captura exceções automaticamente
   - Publica para todos os trackers configurados

3. **Trackers** (destinos de eventos)
   - **FlowEventRepository (Database)**: Persistência permanente em `tb_flow_events`
   - **KafkaTracker**: Publicação para sistemas de analytics em tempo real
   - **WebSocketTracker**: Push para UI receber eventos conforme ocorrem
   - **Logs**: Saída em arquivos (backup, debug)

#### **Dados Rastreados por FlowEvent**

```java
FlowEvent {
    id: Long,                     // PK
    traceId: String,              // UUID único da requisição (trace completo)
    stepName: String,              // "CREATE_USER", "SRVC_ACTIVATE_USER"
    status: FlowStatus,             // SUCCESS, FAILURE, PENDING
    executionTimeMs: int,           // Tempo em milissegundos
    tenantId: Long,                 // Isolamento multi-tenant
    userId: Long,                   // Quem executou (opcional, do auth)
    requestPayload: String,         // Request JSON (opcional, configurable)
    responsePayload: String,        // Response JSON (opcional, configurable)
    errorMessage: String,           // Stack trace se falhou
    errorType: String,              // Classe da exceção
    timestamp: Instant,             // Quando ocorreu
}
```

#### **Convenção de Nomenclatura de Flows**

Padrão consistente para rastreabilidade:

```
REST Controller:    [AÇÃO]_[RECURSO]
                    CREATE_USER
                    READ_USER_ME
                    UPDATE_USER_EMAIL
                    DELETE_USER
                    ADD_USER_ROLES
                    LIST_USER_PERMISSIONS
                    APPROVE_USER
                    REJECT_USER

Service/Handler:    SRVC_[AÇÃO]_[RECURSO]
                    SRVC_CREATE_USER
                    SRVC_ACTIVATE_USER
                    SRVC_SEARCH_BY_ID_USER
                    SRVC_ADD_PERMISSION
                    SRVC_REMOVE_ROLE
```

#### **Implementação no User Handler**

```java
@Service
public class CreateUserHandler implements CommandHandler<CreateUserCommand, CreateUserResponse> {
    
    private final UserRepository userRepository;
    private final DomainEventPublisher eventPublisher;

    @Override
    @TrackFlow("SRVC_CREATE_USER")  // ← Adiciona rastreamento automático
    public CreateUserResponse handle(CreateUserCommand command) {
        // Validar email único
        UserEmail email = new UserEmail(command.email());
        if (userRepository.existsByEmailInTenant(command.tenantId(), email)) {
            throw new UserEmailAlreadyExistsException(command.email());
        }

        // Criar usuário
        User user = User.create(
            command.tenantId(),
            new UserName(command.name()),
            email,
            new UserPassword(command.plainPassword())
        );

        // Persistir
        userRepository.save(user);

        // Publicar eventos
        user.pullDomainEvents().forEach(eventPublisher::publish);

        return new CreateUserResponse(
            user.getId().getValue(),
            user.getEmail().getValue(),
            user.getName().getValue(),
            user.getStatus().toString()
        );
        // ← Após retorno, Aspect captura sucesso e publica FlowEvent automaticamente
    }
}
```

#### **Fluxo Completo de uma Requisição**

```
1. POST /api/users → UsuarioController.criar()
   │
2. @TrackFlow("CREATE_USER") intercepta
   ├─ Cria FlowEvent inicial
   ├─ Inicia timer de execução
   │
3. Controlador valida entrada
   │
4. Chama CreateUserHandler
   │
5. @TrackFlow("SRVC_CREATE_USER") intercepta
   ├─ Cria FlowEvent child (rastreio hierárquico)
   │
6. CreateUserHandler.handle()
   ├─ User.create() (domínio puro)
   ├─ userRepository.save() (persistência)
   ├─ eventPublisher.publish() (domínio events)
   │
7. Retorna CreateUserResponse
   │
8. Aspect do handler captura resultado
   ├─ Execução: 125ms
   ├─ Status: SUCCESS
   ├─ Preserva payloads
   │
9. FlowEvent publicado para:
   ├─ FlowEventRepository (banco)
   ├─ KafkaTracker (analytics em tempo real)
   ├─ WebSocketTracker (UI sabe imediatamente)
   └─ Logs (arquivo de debug)

Resultado: Auditoria completa e rastreável. Possível reconstruir exação requisição.
```

#### **Casos de Uso Práticos**

1. **Auditoria Regulatória (LGPD, GDPR)**
   - Quem criou/modificou cada usuário e quando
   - Rastreio completo para compliance
   - Retenção configurável por tenant

2. **Debug de Bugs em Produção**
   - `GET /api/observability/events/{traceId}` recupera toda a cadeia
   - Ver exatamente qual step falhou
   - Stack trace capturado automaticamente

3. **Performance Monitoring**
   - Medir tempo por endpoint: `GET /api/observability/stats`
   - Identificar gargalos: qual step demorou?
   - Alertar se tempo > SLA configurado

4. **Real-time Dashboards**
   - WebSocket envia eventos conforme ocorrem
   - UI mostra requisições in-flight
   - Notificar admins de operações críticas (DELETE_USER, etc)

5. **Analytics & Business Intelligence**
   - Kafka publica para Elasticsearch/Datadog
   - Entender padrões de uso (quem, o quê, quando)
   - Gerar relatórios de atividade por tenant

#### **Por que Observability vs apenas Logs?**

| Aspecto | Logs | Observability |
|---------|------|---------------|
| **Estrutura** | String free-form | Estruturado (stepName, status, etc) |
| **Query** | grep/regex (lento) | SQL estruturado, índices rápidos |
| **Real-time** | Delay (tail -f) | WebSocket push imediato |
| **Correlação** | TraceId manual | Automático via biblioteca |
| **Analytics** | Manual, ad-hoc | Integrado com Kafka/BI |
| **Retention** | Expires | Banco dedicado com política |
| **Rastreamento** | Linear | Hierárquico (parent-child flows) |

---

## 🔐 Gestão de Acesso

### **Modelo de Autorização**
```
┌──────────────────────┐
│   Usuário (User)     │
│   ├─ Roles           │  ← Perfis padrão (ADMIN, MANAGER, etc)
│   └─ Permissions     │  ← Permissões específicas (diretas)
└──────────────────────┘
         │
         ↓
┌──────────────────────┐
│  hasPermission()     │  ← Verifica se tem permissão
│  hasRole()           │  ← Verifica se tem role
│  hasPermissionByCode()│  ← Verifica por código string
└──────────────────────┘
```

### **Resolução de Autorização**

1. **Por Role**: `user.hasRole(RoleType.ADMIN)`
   - Rápido, verificação simples
   - Útil para lógicas amplas (ex: "Só admins podem deletar")

2. **Por Permissão**: `user.hasPermission(permission)`
   - Granular, controle fino
   - Útil para lógicas específicas

3. **Por Código**: `user.hasPermissionByCode("USER_CREATE")`
   - String-based, agnóstico a implementação
   - Útil para decoradores/filtros REST

### **Decisão: Por que não usar Roles vinculadas a Permissões?**
```
❌ Anti-padrão:
   Role ADMIN {
     permissions: [USER_CREATE, USER_EDIT, USER_DELETE, ...]
   }
   // Problema: Não pode conceder apenas USER_CREATE a alguém sem conceder ADMIN inteiro

✅ Abordagem correta:
   user.roles = {ADMIN, MANAGER}          // Perfis amplos
   user.permissions = {USER_EDIT_OTHER}   // Exceções/customizações
   // Resolve por: roles baseado ou permissions diretas
```

---

## 🏢 Multi-Tenancy

### **Estratégia de Isolamento**

#### **Isolamento em Banco de Dados**
```
Tabela: tb_user
├─ id: 1
├─ tenant_id: 100  ← Sempre presente
├─ email: "joao@empresa.com"
└─ ...

Tabela: tb_tenant
├─ id: 100
├─ nome: "Empresa A"
└─ ...
```

**Implementação**:
- Todo aggregado carrega `tenantId`
- Queries sempre filtram por `tenantId` (segurança em camada)
- Repository valida tenant ante de operar

#### **Isolamento Lógico**
- Usuários de tenant A não podem ver/modificar dados de tenant B
- Validação em múltiplas camadas (repository, handler, domain)

#### **Por que Multi-Tenant?**
1. **Custo-benefício**: Uma única instância serve múltiplos clientes
2. **Escalabilidade**: Fácil onboarding de novos clientes
3. **Manutenção**: Uma codebase para múltiplos clientes
4. **Isolamento**: Dados completamente separados logicamente

---

## 💡 Decisões de Projeto

### **Decisão 1: Mapper Pattern vs BaseEntity**

| Aspecto | Mapper | BaseEntity |
|---------|--------|-----------|
| **Acoplamento** | Domain desacoplado | Domain acoplado a JPA |
| **Testabilidade** | Domain testado puro | Precisa de contexto JPA |
| **Manutenção** | Claro, separação visível | Implícito, menos óbvio |
| **Duplic. Código** | Mais linhas (mapper) | Menos linhas |

**Escolha**: Mapper Pattern

**Motivo**: Mantém domínio puro e agnóstico. Complexidade de mapper é aceitável.

### **Decisão 2: Múltiplas Roles vs Hierarquia**

```
❌ Hierarquia:
   GUEST → USER → MANAGER → ADMIN
   // Usuário herda todas permissões de níveis inferiores

✅ Múltiplas Roles:
   user = {ADMIN, MANAGER}
   // Composição explícita
```

**Motivo**: Mais flexível, suporta cross-cutting roles (ADMIN de um módulo, MANAGER de outro)

### **Decisão 3: Permission Codes vs Enums**

```
❌ Enums (pode ficar desincronizado):
   enum Permission {
     USER_CREATE, USER_READ, ...
   }

✅ Strings validadas (extensível):
   permission = "USER_CREATE"
   // Validação: uppercase + underscore, max 50 chars
```

**Motivo**: Permite adicionar permissões sem recompile. Agnóstico a código.

### **Decisão 4: Event Sourcing**

```
Evento:  UserCreatedEvent(userId, email, tenantId, occurredOn)
         UserActivatedEvent(userId, occurredOn)
         UserRoleAddedEvent(userId, roleType, occurredOn)
```

**Por que?**
1. Auditoria nativa (quem fez quê, quando)
2. Reconstruir estado em qualquer ponto
3. Base para notificações/integrações assíncronas
4. Debugging facilitado (replay eventos)

---

## ⚠️ Desvios Identificados

### **1. Acoplamento à Camada de Persistência**

**Documentado**: 
- Domain layer puro (sem @Entity, sem @Column)
- Mapper Pattern para separação User.java ↔ UserEntity.java

**Real**:
- Aggregados com @Entity direto (ex: `Usuario.java` em v1/features/usuario/domain/entity/)
- Sem camada Domain separada; domain e persistência acoplados
- Model em domain/entity/ com anotações JPA

**Impacto**: 
- Domínio não é testável sem contexto JPA
- Difícil migrar entre tecnologias de persistência
- Violação de Clean Architecture

**Recomendação**: 
- Refatorar Usuario.java para remover @Entity, @Column, @OneToMany, etc
- Criar Usuario.java (puro) e UsuarioEntity.java (JPA)
- Implementar UsuarioMapper.java para conversão

---

### **2. Estrutura de Pacotes Diferente**

**Documentado**:
```
/src/main/java/com/api/erp/core/
├── domain/
│   ├── aggregates/user/
│   ├── valueobjects/user/
│   ├── repositories/
│   └── events/
├── application/
│   └── user/handlers/
└── infrastructure/
```

**Real**:
```
/src/main/java/com/api/erp/v1/
├── features/usuario/
│   ├── domain/
│   │   ├── entity/     (com @Entity - acoplado ⚠️)
│   │   ├── controller/ (em domain? deveria estar em presentation ⚠️)
│   │   ├── service/
│   │   ├── repository/
│   │   └── validator/
│   ├── application/
│   ├── infrastructure/
│   └── presentation/
├── tenant/
├── shared/
└── observability/
```

**Problemas**:
- `/core/` não existe; refatoração para `/v1/features/` não foi documentada
- Controladores em `domain/controller/` (deveria estar em `presentation/`)
- Nomenclatura em português (Usuario) vs English (User) em CLOUD.md
- Pasta `/core/` documentada está vazia no projeto real

---

### **3. Value Objects**

**Documentado**:
- `UserId`, `UserEmail`, `UserPassword`, `UserRole`, `UserPermission` como Value Objects imutáveis
- Validação encapsulada no construtor

**Real**:
- `UsuarioRole` e `UsuarioPermissao` são @Entity (não são Value Objects)
- `Email` e `CPF` corretamente implementados como Value Objects
- `UserPassword` / senha armazenada como String em `senha_hash` (sem Value Object)
- UserName não existe; apenas `nome_completo` como String

**Impacto**: Lógica de validação espalhada, menos type-safety

---

### **4. Domain Events**

**Documentado**:
- `UserCreatedEvent`, `UserActivatedEvent`, `UserRoleAddedEvent`
- Event Sourcing implementado
- Eventos publicados via DomainEventPublisher

**Real**:
- Não há implementação visível de Domain Events
- Domínio não publica eventos de negócio
- Event Sourcing não foi implementado
- FlowEvent em observability (rastreamento técnico, não de domínio)

**Impacto**: Sem base para notificações assíncronas, auditoria limitada

---

### **5. Handlers CQRS**

**Documentado**:
- `CreateUserHandler`, `ActivateUserHandler`, `ChangeUserEmailHandler`
- CommandHandler<Command, Response> pattern
- @TrackFlow para rastreamento automático

**Real**:
- Lógica em Services (v1/features/usuario/domain/service/)
- Não há Handlers/Commands explícitos
- @TrackFlow pode estar implementado em observability
- Controladores chamam services diretamente



---

## 🧩 Componentes Principais

### **User Module (Núcleo de Autenticação/Identidade)**

#### **Camadas**
1. **Domain** (User aggregate, Value Objects, Events)
   - UserStatus, UserRole, UserPermission
   - Métodos: activate(), changeEmail(), addRole(), grantPermission()

2. **Application** (Handlers, Commands)
   - CreateUserHandler: Cria novo usuário
   - ActivateUserHandler: Ativa usuário
   - ChangeUserEmailHandler: Muda email
   - ChangeUserPasswordHandler: Muda senha

3. **Infrastructure** (Persistence)
   - UserEntity (JPA mapping)
   - UserMapper (conversão Domain ↔ Entity)
   - UserRepository (acesso a dados)

4. **REST** (Controllers, DTOs - a implementar conforme necessário)

#### **Fluxo de Criação de Usuário**
```
REST Controller
  ↓
CreateUserCommand (CQRS command)
  ↓
CreateUserHandler (orquestração)
  ↓
User.create() (factory, domínio puro)
  ↓
UserRepository.save() (persistência)
  ↓
eventPublisher.publish() (eventos de domínio)
```

### **Tenant Module (Multi-Tenancy)**

#### **Responsabilidades**
- Isolamento de dados por tenant
- Validação de tenant ativo
- Associação de usuários a tenants

#### **Padrão de Integração**
Todos os agregados implementam `TenantAware`:
```
interface TenantAware {
    Long getTenantId();
    void setTenantId(Long tenantId);
}
```

---

## 🔄 Fluxos Críticos

### **Fluxo 1: Registrar Novo Usuário**
```
1. Cliente chama POST /users
   ├─ Validação: email formato, senha força
   
2. CreateUserCommand criado
   
3. CreateUserHandler.handle()
   ├─ Verifica email único no tenant
   ├─ Chama User.create()
   │  └─ Gera UserCreatedEvent
   ├─ Salva no repository
   └─ Publica eventos (possibilita envio de email, etc)
   
4. Usuário vira INACTIVE (aguardando confirmação email)

5. Após confirmação: ActivateUserHandler
   ├─ Altera status para ACTIVE
   └─ Gera UserActivatedEvent
```

### **Fluxo 2: Atribuir Múltiplas Roles**

```
Comando: "Faça João ser ADMIN e MANAGER ao mesmo tempo"

1. Handler carrega usuário
2. user.addRole(new UserRole(ADMIN))
   ├─ Valida: usuário ativo?
   ├─ Role já existe?
   ├─ Adiciona à set
   └─ Gera UserRoleAddedEvent
3. user.addRole(new UserRole(MANAGER))
   └─ Repete processo
4. Salva agregado
5. Publica ambos eventos
```

### **Fluxo 3: Verificar Autorização**

```
Controlador: "Pode João executar USER_CREATE?"

1. Carrega usuário João
2. Chama: user.hasPermissionByCode("USER_CREATE")
3. Verifica em user.permissions
4. Se não encontrar, poderia estender para:
   └─ Verificar se role ADMIN confere permissão
5. Retorna boolean
```

---

## 📝 Notas Importantes

### **Padrão de Nomeação Ideal (Documentado)**
- **Commands**: `CreateUserCommand`, `ActivateUserCommand`
- **Handlers**: `CreateUserHandler`, `ActivateUserHandler`
- **Value Objects**: `UserId`, `UserEmail`, `UserName`
- **Aggregates**: `User`, `Tenant`, `Product`
- **Events**: `UserCreatedEvent`, `UserRoleAddedEvent`

### **Padrão de Nomeação Real (Implementado)**
- **Commands**: Não explícitos; lógica em services
- **Handlers**: Não existem; Services fazem orquestração
- **Value Objects**: `Email`, `CPF` (parcial)
- **Aggregates**: `Usuario`, `Tenant`, `Produto` (em português)
- **Events**: FlowEvent (técnico) vs Domain Events (não implementados)

### **Validação em Camadas**
```
REST (formato, range) → Handler (regras app) → Domain (invariantes)
```

Cada camada valida seu escopo sem duplicar.

### **Soft Delete**
Usuários deletados mantêm todos os dados para:
- Auditoria (rastrear quem foi deletado)
- Relacionamentos (referência histórica)
- Recuperação futura

### **Criptografia de Senha**
Nunca done de forma reversível. Apenas hash para verificação.

---

## 🚀 Roadmap Arquitetural

### **Fase 1 (Em Progresso)**: Core User Module
- [x] User aggregate (implementado como Usuario.java com @Entity acoplado ⚠️)
- [x] Multi-role support (UsuarioRole como @Entity)
- [x] Independent permissions (UsuarioPermissao como @Entity)
- [ ] Domain events (não implementado)
- [x] REST endpoints (implementado em v1/features/usuario/presentation/)
- ⚠️ [x] Value Objects (parcial: Email, CPF implementados; UserPassword não)

**Status**: ~70% - Funcionalidade básica implementada, mas sem padrões DDD/Clean Architecture rigorosos

---

### **Fase 2 (80% Concluída)**: Extensões
- [x] Product module (v1/features/produto/ - ✅ implementado)
- [x] Customer module (v1/features/cliente/ - ✅ implementado)
- [x] Custom fields (v1/features/customfield/ - ✅ parcialmente implementado)
- [x] Audit logging / Observability (v1/observability/ - ✅ implementado)

**Status**: ~80% - Módulos existem, mas sem integração completa com observability

---

### **Fase 3 (20% Concluída)**: Escalabilidade
- [ ] Event bus assíncrono (RabbitMQ, Kafka) - ❌ não implementado
- [ ] CQRS read model (views otimizadas) - ❌ não implementado
- [ ] Cache distribuído - ❌ não implementado
- [ ] Replicação de dados - ❌ não implementado
- [x] Multi-tenancy support (v1/tenant/ - ✅ implementado)

**Status**: ~20% - Apenas multi-tenancy implementado

---

## 📚 Referências Internas

### **Estrutura Documentada (Ideal - /core/)**
- **User Aggregate**: `/src/main/java/com/api/erp/core/domain/aggregates/user/` ❌ VAZIO
- **Value Objects**: `/src/main/java/com/api/erp/core/domain/valueobjects/user/` ❌ VAZIO
- **Handlers**: `/src/main/java/com/api/erp/core/application/user/handlers/` ❌ VAZIO
- **Repository**: `/src/main/java/com/api/erp/core/domain/repositories/user/` ❌ VAZIO
- **Tests**: `/src/test/java/com/api/erp/core/domain/user/` ❌ VAZIO

### **Estrutura Real (Implementada - /v1/features/)**
- **User Aggregate**: `/src/main/java/com/api/erp/v1/features/usuario/domain/entity/Usuario.java` ⚠️ com @Entity acoplado
- **Value Objects**: `/src/main/java/com/api/erp/v1/shared/domain/valueobject/` (Email.java, CPF.java, NCM.java, etc)
- **Services**: `/src/main/java/com/api/erp/v1/features/usuario/domain/service/`
- **Repository**: `/src/main/java/com/api/erp/v1/features/usuario/domain/repository/`
- **Presentation**: `/src/main/java/com/api/erp/v1/features/usuario/presentation/controller/`
- **Tests**: `/src/test/java/com/api/erp/v1/features/usuario/`

### **Outros Módulos Implementados em /v1/features/**
- **Tenant (Multi-tenancy)**: `/src/main/java/com/api/erp/v1/tenant/` ✅
- **Observability**: `/src/main/java/com/api/erp/v1/observability/` ✅
- **Produto**: `/src/main/java/com/api/erp/v1/features/produto/` ✅
- **Cliente**: `/src/main/java/com/api/erp/v1/features/cliente/` ✅
- **Endereco**: `/src/main/java/com/api/erp/v1/features/endereco/` ✅
- **Permissão**: `/src/main/java/com/api/erp/v1/features/permissao/` ✅
- **Unidade de Medida**: `/src/main/java/com/api/erp/v1/features/unidademedida/` ✅

---

## 🔧 Ações Recomendadas para Alinhar com Arquitetura

Para alinhar a implementação com a arquitetura documentada:

### **Prioridade 1 (Alta)**: 
- ✅ Separar Domain puro de persistência (remover @Entity de Usuario.java)
- ✅ Implementar Mapper Pattern corretamente (Usuario.java puro + UsuarioEntity.java + UsuarioMapper.java)
- ✅ Criar handlers CQRS explícitos (CreateUserHandler, ActivateUserHandler, etc)

### **Prioridade 2 (Média)**:
- ✅ Implementar Domain Events (UserCreatedEvent, UserActivatedEvent, UserRoleAddedEvent)
- ✅ Adicionar @TrackFlow em handlers críticos
- ✅ Documentar Value Objects como imutáveis e testáveis

### **Prioridade 3 (Baixa)**:
- Event Sourcing completo
- Event bus assíncrono (RabbitMQ, Kafka)
- CQRS read model otimizado

---

**Esta documentação é um documento vivo. Atualize à medida que novas decisões são tomadas e padrões evoluem.**
