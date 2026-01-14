# 🔧 Guia de Integração - Multi-Tenancy DATABASE per TENANT

> **VERSÃO 2.0** - Adaptada para integração com código existente

---

## ⚠️ IMPORTANTE: Arquivos JÁ EXISTENTES

Seu projeto **JÁ TEM** implementações de multitenancy:

| Arquivo | Status | O que fazer |
|---------|--------|-----------|
| `TenantFilter.java` | ✅ Existe | **USAR** - já extrai tenantSlug e tenantId |
| `JwtTokenProvider.java` | ✅ Existe | **USA** - getTenantIdFromToken(), getTenantSlugFromToken() |
| `TenantContext.java` | ✅ Existe | ✅ Perfeito - guarda valores em ThreadLocal |
| `SecurityConfig.java` | ✅ Existe | ✅ Registra os filtros automaticamente |
| `BearerTokenFilter.java` | ✅ Existe | ✅ Valida JWT |

---

## 🎯 O Que FOI ADICIONADO

### 1. **Entidade TenantDatasource** ✅
```
src/main/java/.../features/tenant/domain/entity/TenantDatasource.java
```
Armazena configuração de banco para cada tenant

### 2. **MultiTenantRoutingDataSource** ✅
```
src/main/java/.../config/datasource/routing/MultiTenantRoutingDataSource.java
```
Roteia conexões para banco correto

### 3. **DataSourceFactory** ✅
```
src/main/java/.../config/datasource/routing/DataSourceFactory.java
```
Cria DataSources dinâmicos para tenants

### 4. **TenantSchemaService (IMPLEMENTADO)** ✅
```
src/main/java/.../features/tenant/infrastructure/service/TenantSchemaService.java
```
Gerencia configurações de datasources

### 5. **TenantDatasourceRepository** ✅
```
src/main/java/.../features/tenant/domain/repository/TenantDatasourceRepository.java
```
Acesso a dados de configurações

### 6. **TenantAwareBaseEntity** ✅
```
src/main/java/.../shared/domain/entity/TenantAwareBaseEntity.java
```
Classe base com suporte a tenantId

### 7. **Migrations SQL** ✅
```
src/main/resources/db/migration/master/V002__Add_Tenant_Datasource.sql
src/main/resources/db/migration/master/V003__Add_Tenant_Id_Column.sql
```

---

## 🔄 FLUXO DE FUNCIONAMENTO

```
┌──────────────────────────────────────────────────────────────┐
│  1. REQUISIÇÃO CHEGA                                          │
│     POST /api/v1/clientes                                    │
│     Headers:                                                  │
│       Authorization: Bearer {JWT com tenantId=1, tenantSlug} │
│       X-Tenant-Slug: jaguar                                  │
│       X-Tenant-Id: 1                                         │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────────────────────┐
│  2. TenantFilter (já existe!)                                │
│     - Extrai tenantSlug e tenantId dos headers              │
│     - Valida contra JWT                                      │
│     - Armazena em TenantContext (ThreadLocal)               │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────────────────────┐
│  3. MultiTenantRoutingDataSource.getConnection()             │
│     - Lê TenantContext.getTenantSlug() → "jaguar"           │
│     - Busca DataSource no cache                             │
│     - Se não encontrar, cria via DataSourceFactory          │
│     - DataSourceFactory busca TenantDatasource no DB master │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────────────────────┐
│  4. Hibernte se conecta ao banco correto                    │
│     - Jaguar: jaguar_db                                      │
│     - Hece: hece_db                                          │
│     - Cada tenant pode ter seu próprio banco!               │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────────────────────┐
│  5. Filtro @Filter aplica tenantId                          │
│     @Filter(name = "tenantIdFilter")                        │
│     public class Cliente extends TenantAwareBaseEntity      │
│                                                              │
│     WHERE tenant_id = 1 ← automático!                       │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────────────────────┐
│  6. RESPOSTA                                                  │
│     {                                                        │
│       "id": 1,                                               │
│       "nome": "Cliente A",                                   │
│       "tenantId": 1,  ← discriminação garantida            │
│       "createdAt": "2025-01-11"                            │
│     }                                                        │
└──────────────────────────────────────────────────────────────┘
```

---

## ✅ INTEGRAÇÃO - PASSO A PASSO

### Passo 1: Verificar TenantRepository ✅

Precisa ter método para buscar por nome:

```java
// Verificar em:
// src/main/java/.../features/tenant/domain/repository/TenantRepository.java

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    
    // Deve existir:
    Optional<Tenant> findByNome(String nome);
    
    // Ou criar slug:
    Optional<Tenant> findBySlug(String slug);
}
```

