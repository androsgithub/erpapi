package com.api.erp.v1.main.features.product.domain.entity;

/**
 * Enum que representa os diferentes tipos de products
 * 
 * OCP: Aberto para extensão - novos tipos podem ser adicionados sem modificar código existente
 */
public enum ProductType {
    COMPRADO("Comprado", "Product adquirido de fornecedores"),
    FABRICAVEL("Fabricável", "Product fabricado internamente");
    
    private final String descricao;
    private final String detalhes;
    
    ProductType(String descricao, String detalhes) {
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
