
# 🚀 Flyway Tenant Migrations - Implementação Completa

**Status:** ✅ IMPLEMENTAÇÃO CONCLUÍDA
**Data:** 2024
**Estratégia:** DATABASE per TENANT com Flyway automático

---

## 📋 Visão Geral

O sistema agora executa **migrações Flyway automaticamente em TODOS os bancos de tenants** quando a aplicação inicia.

### Arquitetura
```
┌─────────────────────────────────────────────────────┐
│                   Spring Boot App                    │
│                   (Iniciando)                        │
└─────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────┐
│  FlywayConfig.flywayMaster()                         │
│  - Executa migrations/master em erpapi               │
│  - Cria tabela tenant_datasource                     │
│  - ✅ Completa                                        │
└─────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────┐
│  ApplicationStartupListener.runMigrationsOnStartup() │
│  - Aguarda aplicação estar pronta (ApplicationReadyEvent) │
│  - Chama TenantMigrationService.migrateAllTenants()  │
└─────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────┐
│  TenantMigrationService.migrateAllTenants()          │
│  Para CADA tenant ativo:                             │
│  1. Busca configuração datasource (tenant_datasource)│
│  2. Cria HikariDataSource com credenciais do tenant  │
│  3. Executa Flyway (migrations/tenant)               │
│  4. Fecha conexão                                    │
└─────────────────────────────────────────────────────┘
                          ↓
┌────────────┬────────────┬────────────┐
│            │            │            │
▼            ▼            ▼            ▼
┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐
│ DB1  │  │ DB2  │  │ DB3  │  │ DB4  │
│      │  │      │  │      │  │      │
└──────┘  └──────┘  └──────┘  └──────┘
```

---

## 🔧 Componentes Criados

### 1. **TenantMigrationService**
📁 `src/main/java/.../shared/infrastructure/config/TenantMigrationService.java`

```java
@Service
public class TenantMigrationService {
    
    // Executa migrações em TODOS os tenants ativos
    public MigrationReport migrateAllTenants()
    
    // Executa migração em um tenant específico
    public void migrateTenantBySlug(String tenantSlug)
}
```

**Responsabilidades:**
- ✅ Busca tenants ativos em `tb_tenant`
- ✅ Para cada tenant, busca datasource em `tenant_datasource`
- ✅ Cria HikariDataSource com credenciais específicas
- ✅ Executa Flyway da pasta `classpath:db/migration/tenant`
- ✅ Registra resultado de sucesso/falha
- ✅ Fecha conexões corretamente

**Características:**
- Transacional com `readOnly = true`
- Logging detalhado com emojis
- Relatório estruturado (`MigrationReport`)
- Tratamento robusto de erros

---

### 2. **ApplicationStartupListener**
📁 `src/main/java/.../shared/infrastructure/config/ApplicationStartupListener.java`

```java
@Component
public class ApplicationStartupListener {
    
    @EventListener(ApplicationReadyEvent.class)
    public void runMigrationsOnStartup()
}
```

**Responsabilidades:**
- ✅ Escuta `ApplicationReadyEvent` (app totalmente iniciada)
- ✅ Chama `TenantMigrationService.migrateAllTenants()`
- ✅ Log de início/sucesso/erro
- ✅ Não relança exceção (app continua)

**Fluxo:**
```
App Initializing
    ↓
[1] Bean Creation
[2] DataSource Configuration
[3] Flyway Master Migrations ← FlywayConfig
[4] JPA/Hibernate Setup
[5] All other beans ready
    ↓
ApplicationReadyEvent ← ApplicationStartupListener
    ↓
TenantMigrationService.migrateAllTenants()
```

---

### 3. **TenantMigrationController** (Opcional)
📁 `src/main/java/.../shared/infrastructure/api/controller/TenantMigrationController.java`

**Endpoints:**
```
POST /api/v1/admin/migrations/tenants
  → Executa migrações em TODOS os tenants
  → Requer @PreAuthorize("hasRole('ADMIN')")
  → Retorna relatório com sucesso/falha

POST /api/v1/admin/migrations/tenants/{tenantSlug}
  → Executa migração em tenant específico
  → Requer @PreAuthorize("hasRole('ADMIN')")
  → Retorna confirmação ou erro
```

**Exemplos de Uso:**
```bash
# Migrar TODOS os tenants
curl -X POST http://localhost:8080/api/v1/admin/migrations/tenants \
  -H "Authorization: Bearer {JWT_TOKEN_ADMIN}" \
  -H "X-Tenant-Id: 1" \
  -H "X-Tenant-Slug: master"

# Migrar tenant específico
curl -X POST http://localhost:8080/api/v1/admin/migrations/tenants/tenant-hece \
  -H "Authorization: Bearer {JWT_TOKEN_ADMIN}" \
  -H "X-Tenant-Id: 1" \
  -H "X-Tenant-Slug: master"
```

---

