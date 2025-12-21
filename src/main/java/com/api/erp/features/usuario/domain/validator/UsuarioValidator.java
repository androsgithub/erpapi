package com.api.erp.features.usuario.domain.validator;

import com.api.erp.features.usuario.application.dto.request.CreateUsuarioRequest;


public interface UsuarioValidator {
    void validar(CreateUsuarioRequest request);
}
