package com.api.erp.v1.docs.openapi.observability;

import com.api.erp.v1.external.observability.presentation.controller.IObservabilityController;
import com.api.erp.v1.external.observability.presentation.dto.FlowEventDto;
import com.api.erp.v1.external.observability.presentation.dto.PageableResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Interface de documentação OpenAPI para Observability.
 * Esta interface herda de IObservabilityController e adiciona as anotações Swagger.
 */
@Tag(
        name = "Observability",
        description = "Endpoints responsáveis pela visualização e análise de eventos de observability"
)
public interface ObservabilityOpenApiDocumentation extends IObservabilityController {

    @Override
    @Operation(
            summary = "Obter eventos por trace ID",
            description = "Retorna todos os eventos de uma requisição específica, útil para debugar uma requisição específica"
    )
    @ApiResponse(responseCode = "200", description = "Eventos encontrados com sucesso")
    @ApiResponse(responseCode = "404", description = "Trace ID não encontrado")
    ResponseEntity<List<FlowEventDto>> getEventsByTraceId(
            @PathVariable
            @Parameter(description = "Identificador único da requisição")
            String traceId
    );

    @Override
    @Operation(
            summary = "Health check",
            description = "Verifica se o sistema de observability está funcionando corretamente"
    )
    @ApiResponse(responseCode = "200", description = "Sistema funcionando normalmente")
    @ApiResponse(responseCode = "503", description = "Sistema indisponível")
    ResponseEntity<Map<String, String>> health();

    @Override
    @Operation(
            summary = "Obter todos os eventos paginados",
            description = "Retorna todos os eventos com suporte a paginação e filtros avançados"
    )
    @ApiResponse(responseCode = "200", description = "Eventos retornados com sucesso")
    @ApiResponse(responseCode = "400", description = "Parâmetros de filtro inválidos")
    ResponseEntity<PageableResponse<FlowEventDto>> getAll(
            @RequestParam(required = false)
            @Parameter(description = "Filtro por status do evento")
            String status,

            @RequestParam(required = false)
            @Parameter(description = "Filtro por nome do passo/step")
            String stepName,

            @RequestParam(required = false)
            @Parameter(description = "Filtro por trace ID")
            String traceId,

            @RequestParam(required = false)
            @Parameter(description = "Tempo mínimo de execução em milissegundos")
            Integer minExecutionTime,

            @RequestParam(required = false)
            @Parameter(description = "Tempo máximo de execução em milissegundos")
            Integer maxExecutionTime,

            @RequestParam(defaultValue = "0")
            @Parameter(description = "Número da página (começando em 0)")
            int page,

            @RequestParam(defaultValue = "20")
            @Parameter(description = "Quantidade de registros por página")
            int pageSize
    );
}
