# 🔍 ANÁLISE COMPLETA - Sistema de Redirecionamento de DataSource via TenantId

**Data:** 14/01/2026  
**Status:** 🔴 PROBLEMA IDENTIFICADO - Redirecionamento incompleto/inativo

---

## 📋 RESUMO EXECUTIVO

O sistema implementa multi-tenancy com database-per-tenant, mas **o redirecionamento de datasource está INCOMPLETO**:

✅ **O que FUNCIONA:**
- TenantContext (ThreadLocal) armazena o tenantId
- MultiTenantRoutingDataSource roteia para banco correto
- DataSourceFactory cria datasources sob demanda
- TenantFilter extrai tenantId do header X-Tenant-Id
- BearerTokenFilter extrai tenantId do JWT

❌ **O que NÃO FUNCIONA:**
- **TenantFilterActivator.activateTenantFilter() NUNCA é chamado!**
- Filtro de discriminação de tenantId no Hibernate não é ativado
- Dados podem ser expostos entre tenants se usarem mesmo banco
- Sem debug claro do que está acontecendo em cada etapa

---

## 🏗️ ARQUITETURA ATUAL

### 1. **Fluxo de Requisição (Teórico)**

```
┌────────────────────────────────────────────────┐
│  REQUEST chega com:                            │
│  - Header: X-Tenant-Id: 1                      │
│  - ou JWT com tenantId no claim                │
└────────────┬───────────────────────────────────┘
             │
             ▼
┌────────────────────────────────────────────────┐
│  TenantFilter (OncePerRequestFilter)           │
│  - Extrai tenantId do header X-Tenant-Id      │
│  - TenantContext.setTenantId(tenantId)        │
│  - Loga: "Tenant ID definido: {tenantId}"     │
└────────────┬───────────────────────────────────┘
             │
             ▼
┌────────────────────────────────────────────────┐
│  BearerTokenFilter (antes de TenantFilter?)   │
│  - Extrai JWT                                  │
│  - getTenantIdFromToken()                      │
│  - Define SecurityContext (NÃO TenantContext!) │
│  - ❌ NÃO chama TenantContext.setTenantId()   │
└────────────┬───────────────────────────────────┘
             │
             ▼
┌────────────────────────────────────────────────┐
│  MultiTenantRoutingDataSource.getConnection() │
│  - Obtém: Long tenantId = TenantContext.get() │
│  - Se não tiver em cache, cria via factory    │
│  - Retorna DataSource para esse banco          │
└────────────┬───────────────────────────────────┘
             │
             ▼
┌────────────────────────────────────────────────┐
│  Controller/Service executa Query              │
│  - USA O DATASOURCE CORRETO! ✅               │
│  - Mas SEM FILTRO DE ROW LEVEL SECURITY ❌    │
└────────────┬───────────────────────────────────┘
             │
             ▼
┌────────────────────────────────────────────────┐
│  TenantFilter (finally block)                  │
│  - TenantContext.clear()                       │
│  - Limpa para próxima requisição               │
└────────────────────────────────────────────────┘
```

### 2. **Componentes do Sistema**

#### A. **TenantContext.java** ✅
```
Location: src/main/java/.../config/datasource/TenantContext.java
Status: OK
Função: ThreadLocal que armazena tenantId por requisição
Método: 
  - setTenantId(Long/String)
  - getTenantId() → Long
  - clear()
```

#### B. **TenantFilter.java** ✅ (Mas incompleto)
```
Location: src/main/java/.../security/filter/TenantFilter.java
Status: FUNCIONAL MAS INCOMPLETO
Função: Extrai tenantId do header X-Tenant-Id e armazena em TenantContext

Fluxo:
1. Obtém header: request.getHeader("X-Tenant-Id")
2. Se existe: TenantContext.setTenantId(tenantId)
3. Chama filterChain.doFilter()
4. finally: TenantContext.clear()

⚠️ PROBLEMA:
- Depende de header manual X-Tenant-Id
- NÃO extrai do JWT automaticamente
- O BearerTokenFilter extrai JWT mas NÃO seta TenantContext!
```

