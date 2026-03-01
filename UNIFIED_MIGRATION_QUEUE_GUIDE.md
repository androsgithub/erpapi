# 🚀 Refatoração de Fila Unificada de Migrações - Guia Completo

## 📋 Resumo Executivo

Este documento descreve a refatoração completa do fluxo de migrações e validações de tenants na aplicação Spring Boot multi-tenant. A refatoração unifica dois fluxos separados em um único mecanismo baseado em **fila + consumidor**, eliminando duplicação de código e centralizando o controle de execução.

### ✅ Problemas Resolvidos

| Problema | Solução |
|----------|---------|
| **Duplicação de Lógica** | Um único mecanismo unificado |
| **Logs Separados** | Logs centralizados com rastreamento por tenant |
| **Sem Controle Centralizado** | Fila centralizada com eventos trackeáveis |
| **Inconsistência de Comportamento** | Mesmo fluxo para ambos os casos |
| **Sem Retry Automático** | Retry automático com backoff |
| **Difícil Monitoramento** | Endpoints de admin com estatísticas completas |

---

## 🏗️ Arquitetura Nova

```
┌─────────────────────────────────────────────────────────────┐
│                    APLICAÇÃO INICIADA                       │
└─────────────────┬───────────────────────────────────────────┘
                  │
                  ▼
        ┌─────────────────────┐
        │  ApplicationReady    │
        │     Event           │
        └────────┬────────────┘
                 │
                 ▼
    ┌────────────────────────────┐
    │ TenantMigrationStartup     │
    │ Worker                     │
    │                            │
    │ 1. Busca tenants ativos    │
    │ 2. Enfileira cada um       │
    │ 3. Inicia consumidor       │
    └────────┬───────────────────┘
             │
             ▼
    ┌────────────────────────────────────────┐
    │  TenantMigrationQueue           │
    │  (Fila Centralizada - BlockingQueue)   │
    │                                        │
    │  - Eventos PENDING                     │
    │  - Eventos IN_PROGRESS                 │
    │  - Eventos COMPLETED/FAILED (histórico)│
    └────────┬───────────────────────────────┘
             │ (Consumidor puxa eventos)
             ▼
    ┌────────────────────────────────────────┐
    │  TenantMigrationQueueConsumer          │
    │  (Worker Assíncrono)                   │
    │                                        │
    │  Processa cada evento:                 │
    │  - Executa Flyway migrations           │
    │  - Executa seeders (dados iniciais)    │
    │  - Marca sucesso/falha                 │
    │  - Implementa retry automático         │
    │  - Registra no histórico               │
    └────────┬───────────────────────────────┘
             │
             ├──► ✅ Sucesso → Histórico
             │
             └──► ❌ Falha → Retry ou Histórico

┌─ NOVO TENANT ┐
│ (Runtime)    │
└──┬───────────┘
   │ criarTenant()
   ▼
┌──────────────────────────────┐
│  TenantCreatedEvent          │
│  (Publicado automaticamente) │
└──┬───────────────────────────┘
   │
   ▼
┌──────────────────────────────────┐
│  TenantCreationMigration         │
│  Listener                        │
│  (Escuta evento automaticamente) │
└──┬───────────────────────────────┘
   │ Enfileira novo tenant
   ▼
TenantMigrationQueue
   (Mesmo fluxo de inicialização!)
```

---

## 📚 Classes Principais da Refatoração

### 1. **TenantMigrationEvent** (Domain)
**Localização**: `migration/unified/domain/TenantMigrationEvent.java`

**Responsabilidade**: Modelo unificado de evento de migração

**Características**:
- Encapsula dados do tenant a ser migrado
- Rastreia estado (PENDING, IN_PROGRESS, COMPLETED, FAILED)
- Mantém métricas de execução (duração, wait time, migrações, seeds)
- Suporta retry automático com contador

**Estados Possíveis**:
```
PENDING ──┬──► IN_PROGRESS ──┬──► COMPLETED
          │                 │
          │                 └──► FAILED ──► RETRY
          │
          └──────────► Retry ─────────────┘
```

