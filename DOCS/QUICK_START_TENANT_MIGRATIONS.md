
# ✅ Migrações Flyway de Tenants - Implementação Concluída

**Status:** ✅ PRONTO PARA TESTAR
**Estratégia:** DATABASE per TENANT com Flyway automático

---

## 📦 O que foi criado

### 1. **TenantMigrationService**
- Serviço que executa Flyway em cada banco de tenant
- Busca tenants ativos em `tb_tenant`
- Para cada tenant, busca datasource em `tenant_datasource`
- Cria HikariDataSource com credenciais do tenant
- Executa Flyway da pasta `db/migration/tenant`

### 2. **ApplicationStartupListener**
- Component que escuta `ApplicationReadyEvent`
- Chama `TenantMigrationService.migrateAllTenants()` automaticamente
- Executa quando a aplicação está 100% pronta

### 3. **TenantMigrationController** (Opcional)
- Endpoints REST para executar migrações:
  - `POST /api/v1/admin/migrations/tenants` - Migrar todos
  - `POST /api/v1/admin/migrations/tenants/{slug}` - Migrar específico

### 4. **Migration Files**
- `V1__Create_Base_Tables.sql` - Cria tabelas base
- `V2__Seed_Tenant_Data.sql` - Insere dados de teste

### 5. **Repository Updates**
- `TenantRepository.findAllByAtivaTrue()` - Busca tenants ativos
- `TenantDatasourceRepository.findByTenantIdAndStatus()` - Busca datasource

---

## 🚀 Como Testar

### Passo 1: Verificar Pré-requisitos
```bash
bash test_tenant_migrations.sh
```

Isso verifica:
- ✓ MySQL rodando
- ✓ Master database (erpapi) acessível
- ✓ Tabela `tenant_datasource` existe
- ✓ Tenants ativos em `tb_tenant`

### Passo 2: Iniciar a Aplicação
```bash
mvn spring-boot:run
```

Quando iniciar, você verá os logs:
```
🚀 Iniciando migrações de TODOS os tenants
📊 Encontrados 4 tenants ativos

🔄 Processando Tenant: HECE (ID: 1)
📍 Banco de dados: root@localhost:3306/tenant1_db
✅ Migrações executadas com sucesso!
   - Migrações aplicadas: 2

... (para cada tenant)
```

### Passo 3: Verificar Resultados
```bash
bash verify_tenant_migrations.sh
```

Isso mostra:
- ✓ Migrações executadas em cada banco
- ✓ Tabelas criadas
- ✓ Dados inseridos

---

## 📊 Resultado Esperado

### Master Database (erpapi)
```
tb_tenant: 4 registros (tenants ativos)
tenant_datasource: 4 registros (configurações)
flyway_schema_history: 1 registro (V1__Create_Base_Tables.sql)
```

### Tenant Databases (tenant1_db, shared_db)
```
flyway_schema_history: 2 registros
├── V1__Create_Base_Tables.sql
└── V2__Seed_Tenant_Data.sql

Tabelas criadas:
├── tb_cliente
├── tb_endereco
├── tb_contato
├── tb_unidade_medida
├── tb_permissao
├── tb_role
├── tb_usuario
├── tb_produto
└── ... (outras tabelas)
```

---

## 📝 Estrutura de Pastas

```
src/main/
├── java/com/api/erp/v1/
│   └── shared/
│       └── infrastructure/
│           ├── config/
│           │   ├── TenantMigrationService.java ✨ NEW
│           │   ├── ApplicationStartupListener.java ✨ NEW
│           │   └── FlywayConfig.java (atualizado)
│           └── api/controller/
│               └── TenantMigrationController.java ✨ NEW
│
├── features/tenant/
│   └── domain/repository/
│       ├── TenantRepository.java (atualizado)
│       └── TenantDatasourceRepository.java (atualizado)
│
└── resources/db/migration/
    ├── master/ (migrations master)
    └── tenant/ ✨ NEW
        ├── V1__Create_Base_Tables.sql ✨ NEW
        └── V2__Seed_Tenant_Data.sql ✨ NEW
```

