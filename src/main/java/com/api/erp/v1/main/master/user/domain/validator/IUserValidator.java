package com.api.erp.v1.main.master.user.domain.validator;

import com.api.erp.v1.main.master.user.application.dto.request.CreateUserRequest;


public interface IUserValidator {
    void validar(CreateUserRequest request);
}
