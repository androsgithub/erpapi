package com.api.erp.v1.main.features.businesspartner.domain.extension;

import com.api.erp.v1.main.features.businesspartner.application.dto.request.CreateBusinessPartnerDto;
import com.api.erp.v1.main.features.businesspartner.domain.entity.BusinessPartner;

public interface IBusinessPartnerExtension {
    void antesDeCreate(BusinessPartner businesspartner, CreateBusinessPartnerDto dto);

    void depoisDeCreate(BusinessPartner businesspartner, CreateBusinessPartnerDto dto);

    void antesDeUpdate(BusinessPartner businesspartner, CreateBusinessPartnerDto dto);

    void depoisDeUpdate(BusinessPartner businesspartner, CreateBusinessPartnerDto dto);

    void antesDeDelete(BusinessPartner businesspartner);

    void depoisDeDelete(BusinessPartner businesspartner);

    String getTenantCode();

    boolean isEnabled();
}
