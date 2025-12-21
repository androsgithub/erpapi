package com.api.erp.features.usuario.domain.service;

import com.api.erp.features.usuario.application.dto.request.CreateUsuarioRequest;
import com.api.erp.features.usuario.application.dto.request.UpdateUsuarioRequest;
import com.api.erp.features.usuario.domain.entity.Usuario;
import com.api.erp.features.usuario.domain.entity.StatusUsuario;
import java.util.List;
import java.util.UUID;

public interface UsuarioService {
    Usuario criar(CreateUsuarioRequest request);
    Usuario atualizar(UUID id, UpdateUsuarioRequest request);
    Usuario buscarPorId(UUID id);
    List<Usuario> listarTodos();
    List<Usuario> listarPendentes();
    void inativar(UUID id);

    public Usuario aprovar(UUID usuarioId, UUID gestorId);
    public Usuario rejeitar(UUID usuarioId, UUID gestorId, String motivo);
}
