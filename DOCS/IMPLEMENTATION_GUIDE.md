# 🔧 Guia de Implementação - Multi-Tenancy Database per Tenant

## 📝 Resumo da Estratégia

```
┌─────────────────────────────────────────────────────────────┐
│  Seu Sistema Atual                                          │
├─────────────────────────────────────────────────────────────┤
│  • Tenants registrados em tabela do banco master            │
│  • TenantContext com tenantId e tenantSlug                  │
│  • Hibernate SCHEMA multitenancy                            │
│                                                              │
│  O QUE ADICIONAMOS:                                          │
│  ✅ DATABASE per TENANT: cada tenant → seu próprio banco    │
│  ✅ ROW-based discrimination: tenantId filtra linhas       │
│  ✅ MultiTenantRoutingDataSource: roteia para banco certo   │
│  ✅ Hibernate @Filter: auto-filtra por tenantId            │
└─────────────────────────────────────────────────────────────┘
```

---

## 🎯 O Que Foi Implementado

### 1. **Entidade TenantDatasource** ✅
Armazena configuração de banco para cada tenant no banco master

```
master_db
├── tenant (existente)
│   ├── id: 1, nome: "jaguar"
│   ├── id: 2, nome: "hece"
│   └── id: 3, nome: "acme"
└── tenant_datasource (novo)
    ├── tenant_id: 1, host: "db1.company.com", db: "jaguar_db"
    ├── tenant_id: 2, host: "db1.company.com", db: "hece_db"
    └── tenant_id: 3, host: "db2.company.com", db: "acme_db"
```

### 2. **MultiTenantRoutingDataSource** ✅
Roteia conexões para o banco correto baseado em tenantSlug

```
Requisição "jaguar" → busca TenantDatasource → cria DataSource 
                   → conecta em jaguar_db (db1.company.com:3306)

Requisição "hece" → busca TenantDatasource → cria DataSource 
                 → conecta em hece_db (db1.company.com:3306)
```

### 3. **TenantRequestFilter** ✅
Extrai tenantSlug e tenantId do JWT automaticamente

```
JWT = {
  "tenantSlug": "jaguar",
  "tenantId": "1",
  "sub": "user@jaguar.com"
}
        ↓
TenantContext.setTenantSlug("jaguar")
TenantContext.setTenantId("1")
```

### 4. **TenantAwareBaseEntity** ✅
Classe base para entidades com suporte a tenantId

```java
@Entity
@Filter(name = "tenantIdFilter")
public class Cliente extends TenantAwareBaseEntity {
    private String nome;
    // tenantId herdado automaticamente
}
```

### 5. **Migrations SQL** ✅
- V002__Add_Tenant_Datasource.sql → cria tabela tenant_datasource
- V003__Add_Tenant_Id_Column.sql → adiciona coluna tenant_id

---

## 🛠️ Próximos Passos - O QUE VOCÊ PRECISA FAZER

### Passo 1: Verificar TenantRepository

Precisa de método `findByNome()`:

```java
// Verificar em: src/main/java/.../features/tenant/domain/repository/TenantRepository.java

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByNome(String nome);  // ← Deve existir ou criar
}
```

**Se não existir, adicionar:**
```java
Optional<Tenant> findByNome(String nome);
Optional<Tenant> findBySlug(String slug); // opcional, mas útil
```

---

### Passo 2: Atualizar Entidade Tenant

Adicionar campo `slug` (recomendado, mas opcional):

```java
@Entity
public class Tenant {
    @Id
    private Long id;
    
    private String nome;           // Ex: "JAGUAR"
    
    @Column(unique = true)
    private String slug;           // Ex: "jaguar" (em lowercase, sem espaços)
    
    // ... resto dos campos
}
```

**Se adicionar slug, adicionar método em TenantRepository:**
```java
Optional<Tenant> findBySlug(String slug);
```

---

### Passo 3: Executar Migrations

As migrations já foram criadas em:
- `src/main/resources/db/migration/master/V002__Add_Tenant_Datasource.sql`
- `src/main/resources/db/migration/master/V003__Add_Tenant_Id_Column.sql`

**Flyway executará automaticamente na inicialização do Spring.**

Verifique:
```bash
mvn clean install
# Ou rode direto:
mvn flyway:migrate
```

---

### Passo 4: Atualizar Entidades Existentes

**Para CADA entidade que precisa isolamento por tenant:**

