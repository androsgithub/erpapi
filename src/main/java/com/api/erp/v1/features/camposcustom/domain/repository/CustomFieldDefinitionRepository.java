package com.api.erp.v1.features.camposcustom.domain.repository;

import com.api.erp.v1.features.camposcustom.domain.entity.CustomFieldDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomFieldDefinitionRepository
        extends JpaRepository<CustomFieldDefinition, Long> {

    List<CustomFieldDefinition> findByTenantIdAndTableNameAndActiveTrueOrderByFieldOrder(
            Long tenantId,
            String tableName
    );
    Optional<CustomFieldDefinition> findByIdAndTenantId(Long id, Long tenantId);

    boolean existsByTenantIdAndTableNameAndFieldKey(
            Long tenantId,
            String tableName,
            String fieldKey
    );

}

