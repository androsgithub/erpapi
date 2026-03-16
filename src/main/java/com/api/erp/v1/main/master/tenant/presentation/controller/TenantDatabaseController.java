package com.api.erp.v1.main.master.tenant.presentation.controller;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.master.tenant.application.dto.TenantDatasourceRequest;
import com.api.erp.v1.main.master.tenant.application.dto.TenantDatasourceResponse;
import com.api.erp.v1.main.master.tenant.application.dto.UpdateDatasourceResponse;
import com.api.erp.v1.main.master.tenant.domain.controller.ITenantDatabaseController;
import com.api.erp.v1.docs.openapi.tenant.TenantDatabaseOpenApiDocumentation;
import com.api.erp.v1.main.master.tenant.domain.entity.TenantPermissions;
import com.api.erp.v1.main.master.tenant.domain.service.ITenantDatasourceService;
import com.api.erp.v1.main.shared.infrastructure.documentation.RequiresXTenantId;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import com.api.erp.v1.main.migration.domain.TenantMigrationEvent;
import com.api.erp.v1.main.migration.service.TenantMigrationQueue;
import com.api.erp.v1.main.master.tenant.domain.repository.TenantDatasourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/tenant/database")
/**
 * PRESENTATION - REST Controller for Managing Tenant Datasources
 * 
 * Offers 3 main endpoints with well-defined responsibilities:
 * 
 * 1. POST /datasource/validate
 *    - Apenas VALIDA a conexão de um datasource
 *    - NÃO cria/atualiza nada no banco
 *    - Útil para pré-validação antes de criar novo tenant
 *    - Resposta: 200 (válido) ou 400 (inválido)
 * 
 * 2. PUT /datasource
 *    - ATUALIZA a configuração de datasource do tenant
 *    - Opcionalmente ENFILEIRA migrações (via flag: runMigrations = true)
 *    - Se runMigrations = false (padrão): Apenas atualiza config
 *    - Se runMigrations = true: Atualiza + Valida + Enfileira migrações
 *    - Resposta: 200 (apenas update) ou 202 (update + migrations)
 * 
 * 3. POST /datasource/configure-and-migrate
 *    - ATUALIZA datasource + VALIDA + ENFILEIRA migrações (sempre)
 *    - Alternativa ao PUT com runMigrations=true
 *    - Conveniência quando sempre quer reenfileirar
 *    - Fases: config → teste conexão → enfileira Flyway + seed
 *    - Resposta: 202 (aceito, processando em background)
 * 
 * FLUXOS DE USO:
 * 
 * A. Create novo tenant:
 *    1. POST /datasource/validate (pré-validação)
 *    2. POST /api/v1/tenant/register (cria tudo + enfileira)
 * 
 * B. Update APENAS config de datasource (SEM rer-migrar):
 *    1. PUT /datasource (com runMigrations: false ou omitido)
 * 
 * C. Update datasource E reenfileirar migrações:
 *    OPÇÃO 1 - Via PUT:
 *    1. PUT /datasource (com runMigrations: true)
 *    
 *    OPÇÃO 2 - Via POST (mais explícito):
 *    1. POST /datasource/validate (pré-validação)
 *    2. POST /datasource/configure-and-migrate (atualiza + reenfileira)
 * 
 * @author ERP System
 * @version 1.0
 */
public class TenantDatabaseController implements ITenantDatabaseController, TenantDatabaseOpenApiDocumentation {
    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";
    private static final String ERROR = "error";
    private static final String DATASOURCE = "datasource";
    private static final String STATUS = "status";
    private static final String EVENT_ID = "eventId";
    private static final String ENQUEUED_AT = "enqueuedAt";
    private static final String SOURCE = "source";
    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String DATABASE_NAME = "databaseName";
    private static final String DB_TYPE = "dbType";
    private static final String USERNAME = "username";

    private final ITenantDatasourceService tenantDataSourceService;
    private final TenantMigrationQueue migrationQueue;
    private final TenantDatasourceRepository tenantDatasourceRepository;

