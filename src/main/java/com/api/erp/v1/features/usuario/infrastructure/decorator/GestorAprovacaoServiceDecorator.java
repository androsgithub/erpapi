package com.api.erp.v1.features.usuario.infrastructure.decorator;

import com.api.erp.v1.features.permissao.domain.entity.Permissao;
import com.api.erp.v1.features.permissao.domain.entity.Role;
import com.api.erp.v1.features.usuario.application.dto.request.AdicionarPermissoesRequest;
import com.api.erp.v1.features.usuario.application.dto.request.AdicionarRolesRequest;
import com.api.erp.v1.features.usuario.application.dto.request.CreateUsuarioRequest;
import com.api.erp.v1.features.usuario.application.dto.request.UpdateUsuarioRequest;
import com.api.erp.v1.features.usuario.domain.entity.StatusUsuario;
import com.api.erp.v1.features.usuario.domain.entity.Usuario;
import com.api.erp.v1.features.usuario.domain.service.INotificacaoService;
import com.api.erp.v1.features.usuario.domain.service.IUsuarioService;
import com.api.erp.v1.shared.domain.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class GestorAprovacaoServiceDecorator implements IUsuarioService {
    private final IUsuarioService wrapped;

    @Autowired
    @Qualifier("notificacaoService")
    private INotificacaoService notificacaoService;

    public GestorAprovacaoServiceDecorator(IUsuarioService wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public Usuario criar(CreateUsuarioRequest request) {
        // Cria o usuário usando serviço base
        Usuario usuario = wrapped.criar(request);

        // ADICIONA comportamento: muda status para pendente


        // Notifica gestores para aprovação
        notificacaoService.notificarGestores(usuario);

        return usuario;
    }

    @Override
    public Usuario aprovar(Long usuarioId, Long gestorId) {
        Usuario usuario = wrapped.buscarPorId(usuarioId);

        if (!usuario.isPendente()) {
            throw new BusinessException("Usuário não está pendente de aprovação");
        }

        usuario.aprovar(gestorId);
        notificacaoService.notificarUsuarioAprovado(usuario);

        return usuario;
    }

    @Override
    public Usuario rejeitar(Long usuarioId, Long gestorId, String motivo) {
        Usuario usuario = wrapped.buscarPorId(usuarioId);

        if (!usuario.isPendente()) {
            throw new BusinessException("Usuário não está pendente de aprovação");
        }

        usuario.rejeitar();
        notificacaoService.notificarUsuarioRejeitado(usuario, motivo);

        return usuario;
    }

    @Override
    public void adicionarPermissoes(Long usuarioId, AdicionarPermissoesRequest request) {
        wrapped.adicionarPermissoes(usuarioId, request);
    }

    @Override
    public void removerPermissao(Long usuarioId, Long permissaoId) {
        wrapped.removerPermissao(usuarioId, permissaoId);
    }

    @Override
    public List<Permissao> listarPermissoes(Long usuarioId) {
        return wrapped.listarPermissoes(usuarioId);
    }

    @Override
    public void adicionarRoles(Long usuarioId, AdicionarRolesRequest request) {
        wrapped.adicionarRoles(usuarioId, request);
    }

    @Override
    public void removerRole(Long usuarioId, Long roleId) {
        wrapped.removerRole(usuarioId, roleId);
    }

    @Override
    public List<Role> listarRoles(Long usuarioId) {
        return wrapped.listarRoles(usuarioId);
    }

    // Delega métodos para o serviço wrapped
    @Override
    public Usuario atualizar(Long id, UpdateUsuarioRequest request) {
        return wrapped.atualizar(id, request);
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return wrapped.buscarPorId(id);
    }

    @Override
    public List<Usuario> listarTodos() {
        return wrapped.listarTodos();
    }

    @Override
    public List<Usuario> listarPendentes() {
        return wrapped.listarPendentes();
    }

    @Override
    public void inativar(Long id) {
        wrapped.inativar(id);
    }
}
