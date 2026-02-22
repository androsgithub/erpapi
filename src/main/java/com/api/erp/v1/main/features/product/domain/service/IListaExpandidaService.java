package com.api.erp.v1.main.features.product.domain.service;

import com.api.erp.v1.main.features.product.application.dto.ListaExpandidaResponseDTO;

import java.math.BigDecimal;

public interface IListaExpandidaService {
    ListaExpandidaResponseDTO gerarListaExpandida(Long productId, BigDecimal quantidade);

    ListaExpandidaResponseDTO gerarListaCompras(Long productId, BigDecimal quantidade);
}
