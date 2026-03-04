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
public class BusinessPartnerDadosFinanceiros {

    @Column(name = "limite_credito")
    private Double limiteCredito;

    @Column(name = "limite_desconto")
    private Double limiteDesconto;

    @Column(name = "restricao_financeira")
    private Boolean restricaoFinanceira;

    @Column(name = "protestado")
    private Boolean protestado;
}