### 2. **TenantMigrationQueue** (Service)
**Localização**: `migration/unified/service/TenantMigrationQueue.java`

**Responsabilidade**: Gerencia fila centralizada de eventos

**Methods Principais**:
```java
// Enfileirar
enqueueEvent(TenantMigrationEvent)
enqueueEvent(tenantId, name, datasource, source)

// Consultar
pollNext(timeout)              // Bloqueante
pollNextNonBlocking()          // Não bloqueante
getEvent(eventId)              // Por ID
getAllEvents()                 // Todos
getPendingEvents()             // Pendentes
getFailedEvents()              // Com falha
getCompletedEvents()           // Completados

// Estatísticas
getStats()                     // Retorna MigrationQueueStats
getQueueSize()
isQueueEmpty()

// Retry
enqueueForRetry(event)         // Recoloca na fila
```

**Características**:
- Thread-safe (BlockingQueue + ConcurrentHashMap)
- Histórico de execução
- Estatísticas de progresso e taxa de sucesso
- Suporte a retry automático com limite configurável

### 3. **TenantMigrationQueueConsumer** (Service)
**Localização**: `migration/unified/service/TenantMigrationQueueConsumer.java`

**Responsabilidade**: Worker assíncrono que consome e processa eventos

**Flow**:
```
startConsumer() ──► pollNext(timeout) ──┬──► processEvent() ──► recordCompletion()
                                        │
                                        └──► catch(Exception) ──► markFailed() ──► enqueueForRetry()
                                        │
                                        └──► InterruptedException ──► exit
```

**Características**:
- Executa continuamente (loop infinito controlado)
- Processa eventos sequencialmente (configurável)
- Implementa retry automático com backoff simples (5 segundos)
- Rastreia tempo de espera e execução
- Integra com TenantContext para isolamento de tenant

**Fases de Processamento**:
1. **Marca como iniciado** → status = IN_PROGRESS
2. **Executa Migrações Flyway** → vinculado ao tenant
3. **Executa Seeders** → dados iniciais (permissões, usuário admin, etc)
4. **Marca como sucesso** → status = COMPLETED
5. **Em caso de erro** → status = FAILED → retry ou histórico

### 4. **TenantMigrationStartupWorker** (Startup Component)
**Localização**: `migration/unified/startup/TenantMigrationStartupWorker.java`

**Responsabilidade**: Inicializa fila e consumidor na startup

**Fluxo na Inicialização**:
```
1. Busca todos os tenants ativos do banco
2. Para cada tenant:
   - Busca seu datasource
   - Cria TenantMigrationEvent com source = APPLICATION_STARTUP
   - Enfileira na TenantMigrationQueue
3. Inicia TenantMigrationQueueConsumer em background
```

**Logs Estruturados**: Reporta enfileiramento com estatísticas

### 5. **ApplicationStartupListener** (Infrastructure)
**Localização**: `config/startup/ApplicationStartupListener.java`

**Responsabilidade**: Listener de evento ApplicationReady

**Atua Como**:
- Gatilho para inicialização da fila
- Chamada automática pelo Spring Boot

**Refatoração**: Ativado e refatorado para usar novo worker

### 6. **TenantCreatedEvent** + **TenantCreationMigrationListener** (Integration)
**Localização**: 
- `migration/unified/domain/TenantCreatedEvent.java`
- `migration/unified/service/TenantCreationMigrationListener.java`

**Responsabilidade**: Integra criação de novo tenant com migração automática

**Flow**:
```
TenantService.criarTenant()
    │
    ├─► tenantRepository.save(tenant)
    │
    └─► eventPublisher.publishEvent(TenantCreatedEvent)
                │
                ▼
        TenantCreationMigrationListener.onTenantCreated()
                │
                ├─► Busca datasource do tenant
                │
                └─► migrationQueue.enqueueEvent(
                        tenantId, name, datasource, 
                        source = TENANT_CREATION
                    )
                    
        (Mesmo fluxo da inicialização!)
```

### 7. **MigrationQueueAdminController** (REST)
**Localização**: `migration/unified/presentation/controller/MigrationQueueAdminController.java`

