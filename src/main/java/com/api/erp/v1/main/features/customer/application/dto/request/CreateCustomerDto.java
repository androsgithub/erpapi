package com.api.erp.v1.main.features.customer.application.dto.request;

import com.api.erp.v1.main.features.customer.application.dto.CustomerDadosFinanceirosDto;
import com.api.erp.v1.main.features.customer.application.dto.CustomerDadosFiscaisDto;
import com.api.erp.v1.main.features.customer.application.dto.CustomerPreferenciasDto;
import com.api.erp.v1.main.features.customer.domain.entity.CustomerStatus;
import com.api.erp.v1.main.features.customer.domain.entity.TipoCustomer;

import java.util.Map;

public record CreateCustomerDto(
        String nome,
        CustomerDadosFiscaisDto dadosFiscais,
        CustomerStatus status,
        TipoCustomer tipoCustomer,
        CustomerDadosFinanceirosDto dadosFinanceiros,
        CustomerPreferenciasDto preferencias,
        Map<String, Object> customData
) {
}
