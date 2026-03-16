package com.api.erp.v1.main.dynamic.features.businesspartner.application.mapper;

import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.response.BusinessPartnerSimpleResponseDto;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.entity.BusinessPartner;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class IBusinessPartnerSimpleMapper {

    public BusinessPartnerSimpleResponseDto toResponse(BusinessPartner businesspartner) {
        if (businesspartner == null) {
            return null;
        }
        return new BusinessPartnerSimpleResponseDto(
                businesspartner.getId(),
                businesspartner.getNome(),
                businesspartner.getCreatedAt().toLocalDateTime(),
                businesspartner.getUpdatedAt().toLocalDateTime()
        );
    }

    public List<BusinessPartnerSimpleResponseDto> toResponseList(List<BusinessPartner> businesspartners) {
        if (businesspartners == null) {
            return List.of();
        }
        return businesspartners.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Page<BusinessPartnerSimpleResponseDto> toResponsePage(Page<BusinessPartner> page) {
        return page.map(this::toResponse);
    }
}
