package com.api.erp.v1.features.endereco.infrastructure.event;

import com.api.erp.v1.features.empresa.domain.entity.EnderecoConfig;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Evento publicado quando a configuração de Endereco é atualizada.
 * 
 * Este evento é publicado pelo EmpresaService.updateEnderecoConfig()
 * e capturado pelo EnderecoConfigUpdateListener para recarregar os decorators.
 * 
 * FLUXO:
 * 1. EmpresaService.updateEnderecoConfig() atualiza config no DB
 * 2. EmpresaService publica EnderecoConfigUpdateEvent
 * 3. EnderecoConfigUpdateListener escuta o evento (async)
 * 4. Reconstrói EnderecoService com novos decorators
 * 5. EnderecoServiceHolder.updateService() atualiza referência
 * 6. Próximas chamadas usam nova config
 */
@Getter
public class EnderecoConfigUpdateEvent extends ApplicationEvent {

    private final EnderecoConfig novaConfiguracao;
    private final Long empresaId;
    private final String usuario;

    public EnderecoConfigUpdateEvent(
            Object source,
            EnderecoConfig novaConfiguracao,
            Long empresaId,
            String usuario) {
        super(source);
        this.novaConfiguracao = novaConfiguracao;
        this.empresaId = empresaId;
        this.usuario = usuario;
    }
}