    public TenantDatabaseController(
            ITenantDatasourceService tenantDataSourceService,
            TenantMigrationQueue migrationQueue,
            TenantDatasourceRepository tenantDatasourceRepository) {
        this.tenantDataSourceService = tenantDataSourceService;
        this.migrationQueue = migrationQueue;
        this.tenantDatasourceRepository = tenantDatasourceRepository;
    }

    /**
     * ═════════════════════════════════════════════════════════════════════════
     * VALIDAR CONFIGURAÇÃO DE DATASOURCE
     * 
     * Valida a configuração e conectividade de um datasource ANTES de utilizá-lo.
     * Útil para o pré-requisito do fluxo de criação de novo tenant.
     * 
     * Resposta:
     * - 200: Datasource válido e conectável
     * - 400: Erro de validação ou conexão falhou
     * ═════════════════════════════════════════════════════════════════════════
     */
    @PostMapping("/datasource/validate")
    public ResponseEntity<?> validarDatasource(
            @RequestBody TenantDatasourceRequest request) {
        
        log.info("[DATASOURCE CONTROLLER] Validating datasource configuration...");
        log.info("[DATASOURCE CONTROLLER] Host: {} | Port: {} | Database: {}", 
                request.host(), request.port(), request.databaseName());
        
        try {
            // Test connection with the datasource
            boolean isValid = tenantDataSourceService.testarConexao(request);
            
            if (!isValid) {
                log.error("[DATASOURCE CONTROLLER] ❌ Failed to validate datasource");
                return ResponseEntity.badRequest().body(buildErrorResponse(
                        "Failed to connect to database. Check your credentials and settings."));
            }
            
            log.info("[DATASOURCE CONTROLLER] ✅ Datasource validated successfully");
            return ResponseEntity.ok(buildValidationSuccessResponse(request));
            
        } catch (Exception e) {
            log.error("[DATASOURCE CONTROLLER] ❌ Error validating datasource", e);
            return ResponseEntity.badRequest().body(buildErrorResponse(
                    "Error validating datasource: " + e.getMessage()
            ));
        }
    }
    
    private Map<String, Object> buildValidationSuccessResponse(TenantDatasourceRequest request) {
        Map<String, Object> response = new java.util.HashMap<>();
        response.put(SUCCESS, true);
        response.put(MESSAGE, "Datasource is valid and connectable");
        
        Map<String, Object> datasourceMap = new java.util.HashMap<>();
        datasourceMap.put(HOST, request.host());
        datasourceMap.put(PORT, request.port());
        datasourceMap.put(DATABASE_NAME, request.databaseName());
        datasourceMap.put(DB_TYPE, request.dbType());
        datasourceMap.put(USERNAME, request.username());
        response.put(DATASOURCE, datasourceMap);
        
        return response;
    }
    
