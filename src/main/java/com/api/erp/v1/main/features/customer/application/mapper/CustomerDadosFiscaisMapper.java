package com.api.erp.v1.main.features.customer.application.mapper;

import com.api.erp.v1.main.features.customer.application.dto.CustomerDadosFiscaisDto;
import com.api.erp.v1.main.features.customer.domain.entity.CustomerDadosFiscais;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerDadosFiscaisMapper {

    default CustomerDadosFiscaisDto toDto(CustomerDadosFiscais dados) {
        if (dados == null) return null;

        return new CustomerDadosFiscaisDto(
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

