package com.api.erp.v1.main.tenant.application.dto;

import com.api.erp.v1.main.tenant.domain.entity.configs.TenantConfig;
import com.api.erp.v1.main.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.main.shared.domain.valueobject.Email;
import com.api.erp.v1.main.shared.domain.valueobject.Telefone;

import java.time.LocalDateTime;

public record TenantResponse(
        String id,
        String nome,
        String tenantSlug,
        CNPJ cnpj,
        Email email,
        Telefone telefone,
        String addressId,
        boolean ativa,
        TenantConfig config,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao

) {
}
