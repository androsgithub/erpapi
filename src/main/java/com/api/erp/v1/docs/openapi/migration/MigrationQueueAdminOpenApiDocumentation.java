package com.api.erp.v1.docs.openapi.migration;

import com.api.erp.v1.main.migration.domain.controller.IMigrationQueueAdminController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * DOCUMENTATION - OpenAPI Interface para Administração da Fila de Migrações
 * <p>
 * Esta interface herda de IMigrationQueueAdminController e adiciona
 * as anotações Swagger/OpenAPI para documentação automática.
 * <p>
 * Endpoints:
 * - GET /api/v1/admin/migrations/queue/stats - Estatísticas
 * - GET /api/v1/admin/migrations/queue/events - Listar eventos
 * - GET /api/v1/admin/migrations/queue/events/{eventId} - Detalhe
 * - GET /api/v1/admin/migrations/queue/events/pending - Pendentes
 * - GET /api/v1/admin/migrations/queue/events/failed - Falhados
 * - GET /api/v1/admin/migrations/queue/events/completed - Completos
 * - GET /api/v1/admin/migrations/queue/events/in-progress - Em progresso
 * - POST /api/v1/admin/migrations/queue/reprocess/{eventId} - Reprocessar
 *
 * @author ERP System
 * @version 1.0
 */
@Tag(
        name = "Admin - Migration Queue",
        description = "Endpoints de administração da fila unificada de processamento de migrações de tenants"
)
public interface MigrationQueueAdminOpenApiDocumentation extends IMigrationQueueAdminController {

    @Override
    @Operation(
            summary = "Obter estatísticas da fila",
            description = "Retorna informações sobre o estado geral da fila de migrações. " +
                    "Inclui total de eventos, status de cada um, taxa de sucesso e tempos de execução."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Estatísticas retornadas com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = """
                                    {
                                      "totalEvents": 150,
                                      "pendingEvents": 5,
                                      "inProgressEvents": 2,
                                      "completedEvents": 140,
                                      "failedEvents": 3,
                                      "queueSize": 7,
                                      "progressPercent": 95.3,
                                      "successRatePercent": 97.8,
                                      "totalExecutionTimeMs": 450000,
                                      "avgExecutionTimeMs": 3214
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado ou sem permissão de admin")
    @ApiResponse(responseCode = "500", description = "Erro interno ao coletar estatísticas")
    ResponseEntity<Map<String, Object>> getQueueStats();

    @Override
    @Operation(
            summary = "Listar todos os eventos",
            description = "Retorna a lista completa de todos os eventos de migração registrados na fila, " +
                    "independente de seu status."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de eventos retornada com sucesso",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado ou sem permissão de admin")
    @ApiResponse(responseCode = "500", description = "Erro ao listar eventos")
    ResponseEntity<Map<String, Object>> getAllEvents();

    @Override
    @Operation(
            summary = "Obter detalhes de um evento",
            description = "Retorna informações detalhadas sobre um evento específico de migração, " +
                    "incluindo seu histórico de execução, erros (se houver) e tempos."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Evento encontrado e detalhes retornados",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado ou sem permissão de admin")
    @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro ao recuperar evento")
    ResponseEntity<?> getEvent(String eventId);

    @Override
    @Operation(
            summary = "Listar eventos pendentes",
            description = "Retorna lista de eventos que estão aguardando processamento na fila."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de eventos pendentes retornada com sucesso",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado ou sem permissão de admin")
    @ApiResponse(responseCode = "500", description = "Erro ao listar eventos pendentes")
    ResponseEntity<Map<String, Object>> getPendingEvents();

    @Override
    @Operation(
            summary = "Listar eventos com falha",
            description = "Retorna lista de eventos que falharam durante o processamento. " +
                    "Útil para diagnosticar problemas e analisar necessidade de reprocessamento."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de eventos falhados retornada com sucesso",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado ou sem permissão de admin")
    @ApiResponse(responseCode = "500", description = "Erro ao listar eventos falhados")
    ResponseEntity<Map<String, Object>> getFailedEvents();

    @Override
    @Operation(
            summary = "Listar eventos completos",
            description = "Retorna lista de eventos que foram processados com sucesso."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de eventos completos retornada com sucesso",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado ou sem permissão de admin")
    @ApiResponse(responseCode = "500", description = "Erro ao listar eventos completos")
    ResponseEntity<Map<String, Object>> getCompletedEvents();

    @Override
    @Operation(
            summary = "Listar eventos em progresso",
            description = "Retorna lista de eventos que estão sendo processados no momento."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de eventos em progresso retornada com sucesso",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado ou sem permissão de admin")
    @ApiResponse(responseCode = "500", description = "Erro ao listar eventos em progresso")
    ResponseEntity<Map<String, Object>> getInProgressEvents();

    @Override
    @Operation(
            summary = "Reprocessar um evento",
            description = "Força o reprocessamento de um evento específico. Útil quando um evento falha " +
                    "e você precisa reexecutá-lo após resolver o problema raiz (ex: banco de dados indisponível, " +
                    "script de migração corrigido, etc). O evento voltará para a fila e será processado novamente."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Evento reenfileirado com sucesso para reprocessamento",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = """
                                    {
                                      "status": "success",
                                      "message": "Evento reprocessado com sucesso"
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado ou sem permissão de admin")
    @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    @ApiResponse(responseCode = "400", description = "Erro ao reprocessar evento (ex: estado inválido)")
    @ApiResponse(responseCode = "500", description = "Erro interno ao reprocessar evento")
    ResponseEntity<Map<String, Object>> reprocessEvent(String eventId);
}
