package com.api.erp.v1.main.features.unidademedida.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para resposta de UnidadeMedida
 * 
 * SRP: Transferência de dados entre camadas
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnidadeMedidaResponseDTO {
    private Long id;
    private String sigla;
    private String descricao;
}
