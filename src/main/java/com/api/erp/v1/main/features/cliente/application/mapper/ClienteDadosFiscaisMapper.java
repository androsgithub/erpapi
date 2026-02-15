package com.api.erp.v1.main.features.cliente.application.mapper;

import com.api.erp.v1.main.features.cliente.application.dto.ClienteDadosFiscaisDto;
import com.api.erp.v1.main.features.cliente.domain.entity.ClienteDadosFiscais;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClienteDadosFiscaisMapper {

    default ClienteDadosFiscaisDto toDto(ClienteDadosFiscais dados) {
        if (dados == null) return null;

        return new ClienteDadosFiscaisDto(
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

