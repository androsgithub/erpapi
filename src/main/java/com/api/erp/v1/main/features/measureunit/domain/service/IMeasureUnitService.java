package com.api.erp.v1.main.features.measureunit.domain.service;

import com.api.erp.v1.main.features.measureunit.application.dto.request.MeasureUnitRequestDTO;
import com.api.erp.v1.main.features.measureunit.application.dto.response.MeasureUnitResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IMeasureUnitService {
    public MeasureUnitResponseDTO criar(MeasureUnitRequestDTO dto);

    public MeasureUnitResponseDTO atualizar(Long id, MeasureUnitRequestDTO dto);

    public MeasureUnitResponseDTO obter(Long id);

    public Page<MeasureUnitResponseDTO> listar(Pageable pageable);

    public MeasureUnitResponseDTO ativar(Long id);

    public MeasureUnitResponseDTO desativar(Long id);

    public void deletar(Long id);
}
