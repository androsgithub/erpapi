package com.api.erp.v1.features.contato.infrastructure.event;

import com.api.erp.v1.features.empresa.domain.entity.ContatoConfig;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Evento de negócio disparado quando configurações de Contato mudam.
 * 
 * Este evento é publicado pelo EmpresaService.updateContatoConfig()
 * e escutado por ContatoConfigUpdateListener para recarregar decorators.
 * 
 * USO:
 * 1. EmpresaService.updateContatoConfig() atualiza config no DB
 * 2. Publica ContatoConfigUpdateEvent
 * 3. ContatoConfigUpdateListener escuta e recarrega decorators
 * 4. Próximas requisições usam nova config
 * 
 * PADRÃO EVENT-DRIVEN:
 * - Desacoplamento entre feature Empresa e feature Contato
 * - Permite múltiplos listeners se necessário
 * - Assincronismo evita bloquear transação
 */
@Getter
public class ContatoConfigUpdateEvent extends ApplicationEvent {

    private final ContatoConfig novaConfiguracao;
    private final Long empresaId;
    private final String usuario;

    /**
     * @param source Objeto que disparou o evento (normalmente EmpresaService)
     * @param novaConfiguracao Nova configuração de contato
     * @param empresaId ID da empresa que teve config alterada
     * @param usuario Usuário que fez a alteração (para auditoria)
     */
    public ContatoConfigUpdateEvent(
            Object source,
            ContatoConfig novaConfiguracao,
            Long empresaId,
            String usuario) {
        super(source);
        this.novaConfiguracao = novaConfiguracao;
        this.empresaId = empresaId;
        this.usuario = usuario;
    }
}
