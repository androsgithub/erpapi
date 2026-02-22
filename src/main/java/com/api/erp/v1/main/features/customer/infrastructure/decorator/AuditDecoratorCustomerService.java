package com.api.erp.v1.main.features.customer.infrastructure.decorator;

import com.api.erp.v1.main.features.customer.application.dto.request.CreateCustomerDto;
import com.api.erp.v1.main.features.customer.domain.entity.Customer;
import com.api.erp.v1.main.features.customer.domain.service.ICustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Decorator para adicionar auditoria ao serviço de Customer
 * 
 * Registra todas as operações de customer para fins de rastreabilidade,
 * compliance e investigação de problemas.
 * 
 * Exemplo de composição:
 * CustomerService → AuditDecorator → ValidationDecorator
 * 
 * Thread-Safe: Sim, apenas realiza logging sem alterar estado
 * Performance: Mínimo impacto, apenas registra informações
 */
@Slf4j
@RequiredArgsConstructor
public class AuditDecoratorCustomerService implements ICustomerService {

    private final ICustomerService service;

    @Override
    public Page<Customer> pegarTodos(Pageable pageable) {
        log.debug("[AUDIT CUSTOMER] Buscando todos os customers: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<Customer> result = service.pegarTodos(pageable);
            log.debug("[AUDIT CUSTOMER] Total de customers encontrados: {}", result.getTotalElements());
            return result;
        } catch (Exception e) {
            log.error("[AUDIT CUSTOMER] Erro ao buscar customers: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Customer criar(CreateCustomerDto customerDto) {
        log.info("[AUDIT CUSTOMER] Criando novo customer: nome={}, cnpj={}",
                customerDto.nome(), customerDto.dadosFiscais().cnpj());
        
        try {
            Customer customer = service.criar(customerDto);
            log.info("[AUDIT CUSTOMER] Customer criado com sucesso: id={}, nome={}, cnpj={}",
                    customer.getId(), customer.getNome(), customer.getDadosFiscais().getCnpj());
            return customer;
        } catch (Exception e) {
            log.error("[AUDIT CUSTOMER] Erro ao criar customer: nome={}, erro={}",
                    customerDto.nome(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Customer atualizar(Long id, CreateCustomerDto customerDto) {
        log.info("[AUDIT CUSTOMER] Atualizando customer: id={}, nome={}",
                id, customerDto.nome());
        
        try {
            Customer customer = service.atualizar(id, customerDto);
            log.info("[AUDIT CUSTOMER] Customer atualizado com sucesso: id={}, nome={}",
                    customer.getId(), customer.getNome());
            return customer;
        } catch (Exception e) {
            log.error("[AUDIT CUSTOMER] Erro ao atualizar customer: id={}, erro={}",
                    id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Customer pegarPorId(Long id) {
        log.debug("[AUDIT CUSTOMER] Buscando customer por ID: id={}", id);
        
        try {
            Customer customer = service.pegarPorId(id);
            log.debug("[AUDIT CUSTOMER] Customer encontrado: id={}, nome={}", id, customer.getNome());
            return customer;
        } catch (Exception e) {
            log.warn("[AUDIT CUSTOMER] Customer not found: id={}", id);
            throw e;
        }
    }

    @Override
    public void deletar(Long id) {
        log.info("[AUDIT CUSTOMER] Deletando customer: id={}", id);
        
        try {
            Customer customer = service.pegarPorId(id);
            service.deletar(id);
            log.info("[AUDIT CUSTOMER] Customer deletado com sucesso: id={}, nome={}",
                    id, customer.getNome());
        } catch (Exception e) {
            log.error("[AUDIT CUSTOMER] Erro ao deletar customer: id={}, erro={}",
                    id, e.getMessage(), e);
            throw e;
        }
    }
}
