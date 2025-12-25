package com.api.erp.v1.features.usuario.infrastructure.decorator;

import com.api.erp.v1.features.usuario.domain.entity.Usuario;
import com.api.erp.v1.shared.websocket.WebSocketNotificacaoService;
import com.api.erp.v1.shared.websocket.dto.NotificacaoDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificacaoServiceImpl implements NotificacaoService {

    private final WebSocketNotificacaoService webSocketService;

    public NotificacaoServiceImpl(WebSocketNotificacaoService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @Override
    public void notificarGestores(Usuario usuario) {
        webSocketService.notificarGestores(
                new NotificacaoDTO(
                        "Novo usuário pendente",
                        "Usuário " + usuario.getNomeCompleto() + " aguarda aprovação",
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
