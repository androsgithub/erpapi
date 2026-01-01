package com.api.erp.v1.features.produto.domain.service;

import com.api.erp.v1.features.produto.application.dto.ListaExpandidaResponseDTO;

import java.math.BigDecimal;

public interface IListaExpandidaService {
    ListaExpandidaResponseDTO gerarListaExpandida(Long produtoId, BigDecimal quantidade);

    ListaExpandidaResponseDTO gerarListaCompras(Long produtoId, BigDecimal quantidade);
}
