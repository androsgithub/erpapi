# Análise e Implementação: Fila de Migrações no Tenant Datasource Controller

## 1. Análise do Sistema Existente

### 1.1 Arquitetura de Migrações

O sistema ERP possui uma arquitetura robusta de migrações assíncronas:

```
Migration Queue System
├── MigrationQueueService (Serviço Principal)
├── MigrationQueueTask (Tarefa de Migração)
├── MigrationQueueController (Endpoints de Monitoramento)
└── TenantMigrationService (Execução de Migrações)
```

### 1.2 Componentes Principais

#### **MigrationQueueService** (`src/main/java/com/api/erp/v1/main/migration/async/service`)
- **Responsabilidades:**
  - Gerencia a fila de migrações assincronamente
  - Enfileira migrações para todos os tenants ou para um tenant específico
  - Rastreia o status de cada migração
  - Processa a fila em background sem bloquear a aplicação

- **Métodos Principais:**
  - `enqueueTenantMigration(Long tenantId)` - Enfileira migração de um tenant
  - `enqueueAllTenantMigrations()` - Enfileira migrações de todos os tenants ativos
  - `processMigrationQueue()` - Processa a fila assincronamente
  - `getStats()` - Retorna estatísticas da fila

#### **MigrationQueueTask** (`src/main/java/com/api/erp/v1/main/migration/async/domain`)
- Representa uma tarefa de migração na fila
- Atributos principais:
  - `taskId` - Identificador único (UUID)
  - `tenantId` - ID do tenant
  - `tenantName` - Nome do tenant
  - `datasource` - Configuração da fonte de dados
  - `status` - Estado da tarefa (PENDING, IN_PROGRESS, COMPLETED, FAILED)
  - Timestamps de enfileiramento, início e conclusão

#### **TenantSchemaController** (`src/main/java/com/api/erp/v1/main/tenant/presentation/controller`)
- **Rota Base:** `/api/v1/tenant/database`
- Gerencia configurações de datasource do tenant
- Endpoints existentes:
  - `POST /datasource` - Configurar datasource
  - `GET /datasource` - Obter configuração
  - `PUT /datasource` - Atualizar datasource
  - `POST /datasource/teste` - Testar conexão

### 1.3 Sistema de Migrações Existente

**Arquivos de Migração:**
```
schemas/
├── clickhouse/
├── cockroachdb/
├── db2/
├── derby/
├── firebird/
├── h2/
├── hsqldb/
├── informix/
├── mariadb/
├── mysql/
├── oracle/
├── postgresql/
├── sqlite/
├── sqlserver/
├── sybase/
└── vertica/
```

As migrações são versionadas (V1__, V2__, etc.) e executadas via `TenantMigrationService`.

## 2. Implementação Realizada

### 2.1 Modificações no TenantSchemaController

**Arquivo:** [src/main/java/com/api/erp/v1/main/tenant/presentation/controller/TenantSchemaController.java](src/main/java/com/api/erp/v1/main/tenant/presentation/controller/TenantSchemaController.java)

#### Adições:
1. **Injeção de Dependência**
   ```java
   @Autowired
   private MigrationQueueService migrationQueueService;
   ```

2. **Novo Endpoint: Enfileirar Migração**
   ```
   POST /api/v1/tenant/database/datasource/migrate
   ```

#### Especificações do Endpoint

- **Método HTTP:** `POST`
- **Rota Completa:** `/api/v1/tenant/database/datasource/migrate`
- **Autenticação:** Requerido (`@RequiresXTenantId`)
- **Permissão:** `TenantPermissions.ATUALIZAR` (Atualizar)
- **Entrada:** Nenhum body necessário (utiliza tenantId do contexto)
- **Saída (202 Accepted - Sucesso):**
  ```json
  {
    "success": true,
    "taskId": "uuid-da-tarefa",
    "tenantId": 123,
    "tenantName": "Nome da Empresa",
    "status": "PENDING",
    "statusDescription": "Aguardando execução",
    "enqueuedAt": "2024-02-22T10:30:00",
    "message": "Migração enfileirada com sucesso"
  }
  ```

- **Saída (400 Bad Request - Erro de Validação):**
  ```json
  {
    "success": false,
    "error": "Datasource não configurado para tenant: 123"
  }
  ```

- **Saída (500 Internal Server Error - Erro Inesperado):**
  ```json
  {
    "success": false,
    "error": "Erro ao enfileirar migração: descrição do erro"
  }
  ```

### 2.2 Fluxo de Execução

**Sequência de Operação:**

```
CLIENT
  │
  └─→ POST /api/v1/tenant/database/datasource/migrate
       │
       ├─→ [Validação] TenantContext extrai tenantId
       │
       ├─→ [Validação] Verifica permissão ATUALIZAR
       │
       ├─→ [Chamada] migrationQueueService.enqueueTenantMigration(tenantId)
       │    │
       │    ├─→ Verifica se tenant existe
       │    ├─→ Verifica se datasource está configurado
       │    ├─→ Cria MigrationQueueTask
       │    ├─→ Adiciona à fila (taskQueue)
       │    └─→ Registra no taskRegistry
       │
       ├─→ [Resposta] 202 Accepted com detalhes da tarefa
       │
       └─→ [Background] MigrationQueueService processa fila
            │
            ├─→ @Async processMigrationQueue()
            ├─→ Executa tenantMigrationService.migrateTenantById(tenantId)
            └─→ Atualiza status da tarefa (COMPLETED ou FAILED)
```

