package com.api.erp.v1.features.empresa.application.dto;

import com.api.erp.v1.features.empresa.domain.entity.EmpresaConfig;
import com.api.erp.v1.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.shared.domain.valueobject.Email;
import com.api.erp.v1.shared.domain.valueobject.Telefone;

import java.time.LocalDateTime;

public record EmpresaResponse(
        String id,
        String nome,
        CNPJ cnpj,
        Email email,
        Telefone telefone,
        String enderecoId,
        boolean ativa,
        EmpresaConfig config,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao

) {
}
