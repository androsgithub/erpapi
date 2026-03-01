# 📊 Diagramas da Arquitetura (Mermaid)

## 1. Comparação: Antes vs. Depois

### ANTES (Duplicação)
```
┌─────────────────────────────────────────────────────────────────┐
│                     Inicialização                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  MTMigrationBoostrap.execute()                                  │
│           ↓                                                      │
│  MigrationJobLauncher.launchMigrationJob()                      │
│           ↓                                                      │
│  Spring Batch Job (TenantMigrationProcessor)                    │
│           ├─ Reader: TenantMigrationReader                      │
│           ├─ Processor: TenantMigrationProcessor                │
│           └─ Writer: TenantMigrationWriter                      │
│           ↓                                                      │
│  ✅ Tenants migrados                                             │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│             Criação de Novo Tenant (Duplicado!)                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  TenantService.criarTenant()                                    │
│           ↓                                                      │
│  MigrationQueueService.enqueueTenantMigration()                │
│           ↓                                                      │
│  MigrationQueueService.processMigrationQueue()                 │
│           ├─ Executa Flyway                                     │
│           └─ Executa Seed                                       │
│           ↓                                                      │
│  ✅ Tenant migrado                                               │
│                                                                 │
│  ❌ DUPLICAÇÃO: Mesma lógica, 2 implementações!                │
└─────────────────────────────────────────────────────────────────┘
```

### DEPOIS (Unificado)
```
┌─────────────────────────────────────────────────────────────────┐
│                 Fila Unificada de Migrações                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Inicialização          ┌──────────────────┐   Novo Tenant     │
│       ↓                 │                  │        ↓           │
│  Tenants ativos    TenantMigration├──criarTenant()     │
│       ↓            Queue (BlockingQueue)  │        ↓            │
│  enqueueEvent()    ↑                      │  TenantCreatedEvent│
│       ↓            │                      │        ↓            │
│  ┌─────────────────┼──────────────────┐  │ TenantCreationListener
│  │   FILA ÚNICA    │ Compatível com │  │  │        ↓            │
│  │   (Thread-     │ múltiplas fontes│  └──┼──enqueueEvent()   │
│  │    safe)       │ de eventos      │     │        ↓            │
│  │                │                  │     └────────┼────────── │
│  └─────────────────┼──────────────────┘              │          │
│                    │                                 │          │
│                    ▼ (infinito)                      │          │
│        TenantMigrationQueueConsumer                  │          │
│        (1 worker em background)                      │          │
│             ↓                                         │          │
│        processEvent()                                │          │
│             ├─ Flyway migrations                    │          │
│             └─ Seed data                            │          │
│             ↓                                         │          │
│        ✅ SUCESSO  ou  ❌ FALHA → Retry (3x)        │          │
│             ↓                                         │          │
│        Histórico de Eventos                         │          │
│                                                      │          │
│  RESULTADO: ✅ Apenas 1 implementação!              │          │
│             ✅ 100% de duplicação eliminada!        │          │
│             ✅ Logs centralizados!                  │          │
│             ✅ Admin API para monitoramento!        │          │
└──────────────────────────────────────────────────────────────────┘
```

---

## 2. Flow Detalhado - Inicialização

```
┌──────────────────────────┐
│   Spring Boot Start      │
└────────────┬─────────────┘
             │
             ▼
┌──────────────────────────┐
│ Master DataSource Created│
│ (FlywayConfig)           │
└────────────┬─────────────┘
             │
             ▼
┌──────────────────────────┐
│ Master Migrations        │
│ (Synchronous)            │
└────────────┬─────────────┘
             │
             ▼
┌──────────────────────────────────────────┐
│ Spring Boot Ready                        │
│ → ApplicationReadyEvent dispatched       │
└────────────┬─────────────────────────────┘
             │
             ▼
┌──────────────────────────────────────────┐
│ ApplicationStartupListener                │
│ @EventListener(ApplicationReadyEvent)    │
└────────────┬─────────────────────────────┘
             │
             ▼
┌──────────────────────────────────────────┐
│ TenantMigrationStartupWorker              │
│ .initializeAndStart()                    │
└────────────┬─────────────────────────────┘
             │
             ├─────────────────────────────┐
             │                             │
             ▼                             ▼
    ┌──────────────────┐      ┌───────────────────────┐
    │ tenantRepository │      │ For each tenant:      │
    │ .findAllByAtiva  │      │  1. Get datasource    │
    │ True()           │      │  2. Create event      │
    │                  │      │  3. Enqueue           │
    │ Returns: [T1,    │      │  4. Register in map   │
    │  T2, T3...]      │      │                       │
    └──────────────────┘      └───────────────────────┘
             │                             │
             └──────────────┬──────────────┘
                            │
                            ▼
        ┌─────────────────────────────────────────┐
        │ TenantMigrationQueue             │
        │                                         │
        │ Events:                                 │
        │  [Event1]→PENDING                       │
        │  [Event2]→PENDING                       │
        │  [Event3]→PENDING                       │
        │                                         │
        │ Registry: {Event1→..., Event2→...}      │
        └─────────────────────────────────────────┘
             │
             ▼
        ┌─────────────────────────────────────────┐
        │ TenantMigrationQueueConsumer             │
        │ .startConsumer() @Async                 │
        │                                         │
        │ ∞ Loop:                                 │
        │   while (!interrupted) {                │
        │     event = queue.pollNext(30s)         │
        │     if (event != null) {                │
        │       processEvent(event)               │
        │       recordCompletion(event)           │
        │     }                                   │
        │   }                                     │
        └─────────────────────────────────────────┘
             │
             ├─ Executa Flyway migrations
             ├─ TenantContext.setTenantId(...)
             ├─ mainSeed.executar()
             ├─ TenantContext.clear()
             ├─ event.markSuccess(...)
             └─ Volta para pollNext()
```

