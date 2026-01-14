package com.api.erp.v1.features.contato.application.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO de resposta para contatos de um usuário
 */
public record UsuarioContatosResponse(
        Long usuarioContatoId,
        Long usuarioId,
        Set<ContatoResponse> contatos,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
}
