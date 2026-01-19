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
    @TrackFlow("SRVC_CREATE_USER")
    Usuario criar(CreateUsuarioRequest request);
    @TrackFlow("SRVC_UPDATE_USER")
    Usuario atualizar(Long id, UpdateUsuarioRequest request);
    @TrackFlow("SRVC_SEARCHBYID_USER")
    Usuario buscarPorId(Long id);
    @TrackFlow("SRVC_LIST_USER")
    List<Usuario> listarTodos();
    @TrackFlow("SRVC_LIST_PENDING_USER")
    List<Usuario> listarPendentes();
    @TrackFlow("SRVC_DISABLE_USER")
    void inativar(Long id);

    @TrackFlow("SRVC_APROVE_USER")
    public Usuario aprovar(Long usuarioId, Long gestorId);
    @TrackFlow("SRVC_REJECT_USER")
    public Usuario rejeitar(Long usuarioId, Long gestorId, String motivo);

    @TrackFlow("SRVC_ADD_USER_PERMS")
    void adicionarPermissoes(Long usuarioId, AdicionarPermissoesRequest request);
    @TrackFlow("SRVC_REMOVE_USER_PERMS")
    void removerPermissao(Long usuarioId, Long permissaoId);
    @TrackFlow("SRVC_LIST_USER_PERMS")
    List<Permissao> listarPermissoes(Long usuarioId);
    @TrackFlow("SRVC_ADD_USER_ROLES")
    void adicionarRoles(Long usuarioId, AdicionarRolesRequest request);
    @TrackFlow("SRVC_REMOVE_USER_ROLE")
    void removerRole(Long usuarioId, Long roleId);
    @TrackFlow("SRVC_LIST_USER_ROLES")
    List<Role> listarRoles(Long usuarioId);
}
