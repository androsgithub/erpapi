package com.api.erp.v1.features.usuario.domain.validator;

import com.api.erp.v1.features.usuario.application.dto.request.CreateUsuarioRequest;


public interface IUsuarioValidator {
    void validar(CreateUsuarioRequest request);
}
