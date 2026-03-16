package com.api.erp.v1.main.dynamic.features.product.domain.controller;

import com.api.erp.v1.main.dynamic.features.product.application.dto.CompositionRequestDTO;
import com.api.erp.v1.main.dynamic.features.product.application.dto.CompositionResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICompositionController {

    public ResponseEntity<CompositionResponseDTO> criar(
            CompositionRequestDTO dto);

    public ResponseEntity<CompositionResponseDTO> atualizar(
            Long id,
            CompositionRequestDTO dto);

    public ResponseEntity<CompositionResponseDTO> obter(Long id);

    public ResponseEntity<List<CompositionResponseDTO>> listarComposicoesPor(
            Long productFabricadoId);

    public ResponseEntity<Void> deletar(Long id);

    public ResponseEntity<Void> deletarComposicoesDeProduct(
            Long productFabricadoId);
}