---

## 3. Flow Detalhado - Novo Tenant

```
┌─────────────────────────────────┐
│ POST /api/v1/tenants            │
│ {nome, email, cnpj, ...}        │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────────────┐
│ TenantController                        │
│ → TenantService.criarTenant(request)    │
└────────────┬────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────┐
│ TenantService.criarTenant()             │
│                                         │
│ 1. new Tenant()                         │
│ 2. tenant.setNome(...)                  │
│ 3. tenant.setEmail(...)                 │
│ ... (outros campos)                     │
│ 4. tenantRepository.save(tenant)        │
│    → TenantDatasource criado             │
└────────────┬────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────┐
│ eventPublisher.publishEvent(            │
│   new TenantCreatedEvent(this, tenant)  │
│ )                                       │
└────────────┬────────────────────────────┘
             │
             ▼ (Spring Event Dispatch)
┌──────────────────────────────────────────────┐
│ TenantCreationMigrationListener              │
│ @EventListener public void                   │
│ onTenantCreated(TenantCreatedEvent event)   │
└────────────┬─────────────────────────────────┘
             │
             ├─────────────────────────┐
             │                         │
             ▼                         ▼
    ┌──────────────────┐    ┌──────────────────┐
    │ Get tenant from  │    │ tenantDatasource │
    │ event            │    │ Repository.find  │
    │                  │    │ ByTenantIdAndSta │
    │ tenant.getId()   │    │ tus(...)         │
    └──────────────────┘    └──────────────────┘
             │                         │
             └──────────────┬──────────┘
                            │
                            ▼
        ┌─────────────────────────────────────────┐
        │ migrationQueue.enqueueEvent(            │
        │   tenantId,                             │
        │   tenantName,                           │
        │   datasource,                           │
        │   Source.TENANT_CREATION               │
        │ )                                       │
        │                                         │
        │ → event = new TenantMigrationEvent(...) │
        │ → eventQueue.offer(event)               │
        │ → eventRegistry.put(eventId, event)     │
        │ → return event                          │
        └────────────┬────────────────────────────┘
                     │
                     ▼
        ┌──────────────────────────────────────────┐
        │ TenantMigrationQueueConsumer             │
        │ próxima iteração do loop                │
        │                                          │
        │ event = queue.pollNext(30s) ← Nova task │
        │ processEvent(event)                      │
        │ ├─ Flyway: migrateTenant()              │
        │ ├─ Seed: mainSeed.executar()            │
        │ └─ Mark: COMPLETED                      │
        │                                          │
        │ recordCompletion(event)                 │
        └──────────────────────────────────────────┘
```

---

## 4. Estado da Fila em Tempo Real

```
┌──────────────────────────────────────────────────────────────┐
│ TenantMigrationQueue                                  │
│                                                              │
│ blockingQueue (thread-safe):                                │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ Event1: PENDING   (esperando)                         │ │
│  │ Event2: PENDING   (esperando)                         │ │
│  │ Event3: PENDING   (esperando)                         │ │
│  └────────────────────────────────────────────────────────┘ │
│                                                              │
│ eventRegistry (mapa completo):                             │
│  {                                                          │
│    "evt-001": Event1{status:IN_PROGRESS, ...} ← Processando │
│    "evt-002": Event2{status:PENDING, ...}                   │
│    "evt-003": Event3{status:PENDING, ...}                   │
│    "evt-004": Event4{status:COMPLETED, ...} ← Histórico     │
│    "evt-005": Event5{status:COMPLETED, ...} ← Histórico     │
│    "evt-006": Event6{status:FAILED, ...}    ← Falha/Retry   │
│  }                                                           │
│                                                              │
│ eventHistory (histórico):                                  │
│  [Event4, Event5, Event6, ...]                             │
│                                                              │
│ Stats:                                                       │
│  totalEvents: 6                                             │
│  pendingEvents: 2                                           │
│  inProgressEvents: 1                                        │
│  completedEvents: 2                                         │
│  failedEvents: 1                                            │
│  progressPercent: 50.0%                                     │
│  successRatePercent: 66.7%                                  │
│  queueSize: 2                                               │
└──────────────────────────────────────────────────────────────┘
```

---

## 5. Processamento com Retry

