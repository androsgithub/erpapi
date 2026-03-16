package com.api.erp.v1.main.dynamic.features.product.domain.service;

import com.api.erp.v1.main.dynamic.features.product.application.dto.ListaExpandidaResponseDTO;

import java.math.BigDecimal;

public interface IListaExpandidaService {
    ListaExpandidaResponseDTO gerarListaExpandida(Long productId, BigDecimal quantidade);

    ListaExpandidaResponseDTO gerarListaCompras(Long productId, BigDecimal quantidade);
}
