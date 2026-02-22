package com.api.erp.v1.main.features.user.infrastructure.decorator;

import com.api.erp.v1.main.features.permission.domain.entity.Permission;
import com.api.erp.v1.main.features.permission.domain.entity.Role;
import com.api.erp.v1.main.features.user.application.dto.request.AdicionarPermissionsRequest;
import com.api.erp.v1.main.features.user.application.dto.request.AdicionarRolesRequest;
import com.api.erp.v1.main.features.user.application.dto.request.CreateUserRequest;
import com.api.erp.v1.main.features.user.application.dto.request.UpdateUserRequest;
import com.api.erp.v1.main.features.user.domain.entity.User;
import com.api.erp.v1.main.features.user.domain.service.INotificacaoService;
import com.api.erp.v1.main.features.user.domain.service.IUserService;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class GestorAprovacaoServiceDecorator implements IUserService {
    private final IUserService wrapped;

    @Autowired
    @Qualifier("notificacaoService")
    private INotificacaoService notificacaoService;

    public GestorAprovacaoServiceDecorator(IUserService wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public User criar(CreateUserRequest request) {
        // Cria o usuário usando serviço base
        User user = wrapped.criar(request);

        // ADICIONA comportamento: muda status para pendente


        // Notifica gestores para aprovação
        notificacaoService.notificarGestores(user);

        return user;
    }

    @Override
    public User aprovar(Long userId, Long gestorId) {
        User user = wrapped.buscarPorId(userId);

        if (!user.isPendente()) {
            throw new BusinessException("User is not pending approval");
        }

        user.aprovar(gestorId);
        notificacaoService.notificarUserAprovado(user);

        return user;
    }

    @Override
    public User rejeitar(Long userId, Long gestorId, String motivo) {
        User user = wrapped.buscarPorId(userId);

        if (!user.isPendente()) {
            throw new BusinessException("User is not pending approval");
        }

        user.rejeitar();
        notificacaoService.notificarUserRejeitado(user, motivo);

        return user;
    }

    @Override
    public void adicionarPermissions(Long userId, AdicionarPermissionsRequest request) {
        wrapped.adicionarPermissions(userId, request);
    }

    @Override
    public void removerPermission(Long userId, Long permissionId) {
        wrapped.removerPermission(userId, permissionId);
    }

    @Override
    public List<Permission> listarPermissions(Long userId) {
        return wrapped.listarPermissions(userId);
    }

    @Override
    public void adicionarRoles(Long userId, AdicionarRolesRequest request) {
        wrapped.adicionarRoles(userId, request);
    }

    @Override
    public void removerRole(Long userId, Long roleId) {
        wrapped.removerRole(userId, roleId);
    }

    @Override
    public List<Role> listarRoles(Long userId) {
        return wrapped.listarRoles(userId);
    }

    // Delega métodos para o serviço wrapped
    @Override
    public User atualizar(Long id, UpdateUserRequest request) {
        return wrapped.atualizar(id, request);
    }

    @Override
    public User buscarPorId(Long id) {
        return wrapped.buscarPorId(id);
    }

    @Override
    public List<User> listarTodos() {
        return wrapped.listarTodos();
    }

    @Override
    public List<User> listarPendentes() {
        return wrapped.listarPendentes();
    }

    @Override
    public void inativar(Long id) {
        wrapped.inativar(id);
    }
}
