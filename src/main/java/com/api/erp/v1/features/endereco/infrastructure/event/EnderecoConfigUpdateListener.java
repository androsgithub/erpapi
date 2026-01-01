package com.api.erp.v1.features.endereco.infrastructure.event;

import com.api.erp.v1.features.endereco.infrastructure.factory.EnderecoServiceFactory;
import com.api.erp.v1.features.endereco.infrastructure.proxy.EnderecoServiceHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Listener para eventos de atualização de configuração de Endereco.
 * 
 * Monitora mudanças nas configurações da empresa e automaticamente
 * recarrega os decorators do EnderecoService.
 * 
 * FUNCIONAMENTO:
 * 1. Evento: EnderecoConfigUpdateEvent é publicado quando config muda
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
 * - EnderecoConfigUpdateEvent é publicado em EmpresaService.updateEnderecoConfig()
 * - @EnableAsync está ativado na aplicação
 * - EnderecoServiceHolder foi injetado como bean
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EnderecoConfigUpdateListener {

    private final EnderecoServiceFactory factory;
    private final EnderecoServiceHolder holder;

    /**
     * Processa evento de atualização de configuração de endereco.
     * 
     * @param event Evento contendo a nova configuração
     * 
     * Este método é executado assincronamente para não bloquear
     * a transação que disparou o evento.
     */
    @EventListener
    @Async
    public void onEnderecoConfigUpdate(EnderecoConfigUpdateEvent event) {
        log.info("[ENDERECO CONFIG LISTENER] Detectada atualização de configuração");
        
        try {
            // Reconstrói o serviço com novos decorators
            recarregarDecorators();
            
            log.info("[ENDERECO CONFIG LISTENER] Decorators recarregados com sucesso. " +
                    "Nova config: {}", event.getNovaConfiguracao());
        } catch (Exception e) {
            log.error("[ENDERECO CONFIG LISTENER] Erro ao recarregar decorators. " +
                    "Sistema continuará usando config anterior. Erro: {}", 
                    e.getMessage(), e);
            // Não relança exceção para não afetar transação que disparou evento
        }
    }

    /**
     * Reconstrói e atualiza o EnderecoService.
     * 
     * Este método sincroniza com o holder através de WriteLock
     * garantindo que transações em andamento terminem antes da mudança.
     */
    private void recarregarDecorators() {
        log.debug("[ENDERECO CONFIG LISTENER] Iniciando reconstrução de decorators");
        
        // Factory cria nova instância com config atual
        var novoService = factory.create();
        
        // Holder atualiza com WriteLock - espera leituras finalizarem
        holder.updateService(novoService);
        
        log.debug("[ENDERECO CONFIG LISTENER] Reconstrução completa");
    }
}
