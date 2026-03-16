package com.api.erp.v1.main.dynamic.features.measureunit.application.dto.request;

import lombok.*;

/**
 * DTO para requisição de criação/atualização de MeasureUnit
 * 
 * SRP: Transferência de dados entre camadas
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeasureUnitRequestDTO {
    
    private String sigla;
    private String descricao;
}
