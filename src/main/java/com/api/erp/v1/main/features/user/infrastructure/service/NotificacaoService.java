package com.api.erp.v1.main.features.user.infrastructure.service;

import com.api.erp.v1.main.features.user.domain.entity.User;
import com.api.erp.v1.main.features.user.domain.service.INotificacaoService;
import com.api.erp.v1.main.shared.infrastructure.messaging.WebSocketNotificacaoService;
import com.api.erp.v1.main.shared.infrastructure.messaging.dto.NotificacaoDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificacaoService implements INotificacaoService {

    private final WebSocketNotificacaoService webSocketService;

    public NotificacaoService(WebSocketNotificacaoService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @Override
    public void notificarGestores(User user) {
        webSocketService.notificarGestores(
                new NotificacaoDTO(
                        "Novo usuário pendente",
                        "Usuário " + user.getNome_completo() + " aguarda aprovação",
                        "USER_PENDENTE",
                        LocalDateTime.now()
                )
        );
    }

    @Override
    public void notificarUserAprovado(User user) {
        webSocketService.notificarUser(
                user.getId().toString(),
                new NotificacaoDTO(
                        "Cadastro aprovado",
                        "Seu acesso ao sistema foi liberado",
                        "USER_APROVADO",
                        LocalDateTime.now()
                )
        );
    }

    @Override
    public void notificarUserRejeitado(User user, String motivo) {
        webSocketService.notificarUser(
                user.getId().toString(),
                new NotificacaoDTO(
                        "Cadastro rejeitado",
                        "Motivo: " + motivo,
                        "USER_REJEITADO",
                        LocalDateTime.now()
                )
        );
    }
}
