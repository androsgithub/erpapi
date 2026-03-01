package com.api.erp.v1.main.tenant.domain.controller;

import com.api.erp.v1.main.tenant.application.dto.TenantDatasourceRequest;
import com.api.erp.v1.main.tenant.application.dto.TenantDatasourceResponse;
import com.api.erp.v1.main.tenant.application.dto.UpdateDatasourceResponse;
import org.springframework.http.ResponseEntity;

public interface ITenantDatabaseController {

    /**
     * Obtém a configuração de datasource atual do tenant
     * GET /api/v1/tenant/database/datasource
     */
    ResponseEntity<TenantDatasourceResponse> obterDatasource();

    /**
     * Atualiza a configuração de datasource do tenant
     * PUT /api/v1/tenant/database/datasource
     * 
     * Parâmetro na request:
     * - runMigrations: Boolean OPCIONAL
     *   - false ou omitido (padrão): Apenas atualiza, sem enfileirar migrações
     *   - true: Atualiza + Enfileira migrações (Flyway + seeders)
     * 
     * Resposta:
     * - 200: Datasource atualizado (runMigrations = false)
     * - 202: Datasource atualizado e migrações enfileiradas (runMigrations = true)
     * - 400: Erro na atualização ou ao enfileirar migrações
     */
    ResponseEntity<UpdateDatasourceResponse> atualizarDatasource(
            TenantDatasourceRequest request
    );

    /**
     * Configura novo datasource e enfileira migrações automaticamente
     * POST /api/v1/tenant/database/datasource/configure-and-migrate
     * 
     * Valida a conexão e enfileira as migrações (Flyway) e seeders
     */
    ResponseEntity<?> configurarDatasourceEEnfileirarMigracao(
            TenantDatasourceRequest request
    );

    /**
     * Enfileira migrações manualmente para um datasource já existente
     * POST /api/v1/tenant/database/datasource/enqueue-migration
     * 
     * Não modifica nenhuma configuração de datasource.
     * Apenas enfileira um novo evento de migração para ser processado.
     * 
     * Resposta:
     * - 202: Migração enfileirada com sucesso
     * - 400: Datasource ativo não encontrado
     * - 500: Erro ao enfileirar
     */
    ResponseEntity<?> enqueueMigration();

    ResponseEntity<?> validarDatasource(
            TenantDatasourceRequest request
    );
}
