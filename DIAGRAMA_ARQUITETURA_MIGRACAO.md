# Diagrama de Arquitetura - Sistema de Migrações

## 1. Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────────┐
│                        CAMADA CLIENTE                            │
│                                                                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐           │
│  │   cURL       │  │   Postman    │  │   Frontend   │           │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘           │
└─────────┼──────────────────┼──────────────────┼──────────────────┘
          │                  │                  │
          └──────────────────┼──────────────────┘
                             │
                   POST /api/v1/tenant/database/datasource/migrate
                             │
┌────────────────────────────┼────────────────────────────────────┐
│            CAMADA APRESENTAÇÃO (CONTROLLER)                     │
│                            │                                     │
│    ┌──────────────────────┴──────────────────────┐              │
│    │   TenantSchemaController                   │              │
│    ├───────────────────────────────────────────┤              │
│    │ ✅ configurarDatasource()                 │              │
│    │ ✅ obterDatasource()                      │              │
│    │ ✅ atualizarDatasource()                  │              │
│    │ ✅ testarConexaoDatasource()              │              │
│    │ 🆕 enqueueDatasourceMigration() <────────┼──────────┐   │
│    └──────────────────┬───────────────────────┘          │   │
│                       │                                   │   │
│ [Validações Aplicadas]                                   │   │
│ • @RequiresXTenantId ─┬─ Autenticação                    │   │
│ • @RequiresPermission ├─ Autorização                     │   │
│ • TenantContext       └─ Isolamento por Tenant          │   │
└───────────────────────┼─────────────────────────────────┘   │
                        │                                      │
│       Injeção de Dependência                               │
└────────────────────────┼──────────────────────────────────┘
                         │
┌────────────────────────┼──────────────────────────────────────┐
│         CAMADA SERVIÇO (SERVICE)                              │
│                        │                                       │
│    ┌───────────────────▼─────────────────────┐               │
│    │  MigrationQueueService                  │               │
│    ├─────────────────────────────────────── ┤               │
│    │  📌 enqueueTenantMigration(tenantId)   │               │
│    │     • Cria nova MigrationQueueTask      │               │
│    │     • Adiciona à fila (Queue)           │               │
│    │     • Registra no taskRegistry          │               │
│    │                                         │               │
│    │  📌 processMigrationQueue() @Async      │               │
│    │     • Processa fila em background       │               │
│    │     • Executa TenantMigrationService    │               │
│    │     • Atualiza status da tarefa         │               │
│    │                                         │               │
│    │  📌 getStats()                          │               │
│    │  📌 getAllTasks()                       │               │
│    │  📌 getInProgressTasks()                │               │
│    │  📌 getFailedTasks()                    │               │
│    └──────┬──────────────────────────────────┘               │
│           │                                                   │
│  ┌────────┴──────────────────┬─────────────────────────────┐ │
│  │                           │                             │ │
│  ▼                           ▼                             ▼ │
│ Task❸                    TaskRegistry              TenantMigrationService
│ Queue                   (Map<TaskId>)              (Executa migrações)
│ (FIFO)                                                     │
└─────────────────────────────────────────────────────────────┘
        │                    │                          │
        │                    │                          │
        ├────────────────────┼──────────────────────────┤
        │                    │                          │
        ▼                    ▼                          ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐
│ Memória da   │  │ Memória da   │  │  BANCO DE DADOS          │
│ Aplicação    │  │  Aplicação   │  │  ┌────────────────────┐  │
│ (Queue)      │  │ (Registry)   │  │  │ Executa Migrations │  │
│              │  │              │  │  │ (Scripts SQL)      │  │
│ PENDING ──>  │  │ taskId ──>   │  │  └────────────────────┘  │
│ IN_PROGRESS  │  │ { task info} │  │                          │
│ COMPLETED    │  │             │  │  ✅ clickhouse/          │
│ FAILED       │  │             │  │  ✅ postgresql/          │
└──────────────┘  └──────────────┘  │  ✅ mysql/               │
                                     │  ✅ oracle/              │
                                     │  ✅ ... (outros BD)      │
                                     └────────────────────────┘
