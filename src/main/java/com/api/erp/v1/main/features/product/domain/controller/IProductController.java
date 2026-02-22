package com.api.erp.v1.main.features.product.domain.controller;

import com.api.erp.v1.main.features.product.application.dto.ProductRequestDTO;
import com.api.erp.v1.main.features.product.application.dto.ProductResponseDTO;
import com.api.erp.v1.main.features.product.domain.entity.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

public interface IProductController {

    ResponseEntity<ProductResponseDTO> criar(ProductRequestDTO dto);

    ResponseEntity<ProductResponseDTO> atualizar(Long id, ProductRequestDTO dto);

    ResponseEntity<ProductResponseDTO> obter(Long id);

    ResponseEntity<Page<ProductResponseDTO>> listar(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "descricao") String sortBy);

    ResponseEntity<Page<ProductResponseDTO>> listarPorTipo(ProductType type, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size);

    ResponseEntity<ProductResponseDTO> ativar(Long id);

    ResponseEntity<ProductResponseDTO> desativar(Long id);

    ResponseEntity<ProductResponseDTO> bloquear(Long id);

    ResponseEntity<ProductResponseDTO> descontinuar(Long id);

    ResponseEntity<Void> deletar(Long id);
}
