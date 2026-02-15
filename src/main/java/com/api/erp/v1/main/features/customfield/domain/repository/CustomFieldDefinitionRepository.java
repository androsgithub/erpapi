package com.api.erp.v1.main.features.customfield.domain.repository;

import com.api.erp.v1.main.features.customfield.domain.entity.CustomFieldDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomFieldDefinitionRepository
        extends JpaRepository<CustomFieldDefinition, Long> {

    List<CustomFieldDefinition> findByTenantIdAndTargetAndActiveTrueOrderByFieldOrder(
            Long tenantId,
            String target
    );
    Optional<CustomFieldDefinition> findByIdAndTenantId(Long id, Long tenantId);

    boolean existsByTenantIdAndTargetAndFieldKey(
            Long tenantId,
            String target,
            String fieldKey
    );

}

