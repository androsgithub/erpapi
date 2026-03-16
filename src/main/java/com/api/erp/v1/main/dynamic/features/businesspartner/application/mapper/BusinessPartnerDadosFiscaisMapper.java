package com.api.erp.v1.main.dynamic.features.businesspartner.application.mapper;

import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.BusinessPartnerDadosFiscaisDto;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.entity.BusinessPartnerDadosFiscais;
import org.springframework.stereotype.Component;

@Component
public class BusinessPartnerDadosFiscaisMapper {

    public BusinessPartnerDadosFiscaisDto toDto(BusinessPartnerDadosFiscais dados) {
        if (dados == null) return null;

        return new BusinessPartnerDadosFiscaisDto(
                dados.getRazaoSocial(),
                dados.getNomeFantasia(),
                dados.getCnpj() != null ? dados.getCnpj().getValor() : null,
                dados.getCpf() != null ? dados.getCpf().getValor() : null,
                dados.getRg() != null ? dados.getRg().getValor() : null,
                dados.getInscricaoEstadual() != null ? dados.getInscricaoEstadual() : null,
                dados.getInscricaoMunicipal() != null ? dados.getInscricaoMunicipal() : null,
                dados.getRegimeTributario(),
                dados.getIcmsContribuinte(),
                dados.getAliquotaIcms(),
                dados.getCnaePrincipal() != null ? dados.getCnaePrincipal() : null,
                dados.getConsumidorFinal()
        );
    }
}

