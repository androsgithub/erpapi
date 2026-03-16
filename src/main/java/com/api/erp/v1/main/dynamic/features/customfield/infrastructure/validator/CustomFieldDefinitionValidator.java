package com.api.erp.v1.main.dynamic.features.customfield.infrastructure.validator;

import com.api.erp.v1.main.dynamic.features.customfield.application.dto.request.CreateCustomFieldRequest;
import com.api.erp.v1.main.dynamic.features.customfield.application.dto.request.UpdateCustomFieldRequest;
import com.api.erp.v1.main.dynamic.features.customfield.domain.entity.CustomFieldDefinition;
import com.api.erp.v1.main.dynamic.features.customfield.domain.repository.CustomFieldDefinitionRepository;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class CustomFieldDefinitionValidator {

    private final CustomFieldDefinitionRepository repository;

    /* ===================== CREATE ===================== */

    public void validateCreate(CreateCustomFieldRequest request,Long tenantId) {

        if (!StringUtils.hasText(request.target())) {
            throw new BusinessException("Table name is required");
        }

        if (!StringUtils.hasText(request.fieldKey())) {
            throw new BusinessException("fieldKey is required");
        }

        if (request.fieldKey().contains(" ")) {
            throw new BusinessException("fieldKey cannot contain spaces");
        }

        if (request.fieldType() == null) {
            throw new BusinessException("Field type is required");
        }

        if (repository.existsByTenantIdAndTargetAndFieldKey(
                tenantId,
                request.target(),
                request.fieldKey()
        )) {
            throw new BusinessException("Field already registered for this table");
        }
    }

    /* ===================== UPDATE ===================== */

    public void validateUpdate(UpdateCustomFieldRequest request) {

        if (request.fieldOrder() != null && request.fieldOrder() < 0) {
            throw new BusinessException("fieldOrder cannot be negative");
        }
    }

    /* ===================== STATUS ===================== */

    public void validateStatusChange(Boolean active) {
        if (active == null) {
            throw new BusinessException("Active/inactive status must be informed");
        }
    }

    /* ===================== DELETE ===================== */

    public void validateDelete(CustomFieldDefinition field) {

        // Regra futura (exemplo):
        // if (fieldIsUsedInData(field)) {
        //     throw new BusinessException("Campo já possui dados e não pode ser removido");
        // }
    }
}
