package com.api.erp.v1.features.endereco.infrastructure.decorator;

import com.api.erp.v1.features.endereco.application.dto.CreateEnderecoRequest;
import com.api.erp.v1.features.endereco.domain.entity.Endereco;
import com.api.erp.v1.features.endereco.domain.service.IEnderecoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Decorator para adicionar auditoria ao serviço de Endereco
 * 
 * Registra todas as operações de endereço para fins de rastreabilidade.
 * Útil para compliance e investigação de problemas.
 * 
 * Exemplo de composição:
 * EnderecoService → AuditDecorator → ValidationDecorator
 */
@Slf4j
@RequiredArgsConstructor
public class AuditDecoratorEnderecoService implements IEnderecoService {

    private final IEnderecoService service;

    @Override
    public Endereco criar(CreateEnderecoRequest request) {
        log.info("[AUDIT ENDERECO] Criando novo endereço: rua={}, número={}, cep={}",
                request.rua(), request.numero(), request.cep());

        try {
            Endereco endereco = service.criar(request);
            log.info("[AUDIT ENDERECO] Endereço criado com sucesso: id={}, rua={}, número={}",
                    endereco.getId(), endereco.getRua(), endereco.getNumero());
            return endereco;
        } catch (Exception e) {
            log.error("[AUDIT ENDERECO] Erro ao criar endereço: rua={}, número={}, erro={}",
                    request.rua(), request.numero(), e.getMessage());
            throw e;
        }
    }

    @Override
    public Endereco buscarPorId(Long id) {
        log.debug("[AUDIT ENDERECO] Buscando endereço por ID: id={}", id);

        try {
            Endereco endereco = service.buscarPorId(id);
            log.debug("[AUDIT ENDERECO] Endereço encontrado: id={}, rua={}", id, endereco.getRua());
            return endereco;
        } catch (Exception e) {
            log.warn("[AUDIT ENDERECO] Endereço não encontrado: id={}", id);
            throw e;
        }
    }

    @Override
    public List<Endereco> buscarTodos() {
        log.debug("[AUDIT ENDERECO] Listando todos os endereços");
        List<Endereco> response = service.buscarTodos();
        log.debug("[AUDIT ENDERECO] Total de endereços encontrados: {}", response.size());
        return response;
    }

    @Override
    public Endereco atualizar(Long id, CreateEnderecoRequest request) {
        log.info("[AUDIT ENDERECO] Atualizando endereço: id={}, rua={}, número={}",
                id, request.rua(), request.numero());

        try {
            Endereco endereco = service.atualizar(id, request);
            log.info("[AUDIT ENDERECO] Endereço atualizado com sucesso: id={}, rua={}",
                    id, endereco.getRua());
            return endereco;
        } catch (Exception e) {
            log.error("[AUDIT ENDERECO] Erro ao atualizar endereço: id={}, erro={}",
                    id, e.getMessage());
            throw e;
        }
    }

    @Override
    public void deletar(Long id) {
        log.info("[AUDIT ENDERECO] Deletando endereço: id={}", id);

        try {
            service.deletar(id);
            log.info("[AUDIT ENDERECO] Endereço deletado com sucesso: id={}", id);
        } catch (Exception e) {
            log.error("[AUDIT ENDERECO] Erro ao deletar endereço: id={}, erro={}",
                    id, e.getMessage());
            throw e;
        }
    }
}
