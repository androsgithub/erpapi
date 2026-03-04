package com.api.erp.v1.main.features.businesspartner.infrastructure.decorator;

import com.api.erp.v1.main.features.businesspartner.application.dto.request.CreateBusinessPartnerDto;
import com.api.erp.v1.main.features.businesspartner.domain.entity.BusinessPartner;
import com.api.erp.v1.main.features.businesspartner.domain.service.IBusinessPartnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Decorator para adicionar auditoria ao serviço de BusinessPartner
 * 
 * Logs todas as operações de businesspartner para fins de rastreabilidade,
 * compliance e investigação de problemas.
 * 
 * Exemplo de composição:
 * BusinessPartnerService → AuditDecorator → ValidationDecorator
 * 
 * Thread-Safe: Sim, apenas realiza logging sem alterar estado
 * Performance: Mínimo impacto, apenas registra informações
 */
@Slf4j
@RequiredArgsConstructor
public class AuditDecoratorBusinessPartnerService implements IBusinessPartnerService {

    private final IBusinessPartnerService service;

    @Override
    public Page<BusinessPartner> pegarTodos(Pageable pageable) {
        log.debug("[AUDIT BUSINESSPARTNER] Buscando todos os businesspartners: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<BusinessPartner> result = service.pegarTodos(pageable);
            log.debug("[AUDIT BUSINESSPARTNER] Total de businesspartners encontrados: {}", result.getTotalElements());
            return result;
        } catch (Exception e) {
            log.error("[AUDIT BUSINESSPARTNER] Erro ao buscar businesspartners: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public BusinessPartner criar(CreateBusinessPartnerDto businesspartnerDto) {
        log.info("[AUDIT BUSINESSPARTNER] Criando novo businesspartner: nome={}, cnpj={}",
                businesspartnerDto.nome(), businesspartnerDto.dadosFiscais().cnpj());
        
        try {
            BusinessPartner businesspartner = service.criar(businesspartnerDto);
            log.info("[AUDIT BUSINESSPARTNER] BusinessPartner criado com sucesso: id={}, nome={}, cnpj={}",
                    businesspartner.getId(), businesspartner.getNome(), businesspartner.getDadosFiscais().getCnpj());
            return businesspartner;
        } catch (Exception e) {
            log.error("[AUDIT BUSINESSPARTNER] Erro ao criar businesspartner: nome={}, erro={}",
                    businesspartnerDto.nome(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public BusinessPartner atualizar(Long id, CreateBusinessPartnerDto businesspartnerDto) {
        log.info("[AUDIT BUSINESSPARTNER] Atualizando businesspartner: id={}, nome={}",
                id, businesspartnerDto.nome());
        
        try {
            BusinessPartner businesspartner = service.atualizar(id, businesspartnerDto);
            log.info("[AUDIT BUSINESSPARTNER] BusinessPartner atualizado com sucesso: id={}, nome={}",
                    businesspartner.getId(), businesspartner.getNome());
            return businesspartner;
        } catch (Exception e) {
            log.error("[AUDIT BUSINESSPARTNER] Erro ao atualizar businesspartner: id={}, erro={}",
                    id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public BusinessPartner pegarPorId(Long id) {
        log.debug("[AUDIT BUSINESSPARTNER] Buscando businesspartner por ID: id={}", id);
        
        try {
            BusinessPartner businesspartner = service.pegarPorId(id);
            log.debug("[AUDIT BUSINESSPARTNER] BusinessPartner encontrado: id={}, nome={}", id, businesspartner.getNome());
            return businesspartner;
        } catch (Exception e) {
            log.warn("[AUDIT BUSINESSPARTNER] BusinessPartner not found: id={}", id);
            throw e;
        }
    }

    @Override
    public void deletar(Long id) {
        log.info("[AUDIT BUSINESSPARTNER] Deletando businesspartner: id={}", id);
        
        try {
            BusinessPartner businesspartner = service.pegarPorId(id);
            service.deletar(id);
            log.info("[AUDIT BUSINESSPARTNER] BusinessPartner deletado com sucesso: id={}, nome={}",
                    id, businesspartner.getNome());
        } catch (Exception e) {
            log.error("[AUDIT BUSINESSPARTNER] Erro ao deletar businesspartner: id={}, erro={}",
                    id, e.getMessage(), e);
            throw e;
        }
    }
}
