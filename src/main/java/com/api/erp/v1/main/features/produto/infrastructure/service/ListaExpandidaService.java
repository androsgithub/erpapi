package com.api.erp.v1.main.features.produto.infrastructure.service;

import com.api.erp.v1.main.features.produto.application.dto.ItemListaExpandidaDTO;
import com.api.erp.v1.main.features.produto.application.dto.ListaExpandidaResponseDTO;
import com.api.erp.v1.main.features.produto.domain.entity.Produto;
import com.api.erp.v1.main.features.produto.domain.exception.ProdutoException;
import com.api.erp.v1.main.features.produto.domain.repository.ProdutoRepository;
import com.api.erp.v1.main.features.produto.domain.service.IListaExpandidaProducaoService;
import com.api.erp.v1.main.features.produto.domain.service.IListaExpandidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ListaExpandidaService implements IListaExpandidaService {
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    @Qualifier("listaExpandidaProducaoServiceProxy")
    private IListaExpandidaProducaoService domainService;
    
    /**
     * Gera a lista expandida de produção para um produto
     */
    public ListaExpandidaResponseDTO gerarListaExpandida(Long produtoId, BigDecimal quantidade) {
        Produto produto = obterProduto(produtoId);
        
        if (!produto.ehFabricavel()) {
            throw ProdutoException.produtoNaoFabricavel(produtoId);
        }
        
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        
        // Calcular lista expandida usando serviço de domínio
        Map<Produto, BigDecimal> listaExpandida = domainService.calcularListaExpandida(produto, quantidade);
        
        // Converter para DTOs
        List<ItemListaExpandidaDTO> itens = listaExpandida.entrySet().stream()
                .map(entry -> converterParaItemDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> a.getDescricaoProduto().compareTo(b.getDescricaoProduto()))
                .collect(Collectors.toList());
        
        return ListaExpandidaResponseDTO.builder()
                .produtoId(produto.getId())
                .codigoProduto(produto.getCodigo())
                .descricaoProduto(produto.getDescricao())
                .quantidadeRequerida(quantidade)
                .unidadeMedidaProduto(produto.getUnidadeMedida().getSigla())
                .itens(itens)
                .totalItens(itens.size())
                .build();
    }
    
    /**
     * Gera a lista de compras (apenas produtos comprados) para um produto
     */
    public ListaExpandidaResponseDTO gerarListaCompras(Long produtoId, BigDecimal quantidade) {
        Produto produto = obterProduto(produtoId);
        
        if (!produto.ehFabricavel()) {
            throw ProdutoException.produtoNaoFabricavel(produtoId);
        }
        
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        
        // Calcular lista de compras usando serviço de domínio
        Map<Produto, BigDecimal> listaCompras = domainService.obterListaCompras(produto, quantidade);
        
        // Converter para DTOs
        List<ItemListaExpandidaDTO> itens = listaCompras.entrySet().stream()
                .map(entry -> converterParaItemDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> a.getDescricaoProduto().compareTo(b.getDescricaoProduto()))
                .collect(Collectors.toList());
        
        return ListaExpandidaResponseDTO.builder()
                .produtoId(produto.getId())
                .codigoProduto(produto.getCodigo())
                .descricaoProduto(produto.getDescricao())
                .quantidadeRequerida(quantidade)
                .unidadeMedidaProduto(produto.getUnidadeMedida().getSigla())
                .itens(itens)
                .totalItens(itens.size())
                .build();
    }
    
    /**
     * Obtém produto por ID ou lança exceção
     */
    private Produto obterProduto(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> ProdutoException.produtoNaoEncontrado(id));
    }
    
    /**
     * Converte produto e quantidade para DTO de item da lista
     */
    private ItemListaExpandidaDTO converterParaItemDTO(Produto produto, BigDecimal quantidade) {
        return ItemListaExpandidaDTO.builder()
                .produtoId(produto.getId())
                .codigoProduto(produto.getCodigo())
                .descricaoProduto(produto.getDescricao())
                .tipoProduto(produto.getTipo().getDescricao())
                .unidadeMedida(produto.getUnidadeMedida().getSigla())
                .quantidadeNecessaria(quantidade)
                .build();
    }
}
