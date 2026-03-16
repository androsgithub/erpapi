package com.api.erp.v1.main.master.permission.application.mapper;

import com.api.erp.v1.main.master.permission.application.dto.response.RoleResponse;
import com.api.erp.v1.main.master.permission.domain.entity.Role;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleMapper {

    public RoleResponse toResponse(Role role) {
        if (role == null) {
            return null;
        }
        return new RoleResponse(
                role.getName(),
                role.getPermissions() != null ? role.getPermissions() : Set.of()
        );
    }

    public Set<RoleResponse> toResponse(Set<Role> roles) {
        if (roles == null) {
            return Set.of();
        }
        return roles.stream()
                .map(this::toResponse)
                .collect(Collectors.toSet());
    }

    public Role fromResponse(RoleResponse roleResponse) {
        if (roleResponse == null) {
            return null;
        }
        return new Role(
                roleResponse.name(),
                roleResponse.permissions() != null ? roleResponse.permissions() : Set.of()
        );
    }
}