    /**
     * ═════════════════════════════════════════════════════════════════════════
     * CONFIGURE DATASOURCE + ENFILEIRAR MIGRAÇÃO + SEED
     * 
     * Novo endpoint que configura datasource E enfileira automaticamente:
     * 1. Configura datasource do tenant
     * 2. Testa conexão com o novo datasource
     * 3. Enfileira migração (Flyway)
     * 4. Enfileira MainSeed (permissões, usuário admin, etc)
     * 
     * Resposta:
     * - 202: Datasource configurado e tarefas enfileiradas com sucesso
     * - 400: Erro de validação ou conexão falhou
     * - 500: Erro ao enfileirar migrações
     * ═════════════════════════════════════════════════════════════════════════
     */
    @Override
    @PostMapping("/datasource/configure-and-migrate")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.UPDATE)
    public ResponseEntity<?> configurarDatasourceEEnfileirarMigracao(
            @RequestBody TenantDatasourceRequest request) {
        Long tenantId = TenantContext.getTenantId();
        
        log.info("[DATASOURCE CONTROLLER] ════════════════════════════════════════════");
        log.info("[DATASOURCE CONTROLLER] CONFIGURANDO DATASOURCE + MIGRAÇÕES + SEED");
        log.info("[DATASOURCE CONTROLLER] Tenant ID: {}", tenantId);
        log.info("[DATASOURCE CONTROLLER] ════════════════════════════════════════════");
        
        try {
            // PHASE 1: Configure datasource
            log.info("[DATASOURCE CONTROLLER] 1️⃣  Configurando novo datasource...");
            TenantDatasourceResponse datasourceResponse = 
                    tenantDataSourceService.configurarDatasource(tenantId, request);
            log.info("[DATASOURCE CONTROLLER] ✅ Datasource configurado com sucesso");
            
            // PHASE 2: Testar conexão
            log.info("[DATASOURCE CONTROLLER] 2️⃣  Testando conexão com datasource...");
            boolean testeConexao = tenantDataSourceService.testarConexao(request);
            if (!testeConexao) {
                log.error("[DATASOURCE CONTROLLER] ❌ Falha ao conectar com datasource");
                Map<String, Object> error = new java.util.HashMap<>();
                error.put(SUCCESS, false);
                error.put(ERROR, "Falha ao conectar com datasource");
                error.put(DATASOURCE, datasourceResponse);
                return ResponseEntity.badRequest().body(error);
            }
            log.info("[DATASOURCE CONTROLLER] ✅ Conexão teste bem-sucedida");
            
            // PHASE 3: Enfileirar migração + seed (novo sistema unificado)
            log.info("[DATASOURCE CONTROLLER] 3️⃣  Enfileirando migração + seeders...");
            var datasource = tenantDatasourceRepository.findByTenantIdAndActiveTrue(tenantId).orElse(null);
            var event = migrationQueue.enqueueEvent(
                    tenantId,
                    "Tenant " + tenantId,
                    datasource,
                    TenantMigrationEvent.MigrationEventSource.MANUAL_REQUEST
            );
            log.info("[DATASOURCE CONTROLLER] ✅ Evento enfileirado (EventID: {})", event.getEventId());
            log.info("[DATASOURCE CONTROLLER] 🚀 Processesmento será iniciado automaticamente");
            
            return ResponseEntity.accepted().body(buildConfigureAndMigrateResponse(event, datasourceResponse));
            
        } catch (IllegalArgumentException e) {
            log.error("[DATASOURCE CONTROLLER] ❌ Erro de validação: {}", e.getMessage());
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("[DATASOURCE CONTROLLER] ❌ Erro ao processar", e);
            return ResponseEntity.internalServerError()
                    .body(buildErrorResponse("Erro ao configurar datasource e enfileirar migrações: " + e.getMessage()));
        }
    }

    @Override
    @GetMapping("/datasource")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.SEARCH)
    public ResponseEntity<TenantDatasourceResponse> obterDatasource() {
        Long tenantId = TenantContext.getTenantId();
        log.info("[TENANT CONTROLLER] Getting datasource configuration for tenant: {}", tenantId);
        return tenantDataSourceService.obterDatasource(tenantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    @PutMapping("/datasource")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.UPDATE)
    public ResponseEntity<UpdateDatasourceResponse> atualizarDatasource(
            @RequestBody TenantDatasourceRequest request) {
        Long tenantId = TenantContext.getTenantId();
        
        log.info("[DATASOURCE CONTROLLER] ════════════════════════════════════════════");
        log.info("[DATASOURCE CONTROLLER] ATUALIZANDO DATASOURCE DO TENANT");
        log.info("[DATASOURCE CONTROLLER] Tenant ID: {} | Host: {} | Port: {}", 
                tenantId, request.host(), request.port());
        log.info("[DATASOURCE CONTROLLER] Enfileirar Migrações: {}", request.runMigrations());
        log.info("[DATASOURCE CONTROLLER] ════════════════════════════════════════════");
        
        try {
            // PHASE 1: Update configuração do datasource
            log.info("[DATASOURCE CONTROLLER] 1️⃣  Atualizando datasource...");
            TenantDatasourceResponse datasourceResponse = tenantDataSourceService.atualizarDatasource(tenantId, request);
            log.info("[DATASOURCE CONTROLLER] ✅ Datasource atualizado com sucesso");
            
            // PHASE 2: Se runMigrations = true, enfileirar migrações
            if (request.runMigrations() != null && request.runMigrations()) {
                log.info("[DATASOURCE CONTROLLER] 2️⃣  Enfileirando migrações (flag: runMigrations = true)...");
                
                try {
                    var datasource = tenantDatasourceRepository.findByTenantIdAndActiveTrue(tenantId).orElse(null);
                    var event = migrationQueue.enqueueEvent(
                            tenantId,
                            "Tenant " + tenantId,
                            datasource,
                            TenantMigrationEvent.MigrationEventSource.MANUAL_REQUEST
                    );
                    log.info("[DATASOURCE CONTROLLER] ✅ Evento enfileirado (EventID: {})", event.getEventId());
                    log.info("[DATASOURCE CONTROLLER] 🚀 Migrações serão processadas automaticamente");
                    
                    // Returns response com info de migração enfileirada
                    Map<String, Object> migrationInfo = new java.util.HashMap<>();
                    migrationInfo.put(EVENT_ID, event.getEventId());
                    migrationInfo.put(STATUS, event.getStatus().getLabel());
                    migrationInfo.put(SOURCE, event.getSource().getLabel());
                    migrationInfo.put(ENQUEUED_AT, event.getEnqueuedAt());
                    migrationInfo.put(MESSAGE, "Migrações enfileiradas com sucesso");
                    UpdateDatasourceResponse responseWithMigration = new UpdateDatasourceResponse(
                            datasourceResponse,
                            migrationInfo,
                            null
                    );
                    return ResponseEntity.accepted().body(responseWithMigration);
                    
                } catch (Exception e) {
                    log.error("[DATASOURCE CONTROLLER] ⚠️  Erro ao enfileirar migrações", e);
                    // Datasource foi atualizado, mas migrações falharam
                    UpdateDatasourceResponse partialResponse = new UpdateDatasourceResponse(
                            datasourceResponse,
                            null,
                            "Datasource atualizado, mas erro ao enfileirar migrações: " + e.getMessage()
                    );
                    return ResponseEntity.accepted().body(partialResponse);
                }
            }
            
            log.info("[DATASOURCE CONTROLLER] ✅ Operação concluída (migrações não enfileiradas)");
            return ResponseEntity.ok(new UpdateDatasourceResponse(datasourceResponse));
            
        } catch (Exception e) {
            log.error("[DATASOURCE CONTROLLER] ❌ Erro ao atualizar datasource", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    /**
     * ═════════════════════════════════════════════════════════════════════════
     * ENFILEIRAR MIGRAÇÕES MANUALMENTE
     * 
     * Endpoint simples para enfileirar novas migrações sem modificar datasource.
     * Útil quando o datasource já está configurado e você só quer rodar migrações.
     * 
     * Resposta:
     * - 202: Migração enfileirada com sucesso
     * - 400: Datasource não encontrado
     * - 500: Erro ao enfileirar
     * ═════════════════════════════════════════════════════════════════════════
     */
    @PostMapping("/datasource/enqueue-migration")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.UPDATE)
    public ResponseEntity<?> enqueueMigration() {
        Long tenantId = TenantContext.getTenantId();
        
        log.info("[DATASOURCE CONTROLLER] ════════════════════════════════════════════");
        log.info("[DATASOURCE CONTROLLER] ENFILEIRANDO MIGRAÇÃO MANUALMENTE");
        log.info("[DATASOURCE CONTROLLER] Tenant ID: {}", tenantId);
        log.info("[DATASOURCE CONTROLLER] ════════════════════════════════════════════");
        
        try {
            // Buscar datasource ativo
            log.info("[DATASOURCE CONTROLLER] 1️⃣  Buscando datasource ativo...");
            var datasource = tenantDatasourceRepository.findByTenantIdAndActiveTrue(tenantId).orElse(null);
            
            if (datasource == null) {
                log.error("[DATASOURCE CONTROLLER] ❌ Datasource ativo não encontrado para tenant: {}", tenantId);
                return ResponseEntity.badRequest().body(buildErrorResponse(
                        "Nenhum datasource ativo encontrado para este tenant"
                ));
            }
            
            log.info("[DATASOURCE CONTROLLER] ✅ Datasource encontrado");
            
            // Enfileirar migration
            log.info("[DATASOURCE CONTROLLER] 2️⃣  Enfileirando migração...");
            var event = migrationQueue.enqueueEvent(
                    tenantId,
                    "Tenant " + tenantId,
                    datasource,
                    TenantMigrationEvent.MigrationEventSource.MANUAL_REQUEST
            );
            
            log.info("[DATASOURCE CONTROLLER] ✅ Evento enfileirado (EventID: {})", event.getEventId());
            log.info("[DATASOURCE CONTROLLER] 🚀 Processesmento será iniciado automaticamente");
            
            return ResponseEntity.accepted().body(buildMigrationQueuedResponse(event));
            
        } catch (Exception e) {
            log.error("[DATASOURCE CONTROLLER] ❌ Erro ao enfileirar migração", e);
            return ResponseEntity.internalServerError().body(buildErrorResponse(
                    "Erro ao enfileirar migração: " + e.getMessage()
            ));
        }
    }

    private Map<String, Object> buildMigrationQueuedResponse(TenantMigrationEvent event) {
        Map<String, Object> response = new java.util.HashMap<>();
        response.put(SUCCESS, true);
        response.put(MESSAGE, "Migração enfileirada com sucesso");
        
        Map<String, Object> migrationMap = new java.util.HashMap<>();
        migrationMap.put(EVENT_ID, event.getEventId());
        migrationMap.put("tenantId", event.getTenantId());
        migrationMap.put("tenantName", event.getTenantName());
        migrationMap.put(STATUS, event.getStatus().getLabel());
        migrationMap.put(SOURCE, event.getSource().getLabel());
        migrationMap.put(ENQUEUED_AT, event.getEnqueuedAt());
        response.put("migration", migrationMap);
        
        return response;
    }

    private Map<String, Object> buildConfigureAndMigrateResponse(
            TenantMigrationEvent event,
            TenantDatasourceResponse datasource) {
        Map<String, Object> response = new java.util.HashMap<>();
        response.put(SUCCESS, true);
        response.put(MESSAGE, "Datasource configurado com sucesso. Migrações enfileiradas.");
        
        Map<String, Object> datasourceMap = new java.util.HashMap<>();
        datasourceMap.put("id", datasource.id());
        datasourceMap.put(HOST, datasource.host());
        datasourceMap.put(DATABASE_NAME, datasource.databaseName());
        datasourceMap.put(DB_TYPE, datasource.dbType());
        datasourceMap.put("isActive", datasource.isActive());
        response.put(DATASOURCE, datasourceMap);
        
        Map<String, Object> migrationMap = new java.util.HashMap<>();
        migrationMap.put(EVENT_ID, event.getEventId());
        migrationMap.put(STATUS, event.getStatus().getLabel());
        migrationMap.put(SOURCE, event.getSource().getLabel());
        migrationMap.put(ENQUEUED_AT, event.getEnqueuedAt());
        response.put("migration", migrationMap);
        
        return response;
    }

    private Map<String, Object> buildErrorResponse(String errorMessage) {
        Map<String, Object> response = new java.util.HashMap<>();
        response.put(SUCCESS, false);
        response.put(ERROR, errorMessage);
        return response;
    }
}

