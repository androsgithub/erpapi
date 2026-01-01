package com.api.erp.v1.features.produto.infrastructure.factory;

import com.api.erp.v1.features.empresa.domain.entity.ProdutoConfig;
import com.api.erp.v1.features.empresa.domain.service.IEmpresaService;
import com.api.erp.v1.features.produto.domain.repository.ProdutoRepository;
import com.api.erp.v1.features.produto.domain.service.IListaExpandidaProducaoService;
import com.api.erp.v1.features.produto.domain.service.IListaExpandidaService;
import com.api.erp.v1.features.produto.infrastructure.service.ListaExpandidaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class ListaExpandidaServiceFactory {
    private final IEmpresaService empresaService;
    private final ProdutoRepository produtoRepository;
    private final IListaExpandidaProducaoService listaExpandidaProducaoService;

    public ListaExpandidaServiceFactory(IEmpresaService empresaService, ProdutoRepository produtoRepository, IListaExpandidaProducaoService listaExpandidaProducaoService) {
        this.empresaService = empresaService;
        this.produtoRepository = produtoRepository;
        this.listaExpandidaProducaoService = listaExpandidaProducaoService;
    }

    public IListaExpandidaService create() {
        ProdutoConfig produtoConfig = empresaService.getEmpresaConfig().getProdutoConfig();

        IListaExpandidaService service = new ListaExpandidaService(produtoRepository, listaExpandidaProducaoService);
        return service;
    }
}
