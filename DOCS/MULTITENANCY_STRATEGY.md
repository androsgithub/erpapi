# Multi-Tenancy Strategy - Database per Tenant + Row-based Discrimination

## 📋 Visão Geral

Sistema de **Multi-Tenancy** com suporte a duas estratégias:

### 1️⃣ **DATABASE per TENANT** (tenantSlug)
- Cada **cliente/tenant** pode ter seu **próprio banco de dados**
- Exemplo:
  - JAGUAR → banco `jaguar_db` no host1
  - HECE → banco `hece_db` no host2
  - ACME → banco `acme_db` no host1

**Vantagens:**
- ✅ Isolamento total de dados
- ✅ Performance dedicada
- ✅ Backup e recovery independentes

### 2️⃣ **ROW-based DISCRIMINATION** (tenantId)
- Dentro do mesmo banco, múltiplos tenants separados por `tenantId`
- Exemplo:
  - JAGUAR Matriz → tenantId=1 (banco: `jaguar_db`)
  - JAGUAR Filial SP → tenantId=2 (banco: `jaguar_db`)
  - JAGUAR Filial RJ → tenantId=3 (banco: `jaguar_db`)

**Vantagens:**
- ✅ Economia de recursos (menos bancos)
- ✅ Matriz e filiais compartilham dados facilmente
- ✅ Filtros automáticos via Hibernate

---

## 🏗️ Arquitetura Implementada

```
┌─────────────────────────────────────────────────────────┐
│                    REQUISIÇÃO HTTP                       │
│          Headers: Authorization: Bearer JWT              │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│          TenantRequestFilter (Servlet Filter)            │
│  - Extrai tenantSlug do JWT                             │
│  - Extrai tenantId do JWT                               │
│  - Armazena em TenantContext (ThreadLocal)              │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│       MultiTenantRoutingDataSource                       │
│  - Consulta TenantContext.getTenantSlug()               │
│  - Busca DataSource correspondente em cache             │
│  - Se não encontrado, carrega do repositório            │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│       DataSourceFactory                                  │
│  - Consulta TenantDatasourceRepository                  │
│  - Obtém credenciais de banco (host, port, db, etc)    │
│  - Cria HikariCP DataSource dinamicamente              │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│    Banco de Dados do Tenant (Específico)                │
│  Ex: jaguar_db ou hece_db                              │
│  ┌─────────────────────────────────────────┐           │
│  │  cliente (tenantId = 1)                  │           │
│  │  cliente (tenantId = 2)  ← Auto-filtrado │           │
│  │  cliente (tenantId = 3)                  │           │
│  └─────────────────────────────────────────┘           │
└─────────────────────────────────────────────────────────┘
```

---

## 📁 Arquivos Criados/Modificados

### Entidades
- ✅ `TenantDatasource.java` - Configuração de datasource no banco master
- ✅ `TenantAwareBaseEntity.java` - Base class com suporte a tenantId

### Configuração
- ✅ `MultiTenantRoutingDataSource.java` - Roteia para banco correto
- ✅ `DataSourceFactory.java` - Cria DataSources dinamicamente
- ✅ `TenantsConfiguration.java` - Configura Hibernate multitenancy
- ✅ `TenantRequestFilter.java` - Extrai tenantSlug/tenantId da requisição

### Repositórios
- ✅ `TenantDatasourceRepository.java` - Acesso a configurações

### Serviços
- ✅ `TenantSchemaService.java` - Lógica de gerenciamento de datasources

### Filtros & Listeners
- ✅ `TenantIdFilterProvider.java` - Provider de filtros Hibernate
- ✅ `HibernateSessionListener.java` - Aplica filtros automaticamente

### Migrations
- ✅ `V002__Add_Tenant_Datasource.sql` - Tabela tenant_datasource
- ✅ `V003__Add_Tenant_Id_Column.sql` - Coluna tenant_id em entidades

---

## 🔄 Fluxo de Requisição (Passo a Passo)

### 1. Cliente faz requisição
```bash
curl -H "Authorization: Bearer eyJhbGc..." \
     http://localhost:8080/api/v1/clientes
```

### 2. JWT contém claims necessários
```json
{
  "tenantSlug": "jaguar",     // Nome único do tenant
  "tenantId": "1",             // ID para discriminação
  "sub": "usuario@jaguar.com"
}
```