```

## 2. Diagrama de Sequência - Enfileiramento de Tarefa

```
Cliente         Controller          Service            Repositório    Banco de Dados
  │                 │                 │                    │               │
  │  POST /migrate  │                 │                    │               │
  ├────────────────>│                 │                    │               │
  │                 │                 │                    │               │
  │                 │ enqueueTenantMigration(tenantId)    │               │
  │                 ├────────────────>│                    │               │
  │                 │                 │                    │               │
  │                 │                 │ findById(tenantId) │               │
  │                 │                 ├───────────────────>│               │
  │                 │                 │                    │ SELECT *      │
  │                 │                 │                    ├──────────────>│
  │                 │                 │                   <│<──────────────┤
  │                 │                 │<───────────────────┤               │
  │                 │                 │                    │               │
  │                 │                 │ findByTenantIdAndStatus(id, true)  │
  │                 │                 ├───────────────────>│               │
  │                 │                 │                    │ SELECT *      │
  │                 │                 │                    ├──────────────>│
  │                 │                 │                   <│<──────────────┤
  │                 │                 │<───────────────────┤               │
  │                 │                 │                    │               │
  │                 │                 │ new MigrationQueueTask()           │
  │                 │                 │ • taskId (UUID)                    │
  │                 │                 │ • tenantId                         │
  │                 │                 │ • status = PENDING                 │
  │                 │                 │ • enqueuedAt = now()               │
  │                 │                 │                    │               │
  │                 │                 │ taskQueue.offer(task)              │
  │                 │                 │ taskRegistry.put(taskId, task)     │
  │                 │                 │                    │               │
  │                 │<──────────────────── return task ─────               │
  │                 │                 │                    │               │
  │  202 Accepted   │                 │                    │               │
  │  { taskId, ... }<────────────────┤                    │               │
  │                 │                 │                    │               │
  │                 │                 │ [Background] @Async processMigrationQueue()
  │                 │                 ├─────────────────────────────────>  │
  │                 │                 │                    │          PROCESSING ...
  │                 │                 │                    │               │
```

## 3. Diagrama de Estados da Tarefa

```
┌─────────┐
│ PENDING │  <-- Tarefa criada e enfileirada
└────┬────┘
     │
     │ Processamento inicia
     │ (MigrationQueueService.processMigrationQueue())
     ▼
┌─────────────┐
│ IN_PROGRESS │  <-- Migração está sendo executada
└────┬────────┘     • startedAt é atualizada
     │               • TenantMigrationService.migrateTenantById()
     │               • Scripts SQL são aplicados
     │
     ├────────────────────────────────────────┐
     │                                        │
     │ Sucesso                              Erro
     │                                        │
     ▼                                        ▼
┌───────────┐                          ┌────────┐
│ COMPLETED │                          │ FAILED │
└───────────┘ <-- Migração bem-sucedida └────────┘
  • completedAt                          • completedAt
  • migrationsExecuted                   • errorMessage
                                         • status = FAILED

[Timing Information]
• enqueuedAt: Quando tarefa é criada
• startedAt: Quando processamento inicia
• completedAt: Quando tarefa finaliza (sucesso ou erro)
• waitTimeSeconds = startedAt - enqueuedAt
• executionTimeSeconds = completedAt - startedAt
```

## 4. Diagrama de Permissões e Segurança

```
┌─────────────────────────────────────────────────────────┐
│                    CAMADA DE SEGURANÇA                  │
└─────────────────────────────────────────────────────────┘
              │                     │
              │                     │
      ┌───────▼────────┐    ┌───────▼──────────┐
      │ Autenticação   │    │ Autorização      │
      ├────────────────┤    ├──────────────────┤
      │ Bearer Token   │    │ Permissão        │
      │ (JWT)          │    │ ATUALIZAR        │
      │                │    │                  │
      │ @Autowired     │    │ @RequiresPermission
      │ SecurityService│    │ (ATUALIZAR)      │
      └────────────────┘    └──────────────────┘
              │                     │
              └─────────────┬───────┘
                            │
                            ▼
      ┌─────────────────────────────────┐
      │  TenantContext.getTenantId()    │
      │  • Valida contexto do tenant    │
      │  • Isola dados por tenant       │
      │  • Previne acesso cruzado       │
      └─────────────────────────────────┘
              │
              ▼
      ┌──────────────────────────┐
      │ Endpoint Protegido ✓     │
      │ enqueueDatasourceMigration│
      └──────────────────────────┘

[Erro Handling]
❌ Sem XTenantId Header    → @RequiresXTenantId Falha
❌ Sem Token              → Falha na Autenticação
❌ Token Inválido         → 401 Unauthorized
❌ Sem Permissão          → 403 Forbidden
❌ Tenant Isolado         → Acesso Negado
```

## 5. Diagrama de Fluxo de Resposta

```
HTTP Request
    │
    └──> TenantSchemaController.enqueueDatasourceMigration()
         │
         ├─> Try Block
         │   │
         │   ├─> migrationQueueService.enqueueTenantMigration(tenantId)
         │   │   │
         │   │   ├─> ✅ Sucesso
         │   │   │   └─> return MigrationQueueTask
         │   │   │
         │   │   └─> ❌ IllegalArgumentException
         │   │       └─> throw exception
         │   │
         │   └─> buildMigrationResponse(task)
         │       └─> return Map<String, Object>
         │
         ├─> Catch IllegalArgumentException
         │   └─> buildErrorResponse("Datasource não configurado...")
         │       └─> return Map<String, Object>
         │
         ├─> Catch Exception
         │   └─> buildErrorResponse("Erro ao enfileirar migração...")
         │       └─> return Map<String, Object>
         │
         └─> ResponseEntity
             │
             ├─> ✅ 202 Accepted + response (sucesso)
             ├─> 400 Bad Request + error (erro validação)
             └─> 500 Internal Server Error + error (erro inesperado)

