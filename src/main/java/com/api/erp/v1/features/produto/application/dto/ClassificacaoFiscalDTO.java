package com.api.erp.v1.features.produto.application.dto;

import lombok.*;

/**
 * DTO para Classificação Fiscal do Produto
 * 
 * Expõe todos os dados fiscais necessários para criar e atualizar
 * a classificação fiscal de um produto conforme regras da NF-e.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassificacaoFiscalDTO {
    
    /**
     * NCM (Nomenclatura Comum do Mercosul)
     * Formato: 8 dígitos numéricos
     * Obrigatório
     */
    private String ncm;
    
    /**
     * Descrição fiscal do produto
     * Máximo 120 caracteres
     * Obrigatório
     */
    private String descricaoFiscal;
    
    /**
     * Origem da mercadoria (código 0-8 conforme RFB)
     * Obrigatório
     * 0: Nacional
     * 1: Estrangeira - Importação direta
     * 2: Estrangeira - Adquirida no mercado interno
     * 3: Nacional com conteúdo importado 40%-70%
     * 4: Nacional por processos produtivos básicos
     * 5: Nacional com conteúdo importado <= 40%
     * 6: Estrangeira - Importação sem similar
     * 7: Estrangeira - Mercado interno sem similar
     * 8: Nacional com conteúdo importado > 70%
     */
    private Integer origemMercadoria;
    
    /**
     * Código de benefício fiscal (opcional)
     * Máximo 10 caracteres
     * Exemplo: "AP01" para regime de tributação especial
     */
    private String codigoBeneficioFiscal;
    
    /**
     * CEST - Código Especificador da Substituição Tributária (opcional)
     * Formato: 7 dígitos numéricos
     * Utilizado em operações de ST
     */
    private String cest;
    
    /**
     * Código da unidade tributável
     * Máximo 6 caracteres (ex: "UN", "KG", "LT")
     * Obrigatório
     */
    private String unidadeTributavelCodigo;
    
    /**
     * Descrição da unidade tributável
     * Máximo 60 caracteres
     * Obrigatório
     */
    private String unidadeTributavelDescricao;
}