**Se não tiver, adicionar:**
```java
Optional<Tenant> findByNome(String nome);
```

---

### Passo 2: Modificar TenantsConfiguration ✅

A configuração já foi atualizada. Verificar:

```java
// Arquivo: src/main/java/.../config/datasource/manual/TenantsConfiguration.java

// Deve ter:
@Bean
@Primary
public MultiTenantRoutingDataSource multiTenantRoutingDataSource(
        DataSourceFactory dataSourceFactory) {
    return new MultiTenantRoutingDataSource(dataSourceFactory);
}

@Bean
public DataSourceFactory dataSourceFactory() {
    return new DataSourceFactory(null); // Injeção será feita
}
```

---

### Passo 3: Registrar DataSourceFactory no Spring ✅

Criar arquivo de configuração:

```java
// src/main/java/.../config/datasource/routing/DataSourceFactoryConfig.java

@Configuration
public class DataSourceFactoryConfig {
    
    @Bean
    public DataSourceFactory dataSourceFactory(
            TenantDatasourceRepository tenantDatasourceRepository) {
        return new DataSourceFactory(tenantDatasourceRepository);
    }
}
```

**OU** modificar DataSourceFactory para não precisar de construtor:

```java
@Component
@RequiredArgsConstructor
public class DataSourceFactory {
    
    private final TenantDatasourceRepository tenantDatasourceRepository;
    
    // Resto do código...
}
```

---

### Passo 4: Executar Migrations ✅

As migrations já foram criadas:
- `V002__Add_Tenant_Datasource.sql` - cria tabela tenant_datasource
- `V003__Add_Tenant_Id_Column.sql` - adiciona coluna tenant_id

Flyway executará automaticamente na inicialização.

---

### Passo 5: Atualizar Entidades (ROW-based discrimination)

Entidades precisam de `tenantId` e `@Filter`:

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

**Entidades a atualizar:**
- [ ] Cliente
- [ ] Contato
- [ ] Endereco
- [ ] Usuario
- [ ] Permissao
- [ ] Produto
- [ ] (outras entidades específicas)

---

### Passo 6: Atualizar Serviços (IMPORTANTE!)

Ao **SALVAR**, definir `tenantId`:

```java
@Service
@RequiredArgsConstructor
public class ClienteService {
    
    private final ClienteRepository repository;
    
    public Cliente criar(ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNome(request.nome());
        
        // ADICIONAR ISTO:
        String tenantId = TenantContext.getTenantId();
        if (tenantId != null) {
            cliente.setTenantId(Long.parseLong(tenantId));
        }
        
        return repository.save(cliente);
    }
}
```

**Ou criar classe base:**

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

### Passo 7: Testes Manuais ✅

**1. Configurar datasource do tenant:**
```bash
curl -X POST http://localhost:8080/api/v1/tenant/database/jaguar/datasource \
  -H "Authorization: Bearer {JWT_jaguar}" \
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

**2. Obter datasource:**
```bash
curl -X GET http://localhost:8080/api/v1/tenant/database/jaguar/datasource \
  -H "Authorization: Bearer {JWT_jaguar}" \
  -H "X-Tenant-Slug: jaguar" \
  -H "X-Tenant-Id: 1"
```

**3. Testar conexão:**
```bash
curl -X POST http://localhost:8080/api/v1/tenant/database/jaguar/datasource/test \
  -H "Authorization: Bearer {JWT_jaguar}" \
  -H "Content-Type: application/json" \
  -d '{
    "host": "localhost",
    "port": 3306,
    "databaseName": "test_db",
    "username": "test_user",
    "password": "test_pass",
    "driverClassName": "com.mysql.cj.jdbc.Driver",
    "dialect": "org.hibernate.dialect.MySQL8Dialect"
  }'
