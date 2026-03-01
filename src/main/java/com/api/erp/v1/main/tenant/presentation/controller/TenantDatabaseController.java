package com.api.erp.v1.main.tenant.presentation.controller;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.tenant.application.dto.TenantDatasourceRequest;
import com.api.erp.v1.main.tenant.application.dto.TenantDatasourceResponse;
import com.api.erp.v1.main.tenant.application.dto.UpdateDatasourceResponse;
import com.api.erp.v1.main.tenant.domain.controller.ITenantDatabaseController;
import com.api.erp.v1.docs.openapi.tenant.TenantDatabaseOpenApiDocumentation;
import com.api.erp.v1.main.tenant.domain.entity.TenantPermissions;
import com.api.erp.v1.main.tenant.domain.service.ITenantDatasourceService;
import com.api.erp.v1.main.shared.infrastructure.documentation.RequiresXTenantId;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import com.api.erp.v1.main.migration.domain.TenantMigrationEvent;
import com.api.erp.v1.main.migration.service.TenantMigrationQueue;
import com.api.erp.v1.main.tenant.domain.repository.TenantDatasourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/tenant/database")
/**
 * PRESENTATION - REST Controller para Gerenciar Datasources de Tenants
 * 
 * Oferece 3 endpoints principais com responsabilidades bem definidas:
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
 * A. Criar novo tenant:
 *    1. POST /datasource/validate (pré-validação)
 *    2. POST /api/v1/tenant/register (cria tudo + enfileira)
 * 
 * B. Atualizar APENAS config de datasource (SEM rer-migrar):
 *    1. PUT /datasource (com runMigrations: false ou omitido)
 * 
 * C. Atualizar datasource E reenfileirar migrações:
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
    @Autowired
    private ITenantDatasourceService tenantDataSourceService;

    @Autowired
    private TenantMigrationQueue migrationQueue;

    @Autowired
    private TenantDatasourceRepository tenantDatasourceRepository;

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
        
        log.info("[DATASOURCE CONTROLLER] Validando configuração de datasource...");
        log.info("[DATASOURCE CONTROLLER] Host: {} | Port: {} | Database: {}", 
                request.host(), request.port(), request.databaseName());
        
        try {
            // Testar conexão com o datasource
            boolean isValid = tenantDataSourceService.testarConexao(request);
            
            if (!isValid) {
                log.error("[DATASOURCE CONTROLLER] ❌ Falha ao validar datasource");
                return ResponseEntity.badRequest().body(buildErrorResponse(
                        "Falha ao conectar com o banco de dados. Verifique as credenciais e configurações."
                ));
            }
            
            log.info("[DATASOURCE CONTROLLER] ✅ Datasource validado com sucesso");
            return ResponseEntity.ok(buildValidationSuccessResponse(request));
            
        } catch (Exception e) {
            log.error("[DATASOURCE CONTROLLER] ❌ Erro ao validar datasource", e);
            return ResponseEntity.badRequest().body(buildErrorResponse(
                    "Erro ao validar datasource: " + e.getMessage()
            ));
        }
    }
    
    private Map<String, Object> buildValidationSuccessResponse(TenantDatasourceRequest request) {
        return new java.util.HashMap<>() {{
            put("success", true);
            put("message", "Datasource válido e conectável");
            put("datasource", new java.util.HashMap<String, Object>() {{
                put("host", request.host());
                put("port", request.port());
                put("databaseName", request.databaseName());
                put("dbType", request.dbType());
                put("username", request.username());
            }});
        }};
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
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<?> configurarDatasourceEEnfileirarMigracao(
            @RequestBody TenantDatasourceRequest request) {
        Long tenantId = TenantContext.getTenantId();
        
        log.info("[DATASOURCE CONTROLLER] ════════════════════════════════════════════");
        log.info("[DATASOURCE CONTROLLER] CONFIGURANDO DATASOURCE + MIGRAÇÕES + SEED");
        log.info("[DATASOURCE CONTROLLER] Tenant ID: {}", tenantId);
        log.info("[DATASOURCE CONTROLLER] ════════════════════════════════════════════");
        
        try {
            // FASE 1: Configurar datasource
            log.info("[DATASOURCE CONTROLLER] 1️⃣  Configurando novo datasource...");
            TenantDatasourceResponse datasourceResponse = 
                    tenantDataSourceService.configurarDatasource(tenantId, request);
            log.info("[DATASOURCE CONTROLLER] ✅ Datasource configurado com sucesso");
            
            // FASE 2: Testar conexão
            log.info("[DATASOURCE CONTROLLER] 2️⃣  Testando conexão com datasource...");
            boolean testeConexao = tenantDataSourceService.testarConexao(request);
            if (!testeConexao) {
                log.error("[DATASOURCE CONTROLLER] ❌ Falha ao conectar com datasource");
                Map<String, Object> error = new java.util.HashMap<>();
                error.put("success", false);
                error.put("error", "Falha ao conectar com datasource");
                error.put("datasource", datasourceResponse);
                return ResponseEntity.badRequest().body(error);
            }
            log.info("[DATASOURCE CONTROLLER] ✅ Conexão teste bem-sucedida");
            
            // FASE 3: Enfileirar migração + seed (novo sistema unificado)
            log.info("[DATASOURCE CONTROLLER] 3️⃣  Enfileirando migração + seeders...");
            var datasource = tenantDatasourceRepository.findByTenantIdAndStatus(tenantId, true);
            var event = migrationQueue.enqueueEvent(
                    tenantId,
                    "Tenant " + tenantId,
                    datasource,
                    TenantMigrationEvent.MigrationEventSource.MANUAL_REQUEST
            );
            log.info("[DATASOURCE CONTROLLER] ✅ Evento enfileirado (EventID: {})", event.getEventId());
            log.info("[DATASOURCE CONTROLLER] 🚀 Processamento será iniciado automaticamente");
            
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
    @RequiresPermission(TenantPermissions.BUSCAR)
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
    @RequiresPermission(TenantPermissions.ATUALIZAR)
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
            // FASE 1: Atualizar configuração do datasource
            log.info("[DATASOURCE CONTROLLER] 1️⃣  Atualizando datasource...");
            TenantDatasourceResponse datasourceResponse = tenantDataSourceService.atualizarDatasource(tenantId, request);
            log.info("[DATASOURCE CONTROLLER] ✅ Datasource atualizado com sucesso");
            
            // FASE 2: Se runMigrations = true, enfileirar migrações
            if (request.runMigrations() != null && request.runMigrations()) {
                log.info("[DATASOURCE CONTROLLER] 2️⃣  Enfileirando migrações (flag: runMigrations = true)...");
                
                try {
                    var datasource = tenantDatasourceRepository.findByTenantIdAndStatus(tenantId, true);
                    var event = migrationQueue.enqueueEvent(
                            tenantId,
                            "Tenant " + tenantId,
                            datasource,
                            TenantMigrationEvent.MigrationEventSource.MANUAL_REQUEST
                    );
                    log.info("[DATASOURCE CONTROLLER] ✅ Evento enfileirado (EventID: {})", event.getEventId());
                    log.info("[DATASOURCE CONTROLLER] 🚀 Migrações serão processadas automaticamente");
                    
                    // Retorna response com info de migração enfileirada
                    UpdateDatasourceResponse responseWithMigration = new UpdateDatasourceResponse(
                            datasourceResponse,
                            new java.util.HashMap<>() {{
                                put("eventId", event.getEventId());
                                put("status", event.getStatus().getLabel());
                                put("source", event.getSource().getLabel());
                                put("enqueuedAt", event.getEnqueuedAt());
                                put("message", "Migrações enfileiradas com sucesso");
                            }},
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
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<?> enqueueMigration() {
        Long tenantId = TenantContext.getTenantId();
        
        log.info("[DATASOURCE CONTROLLER] ════════════════════════════════════════════");
        log.info("[DATASOURCE CONTROLLER] ENFILEIRANDO MIGRAÇÃO MANUALMENTE");
        log.info("[DATASOURCE CONTROLLER] Tenant ID: {}", tenantId);
        log.info("[DATASOURCE CONTROLLER] ════════════════════════════════════════════");
        
        try {
            // Buscar datasource ativo
            log.info("[DATASOURCE CONTROLLER] 1️⃣  Buscando datasource ativo...");
            var datasource = tenantDatasourceRepository.findByTenantIdAndStatus(tenantId, true);
            
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
            log.info("[DATASOURCE CONTROLLER] 🚀 Processamento será iniciado automaticamente");
            
            return ResponseEntity.accepted().body(buildMigrationQueuedResponse(event));
            
        } catch (Exception e) {
            log.error("[DATASOURCE CONTROLLER] ❌ Erro ao enfileirar migração", e);
            return ResponseEntity.internalServerError().body(buildErrorResponse(
                    "Erro ao enfileirar migração: " + e.getMessage()
            ));
        }
    }

    private Map<String, Object> buildMigrationQueuedResponse(TenantMigrationEvent event) {
        return new java.util.HashMap<>() {{
            put("success", true);
            put("message", "Migração enfileirada com sucesso");
            put("migration", new java.util.HashMap<>() {{
                put("eventId", event.getEventId());
                put("tenantId", event.getTenantId());
                put("tenantName", event.getTenantName());
                put("status", event.getStatus().getLabel());
                put("source", event.getSource().getLabel());
                put("enqueuedAt", event.getEnqueuedAt());
            }});
        }};
    }

    private Map<String, Object> buildConfigureAndMigrateResponse(
            TenantMigrationEvent event,
            TenantDatasourceResponse datasource) {
        return new java.util.HashMap<>() {{
            put("success", true);
            put("message", "Datasource configurado com sucesso. Migrações enfileiradas.");
            put("datasource", new java.util.HashMap<>() {{
                put("id", datasource.id());
                put("host", datasource.host());
                put("port", datasource.port());
                put("databaseName", datasource.databaseName());
                put("dbType", datasource.dbType());
                put("isActive", datasource.isActive());
            }});
            put("migration", new java.util.HashMap<>() {{
                put("eventId", event.getEventId());
                put("status", event.getStatus().getLabel());
                put("source", event.getSource().getLabel());
                put("enqueuedAt", event.getEnqueuedAt());
            }});
        }};
    }

    private Map<String, Object> buildErrorResponse(String errorMessage) {
        return new java.util.HashMap<>() {{
            put("success", false);
            put("error", errorMessage);
        }};
    }
}

