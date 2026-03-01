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
@RequestMapping("/src/test/java/com/api/v1/tenant")
public class TenantController implements ITenantController, TenantOpenApiDocumentation {

    @Autowired
    private ITenantService tenantService;
    @Autowired
    private TenantMapper tenantMapper;
    @Autowired
    private NewTenantProvisionerUseCase newTenantProvisionerService;

    @GetMapping()
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.BUSCAR)
    public ResponseEntity<TenantResponse> obter() {
        Long tenantId = TenantContext.getTenantId();
        Tenant tenant = tenantService.getDadosTenant(tenantId);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PostMapping
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> criar(@RequestBody CriarTenantRequest request) {
        log.info("[EMPRESA CONTROLLER] Criando nova tenant: {}", request.nome());
        Tenant tenant = tenantService.criarTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantMapper.toResponse(tenant));
    }

    /**
     * ═════════════════════════════════════════════════════════════════════════
     * CRIAR NOVO TENANT COM DATASOURCE E ENFILEIRAR MIGRAÇÕES
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
            
            // Retorna resposta estruturada
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    buildSuccessResponse(result)
            );
            
        } catch (IllegalArgumentException e) {
            log.error("❌ Erro de validação: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    buildErrorResponse("Validação falhou", e.getMessage())
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
        var migrationTask = result.getMigrationTask();
        
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
                put("taskId", migrationTask.getTaskId());
                put("status", migrationTask.getStatus().name());
                put("executeSeed", migrationTask.isExecuteSeedAfterMigration());
                put("enqueuedAt", migrationTask.getEnqueuedAt());
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
    @RequiresPermission(TenantPermissions.BUSCAR)
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
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> atualizar(

            @RequestBody TenantRequest request) {
        Long tenantId = TenantContext.getTenantId();
        Tenant tenant = tenantService.updateDadosTenant(tenantId, request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PutMapping("/config/customer")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> atualizarCustomerConfig(

            @RequestBody CustomerConfigRequest request) {
        Long tenantId = TenantContext.getTenantId();
        Tenant tenant = tenantService.updateCustomerConfig(tenantId,request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PutMapping("/config/user")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> atualizarUserConfig(

            @RequestBody UserConfigRequest request) {
        Long tenantId = TenantContext.getTenantId();
        Tenant tenant = tenantService.updateUserConfig(tenantId,request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PutMapping("/config/permission")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> atualizarPermissionConfig(

            @RequestBody PermissionConfigRequest request) {
        Long tenantId = TenantContext.getTenantId();
        Tenant tenant = tenantService.updatePermissionConfig(tenantId,request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PutMapping("/config/tenant")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> atualizarTenantConfig(

            @RequestBody InternalTenantConfigRequest request) {
        Long tenantId = TenantContext.getTenantId();
        Tenant tenant = tenantService.updateInternalTenantConfig(tenantId,request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PutMapping("/config/address")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> atualizarAddressConfig(

            @RequestBody AddressConfigRequest request) {
        Long tenantId = TenantContext.getTenantId();
        Tenant tenant = tenantService.updateAddressConfig(tenantId, request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PutMapping("/config/contact")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> atualizarContactConfig(

            @RequestBody ContactConfigRequest request) {
        Long tenantId = TenantContext.getTenantId();
        Tenant tenant = tenantService.updateContactConfig(tenantId,request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }
}
