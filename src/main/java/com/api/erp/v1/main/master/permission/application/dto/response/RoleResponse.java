package com.api.erp.v1.main.master.permission.application.dto.response;

import com.api.erp.v1.main.master.permission.domain.entity.Permission;

import java.util.Set;

public record RoleResponse(
        String name,
        Set<Permission> permissions
) {
}
