package com.api.erp.v1.features.cliente.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDadosFinanceiros {

    @Column(name = "limite_credito")
    private Double limiteCredito;

    @Column(name = "limite_desconto")
    private Double limiteDesconto;

    @Column(name = "restricao_financeira")
    private Boolean restricaoFinanceira;

    @Column(name = "protestado")
    private Boolean protestado;
}

