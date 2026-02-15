package com.api.erp.v1.main.features.cliente.domain.entity;

import com.api.erp.v1.main.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.main.shared.domain.valueobject.CPF;
import com.api.erp.v1.main.shared.domain.valueobject.RG;
import com.dros.taxengine.domain.RegimeTributario;
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
    @Column(name = "razao_social")
    private String razaoSocial;

    @Column(name = "nome_fantasia")
    private String nomeFantasia;

    @Column(name = "cnpj", length = 14)
    private CNPJ cnpj;

    @Column(name = "cpf", length = 11)
    private CPF cpf;

    @Column(name = "rg", length = 9)
    private RG rg;

    @Column(name = "inscricao_estadual")
    private String inscricaoEstadual;

    @Column(name = "inscricao_municipal")
    private String inscricaoMunicipal;

    @Enumerated(EnumType.STRING)
    @Column(name = "regime_tributario")
    private RegimeTributario regimeTributario;

    @Column(name = "icms_contribuinte")
    private Boolean icmsContribuinte;

    @Column(name = "aliquota_icms")
    private Double aliquotaIcms;

    @Column(name = "cnae_principal")
    private String cnaePrincipal;

    @Column(name = "consumidor_final")
    private Boolean consumidorFinal;
}


