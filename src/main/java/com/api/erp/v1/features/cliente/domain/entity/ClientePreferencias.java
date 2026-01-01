package com.api.erp.v1.features.cliente.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientePreferencias {

    private String emailPrincipal;
    private String emailNfe;

    private Boolean enviarEmail;
    private Boolean malaDireta;

}

