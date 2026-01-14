package com.api.erp.v1.features.permissao.infrastructure.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Listener para eventos de atualização de configuração de Permissao.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissaoConfigUpdateListener {

    private final PermissaoServiceFactory factory;
    private final PermissaoServiceHolder holder;

    @EventListener
    @Async
    public void onPermissaoConfigUpdate(PermissaoConfigUpdateEvent event) {
        log.info("[PERMISSAO CONFIG LISTENER] Detectada atualização de configuração");
        
        try {
            recarregarDecorators();
            
            log.info("[PERMISSAO CONFIG LISTENER] Decorators recarregados com sucesso. " +
                    "Nova config: {}", event.getNovaConfiguracao());
        } catch (Exception e) {
            log.error("[PERMISSAO CONFIG LISTENER] Erro ao recarregar decorators. " +
                    "Sistema continuará usando config anterior. Erro: {}", 
                    e.getMessage(), e);
        }
    }

    private void recarregarDecorators() {
        log.debug("[PERMISSAO CONFIG LISTENER] Iniciando reconstrução de decorators");
        
        var novoService = factory.create();
        holder.updateService(novoService);
        
        log.debug("[PERMISSAO CONFIG LISTENER] Reconstrução completa");
    }
}
