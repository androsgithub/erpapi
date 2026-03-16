package com.api.erp.v1.main.dynamic.features.businesspartner.application.mapper;

import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.response.BusinessPartnerCompleteResponseDto;
import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.BusinessPartnerDadosFinanceirosDto;
import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.BusinessPartnerPreferenciasDto;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.entity.BusinessPartner;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.entity.BusinessPartnerDadosFinanceiros;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.entity.BusinessPartnerPreferencias;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class IBusinessPartnerCompleteMapper {

    private final BusinessPartnerDadosFiscaisMapper dadosFiscaisMapper;

    public IBusinessPartnerCompleteMapper(BusinessPartnerDadosFiscaisMapper dadosFiscaisMapper) {
        this.dadosFiscaisMapper = dadosFiscaisMapper;
    }

    public BusinessPartnerCompleteResponseDto toResponse(BusinessPartner businesspartner) {
        if (businesspartner == null) {
            return null;
        }

        return new BusinessPartnerCompleteResponseDto(
                businesspartner.getId(),
                businesspartner.getNome(),
                businesspartner.getStatus(),
                businesspartner.getTipo(),
                dadosFiscaisMapper.toDto(businesspartner.getDadosFiscais()),
                mapearDadosFinanceiros(businesspartner.getDadosFinanceiros()),
                mapearPreferencias(businesspartner.getPreferencias()),
                null, // contacts
                null, // addresss
                null, // customData - not in BusinessPartner
                businesspartner.getCreatedAt().toLocalDateTime(),
                businesspartner.getUpdatedAt().toLocalDateTime()
        );
    }

    private BusinessPartnerDadosFinanceirosDto mapearDadosFinanceiros(BusinessPartnerDadosFinanceiros dados) {
        if (dados == null) {
            return null;
        }
        return new BusinessPartnerDadosFinanceirosDto(
                dados.getLimiteCredito(),
                dados.getLimiteDesconto(),
                dados.getRestricaoFinanceira(),
                dados.getProtestado()
        );
    }

    private BusinessPartnerPreferenciasDto mapearPreferencias(BusinessPartnerPreferencias preferencias) {
        if (preferencias == null) {
            return null;
        }
        return new BusinessPartnerPreferenciasDto(
                preferencias.getEmailPrincipal(),
                preferencias.getEmailNfe(),
                preferencias.getEnviarEmail(),
                preferencias.getMalaDireta()
        );
    }

    public List<BusinessPartnerCompleteResponseDto> toResponseList(List<BusinessPartner> businesspartners) {
        if (businesspartners == null) {
            return List.of();
        }
        return businesspartners.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
