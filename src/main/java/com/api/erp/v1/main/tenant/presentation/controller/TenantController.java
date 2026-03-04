package com.api.erp.v1.main.tenant.presentation.controller;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.tenant.application.dto.*;
import com.api.erp.v1.main.tenant.application.mapper.TenantMapper;
import com.api.erp.v1.main.tenant.domain.controller.ITenantController;
import com.api.erp.v1.docs.openapi.tenant.TenantOpenApiDocumentation;
import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.tenant.domain.entity.TenantPermissions;
import com.api.erp.v1.main.tenant.domain.service.ITenantService;
import com.api.erp.v1.main.shared.infrastructure.documentation.RequiresXTenantId;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import com.api.erp.v1.main.tenant.infrastructure.service.NewTenantProvisionerUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Controller para gerenciar Tenants
 * Todos os métodos usam o datasource padrão (default)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tenant")
public class TenantController implements ITenantController, TenantOpenApiDocumentation {

    private final ITenantService tenantService;
    private final TenantMapper tenantMapper;
    private final NewTenantProvisionerUseCase newTenantProvisionerService;

    public TenantController(
            ITenantService tenantService,
            TenantMapper tenantMapper,
            NewTenantProvisionerUseCase newTenantProvisionerService) {
        this.tenantService = tenantService;
        this.tenantMapper = tenantMapper;
        this.newTenantProvisionerService = newTenantProvisionerService;
    }

    @GetMapping()
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.SEARCH)
    public ResponseEntity<TenantResponse> obter() {
        Long tenantId = TenantContext.getTenantId();
        Tenant tenant = tenantService.getDadosTenant(tenantId);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    /**
     * ═════════════════════════════════════════════════════════════════════════
     * CREATE NEW TENANT WITH DATASOURCE AND QUEUE MIGRATIONS
     * 
     * Lógica complexa delegada para NewTenantProvisionerService
     * 
     * Resposta: 201 Created com tenant, datasource e migration task
     * ═════════════════════════════════════════════════════════════════════════
     */
    @PostMapping("/register")
    public ResponseEntity<?> criarTenantComDatasource(
            @RequestBody CreateNewTenantWithDatasourceRequest request) {
        
        try {
            // Delega para o serviço de provisionamento
            NewTenantProvisionerUseCase.NewTenantProvisionResult result =
                    newTenantProvisionerService.execute(request);
            
            // Returns resposta estruturada
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    buildSuccessResponse(result)
            );
            
        } catch (IllegalArgumentException e) {
            log.error("❌ Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    buildErrorResponse("Validation falhou", e.getMessage())
            );
            
        } catch (Exception e) {
            log.error("❌ Erro ao criar novo tenant", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    buildErrorResponse("Erro interno do servidor", e.getMessage())
            );
        }
    }
    
    private Map<String, Object> buildSuccessResponse(
            NewTenantProvisionerUseCase.NewTenantProvisionResult result) {
        
        var tenant = result.getTenant();
        var datasource = result.getDatasource();
        var migrationEvent = result.getMigrationEvent();
        
        return new HashMap<>() {{
            put("success", true);
            put("timestamp", LocalDateTime.now());
            put("tenant", new HashMap<String, Object>() {{
                put("id", tenant.getId());
                put("nome", tenant.getNome());
                put("email", tenant.getEmail());
            }});
            put("datasource", new HashMap<String, Object>() {{
                put("id", datasource.id());
                put("host", datasource.host());
                put("port", datasource.port());
                put("database", datasource.databaseName());
                put("dbType", datasource.dbType());
            }});
            put("migration", new HashMap<String, Object>() {{
                put("eventId", migrationEvent.getEventId());
                put("status", migrationEvent.getStatus().getLabel());
                put("tenantId", migrationEvent.getTenantId());
                put("source", migrationEvent.getSource().name());
                put("enqueuedAt", migrationEvent.getEnqueuedAt());
            }});
            put("message", "Tenant provisionado com sucesso. Migrações em progresso.");
        }};
    }
    
    private Map<String, Object> buildErrorResponse(String error, String message) {
        return new HashMap<>() {{
            put("success", false);
            put("error", error);
            put("message", message);
            put("timestamp", LocalDateTime.now());
        }};
    }

    @GetMapping("/listar")    
    @RequiresXTenantId    
    @RequiresPermission(TenantPermissions.SEARCH)
    public ResponseEntity<List<TenantResponse>> listar() {
        log.info("[EMPRESA CONTROLLER] Listando todas as tenants");
        List<Tenant> tenants = tenantService.listarTenants();
        List<TenantResponse> responses = tenants.stream()
                .map(tenantMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.UPDATE)
    public ResponseEntity<TenantResponse> atualizar(

            @RequestBody TenantRequest request) {
        Long tenantId = TenantContext.getTenantId();
        Tenant tenant = tenantService.updateDadosTenant(tenantId, request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    /**
     * ═════════════════════════════════════════════════════════════════════════
     * UNIFIED CONFIG PATCH - Consolidação dos 6 PUTs antigos em 1 PATCH
     * 
     * Substitui:
     * - PUT /config/businesspartner
     * - PUT /config/user
     * - PUT /config/permission
     * - PUT /config/tenant
     * - PUT /config/address
     * - PUT /config/contact
     * 
     * Agora: PATCH /config (com campos opcionais para cada tipo de config)
     * 
     * Envie apenas os campos que deseja atualizar:
     * {
     *   "businesspartnerValidationEnabled": true,
     *   "userApprovalRequired": false,
     *   "permissionCacheEnabled": true,
     *   ...outros campos opcionais
     * }
     * 
     * Resposta: 200 OK com tenant atualizado
     * ═════════════════════════════════════════════════════════════════════════
     */
    @PatchMapping("/config")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.UPDATE)
    public ResponseEntity<TenantResponse> atualizarConfigUnificada(
            @RequestBody UnifiedTenantConfigRequest request) {
        Long tenantId = TenantContext.getTenantId();
        Tenant tenant = tenantService.updateConfig(tenantId, request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }
}