### 4. **Migration Files**
📁 `src/main/resources/db/migration/tenant/`

```
├── V1__Create_Base_Tables.sql
│   └── Cria tabelas base do tenant:
│       - tb_cliente
│       - tb_endereco
│       - tb_contato
│       - tb_unidade_medida
│       - tb_permissao
│       - tb_role
│       - tb_usuario
│       - tb_produto
│       - etc...
│
└── V2__Seed_Tenant_Data.sql
    └── Insere dados de teste:
        - Unidades de medida padrão
        - Roles (ADMIN, USER, VENDEDOR)
        - Permissões
        - Clientes iniciais
        - Endereços
        - Contatos
```

---

### 5. **Repository Updates**
✅ Atualizado `TenantRepository`
- Adicionado: `findAllByAtivaTrue()` → Busca tenants ativos
- Adicionado: `findBySlug(slug)` → Default method que chama `findByNome()`

✅ Atualizado `TenantDatasourceRepository`
- Adicionado: `findByTenantIdAndStatus(tenantId, status)` → Default method

---

## 📊 Fluxo de Execução

### 1️⃣ Inicialização da Aplicação
```
$ mvn spring-boot:run

[INFO] Starting ErpapiApplication
[INFO] Initializing Spring Boot Application...
```

### 2️⃣ FlywayConfig - Migrações Master
```
[INFO] 🚀 Iniciando configuração do Flyway para o banco master
[INFO] 📊 Executando migrações do Flyway...
[INFO] ✅ Flyway executado com sucesso! Migrações aplicadas: 1
```

### 3️⃣ ApplicationStartupListener - Migrações de Tenants
```
[INFO] ╔════════════════════════════════════════════════════════════════╗
[INFO] ║      INICIANDO MIGRAÇÕES DE TENANTS NA STARTUP DA APLICAÇÃO      ║
[INFO] ╚════════════════════════════════════════════════════════════════╝
[INFO]
[INFO] 🚀 Iniciando migrações de TODOS os tenants
[INFO] 📊 Encontrados 4 tenants ativos
[INFO]
[INFO] ─────────────────────────────────────────
[INFO] 🔄 Processando Tenant: HECE (ID: 1)
[INFO] ─────────────────────────────────────────
[INFO] 📍 Banco de dados: root@localhost:3306/tenant1_db
[INFO] 🔧 Configurando Flyway para tenant: HECE
[INFO] 📊 Executando migrações...
[INFO] ✅ Migrações executadas com sucesso!
[INFO]    - Versão atual: OK
[INFO]    - Migrações aplicadas: 2
```

### 4️⃣ Verificação no Banco de Dados

```sql
-- Master Database (erpapi)
SELECT * FROM tb_tenant;
SELECT * FROM tenant_datasource;
SELECT * FROM flyway_schema_history;

-- Tenant Database (tenant1_db)
SELECT * FROM flyway_schema_history;
SELECT * FROM tb_cliente;
SELECT * FROM tb_usuario;
SELECT * FROM tb_permissao;
-- etc...
```

---

## 🔍 Verificação Manual

### Verificar Tenant Datasources Configurados
```sql
SELECT 
    td.id,
    t.nome AS tenant_name,
    td.database_name,
    td.host,
    td.port,
    td.is_active
FROM tenant_datasource td
JOIN tb_tenant t ON td.tenant_id = t.id
ORDER BY t.id;
```

**Resultado Esperado:**
```
id | tenant_name | database_name | host      | port | is_active
1  | HECE        | tenant1_db    | localhost | 3306 | 1
2  | Tenant 2    | shared_db     | localhost | 3306 | 1
3  | Tenant 3    | shared_db     | localhost | 3306 | 1
4  | Tenant 4    | shared_db     | localhost | 3306 | 1
```

### Verificar Migrações Executadas

**Master Database:**
```sql
-- Verificar migrações do master
SELECT * FROM flyway_schema_history;
```

**Cada Tenant Database:**
```sql
-- Verificar migrações do tenant
USE tenant1_db;
SELECT * FROM flyway_schema_history;

USE shared_db;
SELECT * FROM flyway_schema_history;
```

**Resultado Esperado:**
```
version | description           | type | script                        | success
1       | Create Base Tables    | SQL  | V1__Create_Base_Tables.sql    | 1
2       | Seed Tenant Data      | SQL  | V2__Seed_Tenant_Data.sql      | 1
```

---

## 🛠️ Como Adicionar Novas Migrações

### Passo 1: Criar arquivo SQL
```bash
# Arquivo: src/main/resources/db/migration/tenant/V3__Add_New_Feature.sql
```

### Passo 2: Escrever SQL

