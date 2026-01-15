package com.api.erp.v1.shared.infrastructure.config.datasource.manual;

import com.api.erp.v1.shared.infrastructure.config.datasource.TenantContext;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component("currentTenantIdentifierResolver")
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId != null) {
            return String.valueOf(tenantId);
        } else {
            return "BOOTSTRAP";
        }
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