## 3. Status da Tarefa

As tarefas de migração passam pelos seguintes estados:

| Status | Descrição |
|--------|-----------|
| `PENDING` | Aguardando execução na fila |
| `IN_PROGRESS` | Sendo processada no momento |
| `COMPLETED` | Concluída com sucesso |
| `FAILED` | Falha durante a execução |

## 4. Monitoramento e Consulta

O sistema fornece endpoints no `MigrationQueueController` para monitorar:

```
GET /api/v1/migrations/queue/stats              → Estatísticas gerais
GET /api/v1/migrations/queue/tasks              → Todas as tarefas
GET /api/v1/migrations/queue/tasks/{taskId}     → Detalhes de uma tarefa
GET /api/v1/migrations/queue/tasks/in-progress  → Tarefas em progresso
GET /api/v1/migrations/queue/tasks/failed       → Tarefas que falharam
GET /api/v1/migrations/queue/tasks/tenant/{id}  → Tarefas de um tenant
```

## 5. Exemplo de Uso

### 5.1 Enfileirar Migração

```bash
curl -X POST http://localhost:8080/api/v1/tenant/database/datasource/migrate \
  -H "X-Tenant-Id: 123" \
  -H "Authorization: Bearer TOKEN"
```

**Resposta:**
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

### 5.2 Monitorar Status

```bash
curl http://localhost:8080/api/v1/migrations/queue/stats \
  -H "Authorization: Bearer TOKEN"
```

### 5.3 Consultar Tarefa Específica

```bash
curl http://localhost:8080/api/v1/migrations/queue/tasks/a1b2c3d4-e5f6-4a5b-9c8d-7e6f5a4b3c2d \
  -H "Authorization: Bearer TOKEN"
```

## 6. Tratamento de Erros

O endpoint implementa tratamento robusto de erros:

### 6.1 Cenários de Erro

1. **Tenant não encontrado**
   - Status: 400 Bad Request
   - Mensagem: "Datasource não configurado para tenant: XXX"

2. **Datasource não configurado**
   - Status: 400 Bad Request
   - Mensagem: "Datasource não configurado para tenant: XXX"

3. **Erro inesperado**
   - Status: 500 Internal Server Error
   - Mensagem: "Erro ao enfileirar migração: [descrição do erro]"

### 6.2 Logging

O sistema registra todas as operações:
```
[TENANT CONTROLLER] Enfileirando migração de datasource para tenant: 123
✅ Tarefa de migração enfileirada para tenant: Empresa ABC (taskId)
```

## 7. Segurança

### 7.1 Camadas de Proteção

1. **Autenticação:** Requerido token JWT/Bearer
2. **Autorização:** Permissão `ATUALIZAR` no tenant
3. **Tenant Isolation:** Apenas o tenant autenticado pode enfileirar suas próprias migrações
4. **Validação:** Verifica existência de datasource antes de enfileirar

### 7.2 Anotações Aplicadas

```java
@RequiresXTenantId           // Autentica e extrai tenantId
@RequiresPermission(ATUALIZAR) // Valida permissão de atualização
```

## 8. Integração com Sistema Existente

- ✅ Usa `TenantMigrationService` existente
- ✅ Reutiliza `MigrationQueueService` já implementado
- ✅ Segue padrão de permissões do sistema
- ✅ Implementa logging consistente
- ✅ Usa estrutura de resposta padrão

## 9. Próximos Passos Opcionais

1. **Dashboard de Monitoramento:** Criar UI para visualizar status das migrações
2. **Notificações:** Enviar email quando migração completar/falhar
3. **Agendamento:** Permitir agendar migrações para horários específicos
4. **Retry Automático:** Implementar retry de migrações falhadas
5. **Histórico de Migrações:** Persistir log de todas as migrações executadas

## 10. Estrutura de Arquivos Afetados

```
src/main/java/com/api/erp/v1/main/
├── tenant/presentation/controller/
│   └── TenantSchemaController.java [MODIFICADO]
├── migration/async/
│   ├── service/
│   │   └── MigrationQueueService.java [EXISTENTE]
│   ├── domain/
│   │   └── MigrationQueueTask.java [EXISTENTE]
│   └── presentation/controller/
│       └── MigrationQueueController.java [EXISTENTE]
└── tenant/infrastructure/
    └── service/
        └── TenantMigrationService.java [EXISTENTE]

schemas/
├── clickhouse/     [Scripts de migração]
├── postgresql/     [Scripts de migração]
├── mysql/          [Scripts de migração]
└── [outros BD...]  [Scripts de migração]
```

---

**Data:** 22 de Fevereiro, 2026  
**Status:** ✅ Implementado e Validado  
**Versão:** 1.0
