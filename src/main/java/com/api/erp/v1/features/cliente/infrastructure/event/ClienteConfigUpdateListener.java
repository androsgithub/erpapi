package com.api.erp.v1.features.cliente.infrastructure.event;

import com.api.erp.v1.features.cliente.infrastructure.factory.ClienteServiceFactory;
import com.api.erp.v1.features.cliente.infrastructure.factory.ClienteValidatorFactory;
import com.api.erp.v1.features.cliente.infrastructure.proxy.ClienteServiceHolder;
import com.api.erp.v1.features.cliente.infrastructure.proxy.ClienteValidatorHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Listener para eventos de atualização de configuração de Cliente.
 * <p>
 * Monitora mudanças nas configurações da empresa e automaticamente
 * recarrega os decorators do ClienteService.
 * <p>
 * FUNCIONAMENTO:
 * 1. Evento: ClienteConfigUpdateEvent é publicado quando config muda
 * 2. Este listener é notificado (assincronamente)
 * 3. Reconstrói a chain de decorators
 * 4. Atualiza o holder com a nova instância
 * 5. Próximas chamadas já usam nova config
 * <p>
 * THREAD-SAFETY:
 * - Usa @Async para não bloquear a transação de update
 * - Holder sincroniza o update com ReadWriteLock
 * - Transações em andamento terminam antes da mudança
 * <p>
 * SEM DOWNTIME:
 * - Não interrompe requisições em andamento
 * - Não requer reinicialização da aplicação
 * - Mudanças levam efeito na próxima requisição
 * <p>
 * PRINCÍPIO SOLID - Open/Closed:
 * - Fechado para modificação (não altera serviço)
 * - Aberto para extensão (novos decorators/events)
 * <p>
 * NOTA: Para funcionar, certifique-se que:
 * - ClienteConfigUpdateEvent é publicado em EmpresaService.updateClienteConfig()
 * - @EnableAsync está ativado na aplicação
 * - ClienteServiceHolder foi injetado como bean
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteConfigUpdateListener {

    private final ClienteServiceFactory serviceFactory;
    private final ClienteValidatorFactory validatorFactory;
    private final ClienteServiceHolder serviceHolder;
    private final ClienteValidatorHolder validatorHolder;


    @EventListener
    @Async
    public void onClienteConfigUpdate(ClienteConfigUpdateEvent event) {
        log.info("[CLIENTE CONFIG LISTENER] Detectada atualização de configuração");

        try {
            // Reconstrói o serviço com novos decorators
            recarregarDecorators();

            log.info("[CLIENTE CONFIG LISTENER] Decorators recarregados com sucesso. " +
                    "Nova config: {}", event.getNovaConfiguracao());
        } catch (Exception e) {
            log.error("[CLIENTE CONFIG LISTENER] Erro ao recarregar decorators. " +
                            "Sistema continuará usando config anterior. Erro: {}",
                    e.getMessage(), e);
            // Não relança exceção para não afetar transação que disparou evento
        }
    }

    /**
     * Reconstrói e atualiza o ClienteService.
     * <p>
     * Este método sincroniza com o holder através de WriteLock
     * garantindo que transações em andamento terminem antes da mudança.
     */
    private void recarregarDecorators() {
        log.debug("[CLIENTE CONFIG LISTENER] Iniciando reconstrução de decorators");

        var novoValidator = validatorFactory.create();
        validatorHolder.updateValidator(novoValidator);

        // Factory cria nova instância com config atual
        var novoService = serviceFactory.create();

        // Holder atualiza com WriteLock - espera leituras finalizarem
        serviceHolder.updateService(novoService);

        log.debug("[CLIENTE CONFIG LISTENER] Reconstrução completa");
    }
}
