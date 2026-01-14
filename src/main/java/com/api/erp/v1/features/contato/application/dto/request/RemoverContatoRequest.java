package com.api.erp.v1.features.contato.application.dto.request;

/**
 * DTO para remover contato de um usuário
 */
public record RemoverContatoRequest(
        Long usuarioId,
        Long contatoId
) {
}
