package com.api.erp.v1.features.produto.infrastructure.service;

import com.api.erp.v1.features.produto.domain.entity.Produto;
import com.api.erp.v1.features.produto.domain.entity.ProdutoComposicao;
import com.api.erp.v1.features.produto.domain.exception.ProdutoException;
import com.api.erp.v1.features.produto.domain.repository.ProdutoComposicaoRepository;
import com.api.erp.v1.features.produto.domain.service.IListaExpandidaProducaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListaExpandidaProducaoService implements IListaExpandidaProducaoService {
    
    private final ProdutoComposicaoRepository composicaoRepository;
    
    /**
     * Calcula a lista expandida de produção para um produto
     * 
     * @param produto Produto fabricável
     * @param quantidadeRequerida Quantidade do produto a fabricar
     * @return Mapa com produtos necessários e suas quantidades acumuladas
     * @throws ProdutoException Se o produto não for fabricável
     */
    @Override
    public Map<Produto, BigDecimal> calcularListaExpandida(Produto produto, BigDecimal quantidadeRequerida) {
        if (!produto.ehFabricavel()) {
            throw ProdutoException.produtoNaoFabricavel(produto.getId());
        }
        
        if (quantidadeRequerida == null || quantidadeRequerida.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantidade requerida deve ser maior que zero");
        }
        
        Map<Produto, BigDecimal> listaExpandida = new HashMap<>();
        Set<Long> processados = new HashSet<>();
        
        expandirComposicao(produto, quantidadeRequerida, listaExpandida, processados);
        
        return listaExpandida;
    }
    
    /**
     * Expande recursivamente a composição de um produto
     * 
     * @param produto Produto a expandir
     * @param quantidadeRequerida Quantidade necessária do produto
     * @param listaExpandida Mapa acumulativo com resultados
     * @param processados Set para evitar processamento infinito
     */
    private void expandirComposicao(
            Produto produto,
            BigDecimal quantidadeRequerida,
            Map<Produto, BigDecimal> listaExpandida,
            Set<Long> processados) {
        
        // Proteção contra ciclos infinitos (embora validado no validator)
        if (processados.contains(produto.getId())) {
            return;
        }
        
        processados.add(produto.getId());
        
        // Obter componentes diretos do produto
        List<ProdutoComposicao> componentes = composicaoRepository.findByProdutoFabricadoId(produto.getId());
        
        if (componentes.isEmpty()) {
            // Produto não tem composição, adicionar como material direto se não for fabricável
            if (produto.ehComprado()) {
                listaExpandida.merge(
                    produto,
                    quantidadeRequerida,
                    BigDecimal::add
                );
            }
            return;
        }
        
        // Processar cada componente
        for (ProdutoComposicao composicao : componentes) {
            Produto componenteProduto = composicao.getProdutoComponente();
            
            // Calcular quantidade total necessária do componente
            BigDecimal quantidadeComponente = composicao.getQuantidadeNecessaria()
                    .multiply(quantidadeRequerida);
            
            // Se o componente é fabricável, expandir sua composição
            if (componenteProduto.ehFabricavel()) {
                expandirComposicao(componenteProduto, quantidadeComponente, listaExpandida, processados);
            } else {
                // Se é comprado, adicionar diretamente à lista
                listaExpandida.merge(
                    componenteProduto,
                    quantidadeComponente,
                    BigDecimal::add
                );
            }
        }
    }
    
    /**
     * Obtém apenas os produtos comprados necessários
     * (útil para gerar lista de compras)
     */
    @Override
    public Map<Produto, BigDecimal> obterListaCompras(Produto produto, BigDecimal quantidadeRequerida) {
        Map<Produto, BigDecimal> listaExpandida = calcularListaExpandida(produto, quantidadeRequerida);
        
        return listaExpandida.entrySet().stream()
                .filter(entry -> entry.getKey().ehComprado())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    /**
     * Obtém todos os produtos da composição expandida de forma ordenada
     */
    @Override
    public List<Map.Entry<Produto, BigDecimal>> obterListaOrdenada(Produto produto, BigDecimal quantidadeRequerida) {
        Map<Produto, BigDecimal> listaExpandida = calcularListaExpandida(produto, quantidadeRequerida);
        
        return listaExpandida.entrySet().stream()
                .sorted((a, b) -> a.getKey().getDescricao().compareTo(b.getKey().getDescricao()))
                .collect(Collectors.toList());
    }
}
