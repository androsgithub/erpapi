package com.api.erp.v1.tenant.application.dto;

import com.api.erp.v1.tenant.domain.entity.TenantConfig;
import com.api.erp.v1.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.shared.domain.valueobject.Email;
import com.api.erp.v1.shared.domain.valueobject.Telefone;

import java.time.LocalDateTime;

public record TenantResponse(
        String id,
        String nome,
        String tenantSlug,
        CNPJ cnpj,
        Email email,
        Telefone telefone,
        String enderecoId,
        boolean ativa,
        TenantConfig config,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao

) {
}
