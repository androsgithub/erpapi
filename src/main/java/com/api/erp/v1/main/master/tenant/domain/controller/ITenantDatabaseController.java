package com.api.erp.v1.main.master.tenant.domain.controller;

import com.api.erp.v1.main.master.tenant.application.dto.request.create.TenantDatasourceRequest;
import com.api.erp.v1.main.master.tenant.application.dto.request.update.UpdateTenantDatasourceWithPasswordRequest;
import com.api.erp.v1.main.master.tenant.application.dto.response.TenantDatasourceResponse;
import com.api.erp.v1.main.master.tenant.application.dto.response.UpdateDatasourceResponse;
import org.springframework.http.ResponseEntity;

public interface ITenantDatabaseController {

    /**
     * Gets a configuração de datasource atual do tenant
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
     * Atualiza datasource com verificação de senha (SEGURO)
     * PUT /api/v1/tenant/database/datasource/update-with-password
     * 
     * Requer verificação da senha atual antes de permitir atualização
     * 
     * Request:
     * {
     *   "currentPassword": "senha_atual_armazenada",
     *   "newPassword": "nova_senha_ou_vazio",
     *   "host": "db.example.com",
     *   "port": 5432,
     *   "databaseName": "tenant_db",
     *   "username": "db_user",
     *   "dbType": "POSTGRESQL",
     *   "runMigrations": true
     * }
     * 
     * Fluxo:
     * 1. Verifica se currentPassword corresponde à senha armazenada
     * 2. Se SIM: Atualiza datasource (com newPassword se fornecido)
     * 3. Se NÃO: Retorna 400 Bad Request com erro de verificação
     * 4. Se runMigrations=true: Enfileira migrações após atualizar
     * 
     * Resposta:
     * - 200: Datasource atualizado (runMigrations = false)
     * - 202: Datasource atualizado e migrações enfileiradas (runMigrations = true)
     * - 400: Erro na verificação de senha ou atualização
     * 
     * Segurança: Verifica a senha atual para prevenir atualizações não autorizadas
     */
    ResponseEntity<UpdateDatasourceResponse> atualizarDatasourceComVerificacaoDeSenha(
            UpdateTenantDatasourceWithPasswordRequest request
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