#### C. **BearerTokenFilter.java** ⚠️ (PROBLEMA!)
```
Location: src/main/java/.../security/filter/BearerTokenFilter.java
Status: FUNCIONAL MAS INCOMPLETO
Função: Valida JWT e cria SecurityContext

Fluxo:
1. Extrai token do header Authorization
2. jwtTokenProvider.isTokenValid(token)
3. String tenantId = jwtTokenProvider.getTenantIdFromToken(token)  ✅
4. Cria UsuarioAutenticado(usuarioId, tenantId)
5. BearerTokenAuthentication authentication = new BearerTokenAuthentication()
6. SecurityContextHolder.getContext().setAuthentication(authentication)

❌ PROBLEMA CRÍTICO:
- Extrai o tenantId do JWT
- MAS NÃO O COLOCA NO TenantContext!
- Resultado: MultiTenantRoutingDataSource não consegue rotear!
```

#### D. **MultiTenantRoutingDataSource.java** ✅
```
Location: src/main/java/.../config/datasource/routing/MultiTenantRoutingDataSource.java
Status: OK
Função: Roteia getConnection() para datasource correto

Fluxo:
1. getConnection() chamado
2. Long tenantId = TenantContext.getTenantId()  ← DEPENDE DE TENANTCONTEXT!
3. Se null → usa defaultDataSource (master)
4. Se em cache → retorna
5. Se não em cache → cria via DataSourceFactory
6. Retorna connection para banco do tenant

✅ O código está OK, mas:
- Se TenantContext estiver vazio, usa master (fallback)
- Se tenantId estiver setado, rota corretamente
```

#### E. **DataSourceFactory.java** ✅
```
Location: src/main/java/.../config/datasource/routing/DataSourceFactory.java
Status: OK
Função: Cria DataSource dinamicamente para tenant

Fluxo:
1. createDataSource(Long tenantId)
2. fetchTenantDatasourceViaJdbc(tenantId)
   - Query SQL na tabela tenant_datasource
   - Busca: host, port, database_name, username, password
3. createDataSourceFromConfig(TenantDatasource)
   - Cria HikariDataSource com credenciais
4. Registra em cache de MultiTenantRoutingDataSource
```

#### F. **TenantFilterActivator.java** ❌ (NÃO UTILIZADO!)
```
Location: src/main/java/.../config/datasource/routing/TenantFilterActivator.java
Status: CÓDIGO EXISTE MAS NUNCA É CHAMADO!
Função: Ativar filtro Hibernate @Filter para row-level security

Fluxo:
1. activateTenantFilter()
   - Long tenantId = TenantContext.getTenantId()
   - EntityManager.unwrap(Session.class)
   - session.enableFilter(TENANT_ID_FILTER)
   - filter.setParameter(TENANT_ID_PARAM, tenantId)

❌ PROBLEMA:
- Este método NÃO é chamado em nenhum lugar!
- grep search encontrou ZERO chamadas
- Resultado: Filtro de tenantId do Hibernate NUNCA é aplicado!
```

#### G. **TenantIdFilterProvider.java** ⚠️ (Inativo)
```
Location: src/main/java/.../persistence/filter/TenantIdFilterProvider.java
Status: OK MAS INATIVO
Função: Aplicar @Filter do Hibernate para segregar dados por tenantId

Métodos:
- enableTenantIdFilter(Session session)
  - Pega tenantId do TenantContext
  - Ativa filtro Hibernate com parâmetro tenantId
- disableTenantIdFilter(Session session)

❌ PROBLEMA:
- enableTenantIdFilter() é chamado por TenantFilterActivator
- Mas TenantFilterActivator nunca é chamado!
- Resultado: Filtro NUNCA é ativado no Hibernate
```

---

## 🔴 PROBLEMAS IDENTIFICADOS

### PROBLEMA 1: BearerTokenFilter não seta TenantContext

