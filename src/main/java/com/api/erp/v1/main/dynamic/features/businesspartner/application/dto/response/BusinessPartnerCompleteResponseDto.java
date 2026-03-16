package com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.response;

import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.BusinessPartnerDadosFinanceirosDto;
import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.BusinessPartnerDadosFiscaisDto;
import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.BusinessPartnerPreferenciasDto;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.entity.BusinessPartnerStatus;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.entity.TipoBusinessPartner;
import com.api.erp.v1.main.dynamic.features.contact.application.dto.response.ContactResponse;
import com.api.erp.v1.main.dynamic.features.address.application.dto.response.AddressResponse;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public record BusinessPartnerCompleteResponseDto(
        long id,
        String nome,

        BusinessPartnerStatus status,
        TipoBusinessPartner tipo,

        BusinessPartnerDadosFiscaisDto dadosFiscais,
        BusinessPartnerDadosFinanceirosDto dadosFinanceiros,
        BusinessPartnerPreferenciasDto preferencias,

        Set<ContactResponse> contacts,
        Set<AddressResponse> addresss,

        Map<String, Object> customData,

        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
}

