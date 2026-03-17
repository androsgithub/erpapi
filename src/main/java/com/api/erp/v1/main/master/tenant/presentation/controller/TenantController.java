package com.api.erp.v1.main.master.tenant.presentation.controller;

import com.api.erp.v1.docs.openapi.tenant.TenantOpenApiDocumentation;
import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.master.tenant.application.dto.request.create.ProvisionTenantRequest;
import com.api.erp.v1.main.master.tenant.application.dto.request.create.TenantRequest;
import com.api.erp.v1.main.master.tenant.application.dto.request.update.UnifiedTenantConfigRequest;
import com.api.erp.v1.main.master.tenant.application.dto.response.TenantResponse;
import com.api.erp.v1.main.master.tenant.application.dto.response.TenantSummaryResponse;
import com.api.erp.v1.main.master.tenant.application.usecase.*;
import com.api.erp.v1.main.master.tenant.domain.controller.ITenantController;
import com.api.erp.v1.main.master.tenant.domain.entity.TenantPermissions;
import com.api.erp.v1.main.shared.infrastructure.documentation.RequiresXTenantId;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Presentation Controller - REST API for Tenant Management
 * <p>
 * ARCHITECTURE:
 * - Receives HTTP requests
 * - Delegates to UseCases (application layer)
 * - UseCases call domain services (domain layer)
 * - Returns HTTP responses
 * <p>
 * Dependency Flow: Controller → UseCase → Domain Service → Repository
 * <p>
 * NO direct service calls - all logic orchestrated via UseCases
 * NO domain logic in controller - only request/response handling
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/tenants")
public class TenantController implements ITenantController, TenantOpenApiDocumentation {

    // UseCases (Application Layer)
    private final CreateTenantUseCase createTenantUseCase;
    private final GetTenantUseCase getTenantUseCase;
    private final ListTenantsUseCase listTenantsUseCase;
    private final UpdateTenantUseCase updateTenantUseCase;
    private final UpdateTenantConfigUseCase updateTenantConfigUseCase;
    private final GetTenantConfigUseCase getTenantConfigUseCase;

