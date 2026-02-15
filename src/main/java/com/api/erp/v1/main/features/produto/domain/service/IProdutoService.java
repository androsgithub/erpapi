package com.api.erp.v1.main.features.produto.domain.service;

import com.api.erp.v1.main.features.produto.application.dto.ProdutoRequestDTO;
import com.api.erp.v1.main.features.produto.domain.entity.Produto;
import com.api.erp.v1.main.features.produto.domain.entity.TipoProduto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProdutoService {
    Produto criar(ProdutoRequestDTO dto);

    Produto atualizar(Long id, ProdutoRequestDTO produtoModificado);

    Produto obter(Long id);

    Page<Produto> listar(Pageable pageable);

    Page<Produto> listarPorTipo(TipoProduto tipo, Pageable pageable);

    Produto ativar(Long id);

    Produto desativar(Long id);

    Produto bloquear(Long id);

    Produto descontinuar(Long id);

    void deletar(Long id);
}