### 3. TenantRequestFilter intercepta
- Decodifica JWT
- Extrai `tenantSlug = "jaguar"`
- Extrai `tenantId = "1"`
- Armazena em TenantContext

### 4. MultiTenantRoutingDataSource roteia
- Consulta `TenantContext.getTenantSlug()` → "jaguar"
- Busca DataSource para "jaguar" no cache
- Se não encontrado, carrega via DataSourceFactory

### 5. DataSourceFactory cria conexão
- Consulta `TenantDatasourceRepository.findByTenant_SlugAndIsActive("jaguar", true)`
- Obtém: host=localhost, port=3306, db=jaguar_db, user=jaguar_user
- Cria HikariCP com essas credenciais

### 6. Hibernate executa query
```sql
SELECT c FROM Cliente c WHERE tenantId = :tenantId
-- (filtro aplicado automaticamente via @Filter)
```

### 7. Resposta retorna com dados de "jaguar" apenas

---

## 💻 Exemplos de Uso

### A. Configurar DataSource de um Tenant

**Requisição:**
```bash
POST /api/v1/tenant/database/jaguar/datasource
Authorization: Bearer <token>
Content-Type: application/json

{
  "host": "db-jaguar.company.com",
  "port": 3306,
  "databaseName": "jaguar_db",
  "username": "jaguar_user",
  "password": "jaguar_pass",
  "driverClassName": "com.mysql.cj.jdbc.Driver",
  "dialect": "org.hibernate.dialect.MySQL8Dialect"
}
```

**O que acontece:**
1. TenantSchemaService.configurarDatasource() é chamado
2. Testa conexão com o banco
3. Salva TenantDatasource no banco master
4. Registra DataSource no MultiTenantRoutingDataSource cache
5. Próximas requisições de "jaguar" usam este banco

### B. Obter DataSource Configurado

**Requisição:**
```bash
GET /api/v1/tenant/database/jaguar/datasource
Authorization: Bearer <token>
```

**Resposta:**
```json
{
  "id": 1,
  "empresaId": 1,
  "host": "db-jaguar.company.com",
  "port": 3306,
  "databaseName": "jaguar_db",
  "username": "jaguar_user",
  "driverClassName": "com.mysql.cj.jdbc.Driver",
  "dialect": "org.hibernate.dialect.MySQL8Dialect",
  "isActive": true,
  "createdAt": "2024-01-11T10:00:00",
  "updatedAt": "2024-01-11T10:00:00"
}
```

### C. Atualizar DataSource

**Requisição:**
```bash
PUT /api/v1/tenant/database/jaguar/datasource
Authorization: Bearer <token>
Content-Type: application/json

{
  "host": "db-jaguar-novo.company.com",
  "port": 3307,
  ...
}
```

### D. Testar Conexão

**Requisição:**
```bash
POST /api/v1/tenant/database/jaguar/datasource/test
Authorization: Bearer <token>
Content-Type: application/json

{
  "host": "db-test.company.com",
  "port": 3306,
  "databaseName": "test_db",
  "username": "test_user",
  "password": "test_pass",
  "driverClassName": "com.mysql.cj.jdbc.Driver",
  "dialect": "org.hibernate.dialect.MySQL8Dialect"
}
```

**Resposta:**
```json
{
  "success": true,
  "message": "Conexão com banco de dados testada com sucesso"
}
```

---

## 🔒 Segurança & Isolamento

### TenantContext (ThreadLocal)
- ✅ Cada thread tem seu próprio contexto
- ✅ Isolamento automático entre requisições
- ✅ Limpeza automática ao fim da requisição

### MultiTenantRoutingDataSource
- ✅ Valida tenantSlug antes de retornar conexão
- ✅ Lança exceção se tenantSlug não está no contexto
- ✅ Cache de DataSources para performance

### Hibernate Filters
- ✅ Filtros automáticos via @Filter nas entidades
- ✅ Qualquer query inclui `WHERE tenant_id = :tenantId`
- ✅ Impossível retornar dados de outro tenant

### Exemplo de Proteção:
```java
// Mesmo que um dev faça:
List<Cliente> clientes = clienteRepository.findAll();

// Hibernate aplica automaticamente:
// SELECT c FROM Cliente c WHERE tenant_id = :tenantId
// Retorna apenas clientes do tenant atual
```

---

