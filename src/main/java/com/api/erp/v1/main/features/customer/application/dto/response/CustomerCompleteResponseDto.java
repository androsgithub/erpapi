package com.api.erp.v1.main.features.customer.application.dto.response;

import com.api.erp.v1.main.features.customer.application.dto.CustomerDadosFinanceirosDto;
import com.api.erp.v1.main.features.customer.application.dto.CustomerDadosFiscaisDto;
import com.api.erp.v1.main.features.customer.application.dto.CustomerPreferenciasDto;
import com.api.erp.v1.main.features.customer.domain.entity.CustomerStatus;
import com.api.erp.v1.main.features.customer.domain.entity.TipoCustomer;
import com.api.erp.v1.main.features.contact.application.dto.response.ContactResponse;
import com.api.erp.v1.main.features.address.application.dto.response.AddressResponse;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public record CustomerCompleteResponseDto(
        long id,
        String nome,

        CustomerStatus status,
        TipoCustomer tipo,

        CustomerDadosFiscaisDto dadosFiscais,
        CustomerDadosFinanceirosDto dadosFinanceiros,
        CustomerPreferenciasDto preferencias,

        Set<ContactResponse> contacts,
        Set<AddressResponse> addresss,

        Map<String, Object> customData,

        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
}

