package com.api.erp.v1.main.features.user.domain.repository;

import com.api.erp.v1.main.features.user.domain.entity.StatusUser;
import com.api.erp.v1.main.features.user.domain.entity.User;
import com.api.erp.v1.main.shared.domain.valueobject.CPF;
import com.api.erp.v1.main.shared.domain.valueobject.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(Email email);

    Optional<User> findByCpf(CPF cpf);

    List<User> findAll();

    List<User> findByStatus(StatusUser status);

    boolean existsByCpf(CPF cpf);

    boolean existsByEmail(Email email);

    void deleteById(UUID id);

    boolean existsByIdAndPermissions_IdAndTenantIdAndTenantGroupId(
            @Param("userId") Long userId,
            @Param("permissionId") Long permissionId,
            @Param("tenantId") Long tenantId,
            @Param("tenantGroupId") Long tenantGroupId
    );

    boolean existsByIdAndRoles_IdAndTenantIdAndTenantGroupId(
            Long userId,
            Long roleId,
            Long tenantId,
            Long tenantGroupId
    );

}
