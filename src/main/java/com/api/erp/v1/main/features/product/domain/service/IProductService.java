package com.api.erp.v1.main.features.product.domain.service;

import com.api.erp.v1.main.features.product.application.dto.ProductRequestDTO;
import com.api.erp.v1.main.features.product.domain.entity.Product;
import com.api.erp.v1.main.features.product.domain.entity.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductService {
    Product criar(ProductRequestDTO dto);

    Product atualizar(Long id, ProductRequestDTO productModificado);

    Product obter(Long id);

    Page<Product> listar(Pageable pageable);

    Page<Product> listarPorTipo(ProductType type, Pageable pageable);

    Product ativar(Long id);

    Product desativar(Long id);

    Product bloquear(Long id);

    Product descontinuar(Long id);

    void deletar(Long id);
}
