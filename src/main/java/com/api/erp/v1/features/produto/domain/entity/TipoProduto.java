package com.api.erp.v1.features.produto.domain.entity;

/**
 * Enum que representa os diferentes tipos de produtos
 * 
 * OCP: Aberto para extensão - novos tipos podem ser adicionados sem modificar código existente
 */
public enum TipoProduto {
    COMPRADO("Comprado", "Produto adquirido de fornecedores"),
    FABRICAVEL("Fabricável", "Produto fabricado internamente");
    
    private final String descricao;
    private final String detalhes;
    
    TipoProduto(String descricao, String detalhes) {
        this.descricao = descricao;
        this.detalhes = detalhes;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public String getDetalhes() {
        return detalhes;
    }
}
