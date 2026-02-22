package com.api.erp.v1.main.features.product.domain.entity;

/**
 * Enum que representa os diferentes status de um product
 * 
 * OCP: Aberto para extensão - novos status podem ser adicionados
 */
public enum ProductStatus {
    ATIVO("Ativo", "Product disponível para uso"),
    INATIVO("Inativo", "Product desativado temporariamente"),
    BLOQUEADO("Bloqueado", "Product bloqueado para uso"),
    DESCONTINUADO("Descontinuado", "Product descontinuado permanentemente");
    
    private final String descricao;
    private final String detalhes;
    
    ProductStatus(String descricao, String detalhes) {
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
