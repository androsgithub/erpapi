package com.api.erp.v1.main.dynamic.features.contact.infrastructure.service;

import com.api.erp.v1.main.dynamic.features.contact.application.dto.CreateContactRequest;
import com.api.erp.v1.main.dynamic.features.contact.application.dto.request.RemoverContactRequest;
import com.api.erp.v1.main.dynamic.features.contact.domain.entity.Contact;
import com.api.erp.v1.main.dynamic.features.contact.domain.repository.ContactRepository;
import com.api.erp.v1.main.dynamic.features.contact.domain.service.IManagementContactService;
import com.api.erp.v1.main.master.user.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ManagementContactService implements IManagementContactService {

    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Contact adicionarContact(Long userId, CreateContactRequest request) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + userId));
//
//        // Cria o contact
//        Contact contact = Contact.builder()
//                .tipo(TipoContact.valueOf(request.tipo().toUpperCase()))
//                .valor(request.valor())
//                .descricao(request.descricao())
//                .principal(request.principal())
//                .ativo(true)
//                .build();
//
//        // Salva o contact
//        contact = contactRepository.save(contact);
//
//        // Cria a associação
//        UserContact userContact = UserContact.builder()
//                .user(user)
//                .contact(contact)
//                .build();
//
//        userContactRepository.save(userContact);
//
//        return contact;
        return null;
    }

    @Override
    @Transactional
    public void removerContact(RemoverContactRequest request) {
//        UserContact userContact = userContactRepository
//                .findByUserIdAndContactId(request.userId(), request.contactId())
//                .orElseThrow(() -> new IllegalArgumentException("Associação não encontrada"));
//
//        userContactRepository.delete(userContact);
    }

    @Override
    @Transactional
    public Contact marcarComoPrincipal(Long userId, Long contactId) {
//        userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + userId));
//
//        // Desmarca outros contacts como principais
//        List<UserContact> userContacts = userContactRepository.findByUserId(userId);
//
//        for (UserContact uc : userContacts) {
//            if (uc.getContact().getId().equals(contactId)) {
//                uc.getContact().setPrincipal(false);
//            } else {
//                uc.getContact().setPrincipal(true);
//            }
//            userContactRepository.save(uc);
//        }
//
//        return contactRepository.findById(contactId)
//                .orElseThrow(() -> new IllegalArgumentException("Contact não encontrado"));
        return null;
    }

    @Override
    @Transactional
    public Contact desativarContact(Long userId, Long contactId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + userId));

        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new IllegalArgumentException("Contact não encontrado"));

        contact.setAtivo(false);
        return contactRepository.save(contact);
    }

    @Override
    @Transactional
    public Contact ativarContact(Long userId, Long contactId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + userId));

        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new IllegalArgumentException("Contact não encontrado"));

        contact.setAtivo(false);
        return contactRepository.save(contact);
    }
}
