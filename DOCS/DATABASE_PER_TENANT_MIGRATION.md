# DATABASE-per-TENANT Migration Summary

## 🎯 Objetivo
Migrar da arquitetura **SCHEMA-per-TENANT** (múltiplos schemas no mesmo banco) para **DATABASE-per-TENANT** (cada tenant tem seu próprio banco de dados).

## ✅ Mudanças Implementadas

### 1. **Simplificação do TenantsConfiguration.java**

**Removido:**
- Importações para `CurrentTenantIdentifierResolver` e `MultiTenantConnectionProvider`
- Bean `multiTenantConnectionProvider()` (específico para SCHEMA mode)
- Bean `tenants()` que lia `spring.flyway.schemas`
- Parâmetro `MultiTenantConnectionProvider` do método `entityManagerFactory()`
- Parâmetro `CurrentTenantIdentifierResolver` do método `entityManagerFactory()`
- Propriedades JPA para multitenancy do Hibernate:
  - `hibernate.multiTenancy = "SCHEMA"`
  - `hibernate.multi_tenant_connection_provider`
  - `hibernate.tenant_identifier_resolver`
- Inner classes `TenantSchemaSelector` (implementava `CurrentTenantIdentifierResolver`)
- Inner class `TenantConnectionProvider` (implementava `MultiTenantConnectionProvider`)

**Mantido:**
- Bean `defaultDataSource` (Master Database)
- Bean `multiTenantRoutingDataSource` @Primary @Lazy (roteamento de banco)
- Configuração de entity scanning
- Apenas `hibernate.dialect` na jpaPropertyMap

**Resultado:**
- 📄 Arquivo reduzido de 204 para 111 linhas
- 🧹 Remova de código específico para SCHEMA mode
- ⚡ Configuração mais limpa e focada em DATABASE per TENANT

### 2. **Desativação do TenantsDatabaseInitializer.java**

**Por quê?**
- Na arquitetura SCHEMA-per-TENANT: cada tenant tinha um schema (`erpapi`, `huron_casasbahia`, `huron_magazine`) no MESMO banco
- Na arquitetura DATABASE-per-TENANT: cada tenant tem seu PRÓPRIO banco de dados
- Portanto, não precisamos criar múltiplos schemas

**Ação:**
- Classe mantida mas completamente desativada
- Código antigo comentado com documentação explicando o histórico
- Não será mais executada durante o startup
- A classe está lá como referência histórica

### 3. **Atualização do application.properties**

**Antes:**
```properties
spring.flyway.enabled=true
spring.flyway.schemas=erpapi,huron_casasbahia,huron_magazine
spring.flyway.locations=db/migration
spring.flyway.baseline-on-migrate=true
```

**Depois:**
```properties
# Flyway Configuration (Master database only - DATABASE per TENANT strategy)
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration/master
spring.flyway.baseline-on-migrate=true
```

**Mudanças:**
- ❌ Removido `spring.flyway.schemas` (não há múltiplos schemas)
- 📍 Alterado `db/migration` → `classpath:db/migration/master` (migrations estão em `/master`)
- 📝 Adicionado comentário explicando que é apenas para master database

### 4. **Estrutura de Migrações**

**Master Database (erpapi):**
- V1: Cria tabelas base (`tb_tenant`, `tb_usuario`, `tb_cliente`, etc.)
- V2: Seed de unidades de medida
- V3-V7: Dados e configurações iniciais
- V8: Cria tabela `tenant_datasource` (APENAS no master)
- V9: Placeholder (todas as colunas já existem em V1)

**Tenant Databases:**
- Cada tenant tem seu próprio banco
- Migrações são aplicadas separadamente a cada banco quando criado
- Configuração de conexão armazenada em `master.tenant_datasource`

---

## 📊 Arquitetura Resultante

```
┌─────────────────────────────────────┐
│  Master Database (erpapi)           │
│  ├─ tb_tenant                       │
│  ├─ tb_tenant_datasource            │
│  ├─ tb_usuario (master admin)       │
│  └─ configurações gerais            │
└─────────────────────────────────────┘
          ▲
          │ (Spring Flyway: V1-V9)
          │
   ┌──────┴────────┬───────────────────┬─────────────┐
   │               │                   │             │
   ▼               ▼                   ▼             ▼
┌─────────┐  ┌─────────────┐  ┌──────────────┐  ┌─────────┐
│ JAGUAR  │  │ CASA BAHIA  │  │  MAGAZINE    │  │ HURON   │
│ Database│  │  Database   │  │   Database   │  │Database │
│ (Tenant)│  │  (Tenant)   │  │   (Tenant)   │  │(Tenant) │
└─────────┘  └─────────────┘  └──────────────┘  └─────────┘
  DataSource      DataSource      DataSource      DataSource
  Config stored   Config stored   Config stored   Config stored
  in master.tenant_datasource table
```

