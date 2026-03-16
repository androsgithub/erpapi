package com.api.erp.v1.main.dynamic.features.customfield.domain.controller;

import com.api.erp.v1.main.dynamic.features.customfield.application.dto.request.ChangeCustomFieldStatusRequest;
import com.api.erp.v1.main.dynamic.features.customfield.application.dto.request.CreateCustomFieldRequest;
import com.api.erp.v1.main.dynamic.features.customfield.application.dto.request.UpdateCustomFieldRequest;
import com.api.erp.v1.main.dynamic.features.customfield.application.dto.response.CustomFieldResponse;

import java.util.List;

public interface ICustomFieldDefinitionController {

    List<CustomFieldResponse> getCustomFields(String table);

    CustomFieldResponse create(CreateCustomFieldRequest request);

    CustomFieldResponse update(Long id, UpdateCustomFieldRequest request);

    CustomFieldResponse changeStatus(Long id, ChangeCustomFieldStatusRequest request);

    void delete(Long id);
}
