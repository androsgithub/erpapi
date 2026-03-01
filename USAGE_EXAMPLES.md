# 💡 Exemplos de Uso - Spring Batch Migrations

Exemplos práticos de como usar o sistema de migrações com Spring Batch.

---

## 1️⃣ Uso Automático (Startup da Aplicação)

### O que acontece
Quando você inicia a aplicação:

```bash
mvn spring-boot:run
```

Logs esperados:

```
╔════════════════════════════════════════════════════════════════╗
║         APLICAÇÃO PRONTA - DISPARANDO JOB DE MIGRAÇÕES        ║
╚════════════════════════════════════════════════════════════════╝

✅ Migrações Master (DataSource Principal) concluídas

📋 Disparando Spring Batch Job para migrações de tenants...
   - O job será executado em background
   - A aplicação continuará recebendo requisições
   - O processamento é em chunks (lotes) para melhor performance

╔════════════════════════════════════════════════════════════════╗
║         INICIANDO SPRING BATCH - MIGRAÇÕES DE TENANTS          ║
╣════════════════════════════════════════════════════════════════╣
║ Job ID:          550e8400-e29b-41d4-a716-446655440000
║ Job Name:        tenantMigrationJob
║ Start Time:      28/02/2026 10:30:45
║ Status:          STARTING
╚════════════════════════════════════════════════════════════════╝

▶ [1] Iniciando migração do tenant: Tenant A
  📋 [1] Executando migrações Flyway...
  ✅ [1] Migrações Flyway concluídas com sucesso
  🌱 [1] Executando seeders (dados iniciais)...
  ✅ [1] Seeders executados com sucesso
✅ [1] Migração COMPLETA: Tenant A (Flyway + Seed)

[... processamento de mais tenants em background ...]

╔════════════════════════════════════════════════════════════════╗
║         FIM DO SPRING BATCH - RESULTS DA EXECUÇÃO              ║
╣════════════════════════════════════════════════════════════════╣
║ Status Final:    COMPLETED
║ End Time:        28/02/2026 10:35:22
║ Duração:         45s
║
║ 📊 ESTATÍSTICAS:
║    ✅ Itens processados:    12
║    ⏭️  Itens pulados:       1
║    ❌ Erros totais:        0
╚════════════════════════════════════════════════════════════════╝

✅ JOB FINALIZADO COM SUCESSO
```

### Código (automático - não precisa fazer nada)
Workflow configurado em:
- `ApplicationStartupListener.java` - dispara listeners
- `MTMigrationBoostrap.execute()` - inicia job launcher
- `MigrationJobLauncher.launchMigrationJob()` - dispara job assíncrono

---

## 2️⃣ Disparar Job Manualmente (via Controller)

### Criar novo endpoint

Adicione este método em `TenantSchemaController` ou crie novo controller:

```java
package com.api.erp.v1.main.tenant.presentation.controller;

import com.api.erp.v1.main.migration.jobs.MigrationJobLauncher;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/migrations")
@RequiredArgsConstructor
public class MigrationControllerExample {

    private final MigrationJobLauncher jobLauncher;

    /**
     * Dispara o job de migração manualmente
     * 
     * Endpoint: POST /api/v1/migrations/run
     */
    @PostMapping("/run")
    public ResponseEntity<?> runMigrationJob() {
        try {
            // Dispara o job assincronamente
            jobLauncher.launchMigrationJob();

            // Retorna response ao cliente
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ACCEPTED");
            response.put("message", "Migration job dispatched successfully");
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.accepted().body(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of(
                            "status", "ERROR",
                            "message", e.getMessage()
                    ));
        }
    }
}
```

### Usar via REST API

```bash
curl -X POST http://localhost:8080/api/v1/migrations/run

# Response:
# {
#   "status": "ACCEPTED",
#   "message": "Migration job dispatched successfully",
#   "timestamp": "2026-02-28T10:30:45"
# }
```

---

## 3️⃣ Disparar Job para Tenant Específico

### Implementar logica personalizada

Estenda `MigrationJobConfig` para permitir filtro por tenant:

