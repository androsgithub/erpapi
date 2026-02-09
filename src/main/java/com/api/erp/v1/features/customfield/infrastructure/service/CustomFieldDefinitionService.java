package com.api.erp.v1.features.customfield.infrastructure.service;

import com.api.erp.v1.features.customfield.application.dto.request.ChangeCustomFieldStatusRequest;
import com.api.erp.v1.features.customfield.application.dto.request.CreateCustomFieldRequest;
import com.api.erp.v1.features.customfield.application.dto.request.UpdateCustomFieldRequest;
import com.api.erp.v1.features.customfield.application.dto.response.CustomFieldResponse;
import com.api.erp.v1.features.customfield.application.mapper.ICustomFieldDefinitionMapper;
import com.api.erp.v1.features.customfield.domain.entity.CustomFieldDefinition;
import com.api.erp.v1.features.customfield.domain.repository.CustomFieldDefinitionRepository;
import com.api.erp.v1.features.customfield.infrastructure.validator.CustomFieldDefinitionValidator;
import com.api.erp.v1.tenant.infrastructure.service.TenantService;
import com.api.erp.v1.shared.domain.exception.BusinessException;
import com.api.erp.v1.shared.infrastructure.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomFieldDefinitionService {

    private final CustomFieldDefinitionRepository cfdRepository;
    private final TenantService tenantService;
    private final ICustomFieldDefinitionMapper cfdMapper;
    private final CustomFieldDefinitionValidator cfdValidator;
    private final SecurityService securityService;

    @Autowired
    public CustomFieldDefinitionService(CustomFieldDefinitionRepository cfdRepository, TenantService tenantService, ICustomFieldDefinitionMapper cfdMapper, CustomFieldDefinitionValidator cfdValidator, SecurityService securityService) {
        this.cfdRepository = cfdRepository;
        this.tenantService = tenantService;
        this.cfdMapper = cfdMapper;
        this.cfdValidator = cfdValidator;
        this.securityService = securityService;
    }

    public List<CustomFieldDefinition> getFieldsForTable(Long tenantId, String target) {
        return cfdRepository.findByTenantIdAndTargetAndActiveTrueOrderByFieldOrder(
                tenantId,
                target
        );
    }


    /* ===================== CREATE ===================== */

    public CustomFieldResponse create(Long tenantId, CreateCustomFieldRequest request) {
        cfdValidator.validateCreate(request, tenantId);

        CustomFieldDefinition field = new CustomFieldDefinition();
        field.setTenantId(tenantId);
        field.setTarget(request.target());
        field.setFieldKey(request.fieldKey());
        field.setLabel(request.label());
        field.setFieldType(request.fieldType());
        field.setRequired(Boolean.TRUE.equals(request.required()));
        field.setFieldOrder(request.fieldOrder() != null ? request.fieldOrder() : 0);
        field.setMetadata(request.metadata());
        field.setActive(true);

        cfdRepository.save(field);

        return cfdMapper.toResponse(field);
    }


    /* ===================== UPDATE ===================== */

    public CustomFieldResponse update(
            Long tenantId,
            Long fieldId,
            UpdateCustomFieldRequest request
    ) {
        CustomFieldDefinition field = cfdRepository
                .findByIdAndTenantId(fieldId, tenantId)
                .orElseThrow(() -> new BusinessException("Campo não encontrado"));

        cfdValidator.validateUpdate(request);

        if (request.label() != null)
            field.setLabel(request.label());

        if (request.fieldType() != null)
            field.setFieldType(request.fieldType());

        if (request.required() != null)
            field.setRequired(request.required());

        if (request.fieldOrder() != null)
            field.setFieldOrder(request.fieldOrder());

        if (request.metadata() != null)
            field.setMetadata(request.metadata());

        cfdRepository.save(field);

        return cfdMapper.toResponse(field);
    }


    /* ===================== ENABLE / DISABLE ===================== */

    public CustomFieldResponse changeStatus(
            Long tenantId,
            Long fieldId,
            ChangeCustomFieldStatusRequest request
    ) {
        CustomFieldDefinition field = cfdRepository
                .findByIdAndTenantId(fieldId, tenantId)
                .orElseThrow(() -> new BusinessException("Campo não encontrado"));

        cfdValidator.validateStatusChange(request.active());

        field.setActive(request.active());
        cfdRepository.save(field);

        return cfdMapper.toResponse(field);
    }


    /* ===================== DELETE ===================== */

    public void delete(Long tenantId, Long fieldId) {
        CustomFieldDefinition field = cfdRepository
                .findByIdAndTenantId(fieldId, tenantId)
                .orElseThrow(() -> new BusinessException("Campo não encontrado"));

        cfdValidator.validateDelete(field);

        cfdRepository.delete(field);
    }


    /* ===================== QUERY ===================== */

    public List<CustomFieldResponse> listByTable(
            Long tenantId,
            String target
    ) {
        List<CustomFieldDefinition> customFieldDefinitions = cfdRepository
                .findByTenantIdAndTargetAndActiveTrueOrderByFieldOrder(
                        tenantId,
                        target
                );
        return cfdMapper.toResponseList(customFieldDefinitions);
    }
}

