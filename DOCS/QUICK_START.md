# 🚀 QUICK START - Multi-Tenancy

## ⚡ 5 Minutos

### 1. Iniciar Aplicação
```bash
mvn clean install
# Migrations executam automaticamente
# TenantsConfiguration registra MultiTenantRoutingDataSource
```

### 2. Configurar Tenant (Admin)
```bash
curl -X POST http://localhost:8080/api/v1/tenant/database/jaguar/datasource \
  -H "Authorization: Bearer {ADMIN_TOKEN}" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Slug: jaguar" \
  -H "X-Tenant-Id: 1" \
  -d '{
    "host": "localhost",
    "port": 3306,
    "databaseName": "jaguar_db",
    "username": "jaguar_user",
    "password": "jaguar_pass",
    "driverClassName": "com.mysql.cj.jdbc.Driver",
    "dialect": "org.hibernate.dialect.MySQL8Dialect"
  }'
```

### 3. Fazer Requisição (User)
```bash
curl -X POST http://localhost:8080/api/v1/clientes \
  -H "Authorization: Bearer {USER_TOKEN}" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Slug: jaguar" \
  -H "X-Tenant-Id: 1" \
  -d '{"nome":"Cliente A"}'
```

✅ Pronto! Cliente salvo com `tenant_id = 1`

---

## 📋 Checklist Essencial

```
ANTES DE RODAR:
- [ ] JWT contém tenantSlug e tenantId
- [ ] Headers X-Tenant-Slug e X-Tenant-Id enviados

NA INICIALIZAÇÃO:
- [ ] Verificar logs: "Migrations applied successfully"
- [ ] Verificar: "MultiTenantRoutingDataSource registered"
- [ ] Verificar DB: tabela tenant_datasource criada

AO USAR:
- [ ] Configurar datasource de cada tenant
- [ ] Entidades têm @Filter e extends TenantAwareBaseEntity
- [ ] Serviços definem tenantId ao salvar
- [ ] Listar dados mostra apenas do tenant correto
```

---

## 🔗 Endpoints Disponíveis

### Configurar Datasource
```
POST /api/v1/tenant/database/{tenantSlug}/datasource
```

### Obter Datasource
```
GET /api/v1/tenant/database/{tenantSlug}/datasource
```

### Atualizar Datasource
```
PUT /api/v1/tenant/database/{tenantSlug}/datasource
```

### Testar Conexão
```
POST /api/v1/tenant/database/{tenantSlug}/datasource/test
```

---

## 🎯 Entidades a Atualizar

Copie e modifique cada uma:

```java
@Entity
@Filter(name = "tenantIdFilter", condition = "tenant_id = :tenantId")
public class Cliente extends TenantAwareBaseEntity {
    private String nome;
    // ... resto dos campos
}
```

Aplique para:
- Cliente
- Contato
- Endereco
- Usuario
- Permissao
- Produto

---

## 💾 Serviços - Template

```java
@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository repository;
    
    public Cliente criar(ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNome(request.nome());
        
        // ESSENCIAL:
        String tenantId = TenantContext.getTenantId();
        if (tenantId != null) {
            cliente.setTenantId(Long.parseLong(tenantId));
        }
        
        return repository.save(cliente);
    }
}
```

---

## 🧪 Teste Rápido

```bash
# 1. Criar em tenant A
POST /api/v1/clientes
Header: X-Tenant-Id: 1

# 2. Criar em tenant B
POST /api/v1/clientes
Header: X-Tenant-Id: 2

# 3. Listar em tenant A
GET /api/v1/clientes
Header: X-Tenant-Id: 1
# Deve retornar apenas cliente de A ✅

# 4. Listar em tenant B
GET /api/v1/clientes
Header: X-Tenant-Id: 2
# Deve retornar apenas cliente de B ✅
```

---

## ❌ Erros Comuns

| Erro | Solução |
|------|---------|
| "TenantSlug não definido" | Enviar header X-Tenant-Slug |
| "Banco não configurado" | Configurar datasource antes |
| "tenantId = null" | Chamar setTenantId no serviço |
| "Ver dados de outro tenant" | Adicionar @Filter na entidade |

---

## 📖 Mais Informações

Leia: `MULTITENANCY_IMPLEMENTATION_COMPLETE.md`
