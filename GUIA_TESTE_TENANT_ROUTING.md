# 🧪 GUIA DE TESTE - Validar Problema do Tenant Routing

## 📋 Checklist de Validação

Use este guia para confirmar EXATAMENTE onde o problema está.

---

## TESTE 1: Verificar Logs (MAIS IMPORTANTE)

### Passo 1.1: Limpar Logs
```bash
# Se estiver usando arquivo de log, limpe-o
rm -f logs/app.log

# Se estiver observando console, note a hora atual: HH:MM:SS
```

### Passo 1.2: Fazer Requisição com Header X-Tenant-Id

```bash
curl -v \
  -H "X-Tenant-Id: 1" \
  -H "Authorization: Bearer {TOKEN_JWT}" \
  http://localhost:8080/api/v1/clientes
```

### Passo 1.3: Buscar por estes logs (em ordem)

**Log 1 - TenantFilter**
```
Tenant ID definido no contexto: 1
```
Se VER: ✅ TenantFilter funcionando  
Se NÃO VER: ❌ TenantFilter não foi acionado

**Log 2 - MultiTenantRoutingDataSource**
```
Buscando DataSource em cache para tenant ID: '1'
```
Se VER: ✅ TenantContext.getTenantId() retornou 1  
Se NÃO VER: ❌ TenantContext está null

**Log 3 - DataSourceFactory**
```
✅ Datasource criado e armazenado em cache para tenant: '1'
Ou: ✅ DataSource já estava em cache para ID: '1'
```
Se VER: ✅ Datasource correto foi usado  
Se NÃO VER: ❌ Usou defaultDataSource (master)

**Log 4 - TenantFilterActivator** (O MAIS IMPORTANTE)
```
Filtro de tenantId ativado para tenant ID: 1
```
Se VER: ✅ Row-level security ativada  
Se NÃO VER: ❌ **PROBLEMA ENCONTRADO!** TenantFilterActivator não foi chamado

---

## TESTE 2: Fazer Requisição SEM Header (Apenas JWT)

### Passo 2.1: Requisição com JWT mas SEM header X-Tenant-Id

```bash
curl -v \
  -H "Authorization: Bearer {TOKEN_JWT}" \
  http://localhost:8080/api/v1/clientes
```

### Passo 2.2: Buscar por logs

**Esperado:**
```
Tenant ID definido no contexto: 1  ← Vindo do JWT, não do header!
```

**Real (Quebrado):**
```
Tenant ID definido no contexto: X  ← NÃO APARECE
Usando defaultDataSource (banco master)  ← Fallback
```

Se NÃO aparecer o log "Tenant ID definido", confirma:
- ❌ BearerTokenFilter NÃO está setando TenantContext
- ❌ Será usado banco master (padrão)

---

## TESTE 3: Validar Isolamento de Dados

### Passo 3.1: Criar dados em tenant 1

```bash
# Login como user@tenant1.com
TOKEN_T1=$(curl -X POST http://localhost:8080/api/v1/usuarios/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@tenant1.com","password":"pass"}' \
  | jq '.token')

# Criar cliente em tenant 1
curl -X POST http://localhost:8080/api/v1/clientes \
  -H "Authorization: Bearer $TOKEN_T1" \
  -H "Content-Type: application/json" \
  -d '{"nome":"Cliente T1","email":"cli@tenant1.com"}' | jq '.id' > /tmp/client_t1.txt

CLIENT_T1=$(cat /tmp/client_t1.txt)
echo "Cliente T1 ID: $CLIENT_T1"
```

### Passo 3.2: Criar dados em tenant 2

```bash
# Login como user@tenant2.com
TOKEN_T2=$(curl -X POST http://localhost:8080/api/v1/usuarios/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@tenant2.com","password":"pass"}' \
  | jq '.token')

# Criar cliente em tenant 2
curl -X POST http://localhost:8080/api/v1/clientes \
  -H "Authorization: Bearer $TOKEN_T2" \
  -H "Content-Type: application/json" \
  -d '{"nome":"Cliente T2","email":"cli@tenant2.com"}' | jq '.id' > /tmp/client_t2.txt

CLIENT_T2=$(cat /tmp/client_t2.txt)
echo "Cliente T2 ID: $CLIENT_T2"
```