**ANTES:**
```java
@Entity
public class Cliente {
    @Id
    private Long id;
    private String nome;
    // ... campos
}
```

**DEPOIS:**
```java
@Entity
@Filter(name = "tenantIdFilter", condition = "tenant_id = :tenantId")
public class Cliente extends TenantAwareBaseEntity {
    private String nome;
    // ... campos
}
```

**Entidades que provavelmente precisam:**
- [ ] Cliente
- [ ] Contato
- [ ] Endereco
- [ ] Usuario
- [ ] Permissao
- [ ] Produto
- [ ] Pedido
- [ ] Nota Fiscal
- [x] TenantDatasource (já com suporte)

---

### Passo 5: Atualizar Serviços (IMPORTANTE!)

Ao **salvar** entidades, definir tenantId:

**ANTES:**
```java
@Service
public class ClienteService {
    public Cliente criar(ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNome(request.nome());
        return repository.save(cliente);  // ❌ tenantId = null!
    }
}
```

**DEPOIS:**
```java
@Service
public class ClienteService {
    public Cliente criar(ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNome(request.nome());
        cliente.setTenantId(Long.parseLong(TenantContext.getTenantId())); // ✅
        return repository.save(cliente);
    }
}
```

**Ou criar um Serviço Base:**
```java
@Service
public abstract class TenantAwareService<T extends TenantAwareBaseEntity> {
    protected void setTenantId(T entity) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId != null) {
            entity.setTenantId(Long.parseLong(tenantId));
        }
    }
}

// Usar:
@Service
public class ClienteService extends TenantAwareService<Cliente> {
    public Cliente criar(ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNome(request.nome());
        setTenantId(cliente);  // ✅ Automático
        return repository.save(cliente);
    }
}
```

---

### Passo 6: Verificar JWT Claims

Seu JWT deve incluir:

```json
{
  "sub": "usuario@empresa.com",
  "tenantSlug": "jaguar",      // Nome único (ex: "jaguar", "hece")
  "tenantId": "1",              // ID numérico (ex: "1", "2")
  "iat": 1234567890,
  "exp": 1234567890
}
```

**Se usar JWT diferente, ajustar TenantRequestFilter:**

```java
// Em: src/main/java/.../config/filter/TenantRequestFilter.java

@Override
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
    // ... seu código de extração de JWT
    
    // Se tenantSlug estiver em outro campo:
    String tenantSlug = claims.get("seu_campo_slug");
    
    // Se tenantId estiver em outro campo:
    String tenantId = claims.get("seu_campo_id");
    
    TenantContext.setTenantSlug(tenantSlug);
    TenantContext.setTenantId(tenantId);
}
```

---

### Passo 7: Testar Configuração

**Endpoints para testar (já existem):**

1. **Configurar datasource de um tenant:**
```bash
POST /api/v1/tenant/database/jaguar/datasource
Authorization: Bearer <token_jaguar>
Content-Type: application/json

{
  "host": "localhost",
  "port": 3306,
  "databaseName": "jaguar_db",
  "username": "jaguar_user",
  "password": "jaguar_pass",
  "driverClassName": "com.mysql.cj.jdbc.Driver",
  "dialect": "org.hibernate.dialect.MySQL8Dialect"
}
```

2. **Obter datasource configurado:**
```bash
GET /api/v1/tenant/database/jaguar/datasource
Authorization: Bearer <token_jaguar>
```

3. **Testar conexão:**
```bash
POST /api/v1/tenant/database/jaguar/datasource/test
Authorization: Bearer <token_jaguar>
Content-Type: application/json

{
  "host": "localhost",
  "port": 3306,
  "databaseName": "test_db",
  "username": "test_user",
  "password": "test_pass",
  "driverClassName": "com.mysql.cj.jdbc.Driver",
  "dialect": "org.hibernate.dialect.MySQL8Dialect"
}
```

---

## 📊 Exemplo Prático Completo

### Cenário: JAGUAR com Matriz e Filiais

**1. Registrar Tenants no banco master:**
```sql
INSERT INTO tenant (nome, slug) VALUES
('JAGUAR Matriz', 'jaguar'),
('JAGUAR Filial SP', 'jaguar-sp'),
('JAGUAR Filial RJ', 'jaguar-rj');
```

**2. Configurar Datasources:**

