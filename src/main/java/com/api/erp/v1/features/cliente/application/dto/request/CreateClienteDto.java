package com.api.erp.v1.features.cliente.application.dto.request;

import com.api.erp.v1.features.cliente.application.dto.ClienteDadosFinanceirosDto;
import com.api.erp.v1.features.cliente.application.dto.ClienteDadosFiscaisDto;
import com.api.erp.v1.features.cliente.application.dto.ClientePreferenciasDto;
import com.api.erp.v1.features.cliente.domain.entity.ClienteStatus;
import com.api.erp.v1.features.cliente.domain.entity.TipoCliente;
import com.api.erp.v1.shared.domain.valueobject.CustomData;

public record CreateClienteDto(
        String nome,
        ClienteDadosFiscaisDto dadosFiscais,
        ClienteStatus status,
        TipoCliente tipoCliente,
        ClienteDadosFinanceirosDto dadosFinanceiros,
        ClientePreferenciasDto preferencias,

        CustomData customData
) {
}
