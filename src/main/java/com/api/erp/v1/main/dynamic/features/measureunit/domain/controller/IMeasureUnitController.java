package com.api.erp.v1.main.dynamic.features.measureunit.domain.controller;

import com.api.erp.v1.main.dynamic.features.measureunit.application.dto.request.MeasureUnitRequestDTO;
import com.api.erp.v1.main.dynamic.features.measureunit.application.dto.response.MeasureUnitResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

public interface IMeasureUnitController {

    public ResponseEntity<MeasureUnitResponseDTO> criar(MeasureUnitRequestDTO dto);

    ResponseEntity<MeasureUnitResponseDTO> atualizar(Long id, MeasureUnitRequestDTO dto);

    ResponseEntity<MeasureUnitResponseDTO> obter(Long id);

    ResponseEntity<Page<MeasureUnitResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "descricao") String sortBy);

    ResponseEntity<MeasureUnitResponseDTO> ativar(Long id);

    ResponseEntity<MeasureUnitResponseDTO> desativar(Long id);

    ResponseEntity<Void> deletar(Long id);
}
