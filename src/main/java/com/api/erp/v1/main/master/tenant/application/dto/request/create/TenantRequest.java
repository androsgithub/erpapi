package com.api.erp.v1.main.master.tenant.application.dto.request.create;

import com.api.erp.v1.main.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.main.shared.domain.valueobject.Email;
import com.api.erp.v1.main.shared.domain.valueobject.Telefone;


public record TenantRequest(
        String nome,
        String cnpj,
        String email,
        String telefone,
        Long addressId
) {
}
