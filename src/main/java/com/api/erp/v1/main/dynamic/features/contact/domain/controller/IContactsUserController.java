package com.api.erp.v1.main.dynamic.features.contact.domain.controller;

import com.api.erp.v1.main.dynamic.features.contact.application.dto.CreateContactRequest;
import com.api.erp.v1.main.dynamic.features.contact.application.dto.request.AssociarContactsRequest;
import com.api.erp.v1.main.dynamic.features.contact.application.dto.request.RemoverContactRequest;
import com.api.erp.v1.main.dynamic.features.contact.application.dto.response.ContactResponse;
import com.api.erp.v1.main.dynamic.features.contact.application.dto.response.UserContactsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface IContactsUserController {

    ResponseEntity<UserContactsResponse> associarContacts(
            @RequestBody AssociarContactsRequest request);

    ResponseEntity<ContactResponse> adicionarContact(
            Long userId,
            @RequestBody CreateContactRequest request);

    ResponseEntity<UserContactsResponse> buscarContactsUser(
            Long userId);

    ResponseEntity<Void> removerContact(
            @RequestBody RemoverContactRequest request);

    ResponseEntity<ContactResponse> marcarComoPrincipal(
            Long userId,
            Long contactId);

    ResponseEntity<ContactResponse> desativarContact(
            Long userId,
            Long contactId);

    ResponseEntity<ContactResponse> ativarContact(
            Long userId,
            Long contactId);
}
