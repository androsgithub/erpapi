package com.api.erp.v1.features.usuario.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
public class UsuarioPermissaoResponse {
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Set<Long> permissoesDiretas;
    private Set<Long> roles;
}


