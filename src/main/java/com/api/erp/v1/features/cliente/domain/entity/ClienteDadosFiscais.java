package com.api.erp.v1.features.cliente.domain.entity;

import com.api.erp.v1.shared.domain.enums.RegimeTributario;
import com.api.erp.v1.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.shared.domain.valueobject.CPF;
import com.api.erp.v1.shared.domain.valueobject.RG;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDadosFiscais {
    private String razaoSocial;
    private String nomeFantasia;

    @Column(name = "cnpj", length = 14)
    private CNPJ cnpj;

    @Column(name = "cpf", length = 11)
    private CPF cpf;

    @Column(name = "rg", length = 9)
    private RG rg;

    private String inscricaoEstadual;
    private String inscricaoMunicipal;

    @Enumerated(EnumType.STRING)
    private RegimeTributario regimeTributario;

    private Boolean icmsContribuinte;
    private Double aliquotaIcms;
    private String cnaePrincipal;
    private Boolean consumidorFinal;
}


