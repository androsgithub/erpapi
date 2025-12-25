package com.api.erp.v1.features.usuario.domain.service;

import com.api.erp.v1.features.usuario.application.dto.request.CreateUsuarioRequest;
import com.api.erp.v1.features.usuario.application.dto.request.UpdateUsuarioRequest;
import com.api.erp.v1.features.usuario.domain.entity.Usuario;

import java.util.List;

public interface UsuarioService {
    Usuario criar(CreateUsuarioRequest request);
    Usuario atualizar(Long id, UpdateUsuarioRequest request);
    Usuario buscarPorId(Long id);
    List<Usuario> listarTodos();
    List<Usuario> listarPendentes();
    void inativar(Long id);

    public Usuario aprovar(Long usuarioId, Long gestorId);
    public Usuario rejeitar(Long usuarioId, Long gestorId, String motivo);
}
