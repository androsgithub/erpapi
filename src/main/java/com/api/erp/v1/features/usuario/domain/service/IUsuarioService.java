package com.api.erp.v1.features.usuario.domain.service;

import com.api.erp.v1.features.permissao.domain.entity.Permissao;
import com.api.erp.v1.features.permissao.domain.entity.Role;
import com.api.erp.v1.features.usuario.application.dto.request.AdicionarPermissoesRequest;
import com.api.erp.v1.features.usuario.application.dto.request.AdicionarRolesRequest;
import com.api.erp.v1.features.usuario.application.dto.request.CreateUsuarioRequest;
import com.api.erp.v1.features.usuario.application.dto.request.UpdateUsuarioRequest;
import com.api.erp.v1.features.usuario.domain.entity.Usuario;
import com.dros.observability.application.annotation.TrackFlow;

import java.util.List;

public interface IUsuarioService {
    Usuario criar(CreateUsuarioRequest request);
    Usuario atualizar(Long id, UpdateUsuarioRequest request);
    Usuario buscarPorId(Long id);
    List<Usuario> listarTodos();
    List<Usuario> listarPendentes();
    void inativar(Long id);
    public Usuario aprovar(Long usuarioId, Long gestorId);
    public Usuario rejeitar(Long usuarioId, Long gestorId, String motivo);

    void adicionarPermissoes(Long usuarioId, AdicionarPermissoesRequest request);
    void removerPermissao(Long usuarioId, Long permissaoId);
    List<Permissao> listarPermissoes(Long usuarioId);
    void adicionarRoles(Long usuarioId, AdicionarRolesRequest request);
    void removerRole(Long usuarioId, Long roleId);
    List<Role> listarRoles(Long usuarioId);
}
