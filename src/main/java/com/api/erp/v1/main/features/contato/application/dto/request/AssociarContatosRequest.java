package com.api.erp.v1.main.features.contato.application.dto.request;

import com.api.erp.v1.main.features.contato.application.dto.CreateContatoRequest;

import java.util.List;

/**
 * DTO para associar múltiplos contatos a um usuário
 */
public record AssociarContatosRequest(
        Long usuarioId,
        List<CreateContatoRequest> contatos
) {
}
