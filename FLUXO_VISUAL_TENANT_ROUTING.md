# 🔄 FLUXO VISUAL - Tenant Routing (Esperado vs Real)

---

## ✅ FLUXO ESPERADO (Correto)

```
┌──────────────────────────────────────────────────────────────┐
│                     REQUEST INCOMING                         │
│                                                              │
│  POST /api/v1/clientes                                      │
│  Headers:                                                    │
│    Authorization: Bearer eyJ...{tenantId: 2}               │
│    Content-Type: application/json                           │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────────────────────┐
│              SECURITY FILTER CHAIN                           │
│                                                              │
│  1️⃣  TenantFilter                                           │
│      └─ Tenta extrair de header X-Tenant-Id                │
│      └─ Se vazio, log: "Tenant ID não encontrado no header" │
│      └─ TenantContext ainda = null (por enquanto)          │
│                                                              │
│  2️⃣  BearerTokenFilter                                      │
│      └─ Extrai JWT do Authorization header     ✅           │
│      └─ String tenantId = getTenantIdFromToken() ✅         │
│      └─ ✅ SETA: TenantContext.setTenantId(tenantId)       │
│      └─ SecurityContextHolder.setAuthentication()          │
│      └─ Log: "Tenant ID definido no contexto: 2" ✅        │
│                                                              │
│  3️⃣  UsernamePasswordAuthenticationFilter                   │
│      └─ Continua normalmente                                │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────────────────────┐
│           DISPATCH TO CONTROLLER                             │
│                                                              │
│  DispatcherServlet → ClienteController.listar()            │
│                                                              │
│  4️⃣  (BEFORE) Interceptor.preHandle()                      │
│      └─ TenantFilterActivator.activateTenantFilter() ✅    │
│      └─ Long tenantId = TenantContext.getTenantId()  = 2  │
│      └─ session.enableFilter("tenantIdFilter")            │
│      └─ filter.setParameter("tenant_id", 2)               │
│      └─ Log: "Filtro de tenantId ativado: 2" ✅           │
│                                                              │
│  5️⃣  GET DATABASE CONNECTION                                │
│      └─ MultiTenantRoutingDataSource.getConnection()      │
│      └─ Long tenantId = TenantContext.getTenantId() = 2  │
│      └─ dataSources.get(2) = HikariDataSource             │
│      └─ Log: "Obtendo conexão para tenant 2" ✅           │
│                                                              │
│  6️⃣  CONTROLLER EXECUTES QUERY                              │
│      └─ repository.findAll()                               │
│      └─ → Hibernate                                        │
│      └─ SQL: SELECT * FROM cliente WHERE tenant_id = ? [2]│
│      └─ ✅ Filtro automático do Hibernate! ✅             │
│      └─ ✅ Usa DataSource de tenant 2! ✅                │
│                                                              │
│      Result: [Cliente 1 (T2), Cliente 2 (T2)] ✅           │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────────────────────┐
│              RESPONSE RETURNED                               │
│                                                              │
│  HTTP 200 OK                                                │
│  Body: [                                                    │
│    {id: 1, nome: "Cliente T2", tenantId: 2},             │
│    {id: 2, nome: "Outro Cliente T2", tenantId: 2}        │
│  ]                                                          │
│                                                              │
│  7️⃣  (FINALLY) Cleanup                                     │
│      └─ TenantFilterActivator.deactivateTenantFilter()    │
│      └─ TenantFilter: TenantContext.clear()               │
│      └─ SecurityContextHolder.clearContext()              │
└──────────────────────────────────────────────────────────────┘
```

---

## ❌ FLUXO REAL (Quebrado)

