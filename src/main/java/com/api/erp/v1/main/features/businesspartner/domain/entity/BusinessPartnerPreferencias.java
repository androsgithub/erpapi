package com.api.erp.v1.main.features.businesspartner.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessPartnerPreferencias {

    @Column(name = "email_principal")
    private String emailPrincipal;

    @Column(name = "email_nfe")
    private String emailNfe;

    @Column(name = "enviar_email")
    private Boolean enviarEmail;

    @Column(name = "mala_direta")
    private Boolean malaDireta;
}

