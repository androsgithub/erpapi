package com.api.erp.v1.main.dynamic.features.contact.application.dto.request;

import com.api.erp.v1.main.dynamic.features.contact.application.dto.CreateContactRequest;

import java.util.List;

/**
 * DTO para associar múltiplos contacts a um usuário
 */
public record AssociarContactsRequest(
        Long userId,
        List<CreateContactRequest> contacts
) {
}
