package com.api.erp.v1.main.features.businesspartner.infrastructure.decorator;

import com.api.erp.v1.main.features.businesspartner.application.dto.request.CreateBusinessPartnerDto;
import com.api.erp.v1.main.features.businesspartner.domain.entity.BusinessPartner;
import com.api.erp.v1.main.features.businesspartner.domain.service.IBusinessPartnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Slf4j
@RequiredArgsConstructor
public class NotificationDecoratorBusinessPartnerService implements IBusinessPartnerService {

    private final IBusinessPartnerService service;

    @Override
    public Page<BusinessPartner> pegarTodos(Pageable pageable) {
        return service.pegarTodos(pageable);
    }

    @Override
    public BusinessPartner criar(CreateBusinessPartnerDto businesspartnerDto) {
        BusinessPartner businesspartner = service.criar(businesspartnerDto);
        notificarBusinessPartnerCriado(businesspartner);
        return businesspartner;
    }

    @Override
    public BusinessPartner atualizar(Long id, CreateBusinessPartnerDto businesspartnerDto) {
        BusinessPartner businesspartner = service.atualizar(id, businesspartnerDto);
        notificarBusinessPartnerAtualizado(businesspartner);
        return businesspartner;
    }

    @Override
    public BusinessPartner pegarPorId(Long id) {
        return service.pegarPorId(id);
    }

    @Override
    public void deletar(Long id) {
        BusinessPartner businesspartner = service.pegarPorId(id);
        service.deletar(id);
        notificarBusinessPartnerDeletado(businesspartner);
    }

    private void notificarBusinessPartnerCriado(BusinessPartner businesspartner) {
        log.info("[NOTIFICATION BUSINESSPARTNER] Evento: BusinessPartner criado - id={}, nome={}, cnpj={}",
                businesspartner.getId(),
                businesspartner.getNome(),
                businesspartner.getDadosFiscais().getCnpj());
    }

    private void notificarBusinessPartnerAtualizado(BusinessPartner businesspartner) {
        log.info("[NOTIFICATION BUSINESSPARTNER] Evento: BusinessPartner atualizado - id={}, nome={}",
                businesspartner.getId(), businesspartner.getNome());
    }

    private void notificarBusinessPartnerDeletado(BusinessPartner businesspartner) {
        log.warn("[NOTIFICATION BUSINESSPARTNER] Evento: BusinessPartner deletado - id={}, nome={}, cnpj={}",
                businesspartner.getId(),
                businesspartner.getNome(),
                businesspartner.getDadosFiscais().getCnpj());
    }
}
