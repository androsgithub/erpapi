package com.api.erp.v1.features.produto.infrastructure.factory;

import com.api.erp.v1.features.empresa.domain.entity.ProdutoConfig;
import com.api.erp.v1.features.empresa.domain.service.IEmpresaService;
import com.api.erp.v1.features.produto.domain.repository.ProdutoComposicaoRepository;
import com.api.erp.v1.features.produto.domain.service.IListaExpandidaProducaoService;
import com.api.erp.v1.features.produto.infrastructure.service.ListaExpandidaProducaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class ListaExpandidaProducaoServiceFactory {
    private final IEmpresaService empresaService;
    private final ProdutoComposicaoRepository produtoComposicaoRepository;

    public ListaExpandidaProducaoServiceFactory(IEmpresaService empresaService, ProdutoComposicaoRepository produtoComposicaoRepository) {
        this.empresaService = empresaService;
        this.produtoComposicaoRepository = produtoComposicaoRepository;
    }


    public IListaExpandidaProducaoService create() {
        ProdutoConfig produtoConfig = empresaService.getEmpresaConfig().getProdutoConfig();

        IListaExpandidaProducaoService service = new ListaExpandidaProducaoService(produtoComposicaoRepository);
        return service;
    }
}
