package com.api.erp.v1.main.features.permission.infrastructure.factory;

import com.api.erp.v1.main.tenant.domain.entity.configs.PermissionConfig;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Evento publicado quando a configuração de Permission é atualizada.
 * 
 * Este evento é publicado pelo TenantService.updatePermissionConfig()
 * e capturado pelo PermissionConfigUpdateListener para recarregar os decorators.
 */
@Getter
public class PermissionConfigUpdateEvent extends ApplicationEvent {

    private final PermissionConfig novaConfiguracao;
    private final Long empresaId;
    private final String user;

    public PermissionConfigUpdateEvent(
            Object source,
            PermissionConfig novaConfiguracao,
            Long empresaId,
            String user) {
        super(source);
        this.novaConfiguracao = novaConfiguracao;
        this.empresaId = empresaId;
        this.user = user;
    }
}
