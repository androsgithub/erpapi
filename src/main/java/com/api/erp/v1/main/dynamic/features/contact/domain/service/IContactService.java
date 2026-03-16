package com.api.erp.v1.main.dynamic.features.contact.domain.service;

import com.api.erp.v1.main.dynamic.features.contact.application.dto.CreateContactRequest;
import com.api.erp.v1.main.dynamic.features.contact.domain.entity.Contact;

import java.util.List;

/**
 * Interface para o serviço de Contact
 * Define o contrato de operações do serviço, permitindo extensão via Decorator Pattern
 * 
 * IMPORTANTE: Esta interface retorna apenas entidades de domínio (Contact)
 * A conversão para DTOs de response é responsabilidade do controller/mapper
 */
public interface IContactService {

    /**
     * Cria um novo contact
     */
    Contact criar(CreateContactRequest request);

    /**
     * Busca um contact por ID
     */
    Contact buscarPorId(Long id);

    /**
     * Lista todos os contacts
     */
    List<Contact> buscarTodos();

    /**
     * Lista todos os contacts ativos
     */
    List<Contact> buscarAtivos();

    /**
     * Lista todos os contacts inativos
     */
    List<Contact> buscarInativos();

    /**
     * Lista contacts por tipo
     */
    List<Contact> buscarPorTipo(String tipo);

    /**
     * Busca o contact marcado como principal
     */
    Contact buscarPrincipal();

    /**
     * Atualiza um contact existente
     */
    Contact atualizar(Long id, CreateContactRequest request);

    /**
     * Ativa um contact
     */
    Contact ativar(Long id);

    /**
     * Desativa um contact
     */
    Contact desativar(Long id);

    /**
     * Deleta um contact
     */
    void deletar(Long id);
}