---

## 🔄 Fluxo de Requisição (DATABASE per TENANT)

1. **Request chega:** POST `/api/clientes`
   - Header: `X-Tenant-Slug: jaguar`

2. **TenantFilter extrai tenant:**
   - `TenantContext.setTenantSlug("jaguar")`

3. **MultiTenantRoutingDataSource roteia:**
   - Busca `tenant_datasource` onde `tenant.slug = "jaguar"`
   - Obtém credentials: `host=localhost, port=3306, database=jaguar_db`
   - Cria/reutiliza conexão para `jdbc:mysql://localhost:3306/jaguar_db`

4. **Hibernate executa query:**
   - `SELECT * FROM tb_cliente WHERE tenant_id = 1` (JAGUAR)
   - Usa DataSource correto (jaguar_db, não master)

5. **Resposta retorna:** Clientes apenas de JAGUAR

---

## 🎓 Conceitos-Chave

### DATABASE per TENANT
- ✅ Cada tenant tem seu próprio banco de dados
- ✅ Isolamento completo de dados (físico, não lógico)
- ✅ Performance otimizada (sem WHERE tenant_id)
- ✅ Facilita compliance/segurança

### ROW-based Discrimination
- ✅ Coluna `tenant_id` ainda existe nas tabelas
- ✅ Aplicação filtra por `tenant_id` como camada extra
- ✅ Previne bugs de segurança (filtragem acidental)

### Master Database
- ✅ Armazena configuração de tenants
- ✅ Tabelas: `tb_tenant`, `tb_tenant_datasource`
- ✅ Nunca roteado via `MultiTenantRoutingDataSource`
- ✅ Sempre usa `defaultDataSource`

---

## 📋 Checklist de Verificação

- ✅ Compilação bem-sucedida (0 erros)
- ✅ Sem schema creation durante startup
- ✅ Master database migrations aplicadas (V1-V9)
- ✅ `defaultDataSource` funcionando
- ✅ `multiTenantRoutingDataSource` @Primary @Lazy
- ✅ Sem referências a SCHEMA multitenancy
- ✅ `TenantsDatabaseInitializer` desativado

---

## 🔗 Arquivos Modificados

1. **src/main/java/com/api/erp/v1/shared/infrastructure/config/datasource/manual/TenantsConfiguration.java**
   - Removidas importações SCHEMA-specific
   - Simplificado para usar apenas DATABASE routing
   - Reduzido de 204 para 111 linhas

2. **src/main/java/com/api/erp/v1/shared/infrastructure/config/datasource/manual/TenantsDatabaseInitializer.java**
   - Desativada (não executa mais)
   - Código antigo preservado com documentação

3. **src/main/resources/application.properties**
   - Removido `spring.flyway.schemas`
   - Atualizado `spring.flyway.locations`
   - Adicionado comentário explicativo

---

## ⚠️ Notas Importantes

### Para Novos Tenants
1. Criar banco de dados: `CREATE DATABASE tenant_name;`
2. Registrar no master: `INSERT INTO tb_tenant_datasource (...)`
3. Migrations automáticas via `DataSourceFactory`

### Histórico
- Original: SCHEMA-per-TENANT com `erpapi`, `huron_casasbahia`, `huron_magazine` schemas
- Migrado: DATABASE-per-TENANT com bancos separados
- Benefício: Isolamento melhor, escalabilidade, segurança

### Compatibilidade
- ✅ `multiTenantRoutingDataSource` continua funcionando
- ✅ `TenantFilter` + `TenantContext` sem mudanças
- ✅ Entity classes sem mudanças
- ✅ Repositories sem mudanças

---

## 📚 Documentação Relacionada

- [FLYWAY_COMPLETE_SETUP.md](FLYWAY_COMPLETE_SETUP.md) - Setup de migrações
- [MULTITENANCY_STRATEGY.md](MULTITENANCY_STRATEGY.md) - Estratégia de multitenancy
- [TenantsConfiguration.java](../src/main/java/com/api/erp/v1/shared/infrastructure/config/datasource/manual/TenantsConfiguration.java) - Configuração atual
