package com.api.erp.v1.main.features.contact.infrastructure.decorator;

import com.api.erp.v1.main.features.contact.application.dto.CreateContactRequest;
import com.api.erp.v1.main.features.contact.domain.entity.Contact;
import com.api.erp.v1.main.features.contact.domain.service.IContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Decorator de Notificação - Exemplo Customizado
 * <p>
 * Envia notificações (via email) quando contacts são criados, atualizados ou deletados.
 * Este decorator pode ser adicionado à factory quando habilitado na configuração.
 * <p>
 * Configuration:
 * contact.decorators.notification.enabled=true
 * contact.decorators.notification.recipient-email=admin@empresa.com
 * <p>
 * Para usar, adicione à ContactServiceDecoratorFactory:
 * if (properties.getNotification().isEnabled()) {
 * service = new NotificationDecoratorContactService(service, emailService, properties);
 * }
 */
@Slf4j
@RequiredArgsConstructor
public class NotificationDecoratorContactService implements IContactService {

    private final IContactService service;


    @Override
    public Contact criar(CreateContactRequest request) {
        Contact contact = service.criar(request);

        if (shouldNotifyOn("CREATE")) {
            enviarNotificacao(
                    "Novo contact criado",
                    String.format("Tipo: %s, Valor: %s", contact.getTipo(), contact.getValor())
            );
        }

        return contact;
    }

    @Override
    public Contact buscarPorId(Long id) {
        return service.buscarPorId(id);
    }

    @Override
    public List<Contact> buscarTodos() {
        return service.buscarTodos();
    }

    @Override
    public List<Contact> buscarAtivos() {
        return service.buscarAtivos();
    }

    @Override
    public List<Contact> buscarInativos() {
        return service.buscarInativos();
    }

    @Override
    public List<Contact> buscarPorTipo(String tipo) {
        return service.buscarPorTipo(tipo);
    }

    @Override
    public Contact buscarPrincipal() {
        return service.buscarPrincipal();
    }

    @Override
    public Contact atualizar(Long id, CreateContactRequest request) {
        Contact contact = service.atualizar(id, request);

        if (shouldNotifyOn("UPDATE")) {
            enviarNotificacao(
                    "Contact atualizado",
                    String.format("ID: %d, Novo valor: %s", id, contact.getValor())
            );
        }

        return contact;
    }

    @Override
    public Contact ativar(Long id) {
        return service.ativar(id);
    }

    @Override
    public Contact desativar(Long id) {
        return service.desativar(id);
    }

    @Override
    public void deletar(Long id) {
        service.deletar(id);

        if (shouldNotifyOn("DELETE")) {
            enviarNotificacao(
                    "Contact deletado",
                    String.format("ID: %d", id)
            );
        }
    }

    /**
     * Verifica se deve notificar para este tipo de operação
     */
    private boolean shouldNotifyOn(String operation) {
        // TODO: Implementar lugar para pegar a configuração
//        String config = properties.getNotification().getEmailNotificationOn();
//        return config != null && config.contains(operation);
        return false;
    }

    /**
     * Envia notificação (aqui apenas log, implementar com EmailService)
     */
    private void enviarNotificacao(String assunto, String mensagem) {
//        String destinatario = properties.getNotification().getRecipientEmail();
//        log.info("[NOTIFICATION] Enviando para {}: {} - {}", destinatario, assunto, mensagem);

        // TODO: Implementar envio real de email
        // emailService.enviar(destinatario, assunto, mensagem);
    }
}
