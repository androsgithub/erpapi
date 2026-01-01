package com.api.erp.v1.features.empresa.application.dto;

import com.api.erp.v1.features.empresa.domain.entity.EmpresaConfig;
import com.api.erp.v1.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.shared.domain.valueobject.Email;
import com.api.erp.v1.shared.domain.valueobject.Telefone;


public record EmpresaRequest(
        String nome,
        CNPJ cnpj,
        Email email,
        Telefone telefone,
        Long enderecoId
) {
}
