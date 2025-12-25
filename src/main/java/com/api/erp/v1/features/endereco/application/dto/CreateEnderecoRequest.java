package com.api.erp.v1.features.endereco.application.dto;

public record CreateEnderecoRequest(String rua, String numero, String complemento, String bairro, String cidade,
                                    String estado, String cep

) {
}
