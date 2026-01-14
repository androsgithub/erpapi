package com.api.erp.v1.features.contato.domain.service;

import com.api.erp.v1.features.contato.application.dto.request.AssociarContatosRequest;
import com.api.erp.v1.features.contato.application.dto.CreateContatoRequest;
import com.api.erp.v1.features.contato.application.dto.request.RemoverContatoRequest;
import com.api.erp.v1.features.contato.domain.entity.Contato;
import com.api.erp.v1.features.contato.domain.entity.UsuarioContato;

/**
 * Interface para o serviço de gerenciamento de contatos de usuário
 * Define o contrato para operações de associação e gerenciamento de múltiplos
 * contatos para um usuário
 * 
 * IMPORTANTE: Esta interface retorna apenas entidades de domínio
 * A conversão para DTOs de response é responsabilidade do controller/mapper
 */
public interface IGerenciamentoContatoService {

    /**
     * Associa múltiplos contatos a um usuário
     * 
     * @param request com usuarioId e lista de contatos
     * @return a entidade UsuarioContato da última associação criada
     */
    UsuarioContato associarContatos(AssociarContatosRequest request);

    /**
     * Adiciona um contato a um usuário existente
     * 
     * @param usuarioId o ID do usuário
     * @param request com dados do contato
     * @return a entidade Contato adicionada
     */
    Contato adicionarContato(Long usuarioId, CreateContatoRequest request);

    /**
     * Remove um contato de um usuário
     * 
     * @param request com usuarioId e contatoId
     */
    void removerContato(RemoverContatoRequest request);

    /**
     * Busca todos os contatos de um usuário
     * 
     * @param usuarioId o ID do usuário
     * @return a entidade UsuarioContato da primeira associação encontrada
     */
    UsuarioContato buscarContatosUsuario(Long usuarioId);

    /**
     * Marca um contato como principal
     * 
     * @param usuarioId o ID do usuário
     * @param contatoId o ID do contato
     * @return a entidade Contato atualizada
     */
    Contato marcarComoPrincipal(Long usuarioId, Long contatoId);

    /**
     * Desativa um contato de um usuário
     * 
     * @param usuarioId o ID do usuário
     * @param contatoId o ID do contato
     * @return a entidade Contato desativada
     */
    Contato desativarContato(Long usuarioId, Long contatoId);

    /**
     * Ativa um contato de um usuário
     * 
     * @param usuarioId o ID do usuário
     * @param contatoId o ID do contato
     * @return a entidade Contato ativada
     */
    Contato ativarContato(Long usuarioId, Long contatoId);
}
