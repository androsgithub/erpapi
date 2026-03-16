package com.api.erp.v1.main.master.permission.application.mapper;

import com.api.erp.v1.main.master.permission.application.dto.response.RoleResponse;
import com.api.erp.v1.main.master.permission.domain.entity.Role;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleResponse toResponse(Role role);

    List<RoleResponse> toResponseList(List<Role> roles);

    Set<RoleResponse> toResponseSet(Set<Role> entities);
}
