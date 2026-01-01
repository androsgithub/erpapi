package com.api.erp.v1.features.contato.infrastructure.event;

import com.api.erp.v1.features.contato.infrastructure.factory.ContatoServiceFactory;
import com.api.erp.v1.features.contato.infrastructure.proxy.ContatoServiceHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Listener para eventos de atualização de configuração de Contato.
 * 
 * Monitora mudanças nas configurações da empresa e automaticamente
 * recarrega os decorators do ContatoService.
 * 
 * FUNCIONAMENTO:
 * 1. Evento: ContatoConfigUpdateEvent é publicado quando config muda
 * 2. Este listener é notificado (assincronamente)
 * 3. Reconstrói a chain de decorators
 * 4. Atualiza o holder com a nova instância
 * 5. Próximas chamadas já usam nova config
 * 
 * THREAD-SAFETY:
 * - Usa @Async para não bloquear a transação de update
 * - Holder sincroniza o update com ReadWriteLock
 * - Transações em andamento terminam antes da mudança
 * 
 * SEM DOWNTIME:
 * - Não interrompe requisições em andamento
 * - Não requer reinicialização da aplicação
 * - Mudanças levam efeito na próxima requisição
 * 
 * PRINCÍPIO SOLID - Open/Closed:
 * - Fechado para modificação (não altera serviço)
 * - Aberto para extensão (novos decorators/events)
 * 
 * NOTA: Para funcionar, certifique-se que:
 * - ContatoConfigUpdateEvent é publicado em EmpresaService.updateContatoConfig()
 * - @EnableAsync está ativado na aplicação
 * - ContatoServiceHolder foi injetado como bean
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContatoConfigUpdateListener {

    private final ContatoServiceFactory factory;
    private final ContatoServiceHolder holder;

    /**
     * Processa evento de atualização de configuração de contato.
     * 
     * @param event Evento contendo a nova configuração
     * 
     * Este método é executado assincronamente para não bloquear
     * a transação que disparou o evento.
     */
    @EventListener
    @Async
    public void onContatoConfigUpdate(ContatoConfigUpdateEvent event) {
        log.info("[CONTATO CONFIG LISTENER] Detectada atualização de configuração");
        
        try {
            // Reconstrói o serviço com novos decorators
            recarregarDecorators();
            
            log.info("[CONTATO CONFIG LISTENER] Decorators recarregados com sucesso. " +
                    "Nova config: {}", event.getNovaConfiguracao());
        } catch (Exception e) {
            log.error("[CONTATO CONFIG LISTENER] Erro ao recarregar decorators. " +
                    "Sistema continuará usando config anterior. Erro: {}", 
                    e.getMessage(), e);
            // Não relança exceção para não afetar transação que disparou evento
        }
    }

    /**
     * Reconstrói e atualiza o ContatoService.
     * 
     * Este método sincroniza com o holder através de WriteLock
     * garantindo que transações em andamento terminem antes da mudança.
     */
    private void recarregarDecorators() {
        log.debug("[CONTATO CONFIG LISTENER] Iniciando reconstrução de decorators");
        
        // Factory cria nova instância com config atual
        var novoService = factory.create();
        
        // Holder atualiza com WriteLock - espera leituras finalizarem
        holder.updateService(novoService);
        
        log.debug("[CONTATO CONFIG LISTENER] Reconstrução completa");
    }
}
