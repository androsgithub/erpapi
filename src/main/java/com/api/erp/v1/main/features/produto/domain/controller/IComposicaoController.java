package com.api.erp.v1.main.features.produto.domain.controller;

import com.api.erp.v1.main.features.produto.application.dto.ComposicaoRequestDTO;
import com.api.erp.v1.main.features.produto.application.dto.ComposicaoResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IComposicaoController {

    public ResponseEntity<ComposicaoResponseDTO> criar(
            ComposicaoRequestDTO dto);

    public ResponseEntity<ComposicaoResponseDTO> atualizar(
            Long id,
            ComposicaoRequestDTO dto);

    public ResponseEntity<ComposicaoResponseDTO> obter(Long id);

    public ResponseEntity<List<ComposicaoResponseDTO>> listarComposicoesPor(
            Long produtoFabricadoId);

    public ResponseEntity<Void> deletar(Long id);

    public ResponseEntity<Void> deletarComposicoesDeProduto(
            Long produtoFabricadoId);
}
