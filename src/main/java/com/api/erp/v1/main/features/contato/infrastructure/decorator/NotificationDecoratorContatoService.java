package com.api.erp.v1.main.features.contato.infrastructure.decorator;

import com.api.erp.v1.main.features.contato.application.dto.CreateContatoRequest;
import com.api.erp.v1.main.features.contato.domain.entity.Contato;
import com.api.erp.v1.main.features.contato.domain.service.IContatoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Decorator de Notificação - Exemplo Customizado
 * <p>
 * Envia notificações (via email) quando contatos são criados, atualizados ou deletados.
 * Este decorator pode ser adicionado à factory quando habilitado na configuração.
 * <p>
 * Configuração:
 * contato.decorators.notification.enabled=true
 * contato.decorators.notification.recipient-email=admin@empresa.com
 * <p>
 * Para usar, adicione à ContatoServiceDecoratorFactory:
 * if (properties.getNotification().isEnabled()) {
 * service = new NotificationDecoratorContatoService(service, emailService, properties);
 * }
 */
@Slf4j
@RequiredArgsConstructor
public class NotificationDecoratorContatoService implements IContatoService {

    private final IContatoService service;


    @Override
    public Contato criar(CreateContatoRequest request) {
        Contato contato = service.criar(request);

        if (shouldNotifyOn("CREATE")) {
            enviarNotificacao(
                    "Novo contato criado",
                    String.format("Tipo: %s, Valor: %s", contato.getTipo(), contato.getValor())
            );
        }

        return contato;
    }

    @Override
    public Contato buscarPorId(Long id) {
        return service.buscarPorId(id);
    }

    @Override
    public List<Contato> buscarTodos() {
        return service.buscarTodos();
    }

    @Override
    public List<Contato> buscarAtivos() {
        return service.buscarAtivos();
    }

    @Override
    public List<Contato> buscarInativos() {
        return service.buscarInativos();
    }

    @Override
    public List<Contato> buscarPorTipo(String tipo) {
        return service.buscarPorTipo(tipo);
    }

    @Override
    public Contato buscarPrincipal() {
        return service.buscarPrincipal();
    }

    @Override
    public Contato atualizar(Long id, CreateContatoRequest request) {
        Contato contato = service.atualizar(id, request);

        if (shouldNotifyOn("UPDATE")) {
            enviarNotificacao(
                    "Contato atualizado",
                    String.format("ID: %d, Novo valor: %s", id, contato.getValor())
            );
        }

        return contato;
    }

    @Override
    public Contato ativar(Long id) {
        return service.ativar(id);
    }

    @Override
    public Contato desativar(Long id) {
        return service.desativar(id);
    }

    @Override
    public void deletar(Long id) {
        service.deletar(id);

        if (shouldNotifyOn("DELETE")) {
            enviarNotificacao(
                    "Contato deletado",
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
