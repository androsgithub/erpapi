package com.api.erp.v1.main.features.contact.application.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO de resposta para contacts de um usuário
 */
public record UserContactsResponse(
        Long userContactId,
        Long userId,
        Set<ContactResponse> contacts,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
}
