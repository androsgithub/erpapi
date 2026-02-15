# 🏗️ Proposta de Reorganização de Arquitetura - ERPAPI

## 📋 Resumo Executivo

Esta proposta reorganiza o projeto seguindo princípios de **DDD (Domain-Driven Design)** com **Clean Architecture** e **modularização por features**, mantendo total compatibilidade com as configurações Spring Boot existentes.

---

## ⚠️ Configurações Críticas a Preservar

### 1. **ErpApplication** (com.api.erp.ErpApplication)
```java
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableAsync
```
> ✅ **Não será alterada** - ComponentScan padrão do @SpringBootApplication escaneia tudo sob `com.api.erp`

### 2. **FeaturesRepositoriesConfig**
```java
@EnableJpaRepositories(
    basePackages = {
        "com.api.erp.v1.features.*.domain.repository",
        // ... lista explícita
    },
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager"
)
```
> ⚠️ **AJUSTE NECESSÁRIO**: Haverá mudança no path, mas continuará funcionando com basePackages específicos

### 3. **TenantsMasterRepositoriesConfig**
```java
@EnableJpaRepositories(
    basePackages = {"com.api.erp.v1.tenant.domain.repository"},
    entityManagerFactoryRef = "masterEntityManagerFactory",
    transactionManagerRef = "masterTransactionManager"
)
```
> ✅ **Sem alterações** - Path permanece igual

### 4. **Flyway**
```yaml
flyway:
  locations: classpath:db/migration/master
```
> ✅ **Sem alterações** - Flyway escaneia subdiretórios automaticamente

---

## 📁 Estrutura de Diretórios Proposta

