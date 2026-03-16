package com.api.erp.v1.main.dynamic.features.businesspartner.domain.service;

import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.request.CreateBusinessPartnerDto;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.entity.BusinessPartner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBusinessPartnerService {
    public Page<BusinessPartner> pegarTodos(Pageable pageable);
    public BusinessPartner criar(CreateBusinessPartnerDto businesspartnerDto);
    public BusinessPartner atualizar(Long id, CreateBusinessPartnerDto businesspartnerDto);

    public BusinessPartner pegarPorId(Long id);

    public void deletar(Long id);
}
