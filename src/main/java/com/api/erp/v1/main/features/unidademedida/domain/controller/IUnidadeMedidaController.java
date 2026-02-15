package com.api.erp.v1.main.features.unidademedida.domain.controller;

import com.api.erp.v1.main.features.unidademedida.application.dto.request.UnidadeMedidaRequestDTO;
import com.api.erp.v1.main.features.unidademedida.application.dto.response.UnidadeMedidaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

public interface IUnidadeMedidaController {

    public ResponseEntity<UnidadeMedidaResponseDTO> criar(UnidadeMedidaRequestDTO dto);

    ResponseEntity<UnidadeMedidaResponseDTO> atualizar(Long id, UnidadeMedidaRequestDTO dto);

    ResponseEntity<UnidadeMedidaResponseDTO> obter(Long id);

    ResponseEntity<Page<UnidadeMedidaResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "descricao") String sortBy);

    ResponseEntity<UnidadeMedidaResponseDTO> ativar(Long id);

    ResponseEntity<UnidadeMedidaResponseDTO> desativar(Long id);

    ResponseEntity<Void> deletar(Long id);
}
