package com.api.erp.v1.main.features.businesspartner.application.mapper;

import com.api.erp.v1.main.features.businesspartner.application.dto.response.BusinessPartnerSimpleResponseDto;
import com.api.erp.v1.main.features.businesspartner.domain.entity.BusinessPartner;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IBusinessPartnerSimpleMapper {

    BusinessPartnerSimpleResponseDto toResponse(BusinessPartner businesspartner);

    List<BusinessPartnerSimpleResponseDto> toResponseList(List<BusinessPartner> businesspartners);

    default Page<BusinessPartnerSimpleResponseDto> toResponsePage(Page<BusinessPartner> page) {
        return page.map(this::toResponse);
    }
}