HTTP Response (JSON)
```

## 6. Diagrama de Monitoramento

```
┌────────────────────────────────────────────────────────┐
│          ENDPOINTS DE MONITORAMENTO (Read-Only)        │
└────────────────────────────────────────────────────────┘
    │           │             │           │            │
    ▼           ▼             ▼           ▼            ▼
  /stats    /tasks       /tasks/{id}  /in-progress  /tenant/{id}
    │           │             │           │            │
    ▼           ▼             ▼           ▼            ▼
 Stats      AllTasks      SpecificTask InProgress   TenantTasks
 ├─ Total      │            │           │            │
 ├─ Pending    ├─ List all   ├─ Details  ├─ Filtering └─ Cada tenant
 ├─ Running    │  tasks      │   for     │             pode ver
 ├─ Completed  │             │   audit   │             suas tasks
 ├─ Failed     │             │           │
 └─ Progress%  │             │           │
```

## 7. Fluxo Completo - Timeline

```
T0  Cliente envia: POST /datasource/migrate
    │
T0  Server recebe requisição
    ├─ Valida X-Tenant-Id
    ├─ Valida Bearer Token
    └─ Valida Permissão ATUALIZAR
    │
T0  TenantSchemaController.enqueueDatasourceMigration()
    │
T1  MigrationQueueService.enqueueTenantMigration(123)
    ├─ Busca Tenant #123 ✅
    ├─ Busca Datasource ✅
    ├─ Cria MigrationQueueTask
    ├─ taskQueue.offer(task)          [PENDING]
    └─ taskRegistry.put(taskId, task)
    │
T2  Responde ao cliente: 202 Accepted
    │ {
    │   "taskId": "a1b2c3d4-...",
    │   "status": "PENDING",
    │   "message": "Migração enfileirada com sucesso"
    │ }
    │
T2  Background: processMigrationQueue() inicia
    │
T5  MigrationQueueTask.markStarted()  [IN_PROGRESS]
    ├─ status = IN_PROGRESS
    └─ startedAt = now()
    │
T5  TenantMigrationService.migrateTenantById(123)
    │
T6  Executar Scripts SQL
    ├─ CREATE TABLE ...
    ├─ INSERT INTO ...
    ├─ UPDATE ...
    └─ ALTER TABLE ...
    │
T15 MigrationQueueTask.markCompleted() [COMPLETED]
    ├─ status = COMPLETED
    ├─ completedAt = now()
    └─ migrationsExecuted = 15
    │
T15 Task finaliza: ✅ Sucesso

[Tempos]
- T0-T2: ~100ms (Resposta ao cliente)
- T2-T5: ~300ms (Enfileiramento + processamento inicial)
- T5-T15: ~10000ms (Execução de migrações)
- Total: ~10 segundos
```

## 8. Distribuição de Responsabilidades

```
┌═════════════════════════════════════════════════════════════┐
│                   CONTROLLER LAYER                          │
│  TenantSchemaController.enqueueDatasourceMigration()        │
│  ✓ Recebe e valida requisição HTTP                         │
│  ✓ Extrai tenantId do contexto                             │
│  ✓ Trata exceções                                          │
│  ✓ Formata respostas                                       │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                   SERVICE LAYER                             │
│  MigrationQueueService                                      │
│  ✓ Gerencia fila de tarefas                                │
│  ✓ Enfileira migrações                                     │
│  ✓ Processa fila em background                             │
│  ✓ Rastreia status de tarefas                              │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                 DOMAIN LAYER                                │
│  MigrationQueueTask                                         │
│  ✓ Representa tarefa de migração                           │
│  ✓ Gerencia estados                                        │
│  ✓ Calcula métricas temporais                              │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│              INFRASTRUCTURE LAYER                           │
│  TenantMigrationService / Database                         │
│  ✓ Executa migrations de fato                              │
│  ✓ Aplica scripts SQL                                      │
│  ✓ Persiste dados (se necessário)                          │
└───────────────────────────────────────────────────────────┘
```

---

**Legenda:**
- ✅ Feature implementada
- ❌ Erro/validação
- 🆕 Nova implementação
- 📌 Método principal
- ⏱️ Timing crítico
- 🔒 Segurança

