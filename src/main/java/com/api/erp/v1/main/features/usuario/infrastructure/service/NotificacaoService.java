package com.api.erp.v1.main.features.usuario.infrastructure.service;

import com.api.erp.v1.main.features.usuario.domain.entity.Usuario;
import com.api.erp.v1.main.features.usuario.domain.service.INotificacaoService;
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
    public void notificarGestores(Usuario usuario) {
        webSocketService.notificarGestores(
                new NotificacaoDTO(
                        "Novo usuário pendente",
                        "Usuário " + usuario.getNome_completo() + " aguarda aprovação",
                        "USUARIO_PENDENTE",
                        LocalDateTime.now()
                )
        );
    }

    @Override
    public void notificarUsuarioAprovado(Usuario usuario) {
        webSocketService.notificarUsuario(
                usuario.getId().toString(),
                new NotificacaoDTO(
                        "Cadastro aprovado",
                        "Seu acesso ao sistema foi liberado",
                        "USUARIO_APROVADO",
                        LocalDateTime.now()
                )
        );
    }

    @Override
    public void notificarUsuarioRejeitado(Usuario usuario, String motivo) {
        webSocketService.notificarUsuario(
                usuario.getId().toString(),
                new NotificacaoDTO(
                        "Cadastro rejeitado",
                        "Motivo: " + motivo,
                        "USUARIO_REJEITADO",
                        LocalDateTime.now()
                )
        );
    }
}