```
src/main/java/com/api/erp/
│
├── ErpApplication.java              ← Classe principal (sem mudança)
│
├── v1/
│   │
│   ├── config/                      ← ⭐ Configurações globais
│   │   ├── aspects/
│   │   │   ├── AOP principal.java
│   │   │   └── Aspects específicos (ex: LoggingAspect)
│   │   │
│   │   ├── database/
│   │   │   ├── DataSourceConfig.java
│   │   │   └── JpaConfig.java
│   │   │
│   │   ├── security/
│   │   │   ├── SecurityConfig.java
│   │   │   ├── JwtFilter.java
│   │   │   └── AuthenticationConfig.java
│   │   │
│   │   ├── swagger/
│   │   │   ├── SwaggerConfig.java
│   │   │   └── OpenAPIConfig.java
│   │   │
│   │   ├── web/
│   │   │   ├── WebConfig.java
│   │   │   ├── CorsConfig.java
│   │   │   └── ExceptionHandler.java
│   │   │
│   │   └── aop/
│   │       ├── LoggingAspect.java
│   │       └── TransactionAspect.java
│   │
│   ├── features/                    ← ⭐ Features por domínio (modular)
│   │   │
│   │   ├── produto/
│   │   │   ├── config/              ← Config específica (se houver)
│   │   │   ├── application/
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   └── response/
│   │   │   │   └── mapper/
│   │   │   ├── domain/
│   │   │   │   ├── entity/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── validator/
│   │   │   │   └── controller/        ← (Port/Interface)
│   │   │   ├── infrastructure/
│   │   │   │   ├── service/
│   │   │   │   ├── validator/
│   │   │   │   ├── decorator/
│   │   │   │   └── proxy/
│   │   │   └── presentation/
│   │   │       └── controller/
│   │   │
│   │   ├── cliente/
│   │   │   ├── config/              ← Config específica (se houver)
│   │   │   ├── application/
│   │   │   ├── domain/
│   │   │   ├── infrastructure/
│   │   │   ├── presentation/
│   │   │   └── tenants/             ← Customizações específicas de tenant
│   │   │       └── hece/
│   │   │           └── validator/
│   │   │
│   │   ├── fornecedor/
│   │   │   ├── config/
│   │   │   ├── application/
│   │   │   ├── domain/
│   │   │   ├── infrastructure/
│   │   │   └── presentation/
│   │   │
│   │   └── [outras features...]
│   │
│   ├── tenant/                      ← ⭐ Configuração multi-tenant
│   │   ├── application/
│   │   │   └── dto/
│   │   │
│   │   ├── domain/
│   │   │   ├── entity/              ← Entidade Tenant
│   │   │   ├── service/
│   │   │   └── repository/          ← TenantRepository (master database)
│   │   │
│   │   ├── infrastructure/
│   │   │   ├── config/              ← 🔴 CRÍTICO
│   │   │   │   ├── TenantsMasterDatabaseConfig.java
│   │   │   │   ├── TenantsMasterRepositoriesConfig.java ← @EnableJpaRepositories (master)
│   │   │   │   ├── TenantsConfiguration.java
│   │   │   │   ├── FeaturesRepositoriesConfig.java      ← @EnableJpaRepositories (features)
│   │   │   │   └── TenantsMultitenantDataSourceConfig.java
│   │   │   │
│   │   │   ├── datasource/
│   │   │   │   ├── manual/
│   │   │   │   │   ├── TenantResolutionStrategy.java
│   │   │   │   │   ├── MultiTenantRoutingDataSource.java
│   │   │   │   │   └── TenantContextHolder.java
│   │   │   │   └── dynamic/
│   │   │   │
│   │   ├── interceptor/
│   │   │   ├── TenantInterceptor.java
│   │   │   └── TenantResolverInterceptor.java
│   │   │
│   │   └── presentation/
│   │       ├── controller/
│   │       └── dto/
│   │
│   ├── shared/                      ← ⭐ Código compartilhado
│   │   │
│   │   ├── core/                    ← Coisas realmente globais
│   │   │   ├── domain/
│   │   │   │   ├── exceptions/
│   │   │   │   ├── events/
│   │   │   │   ├── value-objects/
│   │   │   │   └── base/             ← BaseEntity, BaseRepository, etc
│   │   │   │
│   │   │   ├── infrastructure/
│   │   │   │   ├── persistence/      ← Base repositórios, especificações
│   │   │   │   ├── context/
│   │   │   │   └── utils/
│   │   │   │
│   │   │   └── presentation/
│   │   │       ├── response/         ← APIResponse padrão
│   │   │       ├── dto/
│   │   │       └── controller/       ← BaseController
│   │   │
│   │   └── features/                ← Features compartilhadas entre todos os tenants
│   │       │
│   │       ├── endereco/            ← Compartilhada (pode ser usada em cliente, fornecedor, etc)
│   │       │   ├── application/
│   │       │   ├── domain/
│   │       │   ├── infrastructure/
│   │       │   └── presentation/
│   │       │
│   │       ├── contato/             ← Compartilhada
│   │       │   ├── application/
│   │       │   ├── domain/
│   │       │   ├── infrastructure/
│   │       │   └── presentation/
│   │       │
│   │       ├── permissao/           ← Compartilhada
│   │       │   ├── application/
│   │       │   ├── domain/
│   │       │   ├── infrastructure/
│   │       │   └── presentation/
│   │       │
│   │       └── unidademedida/       ← Compartilhada
│   │           ├── application/
│   │           ├── domain/
│   │           ├── infrastructure/
│   │           └── presentation/
│   │
│   ├── docs/                        ← ⭐ Documentação técnica e OpenAPI
│   │   ├── config/                  ← SwaggerConfig pode ficar aqui ou em config/swagger
│   │   │   └── OpenAPIConfiguration.java
│   │   │
│   │   ├── api/                     ← Documentação das APIs
│   │   │   ├── produto.md
│   │   │   ├── cliente.md
│   │   │   └── ...
│   │   │
│   │   └── technical/               ← Diagramas, arquitetura, decisões
│   │       ├── ARCHITECTURE.md
│   │       ├── DESIGN_DECISIONS.md
│   │       └── DEPLOYMENT.md
│   │
│   └── observability/               ⭐ Logging, métricas, tracing
│       ├── application/
│       ├── config/
│       ├── domain/
│       ├── presentation/
│       └── strategy/
│
└── resources/
    ├── application.yml
    ├── db/
    │   └── migration/
    │       ├── master/               ← Migrações do banco master
    │       │   ├── V1__*.sql
    │       │   └── ...
    │       ├── observability/        ← Migrações do erpapi_logs
    │       │   ├── V1__*.sql
    │       │   └── ...
    │       ├── taxengine/            ← Migrações do erpapi_tax
    │       │   └── ...
    │       └── tenant/               ← Migrações dos bancos de tenants (scripts template)
    │           └── ...
    └── ...
```

---

## 🎯 Cada Camada - Responsabilidades

### **1. Config** - Configurações Globais
- ✅ Beans que servem toda a aplicação
- ✅ Aspectos (AOP), Security, Database, Swagger
- ✅ Sem lógica de negócio
- ✅ Sem Dependencies de features específicas

### **2. Features** - Modulo por Domínio
Cada feature é **independente** e segue a estrutura:

```
feature-name/
├── config/              ← Configs APENAS dessa feature (ex: validadores bean, cachés)
├── application/         ← Use cases, DTOs, Mappers
├── domain/              ← Entidades, Services, Repositórios (interfaces)
├── infrastructure/      ← Implementações, Decorators, Proxies
└── presentation/        ← Controllers, Respostas
```

**Padrão cada Feature:**
- `application/`: Coisas que vêm da aplicação (recebem DTOs, chamam domain services)
- `domain/`: Lógica de negócio PURA
- `infrastructure/`: Implementações técnicas (cache, decorators, services infra)
- `presentation/`: Controllers REST

### **3. Tenant** - Multi-Tenancy
Contém:
- ✅ @EnableJpaRepositories para master database (TB_TENANT)
- ✅ Resolução de tenant
- ✅ MultiTenantRoutingDataSource
- ✅ TenantContextHolder
- ✅ Interceptores de tenant

**Crítico**: Manter `TenantsMasterRepositoriesConfig` com @EnableJpaRepositories apontando para `com.api.erp.v1.tenant.domain.repository`

### **4. Shared** - Código Compartilhado
Dividido em:

