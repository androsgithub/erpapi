package com.api.erp.v1.features.unidademedida.application.dto.request;

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
