# Tenant Routing Validation & Debug Logging

## ✅ O que foi adicionado

### 1. **Enhanced Error Handling in MultiTenantRoutingDataSource**
Agora quando um tenant slug não é encontrado, a requisição falha com error 500 e mensagem clara.

**Logs adicionados:**
- 🔍 `"Buscando DataSource em cache para tenant slug: '{}'"`  
- 📊 `"DataSource NÃO está em cache para slug '{}'. Tentando criar/obter do banco de dados..."`
- ❌ `"ERRO: DataSource não pode ser criado para tenant slug: '{}' - Verifique se existe registro em tenant_datasource!"`
- ✅ `"Datasource criado e armazenado em cache para tenant: '{}'"`
- 🔗 `"Obtendo conexão do DataSource para tenant slug: '{}'"`

### 2. **Better Debug Logging in DataSourceFactory**
Quando busca `tenant_datasource` no banco, agora mostra claramente:
- Se encontrou: `"✅ TenantDatasource ENCONTRADO para slug: '{}' - Tenant ID: {}"`
- Se NÃO encontrou: `"❌ TenantDatasource NÃO ENCONTRADO para slug: '{}' - Verifica: 1) existe tenant com nome='{}' na tb_tenant? 2) existe linha em tenant_datasource apontando para esse tenant? 3) is_active=true?"`

### 3. **Tenant Context Logging in TenantFilter**
Quando header é setado:
- ✅ `"📍 TenantFilter: Setando contexto - slug='{}' | id='{}'"` 
- ❌ `"❌ Headers de tenant são obrigatórios! tenantSlug={}, tenantId={}"`

---

## 🔄 Fluxo de Debug Agora Visível

### Caso 1: Slug ENCONTRADO (Roteamento Correto)
```
TenantFilter: 📍 Setando contexto - slug='a' | id='1'
MultiTenantRoutingDataSource: 🔍 Buscando DataSource em cache para tenant slug: 'a'
MultiTenantRoutingDataSource: 📊 DataSource NÃO está em cache para slug 'a'. Tentando criar/obter do banco de dados...
DataSourceFactory: ✅ TenantDatasource ENCONTRADO para slug: 'a' - Tenant ID: 2
DataSourceFactory: ✅ Configurando DataSource para tenant ID: 2 | DB: localhost:3306/a_db
MultiTenantRoutingDataSource: ✅ Datasource criado e armazenado em cache para tenant: 'a'
MultiTenantRoutingDataSource: 🔗 Obtendo conexão do DataSource para tenant slug: 'a'
AuthenticationService: Tenant: 1/a; Tentativa de login para email: admin@empresa.com
```
✅ Roteamento funcionando!

### Caso 2: Slug NÃO ENCONTRADO (Erro)
```
TenantFilter: 📍 Setando contexto - slug='invalid' | id='1'
MultiTenantRoutingDataSource: 🔍 Buscando DataSource em cache para tenant slug: 'invalid'
MultiTenantRoutingDataSource: 📊 DataSource NÃO está em cache para slug 'invalid'. Tentando criar/obter do banco de dados...
DataSourceFactory: ❌ TenantDatasource NÃO ENCONTRADO para slug: 'invalid' - Verifica: 
   1) existe tenant com nome='invalid' na tb_tenant?
   2) existe linha em tenant_datasource apontando para esse tenant? 
   3) is_active=true?
MultiTenantRoutingDataSource: ❌ ERRO: DataSource não pode ser criado para tenant slug: 'invalid' - Verifique se existe registro em tenant_datasource!

[500 Error Response]
{ "error": "Banco de dados não configurado para tenant: invalid" }
```
❌ Erro claro ao cliente!

---

## 🧪 Como Testar Agora

### Teste 1: Slug válido (deve funcionar)
```bash
curl -H "X-Tenant-Slug: master" \
     -H "X-Tenant-Id: 1" \
     http://localhost:8080/api/auth/login \
     -d '{"login":"admin@empresa.com", "password":"123456"}'
```
✅ Deve retornar token ou erro de senha, não erro de banco

### Teste 2: Slug inválido (deve dar erro 500 claro)
```bash
curl -H "X-Tenant-Slug: invalid_slug" \
     -H "X-Tenant-Id: 1" \
     http://localhost:8080/api/auth/login \
     -d '{"login":"admin@empresa.com", "password":"123456"}'
```
❌ Deve retornar HTTP 500 com message: `"Banco de dados não configurado para tenant: invalid_slug"`

---

## 📋 Verificar Dados no Banco

Para garantir que o routing está correto, rode no banco master:

```sql
-- Ver todos os tenants
SELECT id, nome FROM tb_tenant;

-- Ver todas as configurações de datasource
SELECT td.id, t.nome as tenant_slug, t.id as tenant_id, td.host, td.port, td.database_name, td.is_active
FROM tenant_datasource td
JOIN tb_tenant t ON td.tenant_id = t.id
ORDER BY td.tenant_id;
```

**Esperado:**
- Deve haver uma linha para `tenant.nome = 'master'` (ou qual for seu master)
- Deve haver uma linha para `tenant.nome = 'a'`
- Deve haver uma linha para `tenant.nome = 'TENS'`
- Todas com `is_active = true`
- Cada uma apontando para um `database_name` diferente (master, a_db, TENS_db, etc)

---

## 📝 Resumo das Mudanças

| Arquivo | O que mudou |
|---------|------------|
| TenantFilter.java | ✅ Adicionado @Slf4j, logs para slug/id setado |
| MultiTenantRoutingDataSource.java | ✅ Logs detalhados de cache/criação de datasource |
| DataSourceFactory.java | ✅ Logs diferenciados para encontrado/não encontrado |

**Todos os erros agora propagam corretamente se slug não existir em `tenant_datasource`.**
