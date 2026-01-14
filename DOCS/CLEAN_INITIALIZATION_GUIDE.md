# 🔄 Clean Initialization - Migrations para Teste Limpo

**Data:** 2026-01-11  
**Objetivo:** Rodar o sistema do zero com migrations automáticas  
**Status:** ✅ Pronto

---

## 📋 Migrations Criadas

### V18__Create_Tenant_Databases_And_Datasources.sql
**Função:** Cria os bancos de dados dos tenants e insere dados na tabela `tenant_datasource`

**O que faz:**
1. ✅ Cria banco `tenant1_db` (CHARACTER SET utf8mb4)
2. ✅ Cria banco `shared_db` (CHARACTER SET utf8mb4)
3. ✅ Insere 4 registros em `tenant_datasource`:
   - Tenant 1 (HECE) → `tenant1_db`
   - Tenant 2 (Tech Solutions) → `shared_db`
   - Tenant 3 (Logística Global) → `shared_db`
   - Tenant 4 (Varejo Express) → `shared_db`

**Credenciais:**
```
host: localhost
port: 3306
username: root
password: 12345
```

---

### V19__Insert_Additional_Test_Tenants.sql
**Função:** Insere 3 tenants adicionais (V17 já insere 1)

**Tenants Inseridos:**
| ID | Nome | Slug | Ativo |
|----|------|------|-------|
| 1 | HECE Distribuidora | hece-distribuidora | ✅ |
| 2 | Tech Solutions Brasil | tech-solutions | ✅ |
| 3 | Logística Global | logistica-global | ✅ |
| 4 | Varejo Express | varejo-express | ✅ |

---

## 🚀 Como Rodar o Teste Limpo

### Passo 1: Deletar Bancos Antigos (Já feito)
```bash
# Se não tiver deletado ainda:
mysql -u root -p12345 -e "DROP DATABASE IF EXISTS erpapi, tenant1_db, shared_db;"
```

### Passo 2: Iniciar Aplicação
```bash
cd /path/to/erpapi
mvn clean spring-boot:run
```

**Fluxo Automático:**
```
1. Aplicação inicia
   ↓
2. FlywayConfig executa migrations/master
   ├─ V1__DATABASEINITIALIZER.sql (cria schema master)
   ├─ V2__seed_unidades_medida.sql
   ├─ ...
   ├─ V17__Insert_Tenant_Test_Company.sql (insere Tenant 1)
   ├─ V18__Create_Tenant_Databases_And_Datasources.sql ⭐
   │  └─ Cria tenant1_db e shared_db
   │  └─ Insere 4 datasources em tenant_datasource
   ├─ V19__Insert_Additional_Test_Tenants.sql ⭐
   │  └─ Insere Tenants 2, 3, 4
   └─ Migrations concluídas ✅
   ↓
3. ApplicationReadyEvent disparado
   ↓
4. ApplicationStartupListener.runMigrationsOnStartup()
   ↓
5. TenantMigrationService.migrateAllTenants()
   ├─ Para Tenant 1 (HECE):
   │  ├─ Busca datasource: localhost:3306/tenant1_db
   │  ├─ Executa Flyway (migration/tenant/)
   │  ├─ V1__Create_Base_Tables.sql
   │  ├─ V2__Seed_Tenant_Data.sql
   │  └─ ✅ Completo
   ├─ Para Tenant 2 (Tech Solutions):
   │  ├─ Busca datasource: localhost:3306/shared_db
   │  ├─ Executa Flyway (migration/tenant/)
   │  └─ ✅ Completo
   ├─ Para Tenant 3 (Logística):
   │  ├─ Busca datasource: localhost:3306/shared_db
   │  ├─ Executa Flyway (migration/tenant/)
   │  └─ ✅ Completo
   └─ Para Tenant 4 (Varejo):
      ├─ Busca datasource: localhost:3306/shared_db
      ├─ Executa Flyway (migration/tenant/)
      └─ ✅ Completo
   ↓
6. ✅ Aplicação pronta para uso!
```

### Passo 3: Verificar Migrações
```bash
bash verify_tenant_migrations.sh
```

**Resultado Esperado:**
```
Master Database (erpapi): 19 migrações
├─ V1__DATABASEINITIALIZER.sql
├─ V2__seed_unidades_medida.sql
├─ ...
├─ V17__Insert_Tenant_Test_Company.sql
├─ V18__Create_Tenant_Databases_And_Datasources.sql ⭐
├─ V19__Insert_Additional_Test_Tenants.sql ⭐
└─ ✅ OK

Tenant Databases:
├─ tenant1_db: 2 migrações (V1, V2)
├─ shared_db: 2 migrações (V1, V2)
└─ ✅ OK
```

---

## 🔍 Verificação Manual

### Ver Bancos de Dados Criados
```bash
mysql -u root -p12345 -e "SHOW DATABASES;" | grep -E "erpapi|tenant1_db|shared_db"
```

**Resultado Esperado:**
```
erpapi
shared_db
tenant1_db
```

### Ver Tenants Configurados
```bash
mysql -u root -p12345 erpapi -e "SELECT id, nome, tenant_slug, ativa FROM tb_tenant WHERE ativa = 1;"
```

