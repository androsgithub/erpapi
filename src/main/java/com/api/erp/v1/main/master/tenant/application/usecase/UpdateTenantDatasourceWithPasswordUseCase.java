package com.api.erp.v1.main.master.tenant.application.usecase;

import com.api.erp.v1.main.master.tenant.application.dto.request.update.UpdateTenantDatasourceWithPasswordRequest;
import com.api.erp.v1.main.master.tenant.application.dto.response.TenantDatasourceResponse;
import com.api.erp.v1.main.master.tenant.domain.service.ITenantDatasourceService;
import com.api.erp.v1.main.shared.common.error.InvalidPasswordVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase: Update Tenant Datasource with Password Verification
 * 
 * Responsibility: Update datasource settings with password security validation
 * 
 * Security Flow:
 * 1. Receive tenant ID and UpdateTenantDatasourceWithPasswordRequest
 * 2. Request includes: currentPassword (for verification) + newPassword (optional)
 * 3. Service calls ITenantDatasourceService.atualizarDatasourceComVerificacao()
 * 4. Service verifies currentPassword matches stored password (decrypted)
 * 5. If password matches: Update datasource with newPassword (or keep current if empty)
 * 6. If password doesn't match: Throw InvalidPasswordVerificationException
 * 7. Return TenantDatasourceResponse
 * 
 * Note: Migration queuing is handled separately in the controller
 */
@Slf4j
@Service
public class UpdateTenantDatasourceWithPasswordUseCase {

    private final ITenantDatasourceService datasourceService;

    public UpdateTenantDatasourceWithPasswordUseCase(
            ITenantDatasourceService datasourceService) {
        this.datasourceService = datasourceService;
    }

    /**
     * Execute: Update datasource configuration with password verification
     * 
     * @param tenantId The tenant ID
     * @param request UpdateTenantDatasourceWithPasswordRequest with currentPassword verification
     * @return TenantDatasourceResponse with updated datasource details
     * @throws InvalidPasswordVerificationException if currentPassword doesn't match
     */
    @Transactional
    public TenantDatasourceResponse execute(
            Long tenantId,
            UpdateTenantDatasourceWithPasswordRequest request) {
        
        log.info("[UPDATE DATASOURCE WITH PASSWORD UC] Starting password verification for tenant: {}", tenantId);

        try {
            // Call domain service which performs password verification
            TenantDatasourceResponse response = datasourceService.atualizarDatasourceComVerificacao(tenantId, request);
            
            log.info("[UPDATE DATASOURCE WITH PASSWORD UC] ✅ Datasource updated successfully with password verification");
            
            return response;
        } catch (InvalidPasswordVerificationException e) {
            log.warn("[UPDATE DATASOURCE WITH PASSWORD UC] ❌ Password verification FAILED for tenant: {}", tenantId);
            throw e;
        } catch (Exception e) {
            log.error("[UPDATE DATASOURCE WITH PASSWORD UC] ❌ Error updating datasource with password verification", e);
            throw e;
        }
    }
}
