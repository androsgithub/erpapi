package com.api.erp.v1.main.dynamic.features.contact.domain.service;

import com.api.erp.v1.main.dynamic.features.contact.application.dto.CreateContactRequest;
import com.api.erp.v1.main.dynamic.features.contact.application.dto.request.RemoverContactRequest;
import com.api.erp.v1.main.dynamic.features.contact.domain.entity.Contact;

/**
 * Interface para o serviço de gerenciamento de contacts de usuário
 * Define o contrato para operações de associação e gerenciamento de múltiplos
 * contacts para um usuário
 * 
 * IMPORTANTE: Esta interface retorna apenas entidades de domínio
 * A conversão para DTOs de response é responsabilidade do controller/mapper
 */
public interface IManagementContactService {

    /**
     * Adiciona um contact a um usuário existente
     * 
     * @param userId o ID do usuário
     * @param request com dados do contact
     * @return a entidade Contact adicionada
     */
    Contact adicionarContact(Long userId, CreateContactRequest request);

    /**
     * Remove um contact de um usuário
     * 
     * @param request com userId e contactId
     */
    void removerContact(RemoverContactRequest request);

    /**
     * Marca um contact como principal
     * 
     * @param userId o ID do usuário
     * @param contactId o ID do contact
     * @return a entidade Contact atualizada
     */
    Contact marcarComoPrincipal(Long userId, Long contactId);

    /**
     * Desativa um contact de um usuário
     * 
     * @param userId o ID do usuário
     * @param contactId o ID do contact
     * @return a entidade Contact desativada
     */
    Contact desativarContact(Long userId, Long contactId);

    /**
     * Ativa um contact de um usuário
     * 
     * @param userId o ID do usuário
     * @param contactId o ID do contact
     * @return a entidade Contact ativada
     */
    Contact ativarContact(Long userId, Long contactId);
}
