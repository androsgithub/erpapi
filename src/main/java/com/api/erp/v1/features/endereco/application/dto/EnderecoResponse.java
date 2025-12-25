package com.api.erp.v1.features.endereco.application.dto;

import com.api.erp.v1.shared.domain.valueobject.CEP;

import java.time.LocalDateTime;

public record EnderecoResponse(
        Long id, String rua, String numero, String complemento, String bairro, String cidade, String estado, CEP cep,
        LocalDateTime dataCriacao, LocalDateTime dataAtualizacao
) {
}
