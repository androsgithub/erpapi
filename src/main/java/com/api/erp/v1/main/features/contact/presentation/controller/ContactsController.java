package com.api.erp.v1.main.features.contact.presentation.controller;

import com.api.erp.v1.main.features.contact.application.dto.CreateContactRequest;
import com.api.erp.v1.main.features.contact.application.dto.response.ContactResponse;
import com.api.erp.v1.main.features.contact.application.mapper.IContactMapper;
import com.api.erp.v1.main.features.contact.domain.controller.IContactsController;
import com.api.erp.v1.docs.openapi.features.contact.ContactsOpenApiDocumentation;
import com.api.erp.v1.main.features.contact.domain.entity.ContactPermissions;
import com.api.erp.v1.main.features.contact.domain.service.IContactService;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import com.api.erp.v1.main.shared.infrastructure.documentation.RequiresXTenantId;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/contacts")
public class ContactsController implements IContactsController, ContactsOpenApiDocumentation {

    private final IContactService contactService;
    private final IContactMapper contactMapper;

    public ContactsController(
            @Qualifier("contactServiceProxy") IContactService contactService,
            IContactMapper contactMapper) {
        this.contactService = contactService;
        this.contactMapper = contactMapper;
    }

    @PostMapping
    @RequiresXTenantId
    @RequiresPermission(ContactPermissions.CREATE)
    public ResponseEntity<ContactResponse> criar(@RequestBody CreateContactRequest request) {
        var contact = contactService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(contactMapper.toResponse(contact));
    }

    @GetMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(ContactPermissions.VIEW)
    public ResponseEntity<ContactResponse> buscar(
            @PathVariable @Parameter(description = "ID do contact") Long id) {
        var contact = contactService.buscarPorId(id);
        return ResponseEntity.ok(contactMapper.toResponse(contact));
    }

    @GetMapping
    @RequiresXTenantId
    @RequiresPermission(ContactPermissions.VIEW)
    public ResponseEntity<List<ContactResponse>> listar() {
        var contacts = contactService.buscarTodos();
        return ResponseEntity.ok(contactMapper.toResponseList(contacts));
    }

    @GetMapping("/status/ativos")
    @RequiresXTenantId
    @RequiresPermission(ContactPermissions.VIEW)
    public ResponseEntity<List<ContactResponse>> listarAtivos() {
        var contacts = contactService.buscarAtivos();
        return ResponseEntity.ok(contactMapper.toResponseList(contacts));
    }


    @GetMapping("/status/inativos")
    @RequiresXTenantId
    @RequiresPermission(ContactPermissions.VIEW)
    public ResponseEntity<List<ContactResponse>> listarInativos() {
        var contacts = contactService.buscarInativos();
        return ResponseEntity.ok(contactMapper.toResponseList(contacts));
    }

    @GetMapping("/tipo/{tipo}")
    @RequiresXTenantId
    @RequiresPermission(ContactPermissions.VIEW)
    public ResponseEntity<List<ContactResponse>> listarPorTipo(
            @PathVariable @Parameter(description = "Tipo de contact (TELEFONE, EMAIL, WHATSAPP, etc.)") String tipo) {
        var contacts = contactService.buscarPorTipo(tipo);
        return ResponseEntity.ok(contactMapper.toResponseList(contacts));
    }

    @GetMapping("/principal")
    @RequiresXTenantId
    @RequiresPermission(ContactPermissions.VIEW)
    public ResponseEntity<ContactResponse> buscarPrincipal() {
        var contact = contactService.buscarPrincipal();
        return ResponseEntity.ok(contactMapper.toResponse(contact));
    }

    @PutMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(ContactPermissions.UPDATE)
    public ResponseEntity<ContactResponse> atualizar(
            @PathVariable @Parameter(description = "ID do contact") Long id,
            @RequestBody CreateContactRequest request) {
        var contact = contactService.atualizar(id, request);
        return ResponseEntity.ok(contactMapper.toResponse(contact));
    }


    @PatchMapping("/{id}/ativar")
    @RequiresXTenantId
    @RequiresPermission(ContactPermissions.UPDATE)
    public ResponseEntity<ContactResponse> ativar(
            @PathVariable @Parameter(description = "ID do contact") Long id) {
        var contact = contactService.ativar(id);
        return ResponseEntity.ok(contactMapper.toResponse(contact));
    }

    @PatchMapping("/{id}/desativar")
    @RequiresXTenantId
    @RequiresPermission(ContactPermissions.UPDATE)
    public ResponseEntity<ContactResponse> desativar(
            @PathVariable @Parameter(description = "ID do contact") Long id) {
        var contact = contactService.desativar(id);
        return ResponseEntity.ok(contactMapper.toResponse(contact));
    }

    @DeleteMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(ContactPermissions.DELETE)
    public ResponseEntity<Void> deletar(
            @PathVariable @Parameter(description = "ID do contact") Long id) {
        contactService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
