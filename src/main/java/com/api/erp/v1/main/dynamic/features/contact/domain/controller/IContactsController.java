package com.api.erp.v1.main.dynamic.features.contact.domain.controller;

import com.api.erp.v1.main.dynamic.features.contact.application.dto.CreateContactRequest;
import com.api.erp.v1.main.dynamic.features.contact.application.dto.response.ContactResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

public interface IContactsController {

    public ResponseEntity<ContactResponse> criar(@RequestBody CreateContactRequest request);

    public ResponseEntity<ContactResponse> buscar(Long id);

    public ResponseEntity<Set<ContactResponse>> listar();

    public ResponseEntity<Set<ContactResponse>> listarAtivos();

    public ResponseEntity<Set<ContactResponse>> listarInativos();

    public ResponseEntity<Set<ContactResponse>> listarPorTipo(String tipo);

    public ResponseEntity<ContactResponse> buscarPrincipal();

    public ResponseEntity<ContactResponse> atualizar(Long id, @RequestBody CreateContactRequest request);

    public ResponseEntity<ContactResponse> ativar(Long id);

    public ResponseEntity<ContactResponse> desativar(Long id);

    public ResponseEntity<Void> deletar(Long id);
}
