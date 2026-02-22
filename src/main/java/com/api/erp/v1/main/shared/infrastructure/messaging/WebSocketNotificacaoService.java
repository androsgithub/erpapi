package com.api.erp.v1.main.shared.infrastructure.messaging;

import com.api.erp.v1.main.shared.infrastructure.messaging.dto.NotificacaoDTO;
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

    public void notificarUser(String userId, NotificacaoDTO notificacao) {
        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/notificacoes",
                notificacao
        );
    }
}
