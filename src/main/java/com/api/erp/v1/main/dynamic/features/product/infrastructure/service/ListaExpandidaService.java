package com.api.erp.v1.main.dynamic.features.product.infrastructure.service;

import com.api.erp.v1.main.dynamic.features.product.application.dto.ItemListaExpandidaDTO;
import com.api.erp.v1.main.dynamic.features.product.application.dto.ListaExpandidaResponseDTO;
import com.api.erp.v1.main.dynamic.features.product.domain.entity.Product;
import com.api.erp.v1.main.dynamic.features.product.domain.exception.ProductException;
import com.api.erp.v1.main.dynamic.features.product.domain.repository.ProductRepository;
import com.api.erp.v1.main.dynamic.features.product.domain.service.IListaExpandidaProducaoService;
import com.api.erp.v1.main.dynamic.features.product.domain.service.IListaExpandidaService;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;
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
    private ProductRepository productRepository;
    @Autowired
    @Qualifier("listaExpandidaProducaoServiceProxy")
    private IListaExpandidaProducaoService domainService;
    
    /**
     * Gera a lista expandida de produção para um product
     */
    public ListaExpandidaResponseDTO gerarListaExpandida(Long productId, BigDecimal quantidade) {
        Product product = obterProduct(productId);
        
        if (!product.ehFabricavel()) {
            throw ProductException.productNaoFabricavel(productId);
        }
        
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Quantity must be greater than zero");
        }
        
        // Calculate expanded list using domain service
        Map<Product, BigDecimal> listaExpandida = domainService.calcularListaExpandida(product, quantidade);
        
        // Converter para DTOs
        List<ItemListaExpandidaDTO> itens = listaExpandida.entrySet().stream()
                .map(entry -> converterParaItemDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> a.getDescricaoProduct().compareTo(b.getDescricaoProduct()))
                .collect(Collectors.toList());
        
        return ListaExpandidaResponseDTO.builder()
                .productId(product.getId())
                .codigoProduct(product.getCodigo())
                .descricaoProduct(product.getDescricao())
                .quantidadeRequerida(quantidade)
                .measureUnitProduct(product.getMeasureUnit().getSigla())
                .itens(itens)
                .totalItens(itens.size())
                .build();
    }
    
    /**
     * Gera a lista de compras (apenas products comprados) para um product
     */
    public ListaExpandidaResponseDTO gerarListaCompras(Long productId, BigDecimal quantidade) {
        Product product = obterProduct(productId);
        
        if (!product.ehFabricavel()) {
            throw ProductException.productNaoFabricavel(productId);
        }
        
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Quantity must be greater than zero");
        }
        
        // Obtain shopping list from domain service
        Map<Product, BigDecimal> listaCompras = domainService.obterListaCompras(product, quantidade);
        
        // Converter para DTOs
        List<ItemListaExpandidaDTO> itens = listaCompras.entrySet().stream()
                .map(entry -> converterParaItemDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> a.getDescricaoProduct().compareTo(b.getDescricaoProduct()))
                .collect(Collectors.toList());
        
        return ListaExpandidaResponseDTO.builder()
                .productId(product.getId())
                .codigoProduct(product.getCodigo())
                .descricaoProduct(product.getDescricao())
                .quantidadeRequerida(quantidade)
                .measureUnitProduct(product.getMeasureUnit().getSigla())
                .itens(itens)
                .totalItens(itens.size())
                .build();
    }
    
    /**
     * Gets product por ID ou lança exceção
     */
    private Product obterProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> ProductException.productNaoEncontrado(id));
    }
    
    /**
     * Converte product e quantidade para DTO de item da lista
     */
    private ItemListaExpandidaDTO converterParaItemDTO(Product product, BigDecimal quantidade) {
        return ItemListaExpandidaDTO.builder()
                .productId(product.getId())
                .codigoProduct(product.getCodigo())
                .descricaoProduct(product.getDescricao())
                .tipoProduct(product.getType().getDescricao())
                .measureUnit(product.getMeasureUnit().getSigla())
                .quantidadeNecessaria(quantidade)
                .build();
    }
}
