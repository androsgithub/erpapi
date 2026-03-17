package com.api.erp.v1.main.master.tenant.application.usecase;

import com.api.erp.v1.main.master.tenant.application.dto.request.create.TenantDatasourceRequest;
import com.api.erp.v1.main.master.tenant.application.dto.response.TenantDatasourceResponse;
import com.api.erp.v1.main.master.tenant.domain.service.ITenantDatasourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * UseCase: Validate Tenant Datasource Connection
 * 
 * Responsibility: Validate datasource connectivity without persisting
 * Useful for pre-validation before creating/updating datasource
 * 
 * Flow:
 * 1. Receive TenantDatasourceRequest
 * 2. Call ITenantDatasourceService.testarConexao()
 * 3. Return validation result
 */
@Slf4j
@Service
public class ValidateTenantDatasourceUseCase {

    private final ITenantDatasourceService datasourceService;

    public ValidateTenantDatasourceUseCase(
            ITenantDatasourceService datasourceService) {
        this.datasourceService = datasourceService;
    }

    /**
     * Execute: Validate datasource connection
     * 
     * @param request TenantDatasourceRequest with datasource settings
     * @return true if datasource is valid and connectable, false otherwise
     */
    public boolean execute(TenantDatasourceRequest request) {
        log.info("[VALIDATE DATASOURCE UC] Validating datasource: {}:{}",
                request.host(), request.port());

        // Call domain service to test connection
        boolean isValid = datasourceService.testarConexao(request);

        if (isValid) {
            log.info("[VALIDATE DATASOURCE UC] ✅ Datasource is valid and connectable");
        } else {
            log.warn("[VALIDATE DATASOURCE UC] ❌ Datasource validation failed");
        }

        return isValid;
    }
}
