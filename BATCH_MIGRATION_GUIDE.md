# 🚀 Spring Batch - Migrações de Tenants

## 📋 Visão Geral

Este documento descreve a arquitetura do sistema de **migrações de tenants** usando **Spring Batch** no projeto ERP.

### Objetivo
Executar migrações de banco de dados (Flyway) e seeders (dados iniciais) para múltiplos tenants de forma eficiente e paralela, sem bloquear a inicialização da aplicação.

---

## 🏗️ Arquitetura

### Fluxo de Execução

```
1️⃣ STARTUP DA APLICAÇÃO
   ↓
2️⃣ FlywayConfig.java (SÍNCRONO)
   └─ Master DataSource é criado
   └─ Migrações Master são executadas
   ↓
3️⃣ ApplicationReadyEvent é disparado
   ↓
4️⃣ ApplicationStartupListener
   └─ Chama MTMigrationBoostrap.execute()
   └─ Chama MTSeedBootstrap.execute()
   ↓
5️⃣ MTMigrationBoostrap (este componente)
   └─ Dispara MigrationJobLauncher.launchMigrationJob()
   ↓
6️⃣ MigrationJobLauncher (ASSÍNCRONO - @Async)
   └─ Cria JobParameters únicos
   └─ Dispara tenantMigrationJob
   ↓
7️⃣ Spring Batch Job: tenantMigrationJob
   ├─ Step: migrationStep (chunk-based)
   │  ├─ Reader: TenantMigrationReader
   │  │  └─ Carrega tenants ativos e seus datasources
   │  │
   │  ├─ Processor: TenantMigrationProcessor
   │  │  ├─ Executa Flyway (via TenantMigrationService)
   │  │  └─ Executa Seeders (via MainSeed)
   │  │
   │  └─ Writer: TenantMigrationWriter
   │     └─ Consolida e loga resultados
   │
   └─ Listener: MigrationJobListener
      ├─ beforeJob: Logs iniciais
      └─ afterJob: Logs finais com estatísticas
```

### Componentes Principais

#### 📌 **MigrationJobLauncher.java**
- **Responsabilidade**: Disparar o Spring Batch Job de forma assíncrona
- **Método**: `launchMigrationJob()`
- **Anotação**: `@Async("migrationTaskExecutor")`
- **Localização**: `src/main/java/com/api/erp/v1/main/migration/jobs/`

#### 📌 **MigrationJobConfig.java** - Configuração do Job
```java
@Bean
public Job tenantMigrationJob(Step migrationStep) {
    // Define o job com listener e step
}

@Bean
public Step migrationStep() {
    // Chunk size: 5 tenants por vez
    // Fault Tolerant: pula tenants com erro
}
```

#### 📌 **TenantMigrationReader.java** - Leitura de Dados
```java
@BeforeStep
public void beforeStep(StepExecution stepExecution) {
    // Carrega todos os tenants ativos e seus datasources
    // Prepara iterator para leitura sequencial
}

@Override
public TenantDatasource read() {
    // Retorna próximo datasource ou null se fim
}
```

#### 📌 **TenantMigrationProcessor.java** - Processamento
```java
@Override
public MigrationResult process(TenantDatasource datasource) {
    // Fase 1: Executa Flyway migrations
    tenantMigrationService.migrateTenantById(tenantId);
    
    // Fase 2: Executa Seeders
    TenantContext.setTenantId(tenantId);
    mainSeed.executar();
    TenantContext.clear();
    
    // Retorna MigrationResult.success() ou .failure()
}
```

#### 📌 **TenantMigrationWriter.java** - Consolidação de Resultados
```java
@Override
public void write(Chunk<? extends MigrationResult> results) {
    // Agrupa resultados
    // Log detalhado de sucessos e falhas
    // Estatísticas por lote (chunk)
}
```

#### 📌 **MigrationJobListener.java** - Listener de Eventos
```java
@Override
public void beforeJob(JobExecution jobExecution) {
    // Logs iniciais com ID do job e timestamp
}

@Override
public void afterJob(JobExecution jobExecution) {
    // Logs finais com duração, status e estatísticas
}
```

---

## 🔧 Configurações

### ThreadPoolTaskExecutor para Migrações
Definido em `AsyncMigrationConfig.java`:

```yaml
Core Pool Size:     2 threads
Max Pool Size:      5 threads
Queue Capacity:     100 tasks
Thread Name Prefix: tenant-migration-
Await Termination:  300 segundos
```

### JobLauncher Configuration
Configurado em `MigrationJobConfig.java`:

```java
@Bean
public JobLauncher jobLauncher(JobRepository jobRepository) {
    var launcher = new SimpleJobLauncher();
    launcher.setJobRepository(jobRepository);
    launcher.setTaskExecutor(new SyncTaskExecutor()); // Síncrono dentro da thread assíncrona
    launcher.afterPropertiesSet();
    return launcher;
}
```

---

## 💾 Dados de Migração

### MigrationResult.java
Encapsula o resultado de uma migração:

```java
public class MigrationResult {
    private final Long tenantId;
    private final boolean success;
    private final String errorMessage;
    
    public static MigrationResult success(Long tenantId)
    public static MigrationResult failure(Long tenantId, String errorMessage)
}
```

### MigrationJobTask.java (Legacy - em MigrationQueueService)
Pode ser descontinuado em favor do Spring Batch `JobExecution`

---

## 📊 Fluxo de Chunk Processing

O Spring Batch processa em **chunks** (lotes) de 5 tenants:

```
Batch 1 (Tenants 1-5):
  ├─ Read → Carrega tenants 1-5
  ├─ Process → Migra todos 5
  └─ Write → Log dos 5 resultados

Batch 2 (Tenants 6-10):
  ├─ Read → Carrega tenants 6-10
  ├─ Process → Migra todos 5
  └─ Write → Log dos 5 resultados

...
```

### Fault Tolerance
- **Skip**: Máximo 100 erros antes de falhar
- **No Retry**: Não tenta novamente após erro
- **Continue**: Job continua mesmo com falhas em tenants individuais

---

## ⚙️ Configurações de Aplicação

### application.yml / application.properties

Para habilitar logging do Spring Batch:

```yaml
logging:
  level:
    org.springframework.batch: DEBUG
    com.api.erp.v1.main.migration: INFO
```

---

## 🎯 Casos de Uso

### 1️⃣ Inicialização da Aplicação (Automático)
```
APP START → Migrações Master → ApplicationReadyEvent → Spring Batch Job
```
- **Quando**: Ao iniciar a aplicação
- **Como**: Automático via ApplicationStartupListener
- **Resultado**: Todos os tenants migrados após app pronta

### 2️⃣ Migração de Novo Tenant (via Controller)
Implementar em `TenantSchemaController`:

```java
@PostMapping("/tenants/{id}/migrate")
public ResponseEntity<?> migrateTenant(@PathVariable Long id) {
    JobParameters params = new JobParametersBuilder()
        .addLong("tenantId", id)
        .addString("timestamp", LocalDateTime.now().toString())
        .toJobParameters();
    
    JobExecution execution = jobLauncher.run(tenantMigrationJob, params);
    return buildResponse(execution);
}
```

### 3️⃣ Migração Manual em Batch
```java
@PostMapping("/migrations/batch")
public ResponseEntity<?> runMigrations() {
    migrationJobLauncher.launchMigrationJob();
    return ResponseEntity.accepted().build();
}
```

---

## 📈 Monitoramento & Logs

### Logs Esperados

```
╔════════════════════════════════════════════════════════════════╗
║         APLICAÇÃO PRONTA - DISPARANDO JOB DE MIGRAÇÕES        ║
╚════════════════════════════════════════════════════════════════╝

✅ Migrações Master (DataSource Principal) concluídas

📋 Disparando Spring Batch Job para migrações de tenants...

╔════════════════════════════════════════════════════════════════╗
║         INICIANDO SPRING BATCH - MIGRAÇÕES DE TENANTS          ║
╣════════════════════════════════════════════════════════════════╣
║ Job ID:          xxx-xxx-xxx-xxx
║ Job Name:        tenantMigrationJob
║ Start Time:      28/02/2026 10:30:45
║ Status:          STARTING
╚════════════════════════════════════════════════════════════════╝

▶ [1] Iniciando migração do tenant: Tenant A
  📋 [1] Executando migrações Flyway...
  ✅ [1] Migrações Flyway concluídas com sucesso
  🌱 [1] Executando seeders (dados iniciais)...
  ✅ [1] Seeders executados com sucesso
✅ [1] Migração COMPLETA
```

---

## 🔄 Diferenças: MigrationQueueService vs Spring Batch

| Aspecto | MigrationQueueService | Spring Batch Job |
|---------|----------------------|------------------|
| **Tipo** | Async/Manual | Framework |
| **Persistência** | Em memória | JobRepository (DB) |
| **Historico** | Não | Sim |
| **Retry** | Manual | Built-in |
| **Chunk Processing** | Não | Sim |
| **Listener** | Customizado | JobExecutionListener |
| **Escalabilidade** | Limitada | Robusta |
| **Monitoring** | Limited | Rich |

### Recomendação
**Use Spring Batch Job** para:
- ✅ Inicialização da app
- ✅ Processamento em batch
- ✅ Audit trail completo

**Use MigrationQueueService** para (opcional):
- 🔄 Fila on-demand via controller
- 🔄 Processamento específico por tenant

---

## 🛠️ Troubleshooting

### Problema: Job não dispara na inicialização
**Solução**: 
- Verificar se `MigrationJobLauncher` é registrado como `@Component`
- Verificar se `JobRepository` está configurado
- Ver logs de erro no console

### Problema: Erro "Duplicate key exception"
**Solução**:
- JobParameters devem ser únicos
- MigrationJobLauncher já adiciona `UUID` e timestamp

### Problema: Seed não executa
**Solução**:
- Verificar se `TenantContext.setTenantId()` está sendo chamado
- Verificar se `MainSeed.executar()` não tira exceção
- Conferir logs do TenantMigrationProcessor

### Problema: Aplicação continua mesmo com erros
**Razão**: Proposital - MigrationJobLauncher não relança exceções
**Comportamento**: App fica operacional mesmo com falhas parciais

---

## 📚 Referências

- [Spring Batch Documentation](https://spring.io/projects/spring-batch)
- [Flyway Migration Guide](https://flywaydb.org/)
- [Spring Async Processing](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#scheduling)

---

## 👨‍💻 Próximos Passos Recomendados

1. ✅ **Refatorar MigrationQueueService** 
   - Manter apenas para compatibilidade com controllers
   - Delegaracall ao JobLauncher

2. ✅ **Implementar JobRepository persistente**
   - Usar database em vez de in-memory
   - Permitir recovery de jobs interrompidos

3. ✅ **Adicionar Metrics**
   - Tempo de execução por task
   - Taxa de sucesso/falha
   - Exportar para Prometheus/Grafana

4. ✅ **Criar Dashboard de Monitoramento**
   - Último job executado
   - Status de cada tenant
   - Histórico de execuções

---

**Versão**: 1.0  
**Data**: 28/02/2026  
**Autor**: ERP System