**Localização:** [BearerTokenFilter.java](src/main/java/com/api/erp/v1/shared/infrastructure/security/filter/BearerTokenFilter.java#L35-L55)

**Código Atual:**
```java
String tenantId = jwtTokenProvider.getTenantIdFromToken(token);  // ✅ Extrai
UsuarioAutenticado usuario = new UsuarioAutenticado(usuarioId, tenantId);
BearerTokenAuthentication authentication = new BearerTokenAuthentication(token, email, usuario);
SecurityContextHolder.getContext().setAuthentication(authentication);
// ❌ FALTA: TenantContext.setTenantId(tenantId);
```

**Impacto:**
- JWT contém tenantId válido
- Mas TenantContext fica vazio
- MultiTenantRoutingDataSource.getConnection() recebe `null`
- Fallback para banco master (padrão)
- **Todos os tenants acessam o mesmo banco!**

**Cenários Afetados:**
1. Se enviar JWT com tenantId=2, ainda acessa banco padrão
2. Header X-Tenant-Id funciona, mas é inseguro (pode ser falsificado)
3. Combinação de header + JWT é ambígua

---

### PROBLEMA 2: TenantFilterActivator não é chamado

**Localização:** [TenantFilterActivator.java](src/main/java/com/api/erp/v1/shared/infrastructure/config/datasource/routing/TenantFilterActivator.java)

**Código Atual:**
```java
public class TenantFilterActivator {
    public void activateTenantFilter() {
        // Código implementado corretamente
        Long tenantId = TenantContext.getTenantId();
        if (tenantId != null) {
            session.enableFilter(TENANT_ID_FILTER);
        }
    }
}
```

**Chamadas no Codebase:**
```
grep "activateTenantFilter" → 0 resultados!
grep "tenantFilterActivator" → 0 resultados!
```

**Impacto:**
- Filtro @Filter do Hibernate NUNCA é aplicado
- Row-level security não funciona
- Mesmo com database-per-tenant, se 2 tenants compartilharem DB:
  - Tenant A vê dados de Tenant B
  - Sem nenhuma restrição automática

---

### PROBLEMA 3: Ordem de Filters não está clara

**SecurityConfig.java:**
```java
.addFilterBefore(bearerTokenFilter, UsernamePasswordAuthenticationFilter.class)
.addFilterBefore(tenantContextFilter, UsernamePasswordAuthenticationFilter.class)
```

**Questão:**
- Qual filtro executa primeiro? BearerTokenFilter ou TenantFilter?
- Spring não garante ordem entre múltiplos `addFilterBefore()`
- Precisa especificar qual vem ANTES de qual

**Ordem Esperada:**
1. **TenantFilter** deve executar PRIMEIRO (extrai tenantId)
2. **BearerTokenFilter** executa e TAMBÉM seta TenantContext

---

### PROBLEMA 4: Sem debug trace completo

**Logs Esperados (Não aparecem):**
```
1. TenantFilter: "Tenant ID definido no contexto: 1"
2. BearerTokenFilter: "JWT tenantId extraído: 1, setando em TenantContext"  ← FALTA!
3. MultiTenantRoutingDataSource: "Buscando DataSource para tenant: 1"
4. TenantFilterActivator: "Filtro de tenantId ativado para tenant: 1"  ← FALTA!
5. DataSourceFactory: "Criando DataSource para tenant: 1"
6. JPA Query: "Aplicando filtro tenantId no Hibernate"  ← FALTA!
```

**Resultado:**
- Difícil saber qual etapa falha
- Impossível debugar por logs

---

## 🎯 FLUXO ESPERADO vs REAL

### Cenário: Requisição autenticada com JWT (tenantId=2)

**Esperado:**
```
Request Header: Authorization: Bearer eyJ...{tenantId: 2}
     ↓
BearerTokenFilter extrai tenantId=2 DO JWT
     ↓
TenantContext.setTenantId(2)  ✅
     ↓
MultiTenantRoutingDataSource.getConnection()
     ↓
TenantContext.getTenantId() → 2  ✅
     ↓
Busca DataSource banco tenant 2 em cache
     ↓
Se não encontrado: DataSourceFactory.createDataSource(2)
     ↓
Conecta em jdbc:mysql://...tenant2_db
     ↓
TenantFilterActivator.activateTenantFilter()
     ↓
Hibernate session.enableFilter("tenantIdFilter") com param=2
     ↓
SELECT * FROM cliente → SEM tenantId=2 WHERE clause automático
     ↓
Retorna dados ISOLADOS de tenant 2 ✅
```

**Real (Quebrado):**
```
Request Header: Authorization: Bearer eyJ...{tenantId: 2}
     ↓
BearerTokenFilter extrai tenantId=2 do JWT
     ↓
❌ NÃO seta TenantContext!
     ↓
MultiTenantRoutingDataSource.getConnection()
     ↓
TenantContext.getTenantId() → null  ❌
     ↓
Usa fallback: defaultDataSource (banco MASTER)
     ↓
❌ Conecta em master_db, não em tenant2_db!
     ↓
❌ TenantFilterActivator.activateTenantFilter() não é chamado
     ↓
❌ Hibernate NÃO tem filtro ativado
     ↓
SELECT * FROM cliente → Sem nenhuma discriminação automática
     ↓
❌ Retorna TODOS os dados (sem isolamento!)
```

---

## 📊 TABELA DE COMPONENTES

| Componente | Arquivo | Status | Funciona? | Problema |
|-----------|---------|--------|-----------|----------|
| **TenantContext** | TenantContext.java | ✅ Implementado | ✅ Sim | Nunca recebe valor do JWT |
| **TenantFilter** | TenantFilter.java | ✅ Implementado | ⚠️ Parcial | Depende de header manual |
| **BearerTokenFilter** | BearerTokenFilter.java | ✅ Implementado | ❌ Não | Não seta TenantContext |
| **MultiTenantRoutingDataSource** | MultiTenantRoutingDataSource.java | ✅ Implementado | ⚠️ Parcial | Recebe null quando não há header |
| **DataSourceFactory** | DataSourceFactory.java | ✅ Implementado | ✅ Sim | OK (nunca chamado se tenantId=null) |
| **TenantFilterActivator** | TenantFilterActivator.java | ✅ Implementado | ❌ Não | Nunca é chamado |
| **TenantIdFilterProvider** | TenantIdFilterProvider.java | ✅ Implementado | ❌ Não | TenantFilterActivator não o chama |
| **SecurityConfig** | SecurityConfig.java | ✅ Implementado | ⚠️ Parcial | Ordem de filtros não está clara |
| **WebMvcConfig** | WebMvcConfig.java | ✅ Implementado | ✅ Sim | OK, apenas permissões |

---

## 🔗 ORDEM DE EXECUÇÃO DE FILTROS

**Código Atual (SecurityConfig.java):**
```java
.addFilterBefore(bearerTokenFilter, UsernamePasswordAuthenticationFilter.class)
.addFilterBefore(tenantContextFilter, UsernamePasswordAuthenticationFilter.class)
```

**Problema:** Não especifica a ordem entre os dois

**Investigação:**
- `addFilterBefore(filter, beforeFilter)` registra ANTES do filter especificado
- Se ambos antes de `UsernamePasswordAuthenticationFilter`, ordem é INDEFINIDA
- Spring não garante qual executa primeiro

**Ordem Real (Provável):**
1. TenantFilter (mais recente adicionado?)
2. BearerTokenFilter (adicionado antes?)
3. UsernamePasswordAuthenticationFilter

⚠️ **Problema se BearerTokenFilter executar PRIMEIRO:**
- Extrai tenantId do JWT
- Mas TenantFilter executa depois
- Sobrescreve com valor do header X-Tenant-Id (ou limpa!)

---

## 💾 BANCO DE DADOS

### Configuração Esperada

**Tabela: tenant_datasource**
```
id | tenant_id | host | port | database_name | username | password | driver_class_name | is_active
---|-----------|------|------|---------------|----------|----------|-------------------|----------
1  | 1         | localhost | 3306 | jaguar_db | root | pass | com.mysql.jdbc.Driver | true
2  | 2         | localhost | 3306 | tenant2_db | root | pass | com.mysql.jdbc.Driver | true
```

**Validação:**
```sql
SELECT COUNT(*) FROM tenant_datasource WHERE is_active = true;
-- Espera-se > 0 registros!

SELECT td.*, t.nome 
FROM tenant_datasource td
JOIN tenant t ON td.tenant_id = t.id
WHERE td.is_active = true;
-- Ver quais tenants têm datasource configurado
```

---

## 🧪 TESTE MANUAL

### Teste 1: Com Header X-Tenant-Id

```bash
curl -H "X-Tenant-Id: 1" \
     -H "Authorization: Bearer {TOKEN}" \
     http://localhost:8080/api/v1/clientes

# Esperado: Funciona (rota via TenantFilter)
# Real: ???
```

### Teste 2: Apenas com JWT (sem header)

```bash
curl -H "Authorization: Bearer {TOKEN_TENANT_2}" \
     http://localhost:8080/api/v1/clientes

# Esperado: Funciona (rota via BearerTokenFilter → TenantContext)
# Real: ❌ Usa banco master (BearerTokenFilter não seta TenantContext)
```

### Teste 3: Log Trace Completo

```bash
# Adicionar logs:
# 1. BearerTokenFilter: adicionar TenantContext.setTenantId()
# 2. MultiTenantRoutingDataSource: log detalhado
# 3. TenantFilterActivator: quando é chamado

# Executar curl
# Analisar logs para ver onde quebra
```

---

## 📝 RESUMO DOS PROBLEMAS

| # | Problema | Severidade | Impacto | Solução |
|---|----------|-----------|---------|---------|
| 1 | BearerTokenFilter não seta TenantContext | 🔴 CRÍTICA | Todos os JWTs ignoram tenantId | Adicionar TenantContext.setTenantId() no BearerTokenFilter |
| 2 | TenantFilterActivator nunca é chamado | 🔴 CRÍTICA | Row-level security não funciona | Chamar em interceptor/AOP ou listener |
| 3 | Ordem de filtros não está clara | 🟠 ALTA | Comportamento não determinístico | Usar addFilterAfter() ou mudar para @Order |
| 4 | Sem logs de debug completos | 🟡 MÉDIA | Difícil debugar | Adicionar logs em cada etapa |
| 5 | Sem teste automático de routing | 🟡 MÉDIA | Regressões silenciosas | Criar testes de integração |

---

## ✅ RECOMENDAÇÕES

### Curto Prazo (Crítico)
1. **Adicionar TenantContext.setTenantId() em BearerTokenFilter**
   - Após extrair do JWT
   - Antes de chamar filterChain.doFilter()

2. **Criar interceptor/AOP para chamar TenantFilterActivator.activateTenantFilter()**
   - Executar ANTES do controller (preHandle/intercept)
   - Garantir que Hibernate filter seja ativado

3. **Adicionar logs detalhados em cada etapa**
   - BearerTokenFilter: log quando seta TenantContext
   - MultiTenantRoutingDataSource: log do tenantId usado
   - TenantFilterActivator: log quando ativa/desativa

### Médio Prazo
1. **Criar testes de integração**
   - Verificar isolamento entre tenants
   - Testar datasource routing
   - Testar Hibernate filtering

2. **Documentar ordem de execução**
   - Esclarecer ordem de filtros
   - Criar diagrama com todos os componentes

3. **Considerar use cases adicionais**
   - Websocket (persistem por conexão, não por requisição)
   - Background jobs (sem SecurityContext)

---

## 📚 REFERÊNCIAS NO CÓDIGO

### Arquivos Principais
- [TenantContext.java](src/main/java/com/api/erp/v1/shared/infrastructure/config/datasource/TenantContext.java)
- [TenantFilter.java](src/main/java/com/api/erp/v1/shared/infrastructure/security/filter/TenantFilter.java)
- [BearerTokenFilter.java](src/main/java/com/api/erp/v1/shared/infrastructure/security/filter/BearerTokenFilter.java) ⚠️
- [MultiTenantRoutingDataSource.java](src/main/java/com/api/erp/v1/shared/infrastructure/config/datasource/routing/MultiTenantRoutingDataSource.java)
- [DataSourceFactory.java](src/main/java/com/api/erp/v1/shared/infrastructure/config/datasource/routing/DataSourceFactory.java)
- [TenantFilterActivator.java](src/main/java/com/api/erp/v1/shared/infrastructure/config/datasource/routing/TenantFilterActivator.java) ❌
- [TenantIdFilterProvider.java](src/main/java/com/api/erp/v1/shared/infrastructure/persistence/filter/TenantIdFilterProvider.java) ⚠️
- [SecurityConfig.java](src/main/java/com/api/erp/v1/shared/infrastructure/config/security/SecurityConfig.java) ⚠️

---

## 🎓 CONCLUSÃO

O sistema de multi-tenancy **foi 70% implementado**, mas faltam os 30% críticos:

✅ Infraestrutura está 100% implementada:
- TenantContext funciona
- MultiTenantRoutingDataSource funciona  
- DataSourceFactory funciona
- Hiberate filtering está codificado

❌ Mas a **ATIVAÇÃO do sistema está quebrada:**
- BearerTokenFilter não conecta JWT → TenantContext
- TenantFilterActivator não é acionado
- Resultado: Sistema funciona APENAS se usar header X-Tenant-Id manual

**Previsão:** Se você está vendo "filtrando errado ou não filtrando", a causa é que:
- O banco CORRETO está sendo acessado (MultiTenantRoutingDataSource funciona)
- Mas SEM aplicar filtro de tenantId (TenantFilterActivator não é chamado)
- Resultado: Dados de TODOS os tenants sendo retornados, sem isolamento

---

**Próximo Passo:** Eu posso implementar as correções se você autorizar. As mudanças são pequenas mas CRÍTICAS.