**Resultado Esperado:**
```
id | nome                      | tenant_slug         | ativa
1  | HECE Distribuidora        | hece-distribuidora  | 1
2  | Tech Solutions Brasil     | tech-solutions      | 1
3  | Logística Global          | logistica-global    | 1
4  | Varejo Express            | varejo-express      | 1
```

### Ver Datasources Configurados
```bash
mysql -u root -p12345 erpapi -e "SELECT t.nome, td.database_name, td.host, td.port FROM tenant_datasource td JOIN tb_tenant t ON td.tenant_id = t.id ORDER BY t.id;"
```

**Resultado Esperado:**
```
nome                      | database_name | host      | port
HECE Distribuidora        | tenant1_db    | localhost | 3306
Tech Solutions Brasil     | shared_db     | localhost | 3306
Logística Global          | shared_db     | localhost | 3306
Varejo Express            | shared_db     | localhost | 3306
```

### Ver Migrações Executadas em Cada Banco
```bash
# Master
mysql -u root -p12345 erpapi -e "SELECT version, description FROM flyway_schema_history ORDER BY version LIMIT 5;" 

# Tenant1
mysql -u root -p12345 tenant1_db -e "SELECT version, description FROM flyway_schema_history;"

# Shared
mysql -u root -p12345 shared_db -e "SELECT version, description FROM flyway_schema_history;"
```

### Ver Tabelas Criadas em Cada Banco
```bash
# Tenant1
mysql -u root -p12345 -e "USE tenant1_db; SHOW TABLES;"

# Shared (sem flyway_schema_history)
mysql -u root -p12345 -e "USE shared_db; SHOW TABLES WHERE Tables_in_shared_db NOT LIKE 'flyway%';"
```

---

## 📊 Estrutura Final

```
MySQL
├── erpapi (Master Database)
│   ├── tb_tenant (4 tenants)
│   ├── tenant_datasource (4 datasources)
│   ├── flyway_schema_history (19 migrations)
│   ├── tb_usuario
│   ├── tb_role
│   ├── tb_permissao
│   ├── etc...
│
├── tenant1_db (Banco do Tenant 1)
│   ├── tb_cliente
│   ├── tb_endereco
│   ├── tb_contato
│   ├── tb_usuario
│   ├── tb_role
│   ├── tb_permissao
│   ├── tb_unidade_medida
│   ├── tb_produto
│   ├── flyway_schema_history (2 migrations: V1, V2)
│   └── ... (13 tabelas)
│
└── shared_db (Banco Compartilhado para Tenants 2, 3, 4)
    ├── tb_cliente (isolado por tenant_id)
    ├── tb_endereco (isolado por tenant_id)
    ├── tb_contato (isolado por tenant_id)
    ├── tb_usuario (isolado por tenant_id)
    ├── tb_role (isolado por tenant_id)
    ├── tb_permissao (isolado por tenant_id)
    ├── tb_unidade_medida (isolado por tenant_id)
    ├── tb_produto (isolado por tenant_id)
    ├── flyway_schema_history (2 migrations: V1, V2)
    └── ... (13 tabelas)
```

---

## ✅ Checklist de Sucesso

Após iniciar a aplicação, verificar:

- [ ] Aplicação iniciou sem erros
- [ ] FlywayConfig executou migrations master (V1-V19)
- [ ] ApplicationStartupListener disparou
- [ ] TenantMigrationService.migrateAllTenants() executou
- [ ] Logs mostram "Migrações executadas com sucesso" para 4 tenants
- [ ] Bancos tenant1_db e shared_db foram criados automaticamente
- [ ] Tabelas foram criadas em cada banco (flyway_schema_history)
- [ ] Dados de teste foram inseridos (V2__Seed_Tenant_Data.sql)
- [ ] Sem erros de conexão ou permissão

**Executar após startup:**
```bash
bash verify_tenant_migrations.sh
```

Se todas as verificações passarem: **✅ SUCESSO!**

---

## 🆘 Troubleshooting

### "Database creation failed"
**Possível causa:** Usuário MySQL sem privilégios de CREATE DATABASE

**Solução:**
```bash
# Dar privilégios ao usuário root
mysql -u root -p12345 -e "GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION;"
mysql -u root -p12345 -e "FLUSH PRIVILEGES;"
```

### "Datasource não configurado"
**Possível causa:** V18 não foi executada

**Solução:**
```bash
# Resetar flyway do master
mysql -u root -p12345 erpapi -e "DELETE FROM flyway_schema_history WHERE version >= 18;"

# Reiniciar aplicação
mvn spring-boot:run
```

### "tenant1_db already exists"
**Possível causa:** Banco anterior não foi totalmente deletado

**Solução:**
```bash
# Deletar bancos completamente
mysql -u root -p12345 -e "DROP DATABASE IF EXISTS erpapi, tenant1_db, shared_db;"

# Reiniciar aplicação
mvn spring-boot:run
```

---

## 📞 Suporte

Se algo não funcionar:

1. Verifique logs da aplicação (procure por "TenantMigrationService")
2. Execute `verify_tenant_migrations.sh` para diagnóstico
3. Consulte `FLYWAY_TENANT_MIGRATIONS.md` para detalhes técnicos
4. Verifique conectividade MySQL: `mysql -u root -p12345`

---

**Pronto para teste de inicialização limpa!** 🚀
