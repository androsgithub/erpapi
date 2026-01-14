package com.api.erp.v1.features.permissao.infrastructure.factory;

import com.api.erp.v1.features.tenant.domain.entity.PermissaoConfig;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Evento publicado quando a configuração de Permissao é atualizada.
 * 
 * Este evento é publicado pelo TenantService.updatePermissaoConfig()
 * e capturado pelo PermissaoConfigUpdateListener para recarregar os decorators.
 */
@Getter
public class PermissaoConfigUpdateEvent extends ApplicationEvent {

    private final PermissaoConfig novaConfiguracao;
    private final Long empresaId;
    private final String usuario;

    public PermissaoConfigUpdateEvent(
            Object source,
            PermissaoConfig novaConfiguracao,
            Long empresaId,
            String usuario) {
        super(source);
        this.novaConfiguracao = novaConfiguracao;
        this.empresaId = empresaId;
        this.usuario = usuario;
    }
}
