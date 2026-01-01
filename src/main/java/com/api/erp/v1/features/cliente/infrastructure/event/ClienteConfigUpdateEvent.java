package com.api.erp.v1.features.cliente.infrastructure.event;

import com.api.erp.v1.features.empresa.domain.entity.ClienteConfig;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Evento de negócio disparado quando configurações de Cliente mudam.
 * 
 * Este evento é publicado pelo EmpresaService.updateClienteConfig()
 * e escutado por ClienteConfigUpdateListener para recarregar decorators.
 * 
 * USO:
 * 1. EmpresaService.updateClienteConfig() atualiza config no DB
 * 2. Publica ClienteConfigUpdateEvent
 * 3. ClienteConfigUpdateListener escuta e recarrega decorators
 * 4. Próximas requisições usam nova config
 * 
 * PADRÃO EVENT-DRIVEN:
 * - Desacoplamento entre feature Empresa e feature Cliente
 * - Permite múltiplos listeners se necessário
 * - Assincronismo evita bloquear transação
 */
@Getter
public class ClienteConfigUpdateEvent extends ApplicationEvent {

    private final ClienteConfig novaConfiguracao;
    private final Long empresaId;
    private final String usuario;

    /**
     * @param source Objeto que disparou o evento (normalmente EmpresaService)
     * @param novaConfiguracao Nova configuração de cliente
     * @param empresaId ID da empresa que teve config alterada
     * @param usuario Usuário que fez a alteração (para auditoria)
     */
    public ClienteConfigUpdateEvent(
            Object source,
            ClienteConfig novaConfiguracao,
            Long empresaId,
            String usuario) {
        super(source);
        this.novaConfiguracao = novaConfiguracao;
        this.empresaId = empresaId;
        this.usuario = usuario;
    }
}
