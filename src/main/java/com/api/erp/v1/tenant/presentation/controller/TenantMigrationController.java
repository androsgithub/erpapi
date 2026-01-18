package com.api.erp.v1.tenant.presentation.controller;

import com.api.erp.v1.tenant.infrastructure.config.TenantMigrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller REST para gerenciamento de migrações de tenant.
 * 
 * Endpoints:
 * - POST /api/v1/tenant/migrations/tenants - Executa migrações em todos os tenants
 * - POST /api/v1/tenant/migrations/tenants/{tenantId} - Executa migração em um tenant específico
 * 
 * Nota: Requer role ADMIN por segurança
 */
@RestController
@RequestMapping("/api/v1/tenant/migrations")
@Tag(name = "Tenant Migrations", description = "Endpoints para gerenciamento de migrações de tenants")
public class TenantMigrationController {

    private static final Logger logger = LoggerFactory.getLogger(TenantMigrationController.class);
    
    private final TenantMigrationService tenantMigrationService;

    public TenantMigrationController(TenantMigrationService tenantMigrationService) {
        this.tenantMigrationService = tenantMigrationService;
    }

    /**
     * Executa migrações em TODOS os tenants ativos.
     * 
     * @return Relatório com resultado de cada migração
     */
    @PostMapping("/tenants")
    @Operation(
        summary = "Executar migrações em todos os tenants",
        description = "Busca todos os tenants ativos e executa as migrações Flyway em seus respectivos bancos de dados."
    )
    public ResponseEntity<?> migrateAllTenants() {
        logger.info("🔄 Requisição para migrar todos os tenants recebida");
        
        try {
            var startTime = System.currentTimeMillis();
            
            var report = tenantMigrationService.migrateAllTenants();
            
            long duration = System.currentTimeMillis() - startTime;
            
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("status", "SUCCESS");
            response.put("message", "Migrações executadas com sucesso");
            response.put("successCount", report.getSuccessCount());
            response.put("failureCount", report.getFailureCount());
            response.put("durationMs", duration);
            
            if (report.hasFailures()) {
                response.put("failures", report.getFailures());
                if (report.getCriticalError() != null) {
                    response.put("criticalError", report.getCriticalError());
                }
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(response);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Erro ao executar migrações", e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("timestamp", LocalDateTime.now());
            error.put("status", "ERROR");
            error.put("message", "Erro ao executar migrações");
            error.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Executa migração para um tenant específico.
     *
     * @param tenantId ID único do tenant
     * @return Confirmação de sucesso ou erro
     */
    @PostMapping("/tenants/{tenantId}")
    @Operation(
        summary = "Executar migração para um tenant específico",
        description = "Executa as migrações Flyway para um tenant específico identificado pelo ID."
    )
    public ResponseEntity<?> migrateTenant(@PathVariable Long tenantId) {
        logger.info("🔄 Requisição para migrar tenant: {}", tenantId);

        try {
            var startTime = System.currentTimeMillis();

            tenantMigrationService.migrateTenantById(tenantId);

            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("status", "SUCCESS");
            response.put("message", "Migração executada com sucesso para tenant: " + tenantId);
            response.put("tenantId", tenantId);
            response.put("durationMs", duration);

            logger.info("✅ Migração concluída para tenant: {} ({}ms)", tenantId, duration);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("⚠️ Tenant não encontrado ou inativo: {}", tenantId);

            Map<String, Object> error = new HashMap<>();
            error.put("timestamp", LocalDateTime.now());
            error.put("status", "NOT_FOUND");
            error.put("message", "Tenant não encontrado ou inativo");
            error.put("tenantId", tenantId);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

        } catch (Exception e) {
            logger.error("❌ Erro ao executar migração para tenant: {}", tenantId, e);

            Map<String, Object> error = new HashMap<>();
            error.put("timestamp", LocalDateTime.now());
            error.put("status", "ERROR");
            error.put("message", "Erro ao executar migração");
            error.put("tenantId", tenantId);
            error.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
