package com.api.erp.features.usuario.infrastructure.decorator;

import com.api.erp.features.usuario.application.dto.request.CreateUsuarioRequest;
import com.api.erp.features.usuario.application.dto.request.UpdateUsuarioRequest;
import com.api.erp.features.usuario.domain.entity.Usuario;
import com.api.erp.features.usuario.domain.entity.StatusUsuario;
import com.api.erp.features.usuario.domain.service.UsuarioService;
import com.api.erp.shared.domain.exception.BusinessException;
import java.util.List;
import java.util.UUID;

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
    public Usuario aprovar(UUID usuarioId, UUID gestorId) {
        Usuario usuario = wrapped.buscarPorId(usuarioId);
        
        if (!usuario.isPendente()) {
            throw new BusinessException("Usuário não está pendente de aprovação");
        }
        
        usuario.aprovar(gestorId);
        notificacaoService.notificarUsuarioAprovado(usuario);
        
        return usuario;
    }

    @Override
    public Usuario rejeitar(UUID usuarioId, UUID gestorId, String motivo) {
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
    public Usuario atualizar(UUID id, UpdateUsuarioRequest request) {
        return wrapped.atualizar(id, request);
    }
    
    @Override
    public Usuario buscarPorId(UUID id) {
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
    public void inativar(UUID id) {
        wrapped.inativar(id);
    }
}