```
┌──────────────────────────────────────────────────────────────┐
│                     REQUEST INCOMING                         │
│                                                              │
│  POST /api/v1/clientes                                      │
│  Headers:                                                    │
│    Authorization: Bearer eyJ...{tenantId: 2}               │
│    (SEM X-Tenant-Id)                                       │
│    Content-Type: application/json                           │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────────────────────┐
│              SECURITY FILTER CHAIN                           │
│                                                              │
│  1️⃣  TenantFilter                                           │
│      └─ Tenta extrair de header X-Tenant-Id                │
│      └─ ❌ Header não encontrado!                          │
│      └─ ❌ TenantContext.setTenantId() NÃO é chamado      │
│      └─ TenantContext = null 🔴                            │
│                                                              │
│  2️⃣  BearerTokenFilter                                      │
│      └─ Extrai JWT do Authorization header     ✅           │
│      └─ String tenantId = getTenantIdFromToken() ✅ = "2"  │
│      └─ ❌ FALTA: TenantContext.setTenantId(tenantId)     │
│      └─ SecurityContextHolder.setAuthentication()          │
│      └─ ❌ Log "Tenant ID definido..." NÃO aparece        │
│      └─ TenantContext ainda = null 🔴                      │
│                                                              │
│  3️⃣  UsernamePasswordAuthenticationFilter                   │
│      └─ Continua normalmente                                │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────────────────────┐
│           DISPATCH TO CONTROLLER                             │
│                                                              │
│  DispatcherServlet → ClienteController.listar()            │
│                                                              │
│  4️⃣  (BEFORE) Interceptor.preHandle()                      │
│      └─ TenantFilterActivator NÃO É CHAMADO ❌             │
│      └─ (Ninguém o chama em todo o codebase)               │
│      └─ ❌ activateTenantFilter() = não executado          │
│      └─ ❌ Hibernate filter NÃO é ativado                  │
│      └─ ❌ Nenhum log "Filtro ativado" 🔴                 │
│                                                              │
│  5️⃣  GET DATABASE CONNECTION                                │
│      └─ MultiTenantRoutingDataSource.getConnection()      │
│      └─ Long tenantId = TenantContext.getTenantId() = null│
│      └─ ❌ tenantId é NULL!                               │
│      └─ ❌ Fallback: defaultDataSource (BANCO MASTER!)    │
│      └─ ❌ Log: "Usando defaultDataSource" 🔴             │
│      └─ ❌ Conecta em master_db, não em tenant2_db!      │
│                                                              │
│  6️⃣  CONTROLLER EXECUTES QUERY                              │
│      └─ repository.findAll()                               │
│      └─ → Hibernate                                        │
│      └─ ❌ SQL: SELECT * FROM cliente                      │
│      └─ ❌ SEM WHERE tenant_id = 2                         │
│      └─ ❌ Sem nenhuma restrição automática                │
│      └─ ❌ Usa BANCO MASTER, não tenant2_db              │
│                                                              │
│      Result: [TODOS OS CLIENTES] ❌                        │
│        - Cliente 1 (T1) 🚨 Não deveria ver!             │
│        - Cliente 2 (T1) 🚨 Não deveria ver!             │
│        - Cliente 3 (T2) ✓ Apenas isto era esperado       │
│        - Cliente 4 (T2) ✓ E isto também                  │
│        - Cliente 5 (T3) 🚨 Não deveria ver!             │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────────────────────┐
│              RESPONSE RETURNED (INSEGURO!)                   │
│                                                              │
│  HTTP 200 OK                                                │
│  Body: [                                                    │
│    {id: 1, nome: "Cliente T1", tenantId: 1},  🚨           │
│    {id: 2, nome: "Cliente T1", tenantId: 1},  🚨           │
│    {id: 3, nome: "Cliente T2", tenantId: 2},             │
│    {id: 4, nome: "Cliente T2", tenantId: 2},             │
│    {id: 5, nome: "Cliente T3", tenantId: 3}   🚨           │
│  ]                                                          │
│                                                              │
│  🚨 Usuário de T2 vê dados de T1 e T3 também!            │
│  🚨 Isolamento COMPLETAMENTE QUEBRADO!                    │
│                                                              │
│  7️⃣  (FINALLY) Cleanup                                     │
│      └─ TenantContext.clear()                              │
│      └─ SecurityContextHolder.clearContext()              │
└──────────────────────────────────────────────────────────────┘
```

