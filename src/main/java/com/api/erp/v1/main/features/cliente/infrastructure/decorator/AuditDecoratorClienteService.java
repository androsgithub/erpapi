package com.api.erp.v1.main.features.cliente.infrastructure.decorator;

import com.api.erp.v1.main.features.cliente.application.dto.request.CreateClienteDto;
import com.api.erp.v1.main.features.cliente.domain.entity.Cliente;
import com.api.erp.v1.main.features.cliente.domain.service.IClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Decorator para adicionar auditoria ao serviço de Cliente
 * 
 * Registra todas as operações de cliente para fins de rastreabilidade,
 * compliance e investigação de problemas.
 * 
 * Exemplo de composição:
 * ClienteService → AuditDecorator → ValidationDecorator
 * 
 * Thread-Safe: Sim, apenas realiza logging sem alterar estado
 * Performance: Mínimo impacto, apenas registra informações
 */
@Slf4j
@RequiredArgsConstructor
public class AuditDecoratorClienteService implements IClienteService {

    private final IClienteService service;

    @Override
    public Page<Cliente> pegarTodos(Pageable pageable) {
        log.debug("[AUDIT CLIENTE] Buscando todos os clientes: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<Cliente> result = service.pegarTodos(pageable);
            log.debug("[AUDIT CLIENTE] Total de clientes encontrados: {}", result.getTotalElements());
            return result;
        } catch (Exception e) {
            log.error("[AUDIT CLIENTE] Erro ao buscar clientes: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Cliente criar(CreateClienteDto clienteDto) {
        log.info("[AUDIT CLIENTE] Criando novo cliente: nome={}, cnpj={}",
                clienteDto.nome(), clienteDto.dadosFiscais().cnpj());
        
        try {
            Cliente cliente = service.criar(clienteDto);
            log.info("[AUDIT CLIENTE] Cliente criado com sucesso: id={}, nome={}, cnpj={}",
                    cliente.getId(), cliente.getNome(), cliente.getDadosFiscais().getCnpj());
            return cliente;
        } catch (Exception e) {
            log.error("[AUDIT CLIENTE] Erro ao criar cliente: nome={}, erro={}",
                    clienteDto.nome(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Cliente atualizar(Long id, CreateClienteDto clienteDto) {
        log.info("[AUDIT CLIENTE] Atualizando cliente: id={}, nome={}",
                id, clienteDto.nome());
        
        try {
            Cliente cliente = service.atualizar(id, clienteDto);
            log.info("[AUDIT CLIENTE] Cliente atualizado com sucesso: id={}, nome={}",
                    cliente.getId(), cliente.getNome());
            return cliente;
        } catch (Exception e) {
            log.error("[AUDIT CLIENTE] Erro ao atualizar cliente: id={}, erro={}",
                    id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Cliente pegarPorId(Long id) {
        log.debug("[AUDIT CLIENTE] Buscando cliente por ID: id={}", id);
        
        try {
            Cliente cliente = service.pegarPorId(id);
            log.debug("[AUDIT CLIENTE] Cliente encontrado: id={}, nome={}", id, cliente.getNome());
            return cliente;
        } catch (Exception e) {
            log.warn("[AUDIT CLIENTE] Cliente não encontrado: id={}", id);
            throw e;
        }
    }

    @Override
    public void deletar(Long id) {
        log.info("[AUDIT CLIENTE] Deletando cliente: id={}", id);
        
        try {
            Cliente cliente = service.pegarPorId(id);
            service.deletar(id);
            log.info("[AUDIT CLIENTE] Cliente deletado com sucesso: id={}, nome={}",
                    id, cliente.getNome());
        } catch (Exception e) {
            log.error("[AUDIT CLIENTE] Erro ao deletar cliente: id={}, erro={}",
                    id, e.getMessage(), e);
            throw e;
        }
    }
}
