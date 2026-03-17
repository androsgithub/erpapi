package com.api.erp.v1.main.master.tenant.presentation.controller;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.master.tenant.application.dto.request.create.TenantDatasourceRequest;
import com.api.erp.v1.main.master.tenant.application.dto.response.TenantDatasourceResponse;
import com.api.erp.v1.main.master.tenant.application.dto.response.UpdateDatasourceResponse;
import com.api.erp.v1.main.master.tenant.domain.controller.ITenantDatabaseController;
import com.api.erp.v1.docs.openapi.tenant.TenantDatabaseOpenApiDocumentation;
import com.api.erp.v1.main.master.tenant.domain.entity.TenantPermissions;
import com.api.erp.v1.main.shared.infrastructure.documentation.RequiresXTenantId;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import com.api.erp.v1.main.migration.domain.TenantMigrationEvent;
import com.api.erp.v1.main.migration.service.TenantMigrationQueue;
import com.api.erp.v1.main.master.tenant.domain.repository.TenantDatasourceRepository;
import com.api.erp.v1.main.master.tenant.application.usecase.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/tenants/datasource")
/**
 * PRESENTATION - REST Controller for Managing Tenant Datasources
 * 
 * ARCHITECTURE:
 * - Receives HTTP requests
 * - Delegates to UseCases (application layer)
 * - UseCases call domain services (domain layer)
 * - Returns HTTP responses
 * 
 * Dependency Flow: Controller → UseCase → Domain Service → Repository
 * 
 * NO direct service calls - all logic orchestrated via UseCases
 * NO domain logic in controller - only request/response handling
 * 
 * Offers 3 main endpoints with well-defined responsibilities:
 * 
 * 1. POST /validate
 *    - VALIDATES datasource connection WITHOUT persisting
 *    - Pre-validation before creating/updating tenant
 *    - Response: 200 (valid) or 400 (invalid)
 * 
 * 2. GET / & PUT /
 *    - GET: Retrieve current datasource configuration
 *    - PUT: UPDATE datasource configuration
 *    - Optional flag: runMigrations (enqueue migrations if true)
 *    - Response: 200 (update only) or 202 (update + migrations)
 * 
 * 3. POST /configure-and-migrate
 *    - UPDATE datasource + VALIDATE + ENQUEUE migrations (always)
 *    - Phases: config → test connection → enqueue Flyway + seed
 *    - Response: 202 (Accepted, processing in background)
 * 
 * @author ERP System
 * @version 2.0 - UseCase Based Architecture
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

    // UseCases (Application Layer)
    private final ValidateTenantDatasourceUseCase validateDatasourceUseCase;
    private final ConfigureTenantDatasourceUseCase configureDatasourceUseCase;
    private final UpdateTenantDatasourceUseCase updateDatasourceUseCase;
    private final com.api.erp.v1.main.master.tenant.application.usecase.UpdateTenantDatasourceWithPasswordUseCase updateDatasourceWithPasswordUseCase;
    private final GetTenantDatasourceUseCase getDatasourceUseCase;
    
    // Infrastructure services (for migration queuing)
    private final TenantMigrationQueue migrationQueue;
    private final TenantDatasourceRepository tenantDatasourceRepository;

    public TenantDatabaseController(
            ValidateTenantDatasourceUseCase validateDatasourceUseCase,
            ConfigureTenantDatasourceUseCase configureDatasourceUseCase,
            UpdateTenantDatasourceUseCase updateDatasourceUseCase,
            com.api.erp.v1.main.master.tenant.application.usecase.UpdateTenantDatasourceWithPasswordUseCase updateDatasourceWithPasswordUseCase,
            GetTenantDatasourceUseCase getDatasourceUseCase,
            TenantMigrationQueue migrationQueue,
            TenantDatasourceRepository tenantDatasourceRepository) {
        this.validateDatasourceUseCase = validateDatasourceUseCase;
        this.configureDatasourceUseCase = configureDatasourceUseCase;
        this.updateDatasourceUseCase = updateDatasourceUseCase;
        this.updateDatasourceWithPasswordUseCase = updateDatasourceWithPasswordUseCase;
        this.getDatasourceUseCase = getDatasourceUseCase;
        this.migrationQueue = migrationQueue;
        this.tenantDatasourceRepository = tenantDatasourceRepository;
    }


    /**
     * ═══════════════════════════════════════════════════════════════════════════
     * VALIDATE DATASOURCE CONNECTION
     * 
     * Endpoint: POST /api/v1/admin/tenants/{tenantId}/datasource/validate
     * Purpose: Validate datasource connectivity WITHOUT persisting
     * Returns: 200 (valid) or 400 (invalid)
     * ═══════════════════════════════════════════════════════════════════════════
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validarDatasource(
            @RequestBody TenantDatasourceRequest request) {
        Long tenantId = TenantContext.getTenantId();
        
        log.info("[DATASOURCE CONTROLLER] Validating datasource for tenant: {}", tenantId);
        log.info("[DATASOURCE CONTROLLER] Host: {} | Port: {} | Database: {}", 
                request.host(), request.port(), request.databaseName());
        
        try {
            // Delegate to UseCase
            boolean isValid = validateDatasourceUseCase.execute(request);
            
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
    
    /**
     * Helper: Build validation success response
     */
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
     * ═══════════════════════════════════════════════════════════════════════════
     * GET CURRENT DATASOURCE CONFIGURATION
     * 
     * Endpoint: GET /api/v1/admin/tenants/{tenantId}/datasource
     * Returns: 200 OK with datasource details
     * ═══════════════════════════════════════════════════════════════════════════
     */
    @GetMapping()
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.SEARCH)
    public ResponseEntity<TenantDatasourceResponse> obterDatasource() {
        Long tenantId = TenantContext.getTenantId();
        log.info("[DATASOURCE CONTROLLER] Getting datasource for tenant: {}", tenantId);
        
        try {
            // Delegate to UseCase
            TenantDatasourceResponse response = getDatasourceUseCase.execute(tenantId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("[DATASOURCE CONTROLLER] Datasource not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════════
     * UPDATE DATASOURCE CONFIGURATION (WITH OPTIONAL MIGRATION QUEUING)
     * 
     * Endpoint: PUT /api/v1/admin/tenants/{tenantId}/datasource
     * Returns: 200 (update only) or 202 (update + migrations enqueued)
     * 
     * If runMigrations=true: Configuration + validation + migration enqueueing
     * If runMigrations=false or omitted: Configuration only
     * ═══════════════════════════════════════════════════════════════════════════
     */
    @PutMapping()
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.UPDATE)
    public ResponseEntity<UpdateDatasourceResponse> atualizarDatasource(
            @RequestBody TenantDatasourceRequest request) {
        Long tenantId = TenantContext.getTenantId();
        
        log.info("[DATASOURCE CONTROLLER] ════════════════════════════════════════════");
        log.info("[DATASOURCE CONTROLLER] UPDATING TENANT DATASOURCE");
        log.info("[DATASOURCE CONTROLLER] Tenant ID: {} | Host: {} | Port: {}", 
                tenantId, request.host(), request.port());
        log.info("[DATASOURCE CONTROLLER] Enqueue Migrations: {}", request.runMigrations());
        log.info("[DATASOURCE CONTROLLER] ════════════════════════════════════════════");
        
        try {
            // PHASE 1: Update datasource configuration
            log.info("[DATASOURCE CONTROLLER] 1️⃣  Updating datasource...");
            TenantDatasourceResponse datasourceResponse = updateDatasourceUseCase.execute(tenantId, request);
            log.info("[DATASOURCE CONTROLLER] ✅ Datasource updated successfully");
            
            // PHASE 2: If runMigrations = true, enqueue migrations
            if (request.runMigrations() != null && request.runMigrations()) {
                log.info("[DATASOURCE CONTROLLER] 2️⃣  Enqueueing migrations (flag: runMigrations = true)...");
                
                try {
                    var datasource = tenantDatasourceRepository.findByTenantIdAndActiveTrue(tenantId).orElse(null);
                    var event = migrationQueue.enqueueEvent(
                            tenantId,
                            "Tenant " + tenantId,
                            datasource,
                            TenantMigrationEvent.MigrationEventSource.MANUAL_REQUEST
                    );
                    log.info("[DATASOURCE CONTROLLER] ✅ Event enqueued (EventID: {})", event.getEventId());
                    log.info("[DATASOURCE CONTROLLER] 🚀 Migrations will be processed automatically");
                    
                    // Build response with migration information
                    Map<String, Object> migrationInfo = new java.util.HashMap<>();
                    migrationInfo.put(EVENT_ID, event.getEventId());
                    migrationInfo.put(STATUS, event.getStatus().getLabel());
                    migrationInfo.put(SOURCE, event.getSource().getLabel());
                    migrationInfo.put(ENQUEUED_AT, event.getEnqueuedAt());
                    migrationInfo.put(MESSAGE, "Migrations enqueued successfully");
                    
                    UpdateDatasourceResponse responseWithMigration = new UpdateDatasourceResponse(
                            datasourceResponse,
                            migrationInfo,
                            null
                    );
                    return ResponseEntity.accepted().body(responseWithMigration);
                    
                } catch (Exception e) {
                    log.error("[DATASOURCE CONTROLLER] ⚠️  Error enqueueing migrations", e);
                    // Datasource was updated, but migrations failed
                    UpdateDatasourceResponse partialResponse = new UpdateDatasourceResponse(
                            datasourceResponse,
                            null,
                            "Datasource updated, but error enqueueing migrations: " + e.getMessage()
                    );
                    return ResponseEntity.accepted().body(partialResponse);
                }
            }
            
            log.info("[DATASOURCE CONTROLLER] ✅ Operation completed (migrations not enqueued)");
            return ResponseEntity.ok(new UpdateDatasourceResponse(datasourceResponse, null, null));
            
        } catch (Exception e) {
            log.error("[DATASOURCE CONTROLLER] ❌ Error updating datasource", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════════
     * UPDATE DATASOURCE WITH PASSWORD VERIFICATION
     * 
     * Endpoint: PUT /api/v1/admin/tenants/{tenantId}/datasource/update-with-password
     * 
     * Security-Enhanced Update:
     * - REQUIRES currentPassword verification before allowing update
     * - Optional newPassword parameter to change password during update
     * - If newPassword is empty, keeps current password
     * 
     * Request Body:
     * {
     *   "currentPassword": "existing_password",
     *   "newPassword": "new_password_or_empty",
     *   "host": "db.example.com",
     *   "port": 5432,
     *   "databaseName": "tenant_db",
     *   "username": "db_user",
     *   "dbType": "POSTGRESQL",
     *   "runMigrations": true
     * }
     * 
     * Flow:
     * 1. User provides currentPassword (must match stored password)
     * 2. Service verifies currentPassword against decrypted stored password
     * 3. If match: Update datasource (with newPassword if provided)
     * 4. If no match: Return 400 Bad Request with InvalidPasswordVerification error
     * 5. If runMigrations=true: Enqueue migrations after update
     * 
     * Returns: 200 (update only) or 202 (update + migrations enqueued)
     * ═══════════════════════════════════════════════════════════════════════════
     */
    @PutMapping("/update-with-password")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.UPDATE)
    public ResponseEntity<UpdateDatasourceResponse> atualizarDatasourceComVerificacaoDeSenha(
            @RequestBody com.api.erp.v1.main.master.tenant.application.dto.request.update.UpdateTenantDatasourceWithPasswordRequest request) {
        Long tenantId = TenantContext.getTenantId();
        
        log.info("[DATASOURCE CONTROLLER WITH PASSWORD] ════════════════════════════════════════════");
        log.info("[DATASOURCE CONTROLLER WITH PASSWORD] UPDATING TENANT DATASOURCE WITH PASSWORD VERIFICATION");
        log.info("[DATASOURCE CONTROLLER WITH PASSWORD] Tenant ID: {} | Host: {} | Port: {}", 
                tenantId, request.host(), request.port());
        log.info("[DATASOURCE CONTROLLER WITH PASSWORD] Password Change: {}", 
                request.newPassword() != null && !request.newPassword().isEmpty() ? "Changing" : "Keeping");
        log.info("[DATASOURCE CONTROLLER WITH PASSWORD] Enqueue Migrations: {}", request.runMigrations());
        log.info("[DATASOURCE CONTROLLER WITH PASSWORD] ════════════════════════════════════════════");
        
        try {
            // PHASE 1: Update datasource configuration with password verification
            log.info("[DATASOURCE CONTROLLER WITH PASSWORD] 1️⃣  Verifying password and updating datasource...");
            TenantDatasourceResponse datasourceResponse = updateDatasourceWithPasswordUseCase.execute(tenantId, request);
            log.info("[DATASOURCE CONTROLLER WITH PASSWORD] ✅ Datasource updated successfully with password verification");
            
            // PHASE 2: If runMigrations = true, enqueue migrations
            if (request.runMigrations() != null && request.runMigrations()) {
                log.info("[DATASOURCE CONTROLLER WITH PASSWORD] 2️⃣  Enqueueing migrations (flag: runMigrations = true)...");
                
                try {
                    var datasource = tenantDatasourceRepository.findByTenantIdAndActiveTrue(tenantId).orElse(null);
                    var event = migrationQueue.enqueueEvent(
                            tenantId,
                            "Tenant " + tenantId,
                            datasource,
                            TenantMigrationEvent.MigrationEventSource.MANUAL_REQUEST
                    );
                    log.info("[DATASOURCE CONTROLLER WITH PASSWORD] ✅ Event enqueued (EventID: {})", event.getEventId());
                    log.info("[DATASOURCE CONTROLLER WITH PASSWORD] 🚀 Migrations will be processed automatically");
                    
                    // Build response with migration information
                    Map<String, Object> migrationInfo = new java.util.HashMap<>();
                    migrationInfo.put(EVENT_ID, event.getEventId());
                    migrationInfo.put(STATUS, event.getStatus().getLabel());
                    migrationInfo.put(SOURCE, event.getSource().getLabel());
                    migrationInfo.put(ENQUEUED_AT, event.getEnqueuedAt());
                    migrationInfo.put(MESSAGE, "Migrations enqueued successfully after password-verified update");
                    
                    UpdateDatasourceResponse responseWithMigration = new UpdateDatasourceResponse(
                            datasourceResponse,
                            migrationInfo,
                            null
                    );
                    return ResponseEntity.accepted().body(responseWithMigration);
                    
                } catch (Exception e) {
                    log.error("[DATASOURCE CONTROLLER WITH PASSWORD] ⚠️  Error enqueueing migrations", e);
                    // Datasource was updated, but migrations failed
                    UpdateDatasourceResponse partialResponse = new UpdateDatasourceResponse(
                            datasourceResponse,
                            null,
                            "Datasource updated, but error enqueueing migrations: " + e.getMessage()
                    );
                    return ResponseEntity.accepted().body(partialResponse);
                }
            }
            
            log.info("[DATASOURCE CONTROLLER WITH PASSWORD] ✅ Operation completed (migrations not enqueued)");
            return ResponseEntity.ok(new UpdateDatasourceResponse(datasourceResponse, null, null));
            
        } catch (com.api.erp.v1.main.shared.common.error.InvalidPasswordVerificationException e) {
            log.warn("[DATASOURCE CONTROLLER WITH PASSWORD] ❌ Password verification FAILED for tenant: {}", tenantId);
            log.warn("[DATASOURCE CONTROLLER WITH PASSWORD] Error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[DATASOURCE CONTROLLER WITH PASSWORD] ❌ Error updating datasource with password verification", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════════
     * CONFIGURE DATASOURCE + VALIDATE + ENQUEUE MIGRATIONS
     * 
     * Endpoint: POST /api/v1/admin/tenants/{tenantId}/datasource/configure-migrate
     * Returns: 202 Accepted (processing in background)
     * 
     * All-in-one endpoint for:
     * 1. Configure datasource
     * 2. Test connection
     * 3. Enqueue migrations + seed
     * ═══════════════════════════════════════════════════════════════════════════
     */
    @PostMapping("/configure-migrate")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.UPDATE)
    public ResponseEntity<?> configurarDatasourceEEnfileirarMigracao(
            @RequestBody TenantDatasourceRequest request) {
        Long tenantId = TenantContext.getTenantId();
        
        log.info("[DATASOURCE CONTROLLER] ════════════════════════════════════════════");
        log.info("[DATASOURCE CONTROLLER] CONFIGURING DATASOURCE + MIGRATIONS + SEED");
        log.info("[DATASOURCE CONTROLLER] Tenant ID: {}", tenantId);
        log.info("[DATASOURCE CONTROLLER] ════════════════════════════════════════════");
        
        try {
            // PHASE 1: Configure datasource
            log.info("[DATASOURCE CONTROLLER] 1️⃣  Configuring datasource...");
            TenantDatasourceResponse datasourceResponse = 
                    configureDatasourceUseCase.execute(tenantId, request);
            log.info("[DATASOURCE CONTROLLER] ✅ Datasource configured successfully");
            
            // PHASE 2: Validate connection
            log.info("[DATASOURCE CONTROLLER] 2️⃣  Testing connection...");
            boolean isValid = validateDatasourceUseCase.execute(request);
            if (!isValid) {
                log.error("[DATASOURCE CONTROLLER] ❌ Failed to connect to datasource");
                Map<String, Object> error = new java.util.HashMap<>();
                error.put(SUCCESS, false);
                error.put(ERROR, "Failed to connect to datasource");
                error.put(DATASOURCE, datasourceResponse);
                return ResponseEntity.badRequest().body(error);
            }
            log.info("[DATASOURCE CONTROLLER] ✅ Connection test successful");
            
            // PHASE 3: Enqueue migrations + seed
            log.info("[DATASOURCE CONTROLLER] 3️⃣  Enqueueing migrations + seeders...");
            var datasource = tenantDatasourceRepository.findByTenantIdAndActiveTrue(tenantId).orElse(null);
            var event = migrationQueue.enqueueEvent(
                    tenantId,
                    "Tenant " + tenantId,
                    datasource,
                    TenantMigrationEvent.MigrationEventSource.MANUAL_REQUEST
            );
            log.info("[DATASOURCE CONTROLLER] ✅ Event enqueued (EventID: {})", event.getEventId());
            log.info("[DATASOURCE CONTROLLER] 🚀 Processing will start automatically");
            
            return ResponseEntity.accepted().body(buildConfigureAndMigrateResponse(event, datasourceResponse));
            
        } catch (IllegalArgumentException e) {
            log.error("[DATASOURCE CONTROLLER] ❌ Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("[DATASOURCE CONTROLLER] ❌ Error processing", e);
            return ResponseEntity.internalServerError()
                    .body(buildErrorResponse("Error configuring datasource and enqueueing migrations: " + e.getMessage()));
        }
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════════
     * MANUALLY ENQUEUE MIGRATIONS (without modifying datasource)
     * 
     * Endpoint: POST /api/v1/admin/tenants/{tenantId}/datasource/enqueue-migration
     * Returns: 202 Accepted
     * 
     * Useful when datasource is already configured and you just need to run migrations
     * ═══════════════════════════════════════════════════════════════════════════
     */
    @PostMapping("/enqueue-migration")
    @RequiresXTenantId
    @RequiresPermission(TenantPermissions.UPDATE)
    public ResponseEntity<?> enqueueMigration() {
        Long tenantId = TenantContext.getTenantId();
        
        log.info("[DATASOURCE CONTROLLER] ════════════════════════════════════════════");
        log.info("[DATASOURCE CONTROLLER] ENQUEUEING MIGRATION MANUALLY");
        log.info("[DATASOURCE CONTROLLER] Tenant ID: {}", tenantId);
        log.info("[DATASOURCE CONTROLLER] ════════════════════════════════════════════");
        
        try {
            // PHASE 1: Find active datasource
            log.info("[DATASOURCE CONTROLLER] 1️⃣  Finding active datasource...");
            var datasource = tenantDatasourceRepository.findByTenantIdAndActiveTrue(tenantId).orElse(null);
            
            if (datasource == null) {
                log.error("[DATASOURCE CONTROLLER] ❌ No active datasource found for tenant: {}", tenantId);
                return ResponseEntity.badRequest().body(buildErrorResponse(
                        "No active datasource found for this tenant"
                ));
            }
            
            log.info("[DATASOURCE CONTROLLER] ✅ Datasource found");
            
            // PHASE 2: Enqueue migration
            log.info("[DATASOURCE CONTROLLER] 2️⃣  Enqueueing migration...");
            var event = migrationQueue.enqueueEvent(
                    tenantId,
                    "Tenant " + tenantId,
                    datasource,
                    TenantMigrationEvent.MigrationEventSource.MANUAL_REQUEST
            );
            
            log.info("[DATASOURCE CONTROLLER] ✅ Event enqueued (EventID: {})", event.getEventId());
            log.info("[DATASOURCE CONTROLLER] 🚀 Processing will start automatically");
            
            return ResponseEntity.accepted().body(buildMigrationQueuedResponse(event));
            
        } catch (Exception e) {
            log.error("[DATASOURCE CONTROLLER] ❌ Error enqueueing migration", e);
            return ResponseEntity.internalServerError().body(buildErrorResponse(
                    "Error enqueueing migration: " + e.getMessage()
            ));
        }
    }

    /**
     * Helper: Build response for migration queued
     */
    private Map<String, Object> buildMigrationQueuedResponse(TenantMigrationEvent event) {
        Map<String, Object> response = new java.util.HashMap<>();
        response.put(SUCCESS, true);
        response.put(MESSAGE, "Migration enqueued successfully");
        
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

    /**
     * Helper: Build response for configure and migrate operation
     */
    private Map<String, Object> buildConfigureAndMigrateResponse(
            TenantMigrationEvent event,
            TenantDatasourceResponse datasource) {
        Map<String, Object> response = new java.util.HashMap<>();
        response.put(SUCCESS, true);
        response.put(MESSAGE, "Datasource configured successfully. Migrations enqueued.");
        
        Map<String, Object> datasourceMap = new java.util.HashMap<>();
        datasourceMap.put(HOST, datasource.host());
        datasourceMap.put(DATABASE_NAME, datasource.databaseName());
        datasourceMap.put(DB_TYPE, datasource.dbType());
        response.put(DATASOURCE, datasourceMap);
        
        Map<String, Object> migrationMap = new java.util.HashMap<>();
        migrationMap.put(EVENT_ID, event.getEventId());
        migrationMap.put(STATUS, event.getStatus().getLabel());
        migrationMap.put(SOURCE, event.getSource().getLabel());
        migrationMap.put(ENQUEUED_AT, event.getEnqueuedAt());
        response.put("migration", migrationMap);
        
        return response;
    }

    /**
     * Helper: Build error response
     */
    private Map<String, Object> buildErrorResponse(String errorMessage) {
        Map<String, Object> response = new java.util.HashMap<>();
        response.put(SUCCESS, false);
        response.put(ERROR, errorMessage);
        return response;
    }
}

