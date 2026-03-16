package com.api.erp.v1.main.master.permission.application.mapper;

import com.api.erp.v1.main.master.permission.application.dto.response.PermissionResponse;
import com.api.erp.v1.main.master.permission.domain.entity.Permission;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionResponse toResponse(Permission permission);

    List<PermissionResponse> toResponseList(List<Permission> permissions);
}
