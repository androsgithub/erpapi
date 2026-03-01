# 🚀 Quick Start - Fila Unificada de Migrações

## 📌 TL;DR (Muito Longo; Não Leu)

A nova arquitetura unifica dois fluxos separados em **um único mecanismo baseado em fila**:

```
Inicialização:    Tenants → Fila → Consumidor → Migração ✅
Novo Tenant:      Tenant  → Fila → Consumidor → Migração ✅
                   (Mesmo fluxo!)
```

---

## ⚡ 5 Minutos de Setup

### 1. Compilar
```bash
cd m:/Programacao\ Estudos/Projetos/java/erp/erpapi
mvn clean install
```

### 2. Executar
```bash
mvn spring-boot:run
```

### 3. Monitorar Inicialização
Procure por logs como:
```
╔════════════════════════════════════════════════════════════════╗
║    INICIALIZANDO FILA UNIFICADA DE MIGRAÇÕES DE TENANTS        ║
╚════════════════════════════════════════════════════════════════╝

📋 Buscando tenants ativos...
✅ Encontrados 3 tenants ativos
📥 Enfileirando tenants...
✅ Tenant enfileirado: Tenant A (Event: abc123)
✅ Tenant enfileirado: Tenant B (Event: def456)
✅ Tenant enfileirado: Tenant C (Event: ghi789)

🚀 Iniciando consumidor assíncrono...
```

### 4. Monitorar Processamento
Logs como:
```
╔════════════════════════════════════════════════════════════════╗
║    CONSUMIDOR DE FILA DE MIGRAÇÕES DE TENANTS                 ║
╚════════════════════════════════════════════════════════════════╝

▶ [abc123:1] Iniciando migração do tenant: Tenant A
  📋 [1] Executando migrações Flyway...
  ✅ [1] Migrações Flyway concluídas
  🌱 [1] Executando seeders...
  ✅ [1] Seeders executados
✅ [abc123:1] Migração COMPLETA (Flyway + Seed)
⏱️ Tempo de espera: 45ms | Tempo de execução: 2341ms
```

### 5. Verificar Estatísticas
```bash
curl http://localhost:8080/api/v1/admin/migrations/queue/stats \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta**:
```json
{
  "totalEvents": 3,
  "pendingEvents": 0,
  "inProgressEvents": 0,
  "completedEvents": 3,
  "failedEvents": 0,
  "progressPercent": 100.0,
  "successRatePercent": 100.0
}
```

✅ **Pronto!** A inicialização foi bem-sucedida.

---

## 🧪 Testando Novo Tenant

### 1. Criar Tenant
```bash
curl -X POST http://localhost:8080/api/v1/tenants \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Nova Empresa",
    "email": "empresa@example.com",
    "telefone": "(11) 99999-9999",
    "cnpj": "12.345.678/0001-90",
    "razaoSocial": "Nova Empresa LTDA",
    "tenantType": "ENTERPRISE"
  }'
```

### 2. Monitorar Enfileiramento
Procure por logs:
```
✅ [2] Novo tenant criado: Nova Empresa
   Enfileirando para migração...
📥 [abc789] Tenant enfileirado para migração
   Status: Aguardando (será processado em breve)
```

### 3. Monitorar Processamento
Igual ao startup - logs de Flyway + seed.

### 4. Verificar no Admin
```bash
# Listar todos os eventos
curl http://localhost:8080/api/v1/admin/migrations/queue/events \
  -H "Authorization: Bearer $TOKEN"

# Filtrar por tenant específico (no resultado)
# ou verificar evento específico
curl http://localhost:8080/api/v1/admin/migrations/queue/events/abc789 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🔧 Operações Comuns

### Ver Estatísticas em Tempo Real
```bash
watch -n 1 'curl -s http://localhost:8080/api/v1/admin/migrations/queue/stats \
  -H "Authorization: Bearer $TOKEN" | json_pp'
```

### Listar Eventos com Falha
```bash
curl http://localhost:8080/api/v1/admin/migrations/queue/events/failed \
  -H "Authorization: Bearer $TOKEN" | json_pp
```

### Reprocessar um Evento Específico
```bash
# Primeiro, obtenha o eventId de um evento com falha
EVENT_ID="abc123"

curl -X POST http://localhost:8080/api/v1/admin/migrations/queue/reprocess/$EVENT_ID \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta**:
```json
{
  "status": "success",
  "message": "Evento reprocessado com sucesso"
}
```

### Filtrar Eventos por Status
```bash
# Pending
curl http://localhost:8080/api/v1/admin/migrations/queue/events/pending \
  -H "Authorization: Bearer $TOKEN"

# In Progress
curl http://localhost:8080/api/v1/admin/migrations/queue/events/in-progress \
  -H "Authorization: Bearer $TOKEN"

# Completed
curl http://localhost:8080/api/v1/admin/migrations/queue/events/completed \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📊 Interpretando as Métricas

### Stats Response

