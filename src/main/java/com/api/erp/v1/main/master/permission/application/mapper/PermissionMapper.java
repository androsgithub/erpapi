package com.api.erp.v1.main.master.permission.application.mapper;

import com.api.erp.v1.main.master.permission.application.dto.response.PermissionResponse;
import com.api.erp.v1.main.master.permission.domain.entity.Permission;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PermissionMapper {

    public PermissionResponse toResponse(Permission permission) {
        if (permission == null) {
            return null;
        }
        return new PermissionResponse(
                permission.getCode(),
                permission.getName(),
                permission.getModule(),
                permission.getAction()
        );
    }

    public Set<PermissionResponse> toResponse(Set<Permission> permissions) {
        if (permissions == null) {
            return Set.of();
        }
        return permissions.stream()
                .map(this::toResponse)
                .collect(Collectors.toSet());
    }

    public Permission fromResponse(PermissionResponse permissionResponse) {
        if (permissionResponse == null) {
            return null;
        }
        return new Permission(
                permissionResponse.code(),
                permissionResponse.name(),
                permissionResponse.module(),
                permissionResponse.action()
        );
    }
}