**a) shared/core** - Realmente global
- Exceptions
- Domain Events
- Base classes (BaseEntity, BaseRepository)
- Value Objects comuns
- Response padrão

**b) shared/features** - Features reutilizáveis
- Endereco
- Contato
- Permissão
- UnidadeMedida

> Essas features podem ser usadas por **qualquer tenant** e executam em **TenantDynamicDataSource**

### **5. Docs** - Documentação
- OpenAPI/Swagger config
- Documentação por feature
- Diagramas técnicos
- Decisões arquiteturais

### **6. Observability** - Observabilidade
- Logging, métricas, tracing
- Já separada atualmente ✅

---

## 🔄 Impacto nas Configurações

### **ErpApplication.java**
```java
@SpringBootApplication  // Escaneia tudo sob com.api.erp - ✅ SEM MUDANÇA
@EnableAspectJAutoProxy // ✅ SEM MUDANÇA
@EnableAsync            // ✅ SEM MUDANÇA
public class ErpApplication {
    // sem mudanças
}
```

### **TenantsMasterRepositoriesConfig.java**
```java
@Configuration
@EnableJpaRepositories(
    basePackages = {"com.api.erp.v1.tenant.domain.repository"},  // ✅ SEM MUDANÇA
    entityManagerFactoryRef = "masterEntityManagerFactory",
    transactionManagerRef = "masterTransactionManager"
)
public class TenantsMasterRepositoriesConfig {
}
```

### **FeaturesRepositoriesConfig.java**
```java
@Configuration
@EnableJpaRepositories(
    basePackages = {
        "com.api.erp.v1.features.endereco.domain.repository",
        "com.api.erp.v1.features.cliente.domain.repository",
        "com.api.erp.v1.features.contato.domain.repository",
        "com.api.erp.v1.features.usuario.domain.repository",
        "com.api.erp.v1.features.permissao.domain.repository",
        "com.api.erp.v1.features.produto.domain.repository",
        "com.api.erp.v1.features.customfield.domain.repository",
        "com.api.erp.v1.features.unidademedida.domain.repository",
        "com.api.erp.v1.features.fornecedor.domain.repository",  // NOVO
        "com.api.erp.v1.features.personalizado.domain.repository", // NOVO
    },
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager"
)
public class FeaturesRepositoriesConfig {
}
```
> ⚠️ **ÚNICA MUDANÇA**: Adicionar features novas se criadas fora de `/features`

### **application.yml**
```yaml
spring:
  flyway:
    locations: classpath:db/migration/master  # ✅ SEM MUDANÇA
```

---

## 📋 Passo a Passo da Migração

### **Fase 1: Preparar (sem mover nada)**
1. ☐ Criar nova estrutura de diretórios vazia
2. ☐ Revisar todas as dependências entre packages
3. ☐ Documentar customizações por tenant

### **Fase 2: Mover incrementalmente**
1. ☐ Mover `config/` completo (baixo risco)
2. ☐ Mover features uma por uma, testando @EnableJpaRepositories
3. ☐ Validar Aspects funcionando
4. ☐ Validar Flyway migrando

### **Fase 3: Reorganizar compartilhadas**
1. ☐ Mover Endereco, Contato, Permissão para `shared/features/`
2. ☐ Atualizar imports em features que usam estas
3. ☐ Testar integração

### **Fase 4: Validar**
1. ☐ Rodar todos os testes
2. ☐ Validar AspectJ interceptando corretamente
3. ☐ Validar Flyway executando
4. ☐ Validar multi-tenant funcionando

---

## 🚀 Benefícios desta Arquitetura

| Aspecto | Benefício |
|--------|-----------|
| **Escalabilidade** | Adicionar nova feature = copiar template, não afeta outros |
| **Modularidade** | Features independentes, baixo acoplamento |
| **Testabilidade** | Cada feature pode ser testada isoladamente |
| **Performance** | Lazy loading de features, imports claros |
| **Maintenance** | Fácil encontrar código (feature → camada → classe) |
| **DDD** | Alinhado com Domain-Driven Design |
| **Multi-tenancy** | Separação clara entre master, features e tenant |
| **Observability** | Logging/rastreamento isolado, sem poluir features |

---

## ⚠️ Checklist de Compatibilidade

- ✅ ErpApplication.java: Sem mudanças obrigatórias
- ✅ @SpringBootApplication: Escaneia tudo sob `com.api.erp`
- ✅ TenantsMasterRepositoriesConfig: Path `com.api.erp.v1.tenant.domain.repository` preservado
- ✅ FeaturesRepositoriesConfig: Basepackages de features preservados (apenas estrutura dentro muda)
- ✅ Flyway: Locations `classpath:db/migration/master` funciona com subdirs
- ✅ Aspects: Continuam funcionando (escopo global)
- ✅ Security: Continua em config/security

---

## 📞 Próximos Passos

1. **Revisar** esta proposta com a equipe
2. **Criar padrão** para novas features (template)
3. **Executar migração** de forma incremental (feature por feature)
4. **Validar** cada fase (testes, build, inicialização)
5. **Documentar** padrões de cada feature no README

