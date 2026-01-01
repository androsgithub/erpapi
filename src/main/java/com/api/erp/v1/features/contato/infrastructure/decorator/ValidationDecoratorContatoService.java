package com.api.erp.v1.features.contato.infrastructure.decorator;

import com.api.erp.v1.features.contato.application.dto.CreateContatoRequest;
import com.api.erp.v1.features.contato.domain.service.IContatoService;
import com.api.erp.v1.features.contato.domain.entity.Contato;
import com.api.erp.v1.features.contato.domain.validator.ContatoValidator;
import com.api.erp.v1.shared.domain.exception.BusinessException;

import java.util.List;

/**
 * Decorator para adicionar validações extras ao serviço de Contato
 * 
 * Exemplo de uso do Decorator Pattern para adicionar comportamento
 * sem modificar a classe original.
 * 
 * Pode ser utilizado em conjunto com outros decorators:
 * ContatoService base → ValidationDecorator → AuditDecorator → CacheDecorator
 */
public class ValidationDecoratorContatoService implements IContatoService {

    private final IContatoService service;

    public ValidationDecoratorContatoService(IContatoService service) {
        this.service = service;
    }

    @Override
    public Contato criar(CreateContatoRequest request) {
        // Validações extras antes de criar
        validarRequest(request);
        return service.criar(request);
    }

    @Override
    public Contato buscarPorId(Long id) {
        validarId(id);
        return service.buscarPorId(id);
    }

    @Override
    public List<Contato> buscarTodos() {
        return service.buscarTodos();
    }

    @Override
    public List<Contato> buscarAtivos() {
        return service.buscarAtivos();
    }

    @Override
    public List<Contato> buscarInativos() {
        return service.buscarInativos();
    }

    @Override
    public List<Contato> buscarPorTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new BusinessException("Tipo de contato é obrigatório");
        }
        return service.buscarPorTipo(tipo);
    }

    @Override
    public Contato buscarPrincipal() {
        return service.buscarPrincipal();
    }

    @Override
    public Contato atualizar(Long id, CreateContatoRequest request) {
        validarId(id);
        validarRequest(request);
        return service.atualizar(id, request);
    }

    @Override
    public Contato ativar(Long id) {
        validarId(id);
        return service.ativar(id);
    }

    @Override
    public Contato desativar(Long id) {
        validarId(id);
        return service.desativar(id);
    }

    @Override
    public void deletar(Long id) {
        validarId(id);
        service.deletar(id);
    }

    /**
     * Valida o request completo
     */
    private void validarRequest(CreateContatoRequest request) {
        if (request == null) {
            throw new BusinessException("Request de contato não pode ser nulo");
        }

        ContatoValidator.validar(request.tipo(), request.valor());
        ContatoValidator.validarDescricao(request.descricao());
    }

    /**
     * Valida o ID
     */
    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID do contato deve ser um número positivo");
        }
    }
}