**Endpoints**: Todos requerem autenticação de admin

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/v1/admin/migrations/queue/stats` | Estatísticas da fila |
| GET | `/api/v1/admin/migrations/queue/events` | Todos os eventos |
| GET | `/api/v1/admin/migrations/queue/events/{eventId}` | Detalhes de um evento |
| GET | `/api/v1/admin/migrations/queue/events/pending` | Eventos pendentes |
| GET | `/api/v1/admin/migrations/queue/events/failed` | Eventos com falha |
| GET | `/api/v1/admin/migrations/queue/events/completed` | Eventos completados |
| GET | `/api/v1/admin/migrations/queue/events/in-progress` | Eventos em progresso |
| POST | `/api/v1/admin/migrations/queue/reprocess/{eventId}` | Reprocessar evento |

---

## 🔄 Fluxo Completo de Execução

### Cenário 1: Inicialização da Aplicação

```
1. Spring Boot inicializa
   ├─ Cria Master DataSource
   ├─ Executa migrações Master (Flyway) - SÍNCRONO
   └─ Dispara ApplicationReadyEvent

2. ApplicationStartupListener escuta ApplicationReadyEvent
   └─ Chama TenantMigrationStartupWorker.initializeAndStart()

3. TenantMigrationStartupWorker
   ├─ Busca todos os tenants ativos
   ├─ Para cada tenant:
   │  ├─ Busca datasource
   │  └─ Cria TenantMigrationEvent(source = APPLICATION_STARTUP)
   │  └─ Enfileira em TenantMigrationQueue
   └─ Inicia TenantMigrationQueueConsumer em thread assíncrona

4. TenantMigrationQueueConsumer (em background)
   └─ Loop infinito:
      ├─ Aguarda evento em fila (timeout 30s)
      ├─ Processa evento:
      │  ├─ Executa Flyway para o tenant
      │  ├─ Executa seeders
      │  └─ Marca sucesso ou falha
      ├─ Registra no histórico
      └─ Volta ao passo anterior
```

### Cenário 2: Criação de Novo Tenant em Runtime

```
1. Requisição POST /api/v1/tenants
   └─ TenantService.criarTenant(request)

2. TenantService
   ├─ Cria e salva novo tenant
   ├─ Publica TenantCreatedEvent
   └─ Retorna tenant criado

3. TenantCreationMigrationListener escuta TenantCreatedEvent
   ├─ Busca o tenant recém criado
   ├─ Busca seu datasource
   └─ Enfileira em TenantMigrationQueue
      (source = TENANT_CREATION)

4. TenantMigrationQueueConsumer
   └─ Processa o novo tenant pelo mesmo fluxo!
```

### Cenário 3: Falha e Retry Automático

```
1. Evento sendo processado falha
   └─ Exceção durante Flyway ou seed

2. TenantMigrationQueueConsumer captura exceção
   └─ markEventAsFailed(event, exception)

3. Se retryCount < maxRetries (3):
   ├─ Aguarda retryDelayMs (5 segundos)
   └─ Enfileira novamente com marcado para retry
      └─ retryCount incrementado

4. Próxima iteração do consumer processa retry
   └─ Se falhar novamente, repete processo

5. Se retryCount >= maxRetries:
   ├─ Marca como FAILED definitivamente
   └─ Registra no histórico para investigação manual
