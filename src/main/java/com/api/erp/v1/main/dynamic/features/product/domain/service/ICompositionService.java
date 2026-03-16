package com.api.erp.v1.main.dynamic.features.product.domain.service;

import com.api.erp.v1.main.dynamic.features.product.application.dto.CompositionRequestDTO;
import com.api.erp.v1.main.dynamic.features.product.application.dto.CompositionResponseDTO;

import java.util.List;

public interface ICompositionService {

    public CompositionResponseDTO criar(CompositionRequestDTO dto);

    public CompositionResponseDTO atualizar(Long id, CompositionRequestDTO dto);

    public CompositionResponseDTO obter(Long id);

    public List<CompositionResponseDTO> listarComposicoesPor(Long productFabricadoId);

    public void deletar(Long id);

    public void deletarComposicoesDeProduct(Long productFabricadoId);
}