```java
@PostMapping("/run/{tenantId}")
public ResponseEntity<?> runMigrationJobForTenant(@PathVariable Long tenantId) {
    try {
        // Cria parâmetros com tenant ID específico
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("tenantId", tenantId)
                .addString("executionId", UUID.randomUUID().toString())
                .addString("timestamp", LocalDateTime.now().toString())
                .toJobParameters();

        // Dispara job com parâmetros
        var execution = jobLauncher.run(tenantMigrationJob, jobParameters);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "ACCEPTED");
        response.put("jobId", execution.getJobId());
        response.put("jobStatus", execution.getStatus().toString());

        return ResponseEntity.accepted().body(response);

    } catch (Exception e) {
        return ResponseEntity.status(500)
                .body(Map.of(
                        "status", "ERROR",
                        "message", e.getMessage()
                ));
    }
}
```

### Usar:
```bash
curl -X POST http://localhost:8080/api/v1/migrations/run/123

# Response:
# {
#   "status": "ACCEPTED",
#   "jobId": 1,
#   "jobStatus": "STARTING"
# }
```

---

## 4️⃣ Processa Tenants em Lotes Customizados

### Ajustar Chunk Size

Se quiser processar mais tenants em paralelo:

```java
// Em MigrationJobConfig.java

@Bean
public Step migrationStep() {
    return new StepBuilder("migrationStep", jobRepository)
            .<TenantDatasource, MigrationResult>chunk(10, transactionManager)  // Aumentar de 5 para 10
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .faultTolerant()
            .skip(Exception.class)
            .skipLimit(100)
            .build();
}
```

**Impacto**:
- ✅ Maior: Processamento mais rápido
- ❌ Uso de memória: Aumenta
- ❌ Recursos de BD: Maior consumo

---

## 5️⃣ Customizar ThreadPool

Se quiser mais paralelismo na execução:

```java
// Em AsyncMigrationConfig.java

@Bean(name = "migrationTaskExecutor")
public Executor migrationTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);      // Era 2
    executor.setMaxPoolSize(10);      // Era 5
    executor.setQueueCapacity(200);   // Era 100
    executor.setThreadNamePrefix("tenant-migration-");
    executor.setAwaitTerminationSeconds(600);  // Era 300
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.initialize();
    
    return executor;
}
```

**Impacto**:
- ✅ Maior: Mais jobs assincronos em paralelo
- ❌ Recursos: CPU e memória aumentam

---

## 6️⃣ Monitorar Execução do Job

### Via Logs
Os logs são estruturados - você pode:

```bash
# Ver apenas logs de migration
tail -f app.log | grep -E "\[.*\] (✅|❌|📋|🌱|▶)"

# Ver apenas erros
tail -f app.log | grep "❌"

# Ver estatísticas
tail -f app.log | grep "📊"
```

### Via JobRepository (Banco de Dados)

Se usar JobRepository com DB:

```sql
-- Ver todos os jobs executados
SELECT * FROM batch_job_execution;

-- Ver steps de um job
SELECT * FROM batch_step_execution WHERE job_execution_id = ?;

-- Ver contexto (parâmetros)
SELECT * FROM batch_job_execution_context WHERE job_execution_id = ?;
```

---

## 7️⃣ Tratamento de Erros Durante Migração

### Se um Tenant Falhar

O job não para - continua com próximos:

```
▶ [1] Iniciando migração do tenant: Tenant A
✅ [1] Migração COMPLETA

▶ [2] Iniciando migração do tenant: Tenant B
❌ [2] Erro durante migrações Flyway: Invalid migration file
⏭️ [2] Pulo de tenant com erro

▶ [3] Iniciando migração do tenant: Tenant C
✅ [3] Migração COMPLETA

[Resultado final]
✅ Sucessos: 2
❌ Falhas: 1
```

### Como Investigar o Erro

1. **Checar logs** - procurar linha com `❌ Erro`
2. **Stack trace** - seguir erro para causa raiz
3. **Verify arquivo de migração** - `schemas/postgresql/VX__*.sql`
4. **Corrigir** - remover/refatorar migration inválida
5. **Retry** - disparar job novamente

---

## 8️⃣ Adicionar Custom Logic ao Processor

Se precisar executar código custom durante migração:

