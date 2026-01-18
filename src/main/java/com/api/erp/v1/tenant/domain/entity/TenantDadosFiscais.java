package com.api.erp.v1.tenant.domain.entity;

import com.api.erp.v1.shared.domain.enums.ContribuinteICMS;
import com.api.erp.v1.shared.domain.enums.RegimeTributario;
import com.api.erp.v1.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.shared.infrastructure.persistence.converters.CNPJConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class TenantDadosFiscais {

    /**
     * Documento principal da empresa
     */
    @Convert(converter = CNPJConverter.class)
    @Column(name = "dados_fiscais_cnpj", nullable = false, length = 14)
    private CNPJ cnpj;

    /**
     * Razão Social (obrigatória para NF-e e NFS-e)
     */
    @Column(name = "dados_fiscais_razao_social", nullable = false, length = 150)
    private String razaoSocial;

    /**
     * Nome Fantasia (opcional, mas muito usado)
     */
    @Column(name = "dados_fiscais_nome_fantasia", length = 150)
    private String nomeFantasia;

    /**
     * Inscrição Estadual
     * Pode ser:
     * - null (ISENTO)
     * - número válido
     */
    @Column(name = "dados_fiscais_inscricao_estadual", length = 20)
    private String inscricaoEstadual;

    /**
     * Inscrição Municipal
     * Necessária para NFS-e
     */
    @Column(name = "inscricao_municipal", length = 20)
    private String inscricaoMunicipal;

    /**
     * Regime Tributário
     * Obrigatório para emissão de NF-e
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "dados_fiscais_regime_tributario", nullable = false, length = 30)
    private RegimeTributario regimeTributario;

    /**
     * Indica se a empresa é contribuinte de ICMS
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "contribuinte_icms", nullable = false, length = 20)
    private ContribuinteICMS contribuinteICMS;

    /**
     * CNAE principal (usado em NFS-e e validações fiscais)
     */
    @Column(name = "cnae_principal", length = 7)
    private String cnaePrincipal;

    /**
     * Código do município IBGE (obrigatório para NF-e/NFS-e)
     */
    @Column(name = "codigo_municipio_ibge", length = 7)
    private String codigoMunicipioIbge;
}
