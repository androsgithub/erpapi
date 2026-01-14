package com.api.erp.v1.features.cliente.application.dto.response;

import com.api.erp.v1.features.cliente.application.dto.ClienteDadosFinanceirosDto;
import com.api.erp.v1.features.cliente.application.dto.ClienteDadosFiscaisDto;
import com.api.erp.v1.features.cliente.application.dto.ClientePreferenciasDto;
import com.api.erp.v1.features.cliente.domain.entity.ClienteStatus;
import com.api.erp.v1.features.cliente.domain.entity.TipoCliente;
import com.api.erp.v1.features.contato.application.dto.response.ContatoResponse;
import com.api.erp.v1.features.endereco.application.dto.response.EnderecoResponse;
import com.api.erp.v1.shared.domain.valueobject.CustomData;

import java.time.LocalDateTime;
import java.util.Set;

public record ClienteCompleteResponseDto(
        long id,
        String nome,

        ClienteStatus status,
        TipoCliente tipo,

        ClienteDadosFiscaisDto dadosFiscais,
        ClienteDadosFinanceirosDto dadosFinanceiros,
        ClientePreferenciasDto preferencias,

        Set<ContatoResponse> contatos,
        Set<EnderecoResponse> enderecos,

        CustomData customData,

        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
}

