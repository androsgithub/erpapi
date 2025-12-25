package com.api.erp.v1.features.empresa.application.dto;

import com.api.erp.v1.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.shared.domain.valueobject.Email;
import com.api.erp.v1.shared.domain.valueobject.Telefone;

import java.util.List;


public record EmpresaRequest(
        String nome,
        CNPJ cnpj,
        Email email,
        Telefone telefone,
        Long enderecoId,
        boolean requerAprovacaoGestor,
        boolean requerEmailCorporativo,
        List<String> dominiosPermitidos

) {
}
