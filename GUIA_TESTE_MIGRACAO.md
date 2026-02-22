# Guia de Teste - Endpoint de Migração de Tenant Datasource

## 1. Pré-requisitos

- Servidor ERP API rodando na porta 8080
- Token JWT válido
- Um tenant com ID conhecido (ex: 123)
- Datasource configurado para o tenant

## 2. Testes com cURL

### 2.1 Enfileirar Migração (Sucesso)

```bash
# Variáveis
TENANT_ID=123
TOKEN="seu_token_jwt_aqui"

# Requisição
curl -v -X POST "http://localhost:8080/api/v1/tenant/database/datasource/migrate" \
  -H "X-Tenant-Id: ${TENANT_ID}" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json"
```

**Resposta Esperada (202 Accepted):**
```json
{
  "success": true,
  "taskId": "a1b2c3d4-e5f6-4a5b-9c8d-7e6f5a4b3c2d",
  "tenantId": 123,
  "tenantName": "Empresa ABC",
  "status": "PENDING",
  "statusDescription": "Aguardando execução",
  "enqueuedAt": "2024-02-22T10:30:45.123456",
  "message": "Migração enfileirada com sucesso"
}
```

### 2.2 Enfileirar Migração (Erro - Datasource não configurado)

```bash
# Tenant sem datasource
TENANT_ID=999
TOKEN="seu_token_jwt_aqui"

curl -v -X POST "http://localhost:8080/api/v1/tenant/database/datasource/migrate" \
  -H "X-Tenant-Id: ${TENANT_ID}" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json"
```

**Resposta Esperada (400 Bad Request):**
```json
{
  "success": false,
  "error": "Datasource não configurado para tenant: 999"
}
```

### 2.3 Enfileirar Migração (Erro - Sem Autenticação)

```bash
curl -v -X POST "http://localhost:8080/api/v1/tenant/database/datasource/migrate" \
  -H "X-Tenant-Id: 123" \
  -H "Content-Type: application/json"
```

**Resposta Esperada (401 Unauthorized ou 403 Forbidden)**

## 3. Testes com Postman

### 3.1 Configuração

1. **Base URL:** `http://localhost:8080`
2. **Headers:**
   - `Authorization: Bearer {{token}}`
   - `X-Tenant-Id: {{tenantId}}`
   - `Content-Type: application/json`

### 3.2 Criar Requisição

**Nome:** `Enfileirar Migração de Tenant Datasource`  
**Método:** `POST`  
**URL:** `{{base_url}}/api/v1/tenant/database/datasource/migrate`

### 3.3 Configurar Variáveis do Postman

```
base_url = http://localhost:8080
token = seu_token_jwt_aqui
tenantId = 123
```

## 4. Testes com REST Client (VSCode)

### 4.1 Criar arquivo `test-migration.http`

```http
### Enfileirar Migração - Sucesso
POST http://localhost:8080/api/v1/tenant/database/datasource/migrate
X-Tenant-Id: 123
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

### Monitorar Estatísticas da Fila
GET http://localhost:8080/api/v1/migrations/queue/stats
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

### Consultar Tarefa Específica
GET http://localhost:8080/api/v1/migrations/queue/tasks/a1b2c3d4-e5f6-4a5b-9c8d-7e6f5a4b3c2d
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

### Listar Todas as Tarefas
GET http://localhost:8080/api/v1/migrations/queue/tasks
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

### Listar Tarefas em Progresso
GET http://localhost:8080/api/v1/migrations/queue/tasks/in-progress
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

### Listar Tarefas que Falharam
GET http://localhost:8080/api/v1/migrations/queue/tasks/failed
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

### Listar Tarefas do Tenant
GET http://localhost:8080/api/v1/migrations/queue/tasks/tenant/123
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 5. Testes com Java (JUnit)

### 5.1 Teste Unitário

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TenantDataSourceMigrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testEnqueueMigrationSuccess() throws Exception {
        mockMvc.perform(
                post("/api/v1/tenant/database/datasource/migrate")
                        .header("X-Tenant-Id", "123")
                        .header("Authorization", "Bearer valid_token")
                        .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.taskId").isNotEmpty())
        .andExpect(jsonPath("$.status").value("PENDING"))
        .andExpect(jsonPath("$.message").value("Migração enfileirada com sucesso"));
    }

    @Test
    void testEnqueueMigrationNoDatasource() throws Exception {
        mockMvc.perform(
                post("/api/v1/tenant/database/datasource/migrate")
                        .header("X-Tenant-Id", "999")
                        .header("Authorization", "Bearer valid_token")
                        .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error").containsString("Datasource não configurado"));
    }

    @Test
    void testEnqueueMigrationUnauthorized() throws Exception {
        mockMvc.perform(
                post("/api/v1/tenant/database/datasource/migrate")
                        .header("X-Tenant-Id", "123")
                        .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isUnauthorized());
    }
}
```

