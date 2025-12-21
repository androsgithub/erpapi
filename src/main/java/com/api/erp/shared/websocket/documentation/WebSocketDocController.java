package com.api.erp.shared.websocket.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/docs/websocket")
@Tag(
        name = "WebSocket - Notificações",
        description = "Documentação dos canais WebSocket para notificações em tempo real"
)
public class WebSocketDocController {

    @GetMapping
    @Operation(
            summary = "Conexão WebSocket",
            description = """
        Endpoint WebSocket para notificações em tempo real.

        URL:
        ws://{host}/ws

        Protocolos:
        - STOMP sobre SockJS

        Canais disponíveis:
        - /topic/gestores → notificações para gestores
        - /queue/notificacoes → notificações privadas do usuário

        Payload exemplo:
        {
          "titulo": "Novo usuário pendente",
          "mensagem": "Usuário João Vitor aguarda aprovação",
          "tipo": "USUARIO_PENDENTE",
          "dataHora": "2025-01-01T10:00:00"
        }
        """
    )
    @ApiResponse(responseCode = "200", description = "Documentação do WebSocket")
    @SecurityRequirement(name = "bearerAuth")
    public void websocketInfo() {
        // endpoint fake apenas para documentação
    }
}
