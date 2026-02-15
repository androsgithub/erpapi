package com.api.erp.v1.main.features.produto.domain.service;

import com.api.erp.v1.main.features.produto.application.dto.ComposicaoRequestDTO;
import com.api.erp.v1.main.features.produto.application.dto.ComposicaoResponseDTO;

import java.util.List;

public interface IComposicaoService {

    public ComposicaoResponseDTO criar(ComposicaoRequestDTO dto);

    public ComposicaoResponseDTO atualizar(Long id, ComposicaoRequestDTO dto);

    public ComposicaoResponseDTO obter(Long id);

    public List<ComposicaoResponseDTO> listarComposicoesPor(Long produtoFabricadoId);

    public void deletar(Long id);

    public void deletarComposicoesDeProduto(Long produtoFabricadoId);
}
