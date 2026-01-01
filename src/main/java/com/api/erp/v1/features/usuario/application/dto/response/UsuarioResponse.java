package com.api.erp.v1.features.usuario.application.dto.response;

import com.api.erp.v1.features.contato.application.dto.response.ContatoResponse;
import com.api.erp.v1.features.usuario.domain.entity.StatusUsuario;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class UsuarioResponse {
    private long id;
    private String nomeCompleto;
    private String email;
    private String cpf;
    private StatusUsuario status;
    private LocalDateTime dataCriacao;
    private Set<ContatoResponse> contatos;
}
