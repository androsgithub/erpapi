package com.api.erp.v1.features.contato.domain.service;

import com.api.erp.v1.features.contato.application.dto.CreateContatoRequest;
import com.api.erp.v1.features.contato.domain.entity.Contato;

import java.util.List;

/**
 * Interface para o serviço de Contato
 * Define o contrato de operações do serviço, permitindo extensão via Decorator Pattern
 * 
 * IMPORTANTE: Esta interface retorna apenas entidades de domínio (Contato)
 * A conversão para DTOs de response é responsabilidade do controller/mapper
 */
public interface IContatoService {

    /**
     * Cria um novo contato
     */
    Contato criar(CreateContatoRequest request);

    /**
     * Busca um contato por ID
     */
    Contato buscarPorId(Long id);

    /**
     * Lista todos os contatos
     */
    List<Contato> buscarTodos();

    /**
     * Lista todos os contatos ativos
     */
    List<Contato> buscarAtivos();

    /**
     * Lista todos os contatos inativos
     */
    List<Contato> buscarInativos();

    /**
     * Lista contatos por tipo
     */
    List<Contato> buscarPorTipo(String tipo);

    /**
     * Busca o contato marcado como principal
     */
    Contato buscarPrincipal();

    /**
     * Atualiza um contato existente
     */
    Contato atualizar(Long id, CreateContatoRequest request);

    /**
     * Ativa um contato
     */
    Contato ativar(Long id);

    /**
     * Desativa um contato
     */
    Contato desativar(Long id);

    /**
     * Deleta um contato
     */
    void deletar(Long id);
}
