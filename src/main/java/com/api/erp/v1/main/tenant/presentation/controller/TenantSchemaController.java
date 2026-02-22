package com.api.erp.v1.main.tenant.presentation.controller;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.tenant.application.dto.DatasourceTesteResponse;
import com.api.erp.v1.main.tenant.application.dto.TenantDatasourceRequest;
import com.api.erp.v1.main.tenant.application.dto.TenantDatasourceResponse;
import com.api.erp.v1.main.tenant.domain.controller.ITenantDatabaseController;
import com.api.erp.v1.docs.openapi.tenant.TenantDatabaseOpenApiDocumentation;
import com.api.erp.v1.main.tenant.domain.entity.TenantPermissions;
import com.api.erp.v1.main.tenant.domain.service.ITenantDatasourceService;
import com.api.erp.v1.main.shared.infrastructure.documentation.RequiresXTenantId;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import com.api.erp.v1.main.migration.async.service.MigrationQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/tenant/database")
public class TenantSchemaController implements ITenantDatabaseController, TenantDatabaseOpenApiDocumentation {
    @Autowired
    private ITenantDatasourceService tenantDataSourceService;

    @Autowired
    private MigrationQueueService migrationQueueService;

    @PostMapping("/datasource")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantDatasourceResponse> configurarDatasource(
            @RequestBody TenantDatasourceRequest request) {
        Long tenantId = TenantContext.getTenantId();
        log.info("[EMPRESA CONTROLLER] Configurando datasource para tenant: {}", tenantId);
        TenantDatasourceResponse response = tenantDataSourceService.configurarDatasource(tenantId, request);
        return ResponseEntity.ok(response);
    }

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

    @PutMapping("/datasource")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantDatasourceResponse> atualizarDatasource(
            @RequestBody TenantDatasourceRequest request) {
        Long tenantId = TenantContext.getTenantId();
        log.info("[EMPRESA CONTROLLER] Atualizando datasource do tenant: {}", tenantId);
        TenantDatasourceResponse response = tenantDataSourceService.atualizarDatasource(tenantId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/datasource/teste")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.BUSCAR)
    public ResponseEntity<?> testarConexaoDatasource(
            @RequestBody TenantDatasourceRequest request) {
        Long tenantId = TenantContext.getTenantId();
        log.info("[TENANT CONTROLLER] Testing datasource connection for tenant: {}", tenantId);
        boolean sucesso = tenantDataSourceService.testarConexao(request);

        if (sucesso) {
            return ResponseEntity.ok(new DatasourceTesteResponse("Connection established successfully", true));
        } else {
            return ResponseEntity.badRequest().body(new DatasourceTesteResponse("Falha ao conectar com o banco de dados", false));
        }
    }

    @PostMapping("/datasource/migrate")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<?> enqueueDatasourceMigration() {
        Long tenantId = TenantContext.getTenantId();
        log.info("[TENANT CONTROLLER] Enfileirando migração de datasource para tenant: {}", tenantId);
        
        try {
            var task = migrationQueueService.enqueueTenantMigration(tenantId);
            log.info("[TENANT CONTROLLER] ✅ Tarefa enfileirada: {} (TaskID: {})", tenantId, task.getTaskId());
            
            // Inicia o processamento da fila em background
            log.info("[TENANT CONTROLLER] 🚀 Iniciando processamento da fila de migrações...");
            migrationQueueService.processMigrationQueue();
            
            return ResponseEntity.accepted().body(buildMigrationResponse(task));
        } catch (IllegalArgumentException e) {
            log.error("[TENANT CONTROLLER] Erro ao enfileirar migração: {}", e.getMessage());
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("[TENANT CONTROLLER] Erro inesperado ao enfileirar migração", e);
            return ResponseEntity.internalServerError().body(buildErrorResponse("Erro ao enfileirar migração: " + e.getMessage()));
        }
    }

    private Map<String, Object> buildMigrationResponse(com.api.erp.v1.main.migration.async.domain.MigrationQueueTask task) {
        return new java.util.HashMap<>() {{
            put("success", true);
            put("taskId", task.getTaskId());
            put("tenantId", task.getTenantId());
            put("tenantName", task.getTenantName());
            put("status", task.getStatus().name());
            put("statusDescription", task.getStatus().getDescription());
            put("enqueuedAt", task.getEnqueuedAt());
            put("message", "Migração enfileirada com sucesso");
        }};
    }

    private Map<String, Object> buildErrorResponse(String errorMessage) {
        return new java.util.HashMap<>() {{
            put("success", false);
            put("error", errorMessage);
        }};
    }
}

