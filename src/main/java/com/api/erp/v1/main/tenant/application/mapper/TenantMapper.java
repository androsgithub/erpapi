package com.api.erp.v1.main.tenant.application.mapper;

import com.api.erp.v1.main.tenant.application.dto.TenantResponse;
import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TenantMapper {

    TenantResponse toResponse(Tenant empresa);

    List<TenantResponse> toResponseList(List<Tenant> empresas);
}
