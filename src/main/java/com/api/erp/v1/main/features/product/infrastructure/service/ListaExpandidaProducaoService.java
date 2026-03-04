package com.api.erp.v1.main.features.product.infrastructure.service;

import com.api.erp.v1.main.features.product.domain.entity.Product;
import com.api.erp.v1.main.features.product.domain.entity.ProductComposition;
import com.api.erp.v1.main.features.product.domain.exception.ProductException;
import com.api.erp.v1.main.features.product.domain.repository.ProductCompositionRepository;
import com.api.erp.v1.main.features.product.domain.service.IListaExpandidaProducaoService;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListaExpandidaProducaoService implements IListaExpandidaProducaoService {
    
    private final ProductCompositionRepository compositionRepository;
    
    /**
     * Calcula a lista expandida de produção para um product
     * 
     * @param product Product fabricável
     * @param quantidadeRequerida Quantidade do product a fabricar
     * @return Mapa com products necessários e suas quantidades acumuladas
     * @throws ProductException Se o product não for fabricável
     */
    @Override
    public Map<Product, BigDecimal> calcularListaExpandida(Product product, BigDecimal quantidadeRequerida) {
        if (!product.ehFabricavel()) {
            throw ProductException.productNaoFabricavel(product.getId());
        }
        
        if (quantidadeRequerida == null || quantidadeRequerida.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Required quantity must be greater than zero");
        }
        
        Map<Product, BigDecimal> listaExpandida = new HashMap<>();
        Set<Long> processados = new HashSet<>();
        
        expandirComposition(product, quantidadeRequerida, listaExpandida, processados);
        
        return listaExpandida;
    }
    
    /**
     * Expande recursivamente a composição de um product
     * 
     * @param product Product a expandir
     * @param quantidadeRequerida Quantidade necessária do product
     * @param listaExpandida Mapa acumulativo com resultados
     * @param processados Set para evitar processamento infinito
     */
    private void expandirComposition(
            Product product,
            BigDecimal quantidadeRequerida,
            Map<Product, BigDecimal> listaExpandida,
            Set<Long> processados) {
        
        // Proteção contra ciclos infinitos (embora validado no validator)
        if (processados.contains(product.getId())) {
            return;
        }
        
        processados.add(product.getId());
        
        // Obter componentes diretos do product
        List<ProductComposition> componentes = compositionRepository.findByProductFabricadoId(product.getId());
        
        if (componentes.isEmpty()) {
            // Product não tem composição, adicionar como material direto se não for fabricável
            if (product.ehComprado()) {
                listaExpandida.merge(
                    product,
                    quantidadeRequerida,
                    BigDecimal::add
                );
            }
            return;
        }
        
        // Processesr cada componente
        for (ProductComposition composition : componentes) {
            Product componenteProduct = composition.getProductComponente();
            
            // Calcular quantidade total necessária do componente
            BigDecimal quantidadeComponente = composition.getQuantidadeNecessaria()
                    .multiply(quantidadeRequerida);
            
            // Se o componente é fabricável, expandir sua composição
            if (componenteProduct.ehFabricavel()) {
                expandirComposition(componenteProduct, quantidadeComponente, listaExpandida, processados);
            } else {
                // Se é comprado, adicionar diretamente à lista
                listaExpandida.merge(
                    componenteProduct,
                    quantidadeComponente,
                    BigDecimal::add
                );
            }
        }
    }
    
    /**
     * Gets apenas os products comprados necessários
     * (útil para gerar lista de compras)
     */
    @Override
    public Map<Product, BigDecimal> obterListaCompras(Product product, BigDecimal quantidadeRequerida) {
        Map<Product, BigDecimal> listaExpandida = calcularListaExpandida(product, quantidadeRequerida);
        
        return listaExpandida.entrySet().stream()
                .filter(entry -> entry.getKey().ehComprado())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    /**
     * Gets todos os products da composição expandida de forma ordenada
     */
    @Override
    public List<Map.Entry<Product, BigDecimal>> obterListaOrdenada(Product product, BigDecimal quantidadeRequerida) {
        Map<Product, BigDecimal> listaExpandida = calcularListaExpandida(product, quantidadeRequerida);
        
        return listaExpandida.entrySet().stream()
                .sorted((a, b) -> a.getKey().getDescricao().compareTo(b.getKey().getDescricao()))
                .collect(Collectors.toList());
    }
}