### Passo 3.3: Verificar isolamento

**Teste 3A - Tenant 1 vê APENAS seus dados:**
```bash
curl -H "Authorization: Bearer $TOKEN_T1" \
  http://localhost:8080/api/v1/clientes | jq '.data[].id'

# Esperado: Apenas $CLIENT_T1
# Se VER $CLIENT_T2 aqui = ❌ ISOLAMENTO QUEBRADO
```

**Teste 3B - Tenant 2 vê APENAS seus dados:**
```bash
curl -H "Authorization: Bearer $TOKEN_T2" \
  http://localhost:8080/api/v1/clientes | jq '.data[].id'

# Esperado: Apenas $CLIENT_T2
# Se VER $CLIENT_T1 aqui = ❌ ISOLAMENTO QUEBRADO
```

**Interpretação dos Resultados:**

| Teste 3A | Teste 3B | Diagnóstico |
|----------|----------|-------------|
| Só T1 | Só T2 | ✅ Tudo OK! |
| T1 + T2 | T1 + T2 | ❌ Isolamento completamente quebrado |
| Só T1 | T1 + T2 | ⚠️ Isolamento parcial (problema no filtering) |

---

## TESTE 4: Verificar Banco de Dados Usado

### Passo 4.1: Habilitar Logging do Hibernate

**application.yml ou application.properties:**
```yaml
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### Passo 4.2: Fazer Requisição e Ver SQL

```bash
curl -H "Authorization: Bearer {TOKEN}" \
  http://localhost:8080/api/v1/clientes 2>&1 | grep -i "select"
```

**Esperado (com filtering):**
```sql
select c1_0.id,c1_0.nome,c1_0.tenant_id from cliente c1_0 where c1_0.tenant_id=?
                                                                  ^^^^^^^^^^^^^^^^
                                                         Filtro automático do Hibernate!
```

**Real (quebrado):**
```sql
select c1_0.id,c1_0.nome,c1_0.tenant_id from cliente c1_0
                                            SEM WHERE CLAUSE!
```

Se NÃO tiver `where c1_0.tenant_id=?`, confirma:
- ❌ TenantFilterActivator não foi acionado
- ❌ Hibernate filter não está ativo

---

## TESTE 5: Debug Manual do TenantContext

### Passo 5.1: Criar Controller de Debug

```java
@RestController
@RequestMapping("/api/v1/debug")
public class DebugController {
    
    @GetMapping("/tenant-context")
    public ResponseEntity<?> getTenantContext() {
        Long tenantId = TenantContext.getTenantId();
        
        return ResponseEntity.ok(Map.of(
            "tenantId_from_context", tenantId,
            "tenantId_type", tenantId != null ? tenantId.getClass().getName() : "null",
            "is_null", tenantId == null
        ));
    }
    
    @GetMapping("/security-context")
    public ResponseEntity<?> getSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth instanceof BearerTokenAuthentication) {
            BearerTokenAuthentication bta = (BearerTokenAuthentication) auth;
            return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "principal_tenant_id", bta.getPrincipal().getTenantId(),
                "usuario_id", bta.getPrincipal().getUsuarioId()
            ));
        }
        
        return ResponseEntity.ok(Map.of("authenticated", false));
    }
}
```

### Passo 5.2: Chamar endpoints de debug

```bash
# Teste 5A - TenantContext
curl -H "Authorization: Bearer {TOKEN_TENANT_2}" \
  http://localhost:8080/api/v1/debug/tenant-context

# Esperado:
# {"tenantId_from_context": 2, "is_null": false}

# Quebrado:
# {"tenantId_from_context": null, "is_null": true}


# Teste 5B - SecurityContext
curl -H "Authorization: Bearer {TOKEN_TENANT_2}" \
  http://localhost:8080/api/v1/debug/security-context

# Esperado:
# {"authenticated": true, "principal_tenant_id": "2", "usuario_id": "123"}

# Este sempre vai funcionar porque BearerTokenFilter funciona
```

---

## TESTE 6: Verificar Registro em tenant_datasource

### Passo 6.1: Conectar ao banco master e verificar

```sql
-- No banco MASTER (não do tenant!)
SELECT td.*, t.nome
FROM tenant_datasource td
JOIN tenant t ON td.tenant_id = t.id
WHERE td.is_active = true;

