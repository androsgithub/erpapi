package com.api.erp.v1.main.tenant.application.dto;

import java.util.Map;

/**
 * UpdateDatasourceResponse
 * 
 * DTO para resposta ao atualizar datasource de um tenant.
 * 
 * Contém:
 * - datasource: Configuration do datasource atualizado
 * - migration: OPCIONAL - Informações do evento de migração enfileirado (se runMigrations=true)
 * - migrationError: OPCIONAL - Mensagem de erro ao enfileirar migrações (se houver erro parcial)
 * 
 * Exemplos:
 * 
 * 1. Apenas atualizar (runMigrations=false):
 * {
 *   "datasource": { id, host, port, ... },
 *   "migration": null,
 *   "migrationError": null
 * }
 * 
 * 2. Update + Enfileirar com sucesso (runMigrations=true):
 * {
 *   "datasource": { id, host, port, ... },
 *   "migration": { eventId, status, source, enqueuedAt, message },
 *   "migrationError": null
 * }
 * 
 * 3. Update com sucesso + Erro ao enfileirar:
 * {
 *   "datasource": { id, host, port, ... },
 *   "migration": null,
 *   "migrationError": "Datasource atualizado, mas erro ao enfileirar migrações: ..."
 * }
 */
public record UpdateDatasourceResponse(
        TenantDatasourceResponse datasource,
        Map<String, Object> migration,
        String migrationError
) {
    /**
     * Construtor simplificado para apenas atualizar datasource (sem migrações)
     */
    public UpdateDatasourceResponse(TenantDatasourceResponse datasource) {
        this(datasource, null, null);
    }
}
