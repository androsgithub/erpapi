package com.api.erp.v1.features.customfield.infrastructure.validator;

import com.api.erp.v1.features.customfield.application.dto.request.CreateCustomFieldRequest;
import com.api.erp.v1.features.customfield.application.dto.request.UpdateCustomFieldRequest;
import com.api.erp.v1.features.customfield.domain.entity.CustomFieldDefinition;
import com.api.erp.v1.features.customfield.domain.repository.CustomFieldDefinitionRepository;
import com.api.erp.v1.shared.domain.exception.BusinessException;
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
            throw new BusinessException("Nome da tabela é obrigatório");
        }

        if (!StringUtils.hasText(request.fieldKey())) {
            throw new BusinessException("fieldKey é obrigatório");
        }

        if (request.fieldKey().contains(" ")) {
            throw new BusinessException("fieldKey não pode conter espaços");
        }

        if (request.fieldType() == null) {
            throw new BusinessException("Tipo do campo é obrigatório");
        }

        if (repository.existsByTenantIdAndTargetAndFieldKey(
                tenantId,
                request.target(),
                request.fieldKey()
        )) {
            throw new BusinessException("Campo já registrado para esta tabela");
        }
    }

    /* ===================== UPDATE ===================== */

    public void validateUpdate(UpdateCustomFieldRequest request) {

        if (request.fieldOrder() != null && request.fieldOrder() < 0) {
            throw new BusinessException("fieldOrder não pode ser negativo");
        }
    }

    /* ===================== STATUS ===================== */

    public void validateStatusChange(Boolean active) {
        if (active == null) {
            throw new BusinessException("Status ativo/inativo deve ser informado");
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
