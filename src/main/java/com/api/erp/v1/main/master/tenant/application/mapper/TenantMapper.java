package com.api.erp.v1.main.master.tenant.application.mapper;

import com.api.erp.v1.main.master.tenant.application.dto.TenantResponse;
import com.api.erp.v1.main.master.tenant.domain.entity.Tenant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TenantMapper {

    public TenantResponse toResponse(Tenant empresa) {
        if (empresa == null) {
            return null;
        }
        return new TenantResponse(
                empresa.getId().toString(),
                empresa.getName(),
                null, // tenantSlug - not available in entity
                null // cnpj - not available in entity
        );
    }

    public List<TenantResponse> toResponseList(List<Tenant> empresas) {
        if (empresas == null) {
            return List.of();
        }
        return empresas.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Set<TenantResponse> toResponseSet(Set<Tenant> empresas) {
        if (empresas == null) {
            return Set.of();
        }
        return empresas.stream()
                .map(this::toResponse)
                .collect(Collectors.toSet());
    }
}