```

---

## 📊 Tipos de Eventos

### Origem do Evento (MigrationEventSource)

```java
public enum MigrationEventSource {
    APPLICATION_STARTUP("Inicialização da Aplicação"),
    TENANT_CREATION("Criação de Novo Tenant"),
    MANUAL_REQUEST("Requisição Manual de Suporte");
}
```

### Status do Evento (MigrationEventStatus)

```java
public enum MigrationEventStatus {
    PENDING("Aguardando"),           // Enfileirado, não iniciado
    IN_PROGRESS("Em Progresso"),     // Sendo processado
    COMPLETED("Concluído"),          // Sucesso
    FAILED("Falha"),                 // Tentativas esgotadas
    CANCELLED("Cancelado");          // Cancelado manualmente
}
```

### Resultado Final (MigrationEventResult)

```java
public enum MigrationEventResult {
    SUCCESS("Sucesso"),              // Migração completada
    FAILURE("Falha");                // Erro terminal
}
```

---

## 🧮 Estatísticas da Fila

### Resposta do Endpoint `/api/v1/admin/migrations/queue/stats`

```json
{
  "totalEvents": 250,
  "pendingEvents": 5,
  "inProgressEvents": 2,
  "completedEvents": 240,
  "failedEvents": 3,
  "queueSize": 7,
  "progressPercent": 96.0,
  "successRatePercent": 98.77,
  "totalExecutionTimeMs": 2145000,
  "avgExecutionTimeMs": 8936
}
```

---

## 🧵 Thread Safety

### Sincronização

- **BlockingQueue**: Thread-safe por design
- **ConcurrentHashMap**: Registry de eventos
- **Volatile fields**: Status e timestamps do evento
- **Synchronized methods**: Operações críticas

### Isolamento de Tenant

- **TenantContext**: Armazena tenant_id em ThreadLocal
- **setTenantId(tenantId)**: Define contexto
- **clear()**: Limpa após processamento
- **Finally block**: Garante limpeza mesmo em erro

### Execução Assíncrona

- **@Async("migrationTaskExecutor")**: Usa pool de 2-5 threads
- **Sequencial na fila**: Um consumer processa por vez
- **Paralelo entre tenants**: Múltiplos tenants processados em paralelo

---

## ⚙️ Configuração

### AsyncMigrationConfig.java

```java
@Configuration
@EnableAsync
public class AsyncMigrationConfig {
    
    @Bean(name = "migrationTaskExecutor")
    public Executor migrationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);        // 2 threads por padrão
        executor.setMaxPoolSize(5);         // Máximo 5 em picos
        executor.setQueueCapacity(100);     // Até 100 eventos aguardando
        executor.setThreadNamePrefix("tenant-migration-");
        executor.setAwaitTerminationSeconds(300); // 5 min para finalizar
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
}
```

### Propriedades de Retry

```java
public class TenantMigrationQueue {
    private final int maxRetries = 3;           // Tentativas totais
    private final long retryDelayMs = 5000;     // 5 segundos entre tentativas
}
```

### Timeout do Consumidor

```java
public class TenantMigrationQueueConsumer {
    private static final long POLL_TIMEOUT_MS = 30_000;  // 30 segundos
    private static final long RETRY_DELAY_MS = 5_000;    // 5 segundos
}
```

---

## 🔍 Monitoramento

### Logs Estruturados

Todos os logs mantêm formato consistente:

```
[EventID:TenantID] Mensagem

Exemplos:
✅ [abc123:1] Evento enfileirado: Tenant XYZ
▶ [abc123:1] Iniciando migração do tenant: Tenant XYZ
  📋 [1] Executando migrações Flyway...
  ✅ [1] Migrações Flyway concluídas com sucesso
  🌱 [1] Executando seeders (dados iniciais)...
  ✅ [1] Seeders executados com sucesso