```json
{
  "totalEvents": 250,           // Total de eventos processados
  "pendingEvents": 5,           // Aguardando fila
  "inProgressEvents": 2,        // Sendo processados agora
  "completedEvents": 240,       // Sucesso
  "failedEvents": 3,            // Falha permanente
  "queueSize": 7,               // Na fila aguardando
  "progressPercent": 96.0,      // % de progresso global
  "successRatePercent": 98.77,  // % de taxa de sucesso
  "totalExecutionTimeMs": 2145000,  // Tempo total gasto
  "avgExecutionTimeMs": 8936    // Média por evento
}
```

### Interpretação

- **progressPercent**: Se < 100%, ainda há eventos enfileirados/processando
- **successRatePercent**: Se < 95%, investigar eventos com falha
- **avgExecutionTimeMs**: Benchmark de performance esperada

---

## 🎯 Fluxo de Criação de Tenant (End-to-End)

```
1. Admin cria tenant via API
   ↓
2. TenantService.criarTenant() é executado
   ├─ Salva tenant no banco
   └─ Publica TenantCreatedEvent
   ↓
3. TenantCreationMigrationListener escuta evento
   ├─ Busca datasource do tenant
   └─ Chama migrationQueue.enqueueEvent(...)
   ↓
4. Evento entra na TenantMigrationQueue
   ├─ Status = PENDING
   ├─ EventID gerado automaticamente
   └─ Registrado no registry
   ↓
5. TenantMigrationQueueConsumer processa em background
   ├─ pollNext(timeout) aguarda fila
   ├─ Marca status = IN_PROGRESS
   ├─ Executa Flyway migrations
   ├─ Executa seeders (dados iniciais)
   ├─ Marca status = COMPLETED
   └─ Registra no histórico
   ↓
6. Admin monitora via /api/v1/admin/migrations/queue/stats
   ├─ progressPercent sobe para 100%
   ├─ completedEvents incrementa
   └─ successRatePercent mantém-se alto
```

---

## 🚨 Se Algo Der Errado

### 1. Evento Fica em PENDING
```bash
# Verificar se consumer está rodando
curl http://localhost:8080/api/v1/admin/migrations/queue/stats
# Se inProgressEvents = 0 e pendingEvents > 0, consumer parou

# Ver logs da aplicação (verificar ApplicationStartupListener)
grep "INICIALIZANDO FILA" app.log
grep "CONSUMIDOR DE FILA" app.log
```

### 2. Evento em FAILED
```bash
# Verificar detalhes do erro
curl http://localhost:8080/api/v1/admin/migrations/queue/events/{eventId}

# No resultado, procurar por "errorMessage"
# Corrigir o problema (ex: Flyway syntax error)
# Reprocessar:
curl -X POST http://localhost:8080/api/v1/admin/migrations/queue/reprocess/{eventId}
```

### 3. Taxa de Sucesso Baixa
```bash
# Listar todos os eventos com falha
curl http://localhost:8080/api/v1/admin/migrations/queue/events/failed

# Analisar commonality nos errorMessage
# Exemplos comuns:
# - "Syntax error in migration V1__..." → Erro na script Flyway
# - "Connection refused" → Datasource indisponível
# - "Seed execution failed" → Erro no seed bean

# Corrigir raiz cause e reprocessar
```

---

## 📈 Performance Tips

### Para Aumentar Throughput
```properties
# Em application.properties
# Aumentar pool de threads
executor.corePoolSize=3
executor.maxPoolSize=10
executor.queueCapacity=200
```

### Para Reduzir Latência
```properties
# Processar de forma mais responsiva
consumer.pollTimeoutMs=5000   # Menos espera
```

### Para Debugging
```properties
# Aumentar verbosidade de logs
logging.level.com.api.erp.v1.main.migration=DEBUG
```

---

## 🔐 Segurança

Todos os endpoints de admin requerem:
- `Authorization: Bearer {token}`
- Role de admin no token

Se receber **401 Unauthorized**:
```bash
# Verificar token
echo $TOKEN

# Ou gerar novo token de teste
# (Usar sua metodologia de autenticação específica)
```

---

## 📚 Documentação Completa

Para detalhes aprofundados, consulte:

1. **`UNIFIED_MIGRATION_QUEUE_GUIDE.md`** - Guia técnico completo
2. **`REFACTORING_SUMMARY.md`** - Resumo de implementação

---

## ✅ Checklist Final

- [ ] Aplicação compilou com sucesso
- [ ] Aplicação iniciou sem erros
- [ ] Logs mostram "INICIALIZANDO FILA"
- [ ] Logs mostram "CONSUMIDOR DE FILA"
- [ ] Pelo menos 1 tenant foi enfileirado
- [ ] Stats endpoint retorna dados válidos
- [ ] Criou novo tenant e foi enfileirado
- [ ] Novo tenant foi processado com sucesso
- [ ] Taxa de sucesso > 95%

✨ **Parabéns! Você está pronto para usar a fila unificada!**

---

**Versão**: 2.0  
**Última Atualização**: Fevereiro 2026  