---

## 📊 COMPARAÇÃO LADO A LADO

### Cenário: JWT com tenantId=2, SEM header X-Tenant-Id

| Etapa | Esperado ✅ | Real ❌ | Log Esperado |
|-------|----------|--------|-------------|
| **TenantFilter** | Tenta header, falha gracefully | Tenta header, falha silenciosamente | (nenhum log) |
| **BearerTokenFilter Extrai** | Pega tenantId=2 do JWT ✅ | Pega tenantId=2 do JWT ✅ | (não visto) |
| **BearerTokenFilter Seta** | **TenantContext.setTenantId("2")** ✅ | **❌ NÃO SETA** | (falta este log) |
| **TenantContext.get()** | Retorna 2 ✅ | Retorna null ❌ | (falta este log) |
| **DataSource Selection** | Usa tenant2_db ✅ | Usa master_db ❌ | "Usando defaultDataSource" ❌ |
| **TenantFilterActivator** | Ativa filtro ✅ | ❌ NÃO CHAMADO | "Filtro ativado" ❌ |
| **Hibernate Filter** | WHERE tenant_id = 2 ✅ | SEM WHERE ❌ | (falta este log) |
| **Result** | 2 clientes (T2 apenas) ✅ | 10+ clientes (TODOS) ❌ | (logs mostram tudo) |

---

## 🎯 IDENTIFICAR O PROBLEMA

### Leia os logs procurando por:

#### Log 1 - Deve aparecer:
```
"Tenant ID definido no contexto: 2"
```
- Se **VER**: TenantContext foi setado (via TenantFilter ou BearerTokenFilter)
- Se **NÃO VER**: TenantContext está null ❌ **Problema #1 ou #2**

#### Log 2 - Deve aparecer:
```
"Buscando DataSource em cache para tenant ID: '2'"
```
- Se **VER**: MultiTenantRoutingDataSource.getConnection() achou tenantId
- Se **NÃO VER**: TenantContext.getTenantId() retornou null ❌

#### Log 3 - Deve aparecer:
```
"✅ Datasource já estava em cache para ID: '2'"
ou
"✅ Datasource criado e armazenado em cache para tenant: '2'"
```
- Se **VER**: Banco correto foi acessado ✅
- Se **NÃO VER**: Usou fallback (master) ❌

#### Log 4 - O MAIS IMPORTANTE:
```
"✅ Filtro de tenantId ativado para tenant ID: 2"
```
- Se **VER**: Row-level security funciona ✅
- Se **NÃO VER**: **Problema #2 CONFIRMADO!** ❌

---

## 🔍 RASTREAMENTO DE ERRO

Se você está vendo `SELECT * FROM cliente` SEM `WHERE tenant_id = ?`:

1. Log 4 não aparece? → **Problema #2**
2. Log 1 não aparece? → **Problema #1**
3. Log 3 mostra "defaultDataSource"? → **Problema #1**

---

## 📋 MATRIZ DE DECISÃO

```
                    BearerTokenFilter    TenantFilterActivator
                    seta tenantId?       é chamado?
                    
Caso 1:             ✅ SIM              ✅ SIM              → ✅ TUDO OK
Caso 2:             ❌ NÃO              ✅ SIM              → ❌ PROBLEMA #1
Caso 3:             ✅ SIM              ❌ NÃO              → ❌ PROBLEMA #2
Caso 4:             ❌ NÃO              ❌ NÃO              → ❌ PROBLEMA #1 + #2
```

---

## 🎬 PRÓXIMA AÇÃO

1. Execute um requisição com JWT (sem header X-Tenant-Id)
2. Procure pelos Logs 1-4 acima
3. Identifique qual falta
4. Confirme qual problema corresponde
5. Reporte para implementar solução!

---

**Diagrama criado para facilitar debugging!**
