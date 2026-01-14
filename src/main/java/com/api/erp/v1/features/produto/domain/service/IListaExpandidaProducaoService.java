package com.api.erp.v1.features.produto.domain.service;

import com.api.erp.v1.features.produto.domain.entity.Produto;
import com.api.erp.v1.features.produto.domain.entity.ProdutoComposicao;
import com.api.erp.v1.features.produto.domain.exception.ProdutoException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public interface IListaExpandidaProducaoService {
    public Map<Produto, BigDecimal> calcularListaExpandida(Produto produto, BigDecimal quantidadeRequerida) ;

    public Map<Produto, BigDecimal> obterListaCompras(Produto produto, BigDecimal quantidadeRequerida) ;
    public List<Map.Entry<Produto, BigDecimal>> obterListaOrdenada(Produto produto, BigDecimal quantidadeRequerida);
}