```

---

## 📊 Cenário: JAGUAR com Matriz e Filial

### 1. Registrar tenants:
```sql
INSERT INTO tenant (nome, slug) VALUES
('JAGUAR Matriz', 'jaguar'),
('JAGUAR Filial SP', 'jaguar-sp');
```

### 2. Configurar datasources:

**Matriz:**
```bash
POST /api/v1/tenant/database/jaguar/datasource
{
  "host": "db.company.com",
  "port": 3306,
  "databaseName": "jaguar_db",
  "username": "jaguar_user",
  "password": "jaguar_pass",
  "driverClassName": "com.mysql.cj.jdbc.Driver",
  "dialect": "org.hibernate.dialect.MySQL8Dialect"
}
```

**Filial SP (MESMO BANCO):**
```bash
POST /api/v1/tenant/database/jaguar-sp/datasource
{
  "host": "db.company.com",
  "port": 3306,
  "databaseName": "jaguar_db",  # ← MESMO BANCO!
  "username": "jaguar_user",
  "password": "jaguar_pass",
  "driverClassName": "com.mysql.cj.jdbc.Driver",
  "dialect": "org.hibernate.dialect.MySQL8Dialect"
}
```

### 3. Criar clientes:

**Matriz (JWT com tenantId=1, tenantSlug="jaguar"):**
```bash
POST /api/v1/clientes
{
  "nome": "Cliente A (Matriz)"
}
# Salvo com: tenant_id = 1
```

**Filial SP (JWT com tenantId=2, tenantSlug="jaguar-sp"):**
```bash
POST /api/v1/clientes
{
  "nome": "Cliente B (Filial SP)"
}
# Salvo com: tenant_id = 2
```

### 4. Listar (automático):

**Matriz vê:**
```bash
GET /api/v1/clientes
# SELECT * FROM cliente WHERE tenant_id = 1
# Retorna: Cliente A
```

**Filial vê:**
```bash
GET /api/v1/clientes
# SELECT * FROM cliente WHERE tenant_id = 2
# Retorna: Cliente B
```

**Mesmo banco (jaguar_db), dados completamente segregados!** ✅

---

## 🔍 Checklist FINAL

### Infraestrutura
- [ ] TenantDatasource.java criado
- [ ] MultiTenantRoutingDataSource.java criado
- [ ] DataSourceFactory.java criado
- [ ] TenantSchemaService.java implementado
- [ ] TenantDatasourceRepository.java criado
- [ ] TenantsConfiguration.java atualizado
- [ ] Migrations criadas (V002, V003)

### Entidades
- [ ] TenantAwareBaseEntity criado
- [ ] Cliente extends TenantAwareBaseEntity + @Filter
- [ ] Contato extends TenantAwareBaseEntity + @Filter
- [ ] Endereco extends TenantAwareBaseEntity + @Filter
- [ ] Usuario extends TenantAwareBaseEntity + @Filter
- [ ] Produto extends TenantAwareBaseEntity + @Filter
- [ ] (outras entidades)

### Serviços
- [ ] ClienteService configura tenantId ao salvar
- [ ] ContatoService configura tenantId ao salvar
- [ ] EnderecoService configura tenantId ao salvar
- [ ] UsuarioService configura tenantId ao salvar
- [ ] ProdutoService configura tenantId ao salvar
- [ ] (outros serviços)

### Validação
- [ ] JWT contém tenantSlug e tenantId
- [ ] TenantFilter extrai corretamente
- [ ] TenantContext armazena valores
- [ ] MultiTenantRoutingDataSource roteia corretamente
- [ ] Dados de um tenant não são visíveis a outro
- [ ] Matriz e Filiais podem compartilhar banco

---

## ❓ Dúvidas Comuns

**P: Preciso modificar TenantFilter?**
A: Não! TenantFilter já funciona perfeitamente. Ele extrai dos headers.

**P: E se quiser extrair também do JWT?**
A: JwtTokenProvider já tem `getTenantIdFromToken()` e `getTenantSlugFromToken()`. Pode modificar TenantFilter para usar se necessário.

**P: Preciso criar DataSource para cada entidade?**
A: Não. MultiTenantRoutingDataSource centraliza. Cria um DataSource por tenant automaticamente.

**P: E o @Filter de tenantId?**
A: Adicione em entidades que precisam discriminação. Não é obrigatório se usar DATABASE per TENANT puro, mas recomendo para máxima segurança (matriz + filiais).

**P: Quando rodar as migrations?**
A: Flyway roda automaticamente ao iniciar Spring. Não precisa fazer nada.

**P: Preciso fazer backup dos dados ao migrar?**
A: Recomendo testar em ambiente de DEV primeiro, depois migrar em PROD.

---

## 📞 Próximas Etapas

1. ✅ Código implementado
2. 🔄 Você precisa:
   - [ ] Verificar TenantRepository (findByNome)
   - [ ] Registrar DataSourceFactory como Bean
   - [ ] Atualizar entidades (extends + @Filter)
   - [ ] Atualizar serviços (setTenantId)
   - [ ] Executar migrations
   - [ ] Testar endpoints

Quer que eu ajude em algum desses passos?

---

**Última atualização:** 11/01/2025 | **Status:** Pronto para Implementação
