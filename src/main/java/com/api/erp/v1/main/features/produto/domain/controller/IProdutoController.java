package com.api.erp.v1.main.features.produto.domain.controller;

import com.api.erp.v1.main.features.produto.application.dto.ProdutoRequestDTO;
import com.api.erp.v1.main.features.produto.application.dto.ProdutoResponseDTO;
import com.api.erp.v1.main.features.produto.domain.entity.TipoProduto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

public interface IProdutoController {

    ResponseEntity<ProdutoResponseDTO> criar(ProdutoRequestDTO dto);

    ResponseEntity<ProdutoResponseDTO> atualizar(Long id, ProdutoRequestDTO dto);

    ResponseEntity<ProdutoResponseDTO> obter(Long id);

    ResponseEntity<Page<ProdutoResponseDTO>> listar(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "descricao") String sortBy);

    ResponseEntity<Page<ProdutoResponseDTO>> listarPorTipo(TipoProduto tipo, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size);

    ResponseEntity<ProdutoResponseDTO> ativar(Long id);

    ResponseEntity<ProdutoResponseDTO> desativar(Long id);

    ResponseEntity<ProdutoResponseDTO> bloquear(Long id);

    ResponseEntity<ProdutoResponseDTO> descontinuar(Long id);

    ResponseEntity<Void> deletar(Long id);
}