    public TenantController(
            CreateTenantUseCase createTenantUseCase,
            GetTenantUseCase getTenantUseCase,
            ListTenantsUseCase listTenantsUseCase,
            UpdateTenantUseCase updateTenantUseCase,
            UpdateTenantConfigUseCase updateTenantConfigUseCase,
            GetTenantConfigUseCase getTenantConfigUseCase) {
        this.createTenantUseCase = createTenantUseCase;
        this.getTenantUseCase = getTenantUseCase;
        this.listTenantsUseCase = listTenantsUseCase;
        this.updateTenantUseCase = updateTenantUseCase;
        this.updateTenantConfigUseCase = updateTenantConfigUseCase;
        this.getTenantConfigUseCase = getTenantConfigUseCase;
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════════
     * GET CURRENT TENANT DETAILS
     * <p>
     * Endpoint: GET /api/v1/admin/tenants
     * Requires: X-Tenant-ID header (via @RequiresXTenantId)
     * Returns: 200 OK with tenant details
     * ═══════════════════════════════════════════════════════════════════════════
     */
    @GetMapping()
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.SEARCH)
    public ResponseEntity<TenantSummaryResponse> obter() {
        Long tenantId = TenantContext.getTenantId();
        log.info("[TENANT CONTROLLER] Getting tenant details: {}", tenantId);

        // Delegate to UseCase
        TenantResponse response = getTenantUseCase.execute(tenantId);
        TenantSummaryResponse summaryResponse = new TenantSummaryResponse(
                response.id(),
                response.name(),
                response.active(),
                response.trial()
        );

        return ResponseEntity.ok(summaryResponse);
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════════
     * CREATE NEW TENANT
     * <p>
     * Endpoint: POST /api/v1/admin/tenants
     * Returns: 201 Created with tenant details
     * <p>
     * Delegates to CreateTenantUseCase - simpler creation without immediate datasource
     * ═══════════════════════════════════════════════════════════════════════════
     */
    @PostMapping()
    public ResponseEntity<TenantSummaryResponse> criar(
            @RequestBody ProvisionTenantRequest request) {

        log.info("[TENANT CONTROLLER] Creating new tenant: {}", request.name());

        try {
            // Delegate to CreateTenantUseCase
            TenantResponse response = createTenantUseCase.execute(request);

            TenantSummaryResponse summaryResponse = new TenantSummaryResponse(
                    response.id(),
                    response.name(),
                    response.active(),
                    response.trial()
            );

            log.info("[TENANT CONTROLLER] ✅ Tenant created successfully with ID: {}", response.id());
            return ResponseEntity.status(HttpStatus.CREATED).body(summaryResponse);

        } catch (IllegalArgumentException e) {
            log.error("[TENANT CONTROLLER] ❌ Validation error: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("[TENANT CONTROLLER] ❌ Error creating tenant", e);
            throw e;
        }
    }



    /**
     * ═══════════════════════════════════════════════════════════════════════════
     * LIST ALL TENANTS
     * <p>
     * Endpoint: GET /api/v1/admin/tenants (with query param or as separate method)
     * Returns: 200 OK with list of all tenants
     * ═══════════════════════════════════════════════════════════════════════════
     */
    @GetMapping("/list")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.SEARCH)
    public ResponseEntity<List<TenantSummaryResponse>> listar() {
        log.info("[TENANT CONTROLLER] Listing all tenants");

        // Delegate to UseCase
        List<TenantResponse> responses = listTenantsUseCase.execute();

        // Map to TenantSummaryResponse for consistent response format
        List<TenantSummaryResponse> summaryResponses = responses.stream()
                .map(r -> new TenantSummaryResponse(
                        r.id(),
                        r.name(),
                        r.active(),
                        r.trial()
                ))
                .toList();

        return ResponseEntity.ok(summaryResponses);
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════════
     * UPDATE TENANT BASIC DATA
     * <p>
     * Endpoint: PATCH /api/v1/admin/tenants/{id}
     * Returns: 200 OK with updated tenant
     * ═══════════════════════════════════════════════════════════════════════════
     */
    @PatchMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.UPDATE)
    public ResponseEntity<TenantSummaryResponse> atualizar(
            @PathVariable Long id,
            @RequestBody TenantRequest request) {

        log.info("[TENANT CONTROLLER] Updating tenant: {}", id);

        // Delegate to UseCase
        TenantResponse response = updateTenantUseCase.execute(id, request);
        TenantSummaryResponse summaryResponse = new TenantSummaryResponse(
                response.id(),
                response.name(),
                response.active(),
                response.trial()
        );

        return ResponseEntity.ok(summaryResponse);
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════════
     * GET TENANT CONFIGURATION
     * <p>
     * Endpoint: GET /api/v1/admin/tenants/{id}/config
     * Returns: 200 OK with tenant configuration
     * ═══════════════════════════════════════════════════════════════════════════
     */
    @GetMapping("/{id}/config")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.SEARCH)
    public ResponseEntity<?> obterConfig(@PathVariable Long id) {
        log.info("[TENANT CONTROLLER] Getting tenant config: {}", id);

        try {
            // Delegate to UseCase
            var configResponse = getTenantConfigUseCase.execute(id);
            return ResponseEntity.ok(configResponse);
        } catch (Exception e) {
            log.error("[TENANT CONTROLLER] Error getting config", e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════════
     * UPDATE TENANT CONFIGURATION (UNIFIED)
     * <p>
     * Endpoint: PATCH /api/v1/admin/tenants/{id}/config
     * Returns: 200 OK with updated tenant
     * <p>
     * Consolidates multiple legacy endpoints into single unified update
     * Only non-null fields in request are processed
     * ═══════════════════════════════════════════════════════════════════════════
     */
    @PatchMapping("/{id}/config")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.UPDATE)
    public ResponseEntity<TenantSummaryResponse> atualizarConfigUnificada(
            @PathVariable Long id,
            @RequestBody UnifiedTenantConfigRequest request) {

        log.info("[TENANT CONTROLLER] Updating tenant config: {}", id);

        // Delegate to UseCase
        TenantResponse response = updateTenantConfigUseCase.execute(id, request);
        TenantSummaryResponse summaryResponse = new TenantSummaryResponse(
                response.id(),
                response.name(),
                response.active(),
                response.trial()
        );

        return ResponseEntity.ok(summaryResponse);
    }
}
