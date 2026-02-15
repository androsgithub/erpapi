package com.api.erp.v1.main.features.usuario.domain.service;

import com.api.erp.v1.main.features.usuario.domain.entity.Usuario;

public interface INotificacaoService {
    void notificarGestores(Usuario usuario);
    void notificarUsuarioAprovado(Usuario usuario);
    void notificarUsuarioRejeitado(Usuario usuario, String motivo);
}
