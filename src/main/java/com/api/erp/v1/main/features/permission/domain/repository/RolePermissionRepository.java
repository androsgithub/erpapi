package com.api.erp.v1.main.features.permission.domain.repository;

import com.api.erp.v1.main.features.permission.domain.entity.Role;
import com.api.erp.v1.main.features.permission.domain.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    Optional<Role> findByRole(Role role);
}
