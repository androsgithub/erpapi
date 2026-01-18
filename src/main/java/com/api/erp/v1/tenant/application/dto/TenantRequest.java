package com.api.erp.v1.tenant.application.dto;

import com.api.erp.v1.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.shared.domain.valueobject.Email;
import com.api.erp.v1.shared.domain.valueobject.Telefone;


public record TenantRequest(
        String nome,
        CNPJ cnpj,
        Email email,
        Telefone telefone,
        Long enderecoId
) {
}