```bash
# Para JAGUAR Matriz
POST /api/v1/tenant/database/jaguar/datasource
{
  "host": "db1.company.com",
  "port": 3306,
  "databaseName": "jaguar_db",
  "username": "jaguar_user",
  "password": "jaguar_pass123",
  "driverClassName": "com.mysql.cj.jdbc.Driver",
  "dialect": "org.hibernate.dialect.MySQL8Dialect"
}

# Para JAGUAR Filial SP
POST /api/v1/tenant/database/jaguar-sp/datasource
{
  "host": "db1.company.com",
  "port": 3306,
  "databaseName": "jaguar_db",  # ← MESMO banco!
  "username": "jaguar_user",
  "password": "jaguar_pass123",
  "driverClassName": "com.mysql.cj.jdbc.Driver",
  "dialect": "org.hibernate.dialect.MySQL8Dialect"
}
```

**3. Criar Clientes:**

```bash
# Token da Matriz (tenantSlug="jaguar", tenantId="1")
POST /api/v1/clientes
Authorization: Bearer {token_matriz}
{
  "nome": "Cliente A (Matriz)",
  "email": "clientea@matrix.com"
}
# Salvo com: tenantId = 1

# Token da Filial SP (tenantSlug="jaguar-sp", tenantId="2")
POST /api/v1/clientes
Authorization: Bearer {token_filial_sp}
{
  "nome": "Cliente B (Filial SP)",
  "email": "clienteb@sp.com"
}
# Salvo com: tenantId = 2
```

**4. Listar Clientes:**

```bash
# Requisição da Matriz
GET /api/v1/clientes
Authorization: Bearer {token_matriz}
# Retorna: Cliente A (tenantId=1)

# Requisição da Filial SP
GET /api/v1/clientes
Authorization: Bearer {token_filial_sp}
# Retorna: Cliente B (tenantId=2)
```

**Banco de dados (mesmo jaguar_db):**
```
cliente:
├── id=1, nome="Cliente A (Matriz)", tenant_id=1
└── id=2, nome="Cliente B (Filial SP)", tenant_id=2

# Matriz vê: SELECT * FROM cliente WHERE tenant_id = 1
# Filial vê:  SELECT * FROM cliente WHERE tenant_id = 2
# Ambas no mesmo banco!
```

---

## 🔍 Checklist Final

- [ ] TenantRepository tem método `findByNome()`
- [ ] Entidade Tenant tem campo `slug` (opcional mas recomendado)
- [ ] Migrations foram executadas (check: tabelas `tenant_datasource` e coluna `tenant_id`)
- [ ] Entidades estendidas com `TenantAwareBaseEntity` e `@Filter`
- [ ] Serviços configurando `tenantId` ao salvar
- [ ] JWT contém `tenantSlug` e `tenantId`
- [ ] TenantRequestFilter foi registrado (deve ser automático com @Component)
- [ ] Testes de datasource criados com sucesso
- [ ] Queries retornam dados do tenant correto
- [ ] Dados de um tenant NÃO são visíveis para outro

---

## ❓ Dúvidas Comuns

**P: Preciso criar um novo banco para cada tenant?**
R: Não é obrigatório. Você pode:
- DATABASE per TENANT: cada tenant tem seu banco
- DATABASE per REGION: múltiplos tenants em um banco (com tenantId)
- Misturado: alguns tenants em bancos separados, outros compartilhados

**P: Posso usar o mesmo banco para todos?**
R: Sim! Basta:
1. Registrar o mesmo host/db em TenantDatasource para todos
2. Entidades com tenantId separam automaticamente
3. Filtro @Filter garante isolamento

**P: O tenantId é obrigatório?**
R: Para ROW-based discrimination sim. Para DATABASE per TENANT puro, não.
Mas recomendo sempre usar: matriz + filiais precisam compartilhar.

**P: Como migrar dados existentes?**
R: 
1. Popular coluna `tenant_id` com um script SQL
2. Ou deixar NULL e popular via aplicação ao atualizar
3. Usar migration com UPDATE

---

## 📞 Próximo Passos

1. ✅ Código foi implementado
2. 🔄 Você precisa:
   - [ ] Atualizar entidades
   - [ ] Atualizar serviços
   - [ ] Executar migrations
   - [ ] Testar

Quer que eu ajude em algum desses pontos?

---

**Última atualização:** 11/01/2025 | **Status:** Pronto para Implementação
