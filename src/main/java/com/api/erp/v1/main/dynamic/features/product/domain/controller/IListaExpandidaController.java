package com.api.erp.v1.main.dynamic.features.product.domain.controller;

import com.api.erp.v1.main.dynamic.features.product.application.dto.ListaExpandidaResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

public interface IListaExpandidaController {

    public ResponseEntity<ListaExpandidaResponseDTO> gerarListaExpandida(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") BigDecimal quantidade);

    public ResponseEntity<ListaExpandidaResponseDTO> gerarListaCompras(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") BigDecimal quantidade);
}