✅ [abc123:1] Migração COMPLETA: Tenant XYZ (Flyway + Seed)
⏱️  Tempo de espera: 45ms | Tempo de execução: 2341ms
```

### Métricas Capturadas

- **waitTimeMs**: Tempo desde enfileiramento até início
- **executionTimeMs**: Tempo desde início até conclusão
- **migrationsExecuted**: Número de migrações aplicadas
- **seedersExecuted**: Número de seeders executados
- **retryCount**: Tentativas realizadas
- **errorMessage**: Mensagem de erro (se houver)

---

## 🧪 Testes de Integração

### Testando Enfileiramento

```java
@Test
public void testEnqueueEvent() {
    TenantMigrationEvent event = migrationQueue.enqueueEvent(
            1L, "Test Tenant", datasource, 
            MigrationEventSource.APPLICATION_STARTUP
    );
    
    assertThat(event.getStatus()).isEqualTo(MigrationEventStatus.PENDING);
    assertThat(migrationQueue.getQueueSize()).isEqualTo(1);
}
```

### Testando Processamento

```java
@Test
public void testProcessEvent() throws Exception {
    TenantMigrationEvent event = migrationQueue.enqueueEvent(...);
    
    queueConsumer.startConsumer(); // Em background
    
    // Aguardar processamento
    Thread.sleep(5000);
    
    TenantMigrationEvent completed = migrationQueue.getEvent(event.getEventId()).get();
    assertThat(completed.getStatus()).isEqualTo(MigrationEventStatus.COMPLETED);
}
```

### Testando Retry

```java
@Test
public void testRetryOnFailure() throws Exception {
    // Simular falha na migração
    // Enfileirar evento
    // Verificar que foi reprocessado 3 vezes
    // Verificar que está em FAILED após máximo de retries
}
```

---

## 🚨 Troubleshooting

### Problema: Eventos ficam em PENDING

**Possíveis Causas**:
- Consumer não iniciou
- Thread pool esgotado
- Deadlock

**Solução**:
1. Verificar logs de inicialização
2. Verificar se ApplicationReadyEvent foi disparado
3. Usar endpoint `/api/v1/admin/migrations/queue/stats` para monitorar
4. Verificar pool de threads: `ps aux | grep tenant-migration`

### Problema: Eventos falhando repeatedly

**Possíveis Causas**:
- Erro na migração Flyway
- Error no seed
- Datasource indisponível

**Solução**:
1. Analisar logs do evento específico
2. Usar endpoint GET `/api/v1/admin/migrations/queue/events/{eventId}`
3. Analisar errorMessage
4. Resolver o problema root
5. Usar POST `/api/v1/admin/migrations/queue/reprocess/{eventId}` após fix

### Problema: Taxa de sucesso baixa

**Análise**:
```bash
# Chamar endpoint de stats
curl http://localhost:8080/api/v1/admin/migrations/queue/stats

# Analisar successRatePercent
# Se < 95%, investigar failed events:
curl http://localhost:8080/api/v1/admin/migrations/queue/events/failed
```

---

## 🎯 Benefícios da Refatoração

### ✅ Técnicos

| Benefício | Antes | Depois |
|-----------|-------|--------|
| **Duplicação** | 2 fluxos duplicados | 1 fluxo único |
| **Linhas de código** | ~800 linhas duplicadas | Consolidado |
| **Logs** | Separados por fonte | Centralizados |
| **Retry** | Manual ou nenhum | Automático com backoff |
| **Monitoramento** | Limitado | Endpoints admin completos |
| **Teste** | Difícil | Fácil (fila centralizada) |

### ✅ Operacionais

- **Visibilidade**: Endpoints admin para monitorar tudo
- **Controle**: Reprocessar eventos manualmente se necessário
- **Rastreabilidade**: EventID mantém contexto completo
- **Escalabilidade**: Pool de threads configurável
- **Resiliência**: Retry automático com limite

---

## 📚 Referências

### Arquivos Principais
1. `migration/unified/domain/TenantMigrationEvent.java` - Modelo
2. `migration/unified/service/TenantMigrationQueue.java` - Fila
3. `migration/unified/service/TenantMigrationQueueConsumer.java` - Consumer
4. `migration/unified/startup/TenantMigrationStartupWorker.java` - Inicialização
5. `migration/unified/presentation/controller/MigrationQueueAdminController.java` - Admin

### Arquivos Modificados
1. `config/startup/ApplicationStartupListener.java` - Reativado e refatorado
2. `tenant/infrastructure/service/TenantService.java` - Publica evento
3. `migration/async/config/AsyncMigrationConfig.java` - Executor (sem mudanças)

---

## 🎓 Próximos Passos

1. **Persistência de Eventos**: Substituir histórico em memória por banco de dados
2. **Métricas Prometheus**: Expor métricas para monitoramento
3. **Notifications**: Alertar admin se taxa de falha > threshold
4. **Parallelização**: Processar múltiplos eventos em paralelo
5. **Dead Letter Queue**: Tratar eventos impossíveis de processar

---

**Versão**: 2.0 (Refatorado para Fila Unificada)  
**Data**: Fevereiro 2026  
**Autor**: Sistema ERP  
