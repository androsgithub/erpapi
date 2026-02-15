package com.api.erp.v1.external.observability.presentation.controller;

import com.api.erp.v1.external.observability.presentation.dto.FlowEventDto;
import com.api.erp.v1.external.observability.presentation.dto.PageableResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Interface para Controller de Observability.
 * 
 * Encapsula toda a documentação Swagger, reduzindo a quantidade
 * de código boilerplate no controller.
 * 
 * SOLID: Interface Segregation - contrato bem definido
 * Clean Architecture: Separação entre contrato e implementação
 */
@Tag(name = "Observability", description = "Visualização de eventos de observability")
public interface IObservabilityController {

    /**
     * Obtém todos os eventos de um trace ID específico.
     * 
     * Útil para debugar uma requisição específica.
     * 
     * @param traceId identificador único da requisição
     * @return lista de eventos do trace
     */
    @Operation(
        summary = "Obter eventos por trace ID",
        description = "Retorna todos os eventos de uma requisição específica"
    )
    @ApiResponse(responseCode = "200", description = "Eventos encontrados")
    ResponseEntity<List<FlowEventDto>> getEventsByTraceId(
        @PathVariable
        @Parameter(description = "Identificador único da requisição")
        String traceId
    );

    /**
     * Health check da observability.
     * 
     * @return status 200 se tudo está funcionando
     */
    @Operation(
        summary = "Health check",
        description = "Verifica se o sistema de observability está funcionando"
    )
    @ApiResponse(responseCode = "200", description = "Sistema funcionando normalmente")
    ResponseEntity<Map<String, String>> health();

    /**
     * Busca todos os eventos com paginação e filtros.
     * 
     * Suporta filtros via URL params.
     * Exemplo: GET /all?status=ERROR&stepName=validateUser&page=0&pageSize=50
     * 
     * @param status filtro por status (opcional)
     * @param stepName filtro por nome do passo (opcional)
     * @param traceId filtro por trace ID (opcional)
     * @param minExecutionTime tempo mínimo de execução (opcional)
     * @param maxExecutionTime tempo máximo de execução (opcional)
     * @param page número da página (padrão: 0)
     * @param pageSize tamanho da página (padrão: 20)
     * @return resposta paginada com todos os eventos
     */
    @Operation(
        summary = "Obter todos os eventos paginados",
        description = "Retorna todos os eventos com suporte a paginação e filtros"
    )
    @ApiResponse(responseCode = "200", description = "Eventos retornados com sucesso")
    ResponseEntity<PageableResponse<FlowEventDto>> getAll(
        @RequestParam(required = false)
        @Parameter(description = "Filtro por status")
        String status,
        
        @RequestParam(required = false)
        @Parameter(description = "Filtro por passo")
        String stepName,
        
        @RequestParam(required = false)
        @Parameter(description = "Filtro por trace ID")
        String traceId,
        
        @RequestParam(required = false)
        @Parameter(description = "Tempo mínimo de execução")
        Integer minExecutionTime,
        
        @RequestParam(required = false)
        @Parameter(description = "Tempo máximo de execução")
        Integer maxExecutionTime,
        
        @RequestParam(defaultValue = "0")
        @Parameter(description = "Número da página")
        int page,
        
        @RequestParam(defaultValue = "20")
        @Parameter(description = "Quantidade de registros por página")
        int pageSize
    );
}
