package com.api.erp.v1.features.usuario.infrastructure.decorator;

import com.api.erp.v1.features.usuario.application.dto.request.CreateUsuarioRequest;
import com.api.erp.v1.features.usuario.application.dto.request.UpdateUsuarioRequest;
import com.api.erp.v1.features.usuario.domain.entity.Usuario;
import com.api.erp.v1.features.usuario.domain.entity.StatusUsuario;
import com.api.erp.v1.features.usuario.domain.service.UsuarioService;
import com.api.erp.v1.shared.domain.exception.BusinessException;
import java.util.List;

public class GestorAprovacaoServiceDecorator implements UsuarioService {
    private final UsuarioService wrapped;
    private final NotificacaoService notificacaoService;
    
    public GestorAprovacaoServiceDecorator(UsuarioService wrapped, NotificacaoService notificacaoService) {
        this.wrapped = wrapped;
        this.notificacaoService = notificacaoService;
    }
    
    @Override
    public Usuario criar(CreateUsuarioRequest request) {
        // Cria o usuário usando serviço base
        Usuario usuario = wrapped.criar(request);
        
        // ADICIONA comportamento: muda status para pendente
        usuario.setStatus(StatusUsuario.PENDENTE_APROVACAO);
        
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
