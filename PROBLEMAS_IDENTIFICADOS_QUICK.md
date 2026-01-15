# 🔴 PROBLEMAS CRÍTICOS IDENTIFICADOS - Tenant Routing

## PROBLEMA #1: BearerTokenFilter NÃO seta TenantContext ⚠️ CRÍTICO

**Arquivo:** [BearerTokenFilter.java](src/main/java/com/api/erp/v1/shared/infrastructure/security/filter/BearerTokenFilter.java#L35-L55)

**Situação Atual:**
```java
String tenantId = jwtTokenProvider.getTenantIdFromToken(token);  // ✅ Extrai
UsuarioAutenticado usuario = new UsuarioAutenticado(usuarioId, tenantId);
BearerTokenAuthentication authentication = new BearerTokenAuthentication(token, email, usuario);
SecurityContextHolder.getContext().setAuthentication(authentication);
// ❌ NUNCA faz: TenantContext.setTenantId(tenantId);
```

**Causa do Problema:**
- JWT contém tenantId válido
- É extraído com sucesso
- Armazenado em SecurityContext (apenas)
- **MAS não é colocado em TenantContext (ThreadLocal)**

**Impacto:**
- MultiTenantRoutingDataSource.getConnection() faz: `Long tenantId = TenantContext.getTenantId()` → **null**
- Resultado: Usa fallback para `defaultDataSource` (banco master)
- Todos os tenants acessam o mesmo banco de dados!

**Teste para Confirmar:**
1. Fazer requisição SEM header X-Tenant-Id, apenas com JWT
2. Ver nos logs se usa "master" ou "tenant 2"
3. Se usar master = **PROBLEMA CONFIRMADO**

---

## PROBLEMA #2: TenantFilterActivator NUNCA é chamado ⚠️ CRÍTICO

**Arquivo:** [TenantFilterActivator.java](src/main/java/com/api/erp/v1/shared/infrastructure/config/datasource/routing/TenantFilterActivator.java)

**Situação Atual:**
```
Classe existe ✅
Método activateTenantFilter() implementado ✅
Mas ninguém CHAMA este método ❌

Resultado da busca: grep "activateTenantFilter" = 0 resultados
```

**Código do Método:**
```java
public void activateTenantFilter() {
    Long tenantId = TenantContext.getTenantId();
    if (tenantId != null) {
        Session session = entityManager.unwrap(Session.class);
        TenantIdFilterProvider.enableTenantIdFilter(session);  // ✅ Isso chamaria
        log.debug("Filtro de tenantId ativado para tenant: {}", tenantId);
    }
}
```

**O que DEVERIA acontecer:**
```
1. Request → TenantContext.setTenantId(2)
2. Controller é chamado
3. ANTES: TenantFilterActivator.activateTenantFilter() 
4. Hibernate Filter é ativado com tenantId=2
5. Query: SELECT * FROM cliente
6. Hibernate aplica automaticamente: WHERE tenantId = 2
7. Resultado: Dados isolados ✅
```

**O que REALMENTE acontece:**
```
1. Request → TenantContext.setTenantId(2)
2. Controller é chamado
3. ❌ TenantFilterActivator NÃO é acionado
4. ❌ Hibernate Filter NÃO é ativado
5. Query: SELECT * FROM cliente
6. ❌ SEM nenhuma restrição automática
7. Resultado: Todos os dados (sem isolamento!) ❌
```

**Impacto:**
- Row-level security NÃO funciona
- Se 2 tenants compartilharem banco de dados:
  - Tenant A vê dados de Tenant B
  - Sem nenhuma proteção automática

---

## PROBLEMA #3: Ordem de Filters não está explícita ⚠️ ALTA

**Arquivo:** [SecurityConfig.java](src/main/java/com/api/erp/v1/shared/infrastructure/config/security/SecurityConfig.java#L39-L40)

**Código Atual:**
```java
.addFilterBefore(bearerTokenFilter, UsernamePasswordAuthenticationFilter.class)
.addFilterBefore(tenantContextFilter, UsernamePasswordAuthenticationFilter.class)
```

**Problema:**
- Ambos registram ANTES de UsernamePasswordAuthenticationFilter
- Spring NÃO garante ordem entre eles
- Ordem real: INDEFINIDA

**Ordem Esperada:**
```
1. TenantFilter (extrai do header)
   ↓
2. BearerTokenFilter (extrai do JWT e DEVERIA setal TenantContext)
   ↓
3. UsernamePasswordAuthenticationFilter
```

**O que pode estar acontecendo:**
- BearerTokenFilter executando ANTES de TenantFilter
- Ou TenantFilter limpando o que BearerTokenFilter setou
- Comportamento não determinístico

---

## RESUMO: Por que NÃO está funcionando?

### Cenário Real de Teste:

**Você faz:**
```bash
curl -H "Authorization: Bearer eyJ...{tenantId:2}" \
     http://localhost:8080/api/v1/clientes
```

**O que deveria acontecer:**
```
1. JWT extraído → tenantId = 2 ✅
2. TenantContext.setTenantId(2) ✅
3. DataSource de tenant 2 usado ✅
4. Hibernate filter ativado com tenantId=2 ✅
5. Query: SELECT * FROM cliente WHERE tenantId=2 ✅
6. Resposta: Clientes de tenant 2 apenas ✅
```

**O que REALMENTE acontece:**
```
1. JWT extraído → tenantId = 2 ✅
2. ❌ TenantContext.setTenantId() NÃO é chamado (BearerTokenFilter falha)
3. ❌ TenantContext.getTenantId() retorna null
4. ❌ Fallback para banco master
5. ❌ TenantFilterActivator não é chamado
6. ❌ Query SEM filtro: SELECT * FROM cliente (SEM WHERE)
7. ❌ Resposta: TODOS os clientes (sem isolamento)
```

---

## CENÁRIOS AFETADOS

### ✅ Funciona (com header):
```bash
curl -H "X-Tenant-Id: 1" \
     -H "Authorization: Bearer {TOKEN}" \
     http://localhost:8080/api/v1/clientes
     
# TenantFilter extrai do header → funciona por sorte
```

### ❌ Quebrado (apenas JWT):
```bash
curl -H "Authorization: Bearer {TOKEN}" \
     http://localhost:8080/api/v1/clientes
     
# BearerTokenFilter extrai do JWT MAS não seta TenantContext
# Resultado: Falha silenciosa ou comportamento errado
```

### ❌ Quebrado (WebSocket, background jobs):
```
Não há header X-Tenant-Id em conexões persistentes
Não há SessionCreationPolicy
Sem nenhuma forma de manter TenantContext
```

---

## VALIDAÇÃO

**Para saber se está quebrado, check:**

1. **No log, você VÊ:**
   ```
   "Tenant ID definido no contexto: 1"  (do TenantFilter)
   "Buscando DataSource em cache para tenant: 1"
   "DataSource já estava em cache"
   "Obtendo conexão do DataSource para tenant: 1"
   ```
   ✅ Isso significa que TenantContext FOI setado

2. **Mas você NÃO VÊ:**
   ```
   "Filtro de tenantId ativado para tenant: 1"
   ```
   ❌ Isso significa TenantFilterActivator não foi chamado

3. **Resultado:**
   - Datasource correto foi usado ✅
   - Mas SEM filtro de isolamento ❌
   - Dados podem estar misturados!

---

## PRÓXIMAS AÇÕES

### Imediato (Você precisa fazer):

1. **Verificar logs da próxima requisição:**
   - Vê "Tenant ID definido"?
   - Vê "Filtro de tenantId ativado"?

2. **Confirmar qual header está sendo usado:**
   - Está enviando `X-Tenant-Id`?
   - Ou apenas JWT?

3. **Testar isolamento de dados:**
   - Login como tenant 1, buscar dados
   - Login como tenant 2, buscar dados
   - São dados DIFERENTES ou IGUAIS?

### Próximo passo de implementação:

1. Adicionar `TenantContext.setTenantId()` em BearerTokenFilter
2. Criar interceptor/AOP para chamar TenantFilterActivator
3. Adicionar logs em cada etapa
4. Criar testes de isolamento

---

**Data:** 14/01/2026  
**Status:** 🔴 ANÁLISE COMPLETA - Aguardando validação