-- Esperado:
-- | id | tenant_id | host | port | database_name | username | password | driver_class_name | is_active | nome   |
-- |----|-----------|------|------|---------------|----------|----------|-------------------|-----------|--------|
-- | 1  | 1         | ... | 3306 | tenant1_db    | root     | pass     | com.mysql.jdbc.Driver | 1         | T1     |
-- | 2  | 2         | ... | 3306 | tenant2_db    | root     | pass     | com.mysql.jdbc.Driver | 1         | T2     |
```

Se tiver valor NULL ou is_active = false:
- ❌ DataSourceFactory não conseguirá criar DataSource
- ❌ Fallback para banco master

---

## TESTE 7: Verificar Logs de DataSourceFactory

### Passo 7.1: Adicionar logs (temporário)

[DataSourceFactory.java](src/main/java/com/api/erp/v1/shared/infrastructure/config/datasource/routing/DataSourceFactory.java)

Procure por:
```java
log.debug("Criando DataSource para tenant: {}", tenantId);
```

Se ver isso no log, significa que foi chamado. Se NÃO ver:
- ❌ MultiTenantRoutingDataSource não encontrou em cache
- ❌ Ou TenantContext estava null

---

## 📊 MATRIZ DE DIAGNÓSTICO

### Baseado em Testes 1-7, determine o problema:

| Teste 1 | Teste 2 | Teste 3 | Teste 4 | Teste 5A | Teste 6 | Diagnóstico |
|---------|---------|---------|---------|----------|---------|-------------|
| ✅ | ❌ | ❌ | ❌ | null | ✅ | BearerTokenFilter não seta TenantContext |
| ✅ | ✅ | ❌ | ❌ | ✅ | ✅ | TenantFilterActivator não é chamado |
| ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Tudo funciona! |
| ❌ | ❌ | - | - | - | ✅ | TenantFilter não funciona |
| ✅ | ✅ | ✅ | ❌ | ✅ | ❌ | Banco master padrão (fallback) |

---

## 🎯 PADRÃO ESPERADO

Se tudo estiver funcionando corretamente, você DEVE VER nesta sequência:

```
1. [TenantFilter] Tenant ID definido no contexto: 1
2. [BearerTokenFilter] (deveria ter TenantContext.setTenantId() - ADICIONAR!)
3. [MultiTenantRoutingDataSource] Buscando DataSource em cache para tenant ID: '1'
4. [MultiTenantRoutingDataSource] ✅ DataSource já estava em cache para ID: '1'
   (ou: Datasource criado e armazenado em cache)
5. [TenantFilterActivator] ✅ Filtro de tenantId ativado para tenant ID: 1
6. [Hibernate] select c1_0.id,...,c1_0.tenant_id from cliente c1_0 where c1_0.tenant_id=?
7. [Controller] Retorna dados de tenant 1 apenas
8. [TenantFilter] (finally) TenantContext.clear()
```

Se o Log 5 NÃO aparecer:
- ❌ Problema #2: TenantFilterActivator não está sendo acionado

Se o Log 3 disser "null" ou "using defaultDataSource":
- ❌ Problema #1: TenantContext nunca recebeu valor

---

## 📝 COMO REPORTAR O PROBLEMA

Quando você rodar os testes, reporte:

1. **Teste 1 - Logs (OBRIGATÓRIO):**
   - Copie os 4 logs (TenantFilter, Buscando DataSource, ✅ Datasource, Filtro ativado)
   - Cole aqui qual aparece e qual não

2. **Teste 3 - Isolamento:**
   - Teste 3A retornou: [quais IDs]
   - Teste 3B retornou: [quais IDs]

3. **Teste 4 - SQL:**
   - Copie a query SELECT gerada

4. **Teste 5A - TenantContext:**
   - Cole a resposta JSON

5. **Teste 6 - tenant_datasource:**
   - Copie resultado da query SQL

---

**Data:** 14/01/2026  
**Próximo Passo:** Execute os testes e reporte os resultados!
