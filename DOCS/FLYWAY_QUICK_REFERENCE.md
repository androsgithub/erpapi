# ⚡ Flyway Multi-Tenant - Quick Reference

## 🎯 Comandos Rápidos

### Criar Nova Migração Master
```bash
# Arquivo: src/main/resources/db/migration/master/V{N}__Description.sql
# Exemplo:
cat > V2__Add_Tenant_Customization.sql << 'EOF'
ALTER TABLE tenants ADD COLUMN custom_field VARCHAR(255);
EOF
```

### Criar Nova Migração Tenant
```bash
# Arquivo: src/main/resources/db/migration/tenant/V{N}__Description.sql
# Exemplo:
cat > V2__Add_Products_Table.sql << 'EOF'
CREATE TABLE products (id BIGINT PRIMARY KEY AUTO_INCREMENT, ...);
EOF
```

### Usar no Código
```java
@Autowired
private TenantMigrationService migrationService;

// Provisionar novo tenant
migrationService.provisioning(tenantId, schemaName);

// Verificar status
String status = migrationService.getStatus(tenantId, schemaName);

// Limpar (dev only!)
migrationService.cleanTenant(tenantId, schemaName);
```

---

## 📋 Nomenclatura Padrão

| Tipo | Padrão | Exemplo |
|------|--------|---------|
| Migração com versão | V{N}__{Descrição}.sql | V1__Create_Master_Tables.sql |
| Migração com data | V{YYYYMMDD}_{HHMMSS}__{Desc}.sql | V20260104_120000__Add_Users.sql |

---

## 📂 Estrutura Mínima

```
src/main/resources/db/migration/
├── master/
│   └── V1__Create_Master_Tables.sql      # Obrigatório
└── tenant/
    └── V1__Create_Tenant_Base_Tables.sql # Obrigatório
```

---

## ⚙️ Configuração Mínima

```properties
# application.properties
spring.flyway.enabled=true
spring.flyway.schemas=erpapi
spring.flyway.locations=classpath:db/migration/master,classpath:db/migration/tenant
spring.jpa.hibernate.ddl-auto=validate
```

---

## 🔧 Classes Principais

| Classe | Responsabilidade |
|--------|------------------|
| `FlywayConfig` | Inicializar Flyway no Spring |
| `FlywayMigrationStrategy` | Executar migrações por schema |
| `TenantMigrationService` | Interface de uso |

---

## 🚀 Provocar Migração

### Automático (Startup)
Aplicação inicia → Flyway executa automaticamente

### Manual
```java
TenantMigrationService service = ...;
service.provisioning(tenantId, schemaName);
```

---

## 🔍 Verificar Status

```java
String status = migrationService.getStatus(tenantId, schemaName);
// Retorna: "Migrations pending: 2, Applied: 1"
```

---

## 🧹 Limpar (Dev Only!)

```java
migrationService.cleanTenant(tenantId, schemaName);
// ⚠️ Delete tudo! Use apenas em desenvolvimento
```

---

## ✅ Checklist Rápido

- [ ] pom.xml tem flyway-core e flyway-mysql?
- [ ] application.properties configurado?
- [ ] Diretórios db/migration/master e tenant criados?
- [ ] V1 para master e tenant criados?
- [ ] TenantMigrationService pode ser injetado?

---

## 🆘 Problemas Comuns

| Problema | Solução |
|----------|---------|
| "Baseline migration not found" | Adicione `V1__` em master e tenant |
| "Failed migration" | Corrija SQL e use `cleanTenant()` em dev |
| "Schema not found" | Crie schema antes de migrar |
| "Permission denied" | Verifique permissões do usuário DB |

---

## 📊 Estrutura Banco Criada

### Master Database (erpapi)
```sql
flyway_schema_history (automática)
tenants (V1__Create_Master_Tables.sql)
```

### Tenant Schema (tenant_xxx)
```sql
_tenant_xxx_flyway_schema_history (automática)
users (V1__Create_Tenant_Base_Tables.sql)
user_roles (V1__Create_Tenant_Base_Tables.sql)
```

---

## 🎓 Template Migração

### Master Template
```sql
-- src/main/resources/db/migration/master/V{N}__Describe_Change.sql

-- Descrição clara do que está sendo feito
ALTER TABLE tenants ADD COLUMN novo_campo VARCHAR(255);
CREATE INDEX idx_novo_campo ON tenants(novo_campo);
```

### Tenant Template
```sql
-- src/main/resources/db/migration/tenant/V{N}__Describe_Change.sql

-- Descrição clara do que está sendo feito
CREATE TABLE nova_tabela (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    field VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_field ON nova_tabela(field);
```

---

## 🔐 Boas Práticas

✅ Sempre versione sequencialmente (V1 → V2 → V3)
✅ Descrições claras e em inglês
✅ Uma mudança por arquivo
✅ Testar em dev antes de produção
✅ Manter histórico de migrações
⚠️ Nunca edite migração aplicada
⚠️ Flyway não faz rollback automático
⚠️ Clean só em desenvolvimento

---

## 📝 Referência Rápida

```java
// Injetar serviço
@Autowired
private TenantMigrationService migrationService;

// Provisionar
migrationService.provisioning("tenant-1", "tenant_1");

// Status
System.out.println(migrationService.getStatus("tenant-1", "tenant_1"));

// Limpar (dev)
migrationService.cleanTenant("tenant-1", "tenant_1");
```

---

## 🎯 Fluxo Típico

```
1. Developer cria V2__New_Feature.sql
2. Commit & Push
3. CI/CD tira pull da aplicação
4. Flyway detecta V2 pendente
5. Executa V2 em startup
6. Aplicação inicia normalmente
7. V2 está no histórico para sempre
```

---

## 📞 Documentação Completa

Para mais detalhes, consulte:
- **FLYWAY_SETUP_GUIDE.md** - Guia completo
- **FLYWAY_CHECKLIST.md** - Próximos passos
- **Código Java** - Comentários e Javadoc

---

**Versão**: 1.0 | **Criado**: Janeiro/2026 | **Status**: ✅ Pronto
