package com.api.erp.v1.features.empresa.application.dto;

import com.api.erp.v1.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.shared.domain.valueobject.Email;
import com.api.erp.v1.shared.domain.valueobject.Telefone;

import java.time.LocalDateTime;
import java.util.List;

public record EmpresaResponse(
        String id,
        String nome,
        CNPJ cnpj,
        Email email,
        Telefone telefone,
        String enderecoId,
        boolean ativa,
        boolean requerAprovacaoGestor,
        boolean requerEmailCorporativo,
        List<String> dominiosPermitidos,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao

) {
}
