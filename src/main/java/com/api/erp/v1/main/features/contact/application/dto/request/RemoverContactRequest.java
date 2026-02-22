package com.api.erp.v1.main.features.contact.application.dto.request;

/**
 * DTO para remover contact de um usuário
 */
public record RemoverContactRequest(
        Long userId,
        Long contactId
) {
}
