package com.api.erp.v1.main.dynamic.features.product.domain.service;

import com.api.erp.v1.main.dynamic.features.product.domain.entity.Product;

import java.math.BigDecimal;
import java.util.*;

public interface IListaExpandidaProducaoService {
    public Map<Product, BigDecimal> calcularListaExpandida(Product product, BigDecimal quantidadeRequerida) ;

    public Map<Product, BigDecimal> obterListaCompras(Product product, BigDecimal quantidadeRequerida) ;
    public List<Map.Entry<Product, BigDecimal>> obterListaOrdenada(Product product, BigDecimal quantidadeRequerida);
}
