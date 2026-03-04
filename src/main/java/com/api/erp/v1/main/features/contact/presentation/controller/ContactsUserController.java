package com.api.erp.v1.main.features.contact.presentation.controller;

import com.api.erp.v1.main.features.contact.application.dto.CreateContactRequest;
import com.api.erp.v1.main.features.contact.application.dto.request.AssociarContactsRequest;
import com.api.erp.v1.main.features.contact.application.dto.request.RemoverContactRequest;
import com.api.erp.v1.main.features.contact.application.dto.response.ContactResponse;
import com.api.erp.v1.main.features.contact.application.dto.response.UserContactsResponse;
import com.api.erp.v1.main.features.contact.application.mapper.IContactMapper;
import com.api.erp.v1.main.features.contact.domain.controller.IContactsUserController;
import com.api.erp.v1.docs.openapi.features.contact.ContactsUserOpenApiDocumentation;
import com.api.erp.v1.main.features.contact.domain.entity.ContactPermissions;
import com.api.erp.v1.main.features.contact.domain.service.IManagementContactService;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import com.api.erp.v1.main.shared.infrastructure.documentation.RequiresXTenantId;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para gerenciar contacts de Usuário
 * Fornece endpoints especializados para operações de contacts associados a usuários
 * <p>
 * Este controller é responsável por operações que envolvem a relação
 * entre Usuário e Contact, como:
 * - Associar múltiplos contacts a um usuário
 * - Adicionar/remover contacts de um usuário
 * - Marcar contact como principal
 * - Ativar/desativar contacts de um usuário
 * <p>
 * Para operações básicas de contact (CRUD global), use ContactsController.
 * Para futuras features como "BusinessPartner", crie ContactsBusinessPartnerController seguindo o mesmo padrão.
 * <p>
 * PADRÃO DE ARQUITETURA:
 * - Controller orquestra chamadas ao service de gerenciamento
 * - Service retorna apenas entidades de domínio
 * - Mapper converte entidades para DTOs de response
 * - Controller não contém lógica de negócio, apenas orquestração
 */
@RestController
@RequestMapping("/api/v1/contacts/user")
public class ContactsUserController implements IContactsUserController, ContactsUserOpenApiDocumentation {

    @Autowired

    private IManagementContactService gerenciamentoContactService;
    @Autowired
    private IContactMapper contactMapper;

    @PostMapping("/associar")
    @RequiresXTenantId
    @RequiresPermission(ContactPermissions.CREATE)
    public ResponseEntity<UserContactsResponse> associarContacts(
            @RequestBody AssociarContactsRequest request) {
//        var userContact = gerenciamentoContactService.associarContacts(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(userContactMapper.toResponse(userContact));
        return null;
    }

    @PostMapping("/{userId}/contact")
    @RequiresXTenantId
    @RequiresPermission(ContactPermissions.CREATE)
    public ResponseEntity<ContactResponse> adicionarContact(
            @PathVariable @Parameter(description = "ID do usuário") Long userId,
            @RequestBody CreateContactRequest request) {
        var contact = gerenciamentoContactService.adicionarContact(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(contactMapper.toResponse(contact));
    }

    @GetMapping("/{userId}")
    @RequiresXTenantId
    @RequiresPermission(ContactPermissions.VIEW)
    public ResponseEntity<UserContactsResponse> buscarContactsUser(
            @PathVariable @Parameter(description = "ID do usuário") Long userId) {
//        var userContact = gerenciamentoContactService.buscarContactsUser(userId);
//        return ResponseEntity.ok(userContactMapper.toResponse(userContact));
        return null;
    }

    @DeleteMapping("/remover")
    @RequiresXTenantId
    @RequiresPermission(ContactPermissions.DELETE)
    public ResponseEntity<Void> removerContact(
            @RequestBody RemoverContactRequest request) {
        gerenciamentoContactService.removerContact(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/contact/{contactId}/principal")
    @RequiresXTenantId
    @RequiresPermission(ContactPermissions.UPDATE)
    public ResponseEntity<ContactResponse> marcarComoPrincipal(
            @PathVariable @Parameter(description = "ID do usuário") Long userId,
            @PathVariable @Parameter(description = "ID do contact") Long contactId) {
        var contact = gerenciamentoContactService.marcarComoPrincipal(userId, contactId);
        return ResponseEntity.ok(contactMapper.toResponse(contact));
    }

    @PatchMapping("/{userId}/contact/{contactId}/desativar")
    @RequiresXTenantId
    @RequiresPermission(ContactPermissions.UPDATE)
    public ResponseEntity<ContactResponse> desativarContact(
            @PathVariable @Parameter(description = "ID do usuário") Long userId,
            @PathVariable @Parameter(description = "ID do contact") Long contactId) {
        var contact = gerenciamentoContactService.desativarContact(userId, contactId);
        return ResponseEntity.ok(contactMapper.toResponse(contact));
    }

    @PatchMapping("/{userId}/contact/{contactId}/ativar")
    @RequiresXTenantId
    @RequiresPermission(ContactPermissions.UPDATE)
    public ResponseEntity<ContactResponse> ativarContact(
            @PathVariable @Parameter(description = "ID do usuário") Long userId,
            @PathVariable @Parameter(description = "ID do contact") Long contactId) {
        var contact = gerenciamentoContactService.ativarContact(userId, contactId);
        return ResponseEntity.ok(contactMapper.toResponse(contact));
    }
}
