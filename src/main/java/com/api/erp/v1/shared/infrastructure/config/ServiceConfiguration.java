package com.api.erp.v1.shared.infrastructure.config;

import com.api.erp.v1.features.cliente.infrastructure.config.ClienteServiceConfiguration;
import com.api.erp.v1.features.contato.domain.service.IGerenciamentoContatoService;
import com.api.erp.v1.features.contato.infrastructure.config.ContatoServiceConfiguration;
import com.api.erp.v1.features.contato.infrastructure.factory.GerenciamentoContatoServiceFactory;
import com.api.erp.v1.features.endereco.infrastructure.config.EnderecoServiceConfiguration;
import com.api.erp.v1.features.permissao.domain.service.IGerenciamentoPermissaoService;
import com.api.erp.v1.features.permissao.infrastructure.config.PermissaoServiceConfiguration;
import com.api.erp.v1.features.permissao.infrastructure.factory.GerenciamentoPermissaoServiceFactory;
import com.api.erp.v1.features.produto.domain.service.IListaExpandidaProducaoService;
import com.api.erp.v1.features.produto.domain.service.IListaExpandidaService;
import com.api.erp.v1.features.produto.domain.service.IProdutoService;
import com.api.erp.v1.features.produto.infrastructure.factory.ListaExpandidaProducaoServiceFactory;
import com.api.erp.v1.features.produto.infrastructure.factory.ListaExpandidaServiceFactory;
import com.api.erp.v1.features.produto.infrastructure.factory.ProdutoServiceFactory;
import com.api.erp.v1.features.unidademedida.domain.service.IUnidadeMedidaService;
import com.api.erp.v1.features.unidademedida.infrastructure.factory.UnidadeMedidaServiceFactory;
import com.api.erp.v1.features.usuario.domain.service.IUsuarioService;
import com.api.erp.v1.features.usuario.infrastructure.factory.UsuarioServiceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ClienteServiceConfiguration.class, ContatoServiceConfiguration.class, EnderecoServiceConfiguration.class, PermissaoServiceConfiguration.class})
public class ServiceConfiguration {

    // ⚠️ NOTA: clienteService foi movido para ClienteServiceConfiguration.java
    // ⚠️ NOTA: contatoService foi movido para ContatoServiceConfiguration.java
    // ⚠️ NOTA: enderecoService foi movido para EnderecoServiceConfiguration.java
    // Todas importadas via @Import para garantir carregamento

    @Bean
    public IUsuarioService usuarioService(
            UsuarioServiceFactory factory) {
        return factory.create();
    }

    @Bean
    public IUnidadeMedidaService unidadeMedidaService(
            UnidadeMedidaServiceFactory factory) {
        return factory.create();
    }

    @Bean
    public IProdutoService produtoService(
            ProdutoServiceFactory factory) {
        return factory.create();
    }

    @Bean
    public IListaExpandidaService listaExpandidaService(
            ListaExpandidaServiceFactory factory) {
        return factory.create();
    }

    @Bean
    public IListaExpandidaProducaoService listaExpandidaProducaoService(
            ListaExpandidaProducaoServiceFactory factory) {
        return factory.create();
    }

    @Bean
    public IGerenciamentoPermissaoService gerenciamentoPermissaoService(
            GerenciamentoPermissaoServiceFactory factory) {
        return factory.create();
    }

    @Bean
    public IGerenciamentoContatoService gerenciamentoContatoService(GerenciamentoContatoServiceFactory factory) {
        return factory.create();
    }
}

