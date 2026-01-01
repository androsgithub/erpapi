package com.api.erp.v1.features.unidademedida.domain.service;

import com.api.erp.v1.features.unidademedida.application.dto.request.UnidadeMedidaRequestDTO;
import com.api.erp.v1.features.unidademedida.application.dto.response.UnidadeMedidaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUnidadeMedidaService {
    public UnidadeMedidaResponseDTO criar(UnidadeMedidaRequestDTO dto);

    public UnidadeMedidaResponseDTO atualizar(Long id, UnidadeMedidaRequestDTO dto);

    public UnidadeMedidaResponseDTO obter(Long id);

    public Page<UnidadeMedidaResponseDTO> listar(Pageable pageable);

    public UnidadeMedidaResponseDTO ativar(Long id);

    public UnidadeMedidaResponseDTO desativar(Long id);

    public void deletar(Long id);
}
