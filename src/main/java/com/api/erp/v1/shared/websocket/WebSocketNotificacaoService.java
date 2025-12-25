package com.api.erp.v1.shared.websocket;

import com.api.erp.v1.shared.websocket.dto.NotificacaoDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificacaoService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificacaoService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notificarGestores(NotificacaoDTO notificacao) {
        messagingTemplate.convertAndSend("/topic/gestores", notificacao);
    }

    public void notificarUsuario(String usuarioId, NotificacaoDTO notificacao) {
        messagingTemplate.convertAndSendToUser(
                usuarioId,
                "/queue/notificacoes",
                notificacao
        );
    }
}
