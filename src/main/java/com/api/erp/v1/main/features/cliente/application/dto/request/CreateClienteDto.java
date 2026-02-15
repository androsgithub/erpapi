package com.api.erp.v1.main.features.cliente.application.dto.request;

import com.api.erp.v1.main.features.cliente.application.dto.ClienteDadosFinanceirosDto;
import com.api.erp.v1.main.features.cliente.application.dto.ClienteDadosFiscaisDto;
import com.api.erp.v1.main.features.cliente.application.dto.ClientePreferenciasDto;
import com.api.erp.v1.main.features.cliente.domain.entity.ClienteStatus;
import com.api.erp.v1.main.features.cliente.domain.entity.TipoCliente;

import java.util.Map;

public record CreateClienteDto(
        String nome,
        ClienteDadosFiscaisDto dadosFiscais,
        ClienteStatus status,
        TipoCliente tipoCliente,
        ClienteDadosFinanceirosDto dadosFinanceiros,
        ClientePreferenciasDto preferencias,
        Map<String, Object> customData
) {
}
