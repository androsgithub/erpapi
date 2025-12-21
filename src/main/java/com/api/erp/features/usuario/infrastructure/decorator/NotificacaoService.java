package com.api.erp.features.usuario.infrastructure.decorator;

import com.api.erp.features.usuario.domain.entity.Usuario;

public interface NotificacaoService {
    void notificarGestores(Usuario usuario);
    void notificarUsuarioAprovado(Usuario usuario);
    void notificarUsuarioRejeitado(Usuario usuario, String motivo);
}