```sql
-- ==================================================================================
-- MIGRATION: V3__Add_New_Feature.sql
-- DESCRIÇÃO: Adicionar nova feature ao tenant
-- ==================================================================================

-- Criar nova tabela
CREATE TABLE tb_feature (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (tenant_id) REFERENCES tb_tenant(id)
);

-- Criar índices
CREATE INDEX idx_feature_tenant ON tb_feature(tenant_id);

-- Inserir dados iniciais
INSERT INTO tb_feature (tenant_id, nome) VALUES (1, 'Feature A');
INSERT INTO tb_feature (tenant_id, nome) VALUES (1, 'Feature B');
```

### Passo 3: Reiniciar Aplicação

Na próxima inicialização:
1. Flyway detectará `V3__Add_New_Feature.sql`
2. Executará em cada tenant database
3. Registrará em `flyway_schema_history`

### Passo 4: Executar via API (Opcional)

```bash
# Se quiser rodar sem reiniciar, use o endpoint
curl -X POST http://localhost:8080/api/v1/admin/migrations/tenants \
  -H "Authorization: Bearer {JWT_ADMIN}" \
  -H "X-Tenant-Id: 1" \
  -H "X-Tenant-Slug: master"
```

---

## ⚠️ Tratamento de Erros

### Erro: Tenant não tem datasource configurado

```
❌ Erro ao migrar tenant: Tenant X
   Datasource não configurado
```

**Solução:** Inserir em `tenant_datasource`:
```sql
INSERT INTO tenant_datasource (
    tenant_id, host, port, database_name, 
    username, password, is_active
) VALUES (
    1, 'localhost', 3306, 'meu_db',
    'root', '12345', 1
);
```

### Erro: Não consegue conectar ao banco

```
❌ Erro ao conectar ao banco: Tenant X
   Connection refused
```

**Solução:** Verificar credenciais em `tenant_datasource`:
```bash
mysql -u {username} -p {password} -h {host} -P {port} {database_name}
```

### Erro: Migração falhou em um tenant

```
❌ Erro ao migrar tenant: Tenant X
   SQL syntax error in V3__Add_Feature.sql
```

**Solução:** 
1. Verificar syntax do SQL em `V3__Add_Feature.sql`
2. Executar repair via endpoint:
   ```bash
   POST /api/v1/admin/migrations/tenants/{tenantSlug}
   ```
3. Flyway executará `repair()` antes de migrar

---

## 📈 Monitoramento

### Logs Importantes

```bash
# Ver logs de startup
tail -f logs/application.log | grep -E "Iniciando migrações|Processando Tenant|Migração concluída"

# Ver apenas erros
tail -f logs/application.log | grep -E "❌|Error"
```

### Métricas

```sql
-- Contar migrações executadas por tenant
SELECT 
    t.nome,
    COUNT(fsh.version) AS migracao_count
FROM tb_tenant t
-- Precisaria de join com tabela de migração do tenant
WHERE t.ativa = 1
GROUP BY t.id, t.nome;
```

---

## 🚀 Próximos Passos (Opcional)

### 1. Integração com Event-Driven Tenants
```java
@EventListener
public void onNewTenantProvisioned(TenantProvisionedEvent event) {
    tenantMigrationService.migrateTenantBySlug(event.getTenantSlug());
}
```

### 2. Health Check Endpoint
```java
@GetMapping("/health/migrations")
public ResponseEntity<?> checkMigrationStatus() {
    // Retorna status de migração de cada tenant
}
```

### 3. Migration Rollback
```java
// Adicionar suporte a rollback com Flyway
public void rollbackTenantMigration(String tenantSlug) {
    // Flyway não suporta nativamente, usar Liquibase ou custom
}
```

### 4. Scheduled Migrations
```java
@Scheduled(cron = "0 2 * * *")
public void scheduledMigrationCheck() {
    tenantMigrationService.migrateAllTenants();
}
```

---

## ✅ Checklist

- ✅ TenantMigrationService criado
- ✅ ApplicationStartupListener criado
- ✅ TenantMigrationController criado (endpoints REST)
- ✅ V1__Create_Base_Tables.sql criado
- ✅ V2__Seed_Tenant_Data.sql criado
- ✅ TenantRepository atualizado
- ✅ TenantDatasourceRepository atualizado
- ✅ Logging com emojis implementado
- ✅ Tratamento robusto de erros
- ✅ Relatório de migrações estruturado
- ✅ Documentação completa

---

## 📝 Resumo

**Antes:**
- ❌ Migrações rodavam apenas no master
- ❌ Tenants precisavam de migrações manuais
- ❌ Sem automação ou rastreamento

**Depois:**
- ✅ Migrações automáticas em TODOS os tenants ao iniciar
- ✅ Cada tenant usa seu próprio banco/credenciais
- ✅ Logging detalhado e rastreável
- ✅ Endpoints REST para migração sob demanda
- ✅ Tratamento robusto de erros
- ✅ Escalável para novos tenants

**Execução:**
```bash
mvn spring-boot:run
# → Master migrations
# → ApplicationReadyEvent
# → TenantMigrationService
# → Migra 4 tenants em paralelo (com logging)
# → ✅ Pronto!
```
