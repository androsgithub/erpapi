package com.api.erp.features.produto.domain.entity;

/**
 * Enum que representa os diferentes status de um produto
 * 
 * OCP: Aberto para extensão - novos status podem ser adicionados
 */
public enum StatusProduto {
    ATIVO("Ativo", "Produto disponível para uso"),
    INATIVO("Inativo", "Produto desativado temporariamente"),
    BLOQUEADO("Bloqueado", "Produto bloqueado para uso"),
    DESCONTINUADO("Descontinuado", "Produto descontinuado permanentemente");
    
    private final String descricao;
    private final String detalhes;
    
    StatusProduto(String descricao, String detalhes) {
        this.descricao = descricao;
        this.detalhes = detalhes;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public String getDetalhes() {
        return detalhes;
    }
    
    public boolean podeSerUtilizado() {
        return this == ATIVO;
    }
}
