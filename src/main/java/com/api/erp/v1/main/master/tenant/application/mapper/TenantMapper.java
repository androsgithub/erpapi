package com.api.erp.v1.main.master.tenant.application.mapper;

import com.api.erp.v1.main.master.tenant.application.dto.TenantResponse;
import com.api.erp.v1.main.master.tenant.domain.entity.Tenant;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface TenantMapper {

    TenantResponse toResponse(Tenant empresa);

    List<TenantResponse> toResponseList(List<Tenant> empresas);

    Set<TenantResponse> toResponseSet(Set<Tenant> empresas);
}