## 🚀 Como Usar nas Suas Entidades

### Passo 1: Adicionar coluna tenant_id (Migration já criada)

### Passo 2: Estender TenantAwareBaseEntity

**ANTES:**
```java
@Entity
public class Cliente {
    @Id
    private Long id;
    private String nome;
}
```

**DEPOIS:**
```java
@Entity
@Filter(name = "tenantIdFilter", condition = "tenant_id = :tenantId")
public class Cliente extends TenantAwareBaseEntity {
    private String nome;
}
```

### Passo 3: Usar normalmente
```java
// Ao salvar, tenantId é preenchido automaticamente do contexto
Cliente cliente = new Cliente();
cliente.setNome("Acme");
cliente.setTenantId(TenantContext.getTenantId()); // Automático se usar serviço base
clienteRepository.save(cliente);

// Ao ler, filtro é aplicado automaticamente
List<Cliente> clientes = clienteRepository.findAll();
// ↓ Equivalente a:
// SELECT c FROM Cliente c WHERE tenant_id = :tenantId
```

---

## 📊 Exemplo Real: Matriz e Filiais

**Cenário:**
- JAGUAR Matriz (Curitiba): tenantId=1, banco=jaguar_db
- JAGUAR Filial SP: tenantId=2, banco=jaguar_db
- JAGUAR Filial RJ: tenantId=3, banco=jaguar_db

**Fluxo:**

**Requisição 1: Matriz**
```
Authorization: Bearer {token_tenantId_1_slug_jaguar}
GET /api/v1/clientes
↓
Filtra: SELECT * FROM cliente WHERE tenant_id = 1
↓
Retorna apenas clientes da matriz
```

**Requisição 2: Filial SP**
```
Authorization: Bearer {token_tenantId_2_slug_jaguar}
GET /api/v1/clientes
↓
Filtra: SELECT * FROM cliente WHERE tenant_id = 2
↓
Retorna apenas clientes da filial SP
```

Ambas usam **o mesmo banco** (`jaguar_db`), mas dados são **completamente isolados**.

---

## 🔧 Configuração Necessária (application.properties)

```properties
# JWT Secret (usar variável de ambiente em produção)
jwt.secret=${JWT_SECRET:seu-secret-aqui}

# DataSource Master (onde ficam configurações de tenants)
spring.datasource.url=jdbc:mysql://localhost:3306/master_db
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.multiTenancy=SCHEMA

# HikariCP
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000

# Flyway
spring.flyway.locations=classpath:db/migration/master
spring.flyway.schemas=master
```

---

## 🐛 Troubleshooting

### Erro: "TenantSlug não definido no contexto"
**Causa:** Requisição chegou sem JWT ou JWT não tem claim `tenantSlug`
**Solução:** Verificar token JWT e headers

### Erro: "Banco de dados não configurado para tenant"
**Causa:** Tenant não tem TenantDatasource registrado
**Solução:** Configurar datasource via POST /api/v1/tenant/database/{slug}/datasource

### Query retorna dados de outro tenant
**Causa:** Entidade não estende TenantAwareBaseEntity ou não tem @Filter
**Solução:** Adicionar extends TenantAwareBaseEntity e @Filter na entidade

---

## 📚 Referências

- [Baeldung - Multitenancy with Spring Data JPA](https://www.baeldung.com/multitenancy-with-spring-data-jpa)
- [Hibernate Multitenancy](https://docs.jboss.org/hibernate/orm/6.0/userguide/html_single/Hibernate_User_Guide.html#multitenancy)
- [HikariCP Documentation](https://github.com/brettwooldridge/HikariCP)

---

## ✅ Checklist de Implementação

- [x] Criar TenantDatasource entity
- [x] Implementar MultiTenantRoutingDataSource
- [x] Criar DataSourceFactory
- [x] Implementar TenantSchemaService
- [x] Atualizar TenantsConfiguration
- [x] Criar TenantRequestFilter
- [x] Criar TenantAwareBaseEntity
- [x] Criar TenantIdFilterProvider
- [x] Criar migrations SQL
- [ ] Atualizar controller (TenantSchemaController)
- [ ] Adicionar @Filter nas entidades existentes
- [ ] Testar fluxo completo
- [ ] Documentar em Swagger/OpenAPI

---

**Status:** 🔧 Pronto para uso | **Última atualização:** 11/01/2025