```java
// Em TenantMigrationProcessor.java

@Override
public MigrationResult process(TenantDatasource datasource) {
    Long tenantId = datasource.getTenant().getId();
    
    try {
        // ... migrações padrão ...
        tenantMigrationService.migrateTenantById(tenantId);
        mainSeed.executar();
        
        // ✨ CUSTOM: Executar seu código
        customValidation(tenantId);
        customPostMigrationTask(tenantId);
        customNotification(tenantId);
        
        return MigrationResult.success(tenantId);
        
    } catch (Exception e) {
        // ... error handling ...
    }
}

private void customValidation(Long tenantId) {
    // Validar integridade dos dados após migração
    log.info("🔍 Validando integridade dos dados para tenant {}", tenantId);
}

private void customPostMigrationTask(Long tenantId) {
    // Executar alguma tarefa específica do seu negócio
    log.info("⚙️ Executando tarefas pós-migração para tenant {}", tenantId);
}

private void customNotification(Long tenantId) {
    // Notificar alguém que migração foi completa
    log.info("📧 Notificando sobre conclusão para tenant {}", tenantId);
}
```

---

## 9️⃣ Rollback de Migrations

Se uma migration deu problema:

### Flyway Rollback (se versão PAID do Flyway)

```bash
# Undo última migration
# Nota: Apenas Flyway Pro/Enterprise
flyway undo
```

### Manual Rollback

1. **Remover arquivo de migration** - deletar `VX__*.sql`
2. **Droptar schema** - `DROP SCHEMA IF EXISTS tenant_123 CASCADE;`
3. **Disparar job novamente** - executará todas as migrations novamente

```bash
curl -X POST http://localhost:8080/api/v1/migrations/run/123
```

---

## 🔟 Caso de Uso: Novo Tenant em Produção

### Fluxo Completo

```
1. Cliente novo é criado
   ↓
2. Tenant é inserido no master database
   ↓
3. TenantDatasource é configurada
   ↓
4. Disparar migration job via endpoint
   curl -X POST /api/v1/migrations/run/NEW_TENANT_ID
   ↓
5. Job executa:
   - Migrate schemas (Flyway)
   - Execute seeders (MainSeed)
   - Log sucesso/erro
   ↓
6. Cliente pode usar imediatamente após job terminar
```

### Código para TenantService

```java
@Service
@RequiredArgsConstructor
public class TenantService {
    
    private final TenantRepository tenantRepository;
    private final MigrationJobLauncher jobLauncher;
    
    public Tenant createNewTenant(CreateTenantRequest request) {
        // 1. Criar tenant
        Tenant tenant = new Tenant();
        tenant.setNome(request.getNome());
        tenant.setAtiva(true);
        Tenant saved = tenantRepository.save(tenant);
        
        // 2. Configurar datasource (já realizado em outro lugar)
        // ...
        
        // 3. Disparar migration job
        try {
            jobLauncher.launchMigrationJob();
            log.info("✅ Tenant {} criado e job de migração disparado", saved.getId());
        } catch (Exception e) {
            log.error("❌ Erro ao disparar migração para tenant {}", saved.getId(), e);
        }
        
        return saved;
    }
}
```

---

## 🎯 Best Practices

### ✅ DO's
- ✅ Use chunk processing para múltiplos tenants
- ✅ Implemente custom listeners para mais logging
- ✅ Armazene JobExecution para auditoria
- ✅ Monitore logs em tempo real
- ✅ Implemente retry para falhas temporárias
- ✅ Use JobParameters para rastreabilidade

### ❌ DON'Ts
- ❌ Não altere chunk size muito alto (>50)
- ❌ Não execute operações pesadas no Writer
- ❌ Não ignore erros - sempre log
- ❌ Não reutilize JobParameters (cria duplicatas)
- ❌ Não execute jobs de forma síncrona (bloqueia app)

---

## 📞 Troubleshooting Rápido

### Job não dispara
```bash
# Verificar logs
grep "INICIANDO SPRING BATCH" app.log

# Verificar se MigrationJobLauncher está como @Component
grep -r "public class MigrationJobLauncher"
```

### Job falha para todos os tenants
```bash
# Ver erro específico
grep "❌" app.log | head -5

# Verificar Master DataSource
grep "Migrações Master" app.log
```

### Alguns tenants falham, outros não
```bash
# Comportamento esperado! Fault tolerance ativo
# Ver qual tenant falhou
grep "❌ \[" app.log

# Corrigir migration específica desse tenant
# Retry disparando job novamente
```

### Job muito lento
```bash
# Aumentar chunk size (ex: de 5 para 10)
# Aumentar threads (ex: de 2 para 4)
# Ver tempo por tenant
grep "⏱️" app.log
```

---

**Versão**: 1.0  
**Data**: 28/02/2026
