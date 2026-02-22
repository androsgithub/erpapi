package com.api.erp.v1.main.features.contact.infrastructure.service;

import com.api.erp.v1.main.features.contact.application.dto.CreateContactRequest;
import com.api.erp.v1.main.features.contact.domain.entity.Contact;
import com.api.erp.v1.main.features.contact.domain.entity.TipoContact;
import com.api.erp.v1.main.features.contact.domain.repository.ContactRepository;
import com.api.erp.v1.main.features.contact.domain.service.IContactService;
import com.api.erp.v1.main.features.contact.domain.validator.ContactValidator;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContactService implements IContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Transactional
    public Contact criar(CreateContactRequest request) {
        // Validar dados
        ContactValidator.validar(request.tipo(), request.valor());
        ContactValidator.validarDescricao(request.descricao());

        // Se o contact é marcado como principal, remover principal dos outros
        if (request.principal()) {
            removerPrincipalExistente();
        }

        // Criar novo contact
        TipoContact tipo = TipoContact.valueOf(request.tipo().toUpperCase());
        Contact contact = new Contact(tipo, request.valor());

        if (request.descricao() != null && !request.descricao().isBlank()) {
            contact.setDescricao(request.descricao());
        }

        contact.setPrincipal(request.principal());
//        contact.setCustomData(request.customData());

        return contactRepository.save(contact);
    }


    @Transactional(readOnly = true)
    public Contact buscarPorId(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contact não encontrado com id: " + id));
    }


    @Transactional(readOnly = true)
    public List<Contact> buscarTodos() {
        return contactRepository.findAll();
    }


    @Transactional(readOnly = true)
    public List<Contact> buscarAtivos() {
        return contactRepository.findByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public List<Contact> buscarInativos() {
        return contactRepository.findByAtivoFalse();
    }

    @Transactional(readOnly = true)
    public List<Contact> buscarPorTipo(String tipo) {
        TipoContact tipoContact = TipoContact.valueOf(tipo.toUpperCase());
        return contactRepository.findByTipoAndAtivoTrue(tipoContact);
    }

    @Transactional(readOnly = true)
    public Contact buscarPrincipal() {
        return contactRepository.findPrincipal()
                .orElseThrow(() -> new NotFoundException("Nenhum contact principal encontrado"));
    }

    @Transactional
    public Contact atualizar(Long id, CreateContactRequest request) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contact não encontrado com id: " + id));

        // Validar dados
        ContactValidator.validar(request.tipo(), request.valor());
        ContactValidator.validarDescricao(request.descricao());

        // Se está marcando como principal, remover principal dos outros
        if (request.principal() && !contact.isPrincipal()) {
            removerPrincipalExistente();
        }

        // Atualizar campos
        TipoContact tipo = TipoContact.valueOf(request.tipo().toUpperCase());
        contact.setTipo(tipo);
        contact.setValor(request.valor());
        contact.setDescricao(request.descricao());
        contact.setPrincipal(request.principal());
//        contact.setCustomData(request.customData());

        return contactRepository.save(contact);
    }

    @Transactional
    public Contact ativar(Long id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contact não encontrado com id: " + id));

        contact.setAtivo(true);
        return contactRepository.save(contact);
    }

    @Transactional
    public Contact desativar(Long id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contact não encontrado com id: " + id));
        if (contact.isPrincipal()) {
            contact.setPrincipal(false);
        }

        contact.setAtivo(false);
        return contactRepository.save(contact);
    }

    @Transactional
    public void deletar(Long id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contact não encontrado com id: " + id));

        contactRepository.deleteById(id);
    }

    @Transactional
    private void removerPrincipalExistente() {
        var principal = contactRepository.findPrincipal();
        if (principal.isPresent()) {
            principal.get().setPrincipal(false);
            contactRepository.save(principal.get());
        }
    }
}
