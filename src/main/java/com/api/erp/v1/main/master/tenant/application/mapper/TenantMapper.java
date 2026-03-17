package com.api.erp.v1.main.master.tenant.application.mapper;

import com.api.erp.v1.main.master.tenant.application.dto.response.TenantSummaryResponse;
import com.api.erp.v1.main.master.tenant.domain.entity.Tenant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TenantMapper {

    public TenantSummaryResponse toResponse(Tenant empresa) {
        if (empresa == null) {
            return null;
        }
        return new TenantSummaryResponse(
                empresa.getId(),
                empresa.getName(),
                empresa.getActive(),
                empresa.getTrial()
        );
    }

    public List<TenantSummaryResponse> toResponseList(List<Tenant> empresas) {
        if (empresas == null) {
            return List.of();
        }
        return empresas.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Set<TenantSummaryResponse> toResponseSet(Set<Tenant> empresas) {
        if (empresas == null) {
            return Set.of();
        }
        return empresas.stream()
                .map(this::toResponse)
                .collect(Collectors.toSet());
    }
}
