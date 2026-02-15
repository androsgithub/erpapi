package com.api.erp.v1.main.shared.infrastructure.messaging.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Notificação enviada via WebSocket")
public record NotificacaoDTO(

        @Schema(example = "Novo usuário pendente")
        String titulo,

        @Schema(example = "Usuário João Vitor aguarda aprovação")
        String mensagem,

        @Schema(example = "USUARIO_PENDENTE")
        String tipo,

        @Schema(example = "2025-01-01T10:00:00")
        LocalDateTime dataHora
) {}
