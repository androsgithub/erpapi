package com.api.erp.v1.main.features.businesspartner.application.dto.request;

import com.api.erp.v1.main.features.businesspartner.application.dto.BusinessPartnerDadosFinanceirosDto;
import com.api.erp.v1.main.features.businesspartner.application.dto.BusinessPartnerDadosFiscaisDto;
import com.api.erp.v1.main.features.businesspartner.application.dto.BusinessPartnerPreferenciasDto;
import com.api.erp.v1.main.features.businesspartner.domain.entity.BusinessPartnerStatus;
import com.api.erp.v1.main.features.businesspartner.domain.entity.TipoBusinessPartner;

import java.util.Map;

public record CreateBusinessPartnerDto(
        String nome,
        BusinessPartnerDadosFiscaisDto dadosFiscais,
        BusinessPartnerStatus status,
        TipoBusinessPartner tipoBusinessPartner,
        BusinessPartnerDadosFinanceirosDto dadosFinanceiros,
        BusinessPartnerPreferenciasDto preferencias,
        Map<String, Object> customData
) {
}