```
┌────────────────────────────────────────┐
│ Event processado → Error               │
│ (Flyway ou Seed falhou)                │
└────────────┬───────────────────────────┘
             │
             ▼
┌────────────────────────────────────────┐
│ event.markFailed(errorMessage)         │
│                                        │
│ Status: FAILED                         │
│ errorMessage: "Migration V2 error..."  │
│ completedAt: 2026-02-28T10:35:45       │
└────────────┬───────────────────────────┘
             │
             ▼
┌────────────────────────────────────────┐
│ if (retryCount < maxRetries) ?         │
│   (retryCount = 0, maxRetries = 3)     │
└────────────┬───────────────────────────┘
             │
      ┌──────┴────────┐
      │ SIM           │
      ▼               ▼ NÃO (retried 3x)
   Retry delay     recordEventCompletion()
   (5 segundos)    │
      │            │ (permanentemente falho)
      ▼            ▼
   event.mark     Histórico
   ForRetry()     │
   │              ├─ Status: FAILED
   │              ├─ Reason: Max retries
   ├─ Status:     └─ Manual investigation
   │  PENDING        needed
   ├─ retryCount: 1
   └─ Clear error
      │
      ▼
   enqueueEvent()
   │
   ├─ Volta para queue
   ├─ Consumer aguarda
   ├─ Próxima iteração
   └─ Próxima tentativa


Exemplo de Falha com Retry:
─────────────────────────────

Tentativa 1 (15:00:00):  ❌ FAILED (Flyway error)
                         Retry marcado, retryCount=1

Tentativa 2 (15:00:05):  ❌ FAILED (Mesma erro)
                         Retry marcado, retryCount=2

Tentativa 3 (15:00:10):  ❌ FAILED (Mesma erro)
                         Retry marcado, retryCount=3

Tentativa 4 (15:00:15):  ❌ FAILED (retryCount >= maxRetries)
                         PERMANENTEMENTE FALHO
                         Registrado em histórico
                         Requer investigação manual
```

---

## 6. API Admin - REST Endpoints

```
┌─────────────────────────────────────────────────────────────┐
│ MigrationQueueAdminController                        │
└─────────────────────────────────────────────────────────────┘

GET /api/v1/admin/migrations/queue/stats
├─ Retorna: MigrationQueueStats
└─ Exemplo: {
     "totalEvents": 250,
     "completedEvents": 236,
     "failedEvents": 3,
     "progressPercent": 95.6,
     "successRatePercent": 98.7
   }

GET /api/v1/admin/migrations/queue/events
├─ Retorna: Collection<Map> com todos os eventos
└─ Exemplo: [
     {eventId, tenantId, status, startedAt, ...},
     {eventId, tenantId, status, startedAt, ...}
   ]

GET /api/v1/admin/migrations/queue/events/{eventId}
├─ Retorna: Map com detalhes do evento
└─ Exemplo: {
     "eventId": "abc-123",
     "tenantName": "Empresa XYZ",
     "status": "COMPLETED",
     "waitTimeMs": 250,
     "executionTimeMs": 2341,
     "errorMessage": null
   }

GET /api/v1/admin/migrations/queue/events/pending
├─ Retorna: Events com status PENDING
└─ Total que aguardam na fila

GET /api/v1/admin/migrations/queue/events/failed
├─ Retorna: Events com status FAILED
└─ Total que falharam permanentemente

GET /api/v1/admin/migrations/queue/events/completed
├─ Retorna: Events com status COMPLETED
└─ Total que tiveram sucesso

GET /api/v1/admin/migrations/queue/events/in-progress
├─ Retorna: Events em processamento agora
└─ Usually 0 ou 1 (processamento sequencial)

POST /api/v1/admin/migrations/queue/reprocess/{eventId}
├─ Action: Reprocessa um evento específico
├─ Uso: Após corrigir problema, reprocessar manualmente
└─ Retorna: {status: "success", message: "..."}
```

---

## 7. Integração com Tenant Context

```
┌──────────────────────────────────────────────────────────┐
│ TenantContext (ThreadLocal)                              │
│                                                          │
│ Static methods:                                         │
│  - setTenantId(Long id)                                │
│  - getTenantId() : Long                                │
│  - clear()                                             │
└────────┬─────────────────────────────────────────────────┘
         │
         │ Usado em:
         ├─────────────────────────────────────────────────┐
         │                                                 │
         ▼                                                 ▼
    TenantContext                                   @Aspect/AOP
    .setTenantId(1)                                (TenantIdentifierAspect)
         │                                                 │
         ▼                                                 │
    mainSeed.executar()                            Intercepta
    ({                                             repository.save()
      permissionSeed.create() ─┐                   └─ Adiciona
      userAdminSeed.create() ──┤─► Usa tenantId         filtro
    })                         │   para banco correto    WHERE tenant_id = 1
                               │
                               └─ Isolamento completo

    Após seed:
         │
         ▼
    TenantContext.clear()
         │
         └─ Remove contexto
            Evita thread reuse issues
```

---

**Versão**: 2.0 Diagramatic  
**Gerado**: Fevereiro 2026  
