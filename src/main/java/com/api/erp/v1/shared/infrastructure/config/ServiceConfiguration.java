package com.api.erp.v1.shared.infrastructure.config;

import com.api.erp.v1.features.permissao.domain.service.IGerenciamentoPermissaoService;
import com.api.erp.v1.features.permissao.infrastructure.config.PermissaoServiceConfiguration;
import com.api.erp.v1.features.permissao.infrastructure.factory.GerenciamentoPermissaoServiceFactory;
import com.api.erp.v1.features.unidademedida.domain.service.IUnidadeMedidaService;
import com.api.erp.v1.features.unidademedida.infrastructure.factory.UnidadeMedidaServiceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PermissaoServiceConfiguration.class})
public class ServiceConfiguration {

    @Bean
    public IUnidadeMedidaService unidadeMedidaService(
            UnidadeMedidaServiceFactory factory) {
        return factory.create();
    }

    @Bean
    public IGerenciamentoPermissaoService gerenciamentoPermissaoService(
            GerenciamentoPermissaoServiceFactory factory) {
        return factory.create();
    }
}

