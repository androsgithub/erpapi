package com.api.erp.v1.features.produto.infrastructure.factory;

import com.api.erp.v1.features.empresa.domain.entity.ProdutoConfig;
import com.api.erp.v1.features.empresa.domain.service.IEmpresaService;
import com.api.erp.v1.features.produto.domain.repository.ProdutoRepository;
import com.api.erp.v1.features.produto.domain.service.IProdutoService;
import com.api.erp.v1.features.produto.domain.validator.ProdutoValidator;
import com.api.erp.v1.features.produto.infrastructure.service.ProdutoService;
import com.api.erp.v1.features.unidademedida.domain.repository.UnidadeMedidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class ProdutoServiceFactory {
    private final IEmpresaService empresaService;
    private final ProdutoRepository produtoRepository;
    private final ProdutoValidator produtoValidator;
    private final UnidadeMedidaRepository unidadeMedidaRepository;

    public ProdutoServiceFactory(IEmpresaService empresaService, ProdutoRepository produtoRepository, ProdutoValidator produtoValidator, UnidadeMedidaRepository unidadeMedidaRepository) {
        this.empresaService = empresaService;
        this.produtoRepository = produtoRepository;
        this.produtoValidator = produtoValidator;
        this.unidadeMedidaRepository = unidadeMedidaRepository;
    }

    public IProdutoService create() {
        ProdutoConfig produtoConfig = empresaService.getEmpresaConfig().getProdutoConfig();

        IProdutoService service = new ProdutoService(produtoRepository, unidadeMedidaRepository, produtoValidator);
        return service;
    }
}
