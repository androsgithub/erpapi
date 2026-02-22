package com.api.erp.v1.main.features.permission.infrastructure.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Listener para eventos de atualização de configuração de Permission.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionConfigUpdateListener {

    private final PermissionServiceFactory factory;
    private final PermissionServiceHolder holder;

    @EventListener
    @Async
    public void onPermissionConfigUpdate(PermissionConfigUpdateEvent event) {
        log.info("[PERMISSION CONFIG LISTENER] Permission configuration update detected");
        
        try {
            recarregarDecorators();
            
            log.info("[PERMISSION CONFIG LISTENER] Decorators recarregados com sucesso. " +
                    "Nova config: {}", event.getNovaConfiguracao());
        } catch (Exception e) {
            log.error("[PERMISSION CONFIG LISTENER] Erro ao recarregar decorators. " +
                    "Sistema continuará usando config anterior. Erro: {}", 
                    e.getMessage(), e);
        }
    }

    private void recarregarDecorators() {
        log.debug("[PERMISSION CONFIG LISTENER] Starting decorator reconstruction");
        
        var novoService = factory.create();
        holder.updateService(novoService);
        
        log.debug("[PERMISSION CONFIG LISTENER] Reconstruction complete");
    }
}
