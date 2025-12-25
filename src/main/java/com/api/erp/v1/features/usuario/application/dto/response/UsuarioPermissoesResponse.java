package com.api.erp.v1.features.usuario.application.dto.response;

import com.api.erp.v1.features.permissao.application.dto.response.PermissaoResponse;
import com.api.erp.v1.features.permissao.application.dto.response.RoleResponse;
import com.api.erp.v1.features.usuario.domain.entity.StatusUsuario;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class UsuarioPermissoesResponse {
    private long id;
    private String nomeCompleto;
    private String email;
    private String cpf;
    private StatusUsuario status;
    private LocalDateTime dataCriacao;
    private Set<PermissaoResponse> permissoes;
    private Set<RoleResponse> roles;
}
