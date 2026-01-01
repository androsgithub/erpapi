package com.api.erp.v1.features.cliente.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDadosFinanceiros {

    private Double limiteCredito;
    private Double limiteDesconto;

    private Boolean restricaoFinanceira;
    private Boolean protestado;
}