---

## 🔄 Fluxo de Execução

```
1. Aplicação inicia
   ↓
2. FlywayConfig.flywayMaster()
   → Executa migrations/master em erpapi
   → Cria tabela tenant_datasource
   ↓
3. ApplicationStartupListener.runMigrationsOnStartup()
   → Aguarda ApplicationReadyEvent
   ↓
4. TenantMigrationService.migrateAllTenants()
   → Para cada tenant ativo:
      a. Busca datasource configurado
      b. Cria HikariDataSource temporário
      c. Executa Flyway (migrations/tenant)
      d. Fecha conexão
   ↓
5. Todas as tabelas criadas
   ✅ Aplicação pronta
```

---

## 🛠️ Adicionar Nova Migração

### 1. Criar arquivo SQL
```
src/main/resources/db/migration/tenant/V3__Add_New_Feature.sql
```

### 2. Escrever SQL
```sql
-- MIGRATION: V3__Add_New_Feature.sql
CREATE TABLE tb_feature (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Dados iniciais (obrigatório usar tenant_id = 1 ou adicionar para todos)
INSERT INTO tb_feature (tenant_id, nome) VALUES (1, 'Feature A');
```

### 3. Reiniciar Aplicação
```bash
mvn spring-boot:run
```

Flyway detectará e executará `V3__Add_New_Feature.sql` em cada tenant.

---

## ⚠️ Troubleshooting

### ❌ "Datasource não configurado"
**Causa:** Tenant não tem registro em `tenant_datasource`

**Solução:**
```sql
INSERT INTO tenant_datasource (
    tenant_id, host, port, database_name,
    username, password, is_active
) VALUES (
    1, 'localhost', 3306, 'tenant1_db',
    'root', '12345', 1
);
```

### ❌ "Connection refused"
**Causa:** Credenciais inválidas ou banco não existe

**Solução:**
```bash
# Verificar credenciais
mysql -u root -p12345 -h localhost -P 3306 tenant1_db

# Verificar banco existe
mysql -u root -p12345 -e "SHOW DATABASES LIKE 'tenant%';"
```

### ❌ "ApplicationStartupListener não executou"
**Causa:** A app não chegou em `ApplicationReadyEvent`

**Solução:**
- Verifique logs para erros anteriores
- Certifique-se que FlywayConfig executou (veja logs de master)
- Verifique se DataSource está configurado

---

## 📈 Monitoramento

### Ver Logs de Migração
```bash
grep -E "Iniciando migrações|Processando Tenant|Migração concluída" logs/application.log
```

### Contar Migrações Executadas
```sql
-- Master
SELECT COUNT(*) FROM erpapi.flyway_schema_history;

-- Cada Tenant
SELECT COUNT(*) FROM tenant1_db.flyway_schema_history;
SELECT COUNT(*) FROM shared_db.flyway_schema_history;
```

### Ver Histórico Detalhado
```sql
SELECT version, description, installed_on 
FROM flyway_schema_history 
ORDER BY version;
```

---

## ✅ Checklist Final

- ✅ TenantMigrationService criado
- ✅ ApplicationStartupListener criado  
- ✅ TenantMigrationController criado
- ✅ Migration files criados (V1, V2)
- ✅ Repositórios atualizados
- ✅ Test scripts criados (test_tenant_migrations.sh, verify_tenant_migrations.sh)
- ✅ Documentação completa (FLYWAY_TENANT_MIGRATIONS.md)
- ✅ Pronto para testar com `mvn spring-boot:run`

---

## 🎯 Próximo Passo

**Execute:**
```bash
bash test_tenant_migrations.sh && mvn spring-boot:run
```

**Depois:**
```bash
bash verify_tenant_migrations.sh
```

**Se tudo passar:** ✅ Sistema de migrações de tenant via Flyway funcionando!
