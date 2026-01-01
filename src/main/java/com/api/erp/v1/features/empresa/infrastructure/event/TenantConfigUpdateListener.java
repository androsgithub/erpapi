package com.api.erp.v1.features.empresa.infrastructure.event;

import com.api.erp.v1.features.cliente.infrastructure.factory.ClienteServiceFactory;
import com.api.erp.v1.features.cliente.infrastructure.factory.ClienteValidatorFactory;
import com.api.erp.v1.features.cliente.infrastructure.proxy.ClienteServiceHolder;
import com.api.erp.v1.features.cliente.infrastructure.proxy.ClienteValidatorHolder;
import com.api.erp.v1.features.contato.infrastructure.factory.ContatoServiceFactory;
import com.api.erp.v1.features.contato.infrastructure.proxy.ContatoServiceHolder;
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
public class TenantConfigUpdateListener {

    @EventListener
    @Async
    public void onTenantConfigUpdate(TenantConfigUpdateEvent event) {
        log.info("[TENANT CONFIG LISTENER] Detectada atualização de configuração");

        try {
            recarregarTudo();

            log.info("[TENANT CONFIG LISTENER] Decorators recarregados com sucesso. " +
                    "Nova config: {}", event.getNovaConfiguracao().toString());
        } catch (Exception e) {
            log.error("[TENANT CONFIG LISTENER] Erro ao recarregar decorators. " +
                            "Sistema continuará usando config anterior. Erro: {}",
                    e.getMessage(), e);
        }
    }

    private void recarregarTudo() {
        log.debug("[TENANT CONFIG LISTENER] Tudo: Iniciando reconstrução");
        recarregarCliente();
        recarregarContato();
        log.debug("[TENANT CONFIG LISTENER] Tudo: Reconstrução completa");
    }

    private final ClienteServiceFactory clienteServiceFactory;
    private final ClienteValidatorFactory clienteValidatorFactory;
    private final ClienteServiceHolder clienteServiceHolder;
    private final ClienteValidatorHolder clienteValidatorHolder;

    private void recarregarCliente() {
        log.debug("[TENANT CONFIG LISTENER] Cliente: Iniciando reconstrução de decorators e validators");

        var novoClienteValidator = clienteValidatorFactory.create();
        clienteValidatorHolder.updateValidator(novoClienteValidator);

        var novoClienteService = clienteServiceFactory.create();
        clienteServiceHolder.updateService(novoClienteService);

        log.debug("[TENANT CONFIG LISTENER] Cliente: Reconstrução completa");
    }

    private final ContatoServiceFactory contatoServiceFactory;
    private final ContatoServiceHolder contatoServiceHolder;

    private void recarregarContato() {
        log.debug("[TENANT CONFIG LISTENER] Contato: Iniciando reconstrução de decorators e validators");

        var novoContatoService = contatoServiceFactory.create();
        contatoServiceHolder.updateService(novoContatoService);

        log.debug("[TENANT CONFIG LISTENER] Contato: Reconstrução completa");
    }
}
