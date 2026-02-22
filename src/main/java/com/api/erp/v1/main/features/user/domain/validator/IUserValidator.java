package com.api.erp.v1.main.features.user.domain.validator;

import com.api.erp.v1.main.features.user.application.dto.request.CreateUserRequest;


public interface IUserValidator {
    void validar(CreateUserRequest request);
}