## 6. Cenários de Teste

### 6.1 Casos de Sucesso ✅

| # | Cenário | Tipo | Esperado |
|---|---------|------|----------|
| 1 | Enfileirar migração válida | Normal | 202 Accepted + taskId |
| 2 | Múltiplas migrações sequenciais | Carga | Todas enfileiradas corretamente |
| 3 | Monitorar progresso da fila | Integração | Status muda PENDING → IN_PROGRESS → COMPLETED |
| 4 | Consultar tarefa específica | Integração | Retorna detalhes corretos da tarefa |

### 6.2 Casos de Erro ❌

| # | Cenário | Tipo | Esperado |
|---|---------|------|----------|
| 1 | Sem header X-Tenant-Id | Validação | 400 Bad Request |
| 2 | Sem token de autenticação | Segurança | 401/403 Unauthorized |
| 3 | Tenant inexistente | Validação | 400 Bad Request |
| 4 | Tenant sem datasource | Validação | 400 Bad Request |
| 5 | Permissão insuficiente | Segurança | 403 Forbidden |

## 7. Monitoramento Durante Execução

### 7.1 Verificar Logs

```bash
# Terminal 1 - Acompanhar logs
tail -f logs/erp.log | grep "MIGRATION\|TENANT CONTROLLER"
```

**Saída esperada:**
```
[TENANT CONTROLLER] Enfileirando migração de datasource para tenant: 123
✅ Tarefa de migração enfileirada para tenant: Empresa ABC (a1b2c3d4-e5f6-4a5b-9c8d-7e6f5a4b3c2d)
╔════════════════════════════════════════════════════════════════╗
║     INICIANDO PROCESSAMENTO ASSÍNCRONO DA FILA DE MIGRAÇÕES    ║
╚════════════════════════════════════════════════════════════════╝
[MIGRAÇÃO] Executando migração para tenant: Empresa ABC
✅ Migração concluída com sucesso: Empresa ABC
```

### 7.2 Monitorar Fila em Tempo Real

```bash
# Terminal 2 - Consultar status a cada 5 segundos
watch -n 5 'curl -s http://localhost:8080/api/v1/migrations/queue/stats \
  -H "Authorization: Bearer ${TOKEN}" | jq'
```

## 8. Verificação de Integridade

### 8.1 Validar Resposta

✅ **Validação Necessária:**
- [ ] `success` é `true` (em caso de sucesso)
- [ ] `taskId` é um UUID válido
- [ ] `tenantId` corresponde ao esperado
- [ ] `status` é um dos valores válidos
- [ ] `enqueuedAt` é um timestamp válido

### 8.2 Validar Banco de Dados

```sql
-- Verificar se migração foi documentada (se persistência existir)
SELECT * FROM migration_queue_tasks 
WHERE tenant_id = 123 
ORDER BY enqueued_at DESC 
LIMIT 1;
```

## 9. Teste de Carga

### 9.1 Script Apache Bench

```bash
# Instalar Apache Bench (se não houver)
# macOS: brew install httpd
# Ubuntu: sudo apt-get install apache2-utils

# Executar teste
ab -n 100 -c 10 \
  -H "X-Tenant-Id: 123" \
  -H "Authorization: Bearer ${TOKEN}" \
  -p data.json \
  -T application/json \
  http://localhost:8080/api/v1/tenant/database/datasource/migrate
```

### 9.2 Script JMeter

1. Criar Test Plan
2. Adicionar Thread Group (100 threads, ramp-up 10s)
3. Adicionar HTTP Request Sampler
4. Configurar headers (X-Tenant-Id, Authorization)
5. Executar e analisar resultados

## 10. Checklist de Validação Final

- [ ] Endpoint acessível na rota correta
- [ ] Autenticação funcionando
- [ ] Permissões verificadas
- [ ] Tarefa enfileirada com sucesso
- [ ] Status atualizado corretamente
- [ ] Logs gerados apropriadamente
- [ ] Erros tratados corretamente
- [ ] Respostas no formato esperado
- [ ] Fila processada em background
- [ ] Migrações executadas com sucesso

---

**Nota:** Todos os testes devem ser executados em um ambiente de desenvolvimento/teste, nunca em produção.
