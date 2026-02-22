package com.api.erp.v1.main.features.user.domain.service;

import com.api.erp.v1.main.features.user.domain.entity.User;

public interface INotificacaoService {
    void notificarGestores(User user);
    void notificarUserAprovado(User user);
    void notificarUserRejeitado(User user, String motivo);
}
