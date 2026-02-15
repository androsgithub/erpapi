package com.api.erp.v1.main.features.produto.domain.service;

import com.api.erp.v1.main.features.produto.domain.entity.Produto;

import java.math.BigDecimal;
import java.util.*;

public interface IListaExpandidaProducaoService {
    public Map<Produto, BigDecimal> calcularListaExpandida(Produto produto, BigDecimal quantidadeRequerida) ;

    public Map<Produto, BigDecimal> obterListaCompras(Produto produto, BigDecimal quantidadeRequerida) ;
    public List<Map.Entry<Produto, BigDecimal>> obterListaOrdenada(Produto produto, BigDecimal quantidadeRequerida);
}
