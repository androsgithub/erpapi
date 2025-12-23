package com.api.erp.features.unidademedida.application.dto;

import lombok.*;

/**
 * DTO para requisição de criação/atualização de UnidadeMedida
 * 
 * SRP: Transferência de dados entre camadas
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnidadeMedidaRequestDTO {
    
    private String sigla;
    private String descricao;
}
