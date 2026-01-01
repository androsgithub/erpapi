package com.api.erp.v1.features.empresa.infrastructure.event;

import com.api.erp.v1.features.empresa.domain.entity.TenantConfig;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Evento de negócio disparado quando configurações de Cliente mudam.
 * <p>
 * Este evento é publicado pelo EmpresaService.updateClienteConfig()
 * e escutado por ClienteConfigUpdateListener para recarregar decorators.
 * <p>
 * USO:
 * 1. EmpresaService.updateClienteConfig() atualiza config no DB
 * 2. Publica ClienteConfigUpdateEvent
 * 3. ClienteConfigUpdateListener escuta e recarrega decorators
 * 4. Próximas requisições usam nova config
 * <p>
 * PADRÃO EVENT-DRIVEN:
 * - Desacoplamento entre feature Empresa e feature Cliente
 * - Permite múltiplos listeners se necessário
 * - Assincronismo evita bloquear transação
 */
@Getter
public class TenantConfigUpdateEvent extends ApplicationEvent {

    private final TenantConfig novaConfiguracao;
    private final Long empresaId;
    private final String usuario;

    /**
     * @param source           Objeto que disparou o evento (normalmente EmpresaService)
     * @param novaConfiguracao Nova configuração de tenant
     * @param empresaId        ID da empresa que teve config alterada
     * @param usuario          Usuário que fez a alteração (para auditoria)
     */
    public TenantConfigUpdateEvent(
            Object source,
            TenantConfig novaConfiguracao,
            Long empresaId,
            String usuario) {
        super(source);
        this.novaConfiguracao = novaConfiguracao;
        this.empresaId = empresaId;
        this.usuario = usuario;
    }
}
