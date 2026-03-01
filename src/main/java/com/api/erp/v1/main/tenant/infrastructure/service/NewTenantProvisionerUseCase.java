package com.api.erp.v1.main.tenant.infrastructure.service;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.migration.async.domain.MigrationQueueTask;
import com.api.erp.v1.main.migration.async.service.MigrationQueueService;
import com.api.erp.v1.main.tenant.application.dto.CreateNewTenantWithDatasourceRequest;
import com.api.erp.v1.main.tenant.application.dto.CriarTenantRequest;
import com.api.erp.v1.main.tenant.application.dto.TenantDatasourceRequest;
import com.api.erp.v1.main.tenant.application.dto.TenantDatasourceResponse;
import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.tenant.domain.repository.TenantDatasourceRepository;
import com.api.erp.v1.main.tenant.domain.service.ITenantDatasourceService;
import com.api.erp.v1.main.tenant.domain.service.ITenantService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * INFRASTRUCTURE - Serviço de Provisionamento de Novo Tenant
 * 
 * Orquestra todo o processo de criar um novo tenant completo com datasource,
 * migrações e seeders, escondendo a complexidade do controller.
 * 
 * Responsabilidades:
 * - Criar tenant no master database
 * - Configurar datasource
 * - Testar conexão
 * - Enfileirar migrações + seeders
 * - Iniciar processamento
 * 
 * Benefícios:
 * - Controller fica limpo e legível
 * - Lógica centralizada e testável
 * - Fácil de manter e evoluir
 * 
 * @author ERP System
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NewTenantProvisionerUseCase {
    
    private final ITenantService tenantService;
    private final ITenantDatasourceService tenantDatasourceService;
    private final MigrationQueueService migrationQueueService;
    private final TenantDatasourceRepository tenantDatasourceRepository;
    
    /**
     * Provisiona um novo tenant completo com datasource, migrações e seeders.
     * 
     * Processo:
     * 1. Cria tenant no master database
     * 2. Configura datasource
     * 3. Testa conexão
     * 4. Enfileira migração Flyway + MainSeed
     * 5. Inicia processamento em background
     * 
     * @param request Dados do novo tenant + datasource
     * @return Resultado com tenant, datasource e task de migração
     * @throws IllegalArgumentException Se houver erro de validação
     * @throws Exception Se houver erro durante qualquer fase
     */
    public NewTenantProvisionResult execute(CreateNewTenantWithDatasourceRequest request) {
        
        log.info("");
        log.info("╔════════════════════════════════════════════════════════════════╗");
        log.info("║  PROVISIONANDO NOVO TENANT: {}", request.nome());
        log.info("╚════════════════════════════════════════════════════════════════╝");
        
        try {
            // ═══════════════════════════════════════════════════════════════════
            // FASE 1: CRIAR TENANT NO MASTER DATABASE
            // ═══════════════════════════════════════════════════════════════════
            log.info("1️⃣  Criando tenant no master database...");
            
            CriarTenantRequest criarRequest = new CriarTenantRequest(
                    request.nome(),
                    request.cnpj(),
                    request.razaoSocial(),
                    request.email(),
                    request.telefone(),
                    request.tenantType(),
                    request.tenantSubdomain(),
                    request.contribuinteICMS(),
                    request.regimeTributario()
            );
            
            Tenant tenantCriado = tenantService.criarTenant(criarRequest);
            log.info("✅ Tenant criado: {} (ID: {})", tenantCriado.getNome(), tenantCriado.getId());
            
            // ═══════════════════════════════════════════════════════════════════
            // FASE 2: CONFIGURAR DATASOURCE DO TENANT
            // ═══════════════════════════════════════════════════════════════════
            log.info("2️⃣  Configurando datasource para tenant {} ...", tenantCriado.getId());
            
            TenantDatasourceRequest datasourceRequest = new TenantDatasourceRequest(
                    request.dbHost(),
                    request.dbPort(),
                    request.dbName(),
                    request.dbUsername(),
                    request.dbPassword(),
                    request.dbType()
            );
            
            TenantDatasourceResponse datasourceResponse = null;
            TenantContext.setTenantId(tenantCriado.getId());
            try {
                datasourceResponse = tenantDatasourceService.configurarDatasource(
                        tenantCriado.getId(),
                        datasourceRequest
                );
                log.info("✅ Datasource configurado com sucesso");
            } finally {
                TenantContext.clear();
            }
            
            // ═══════════════════════════════════════════════════════════════════
            // FASE 3: ENFILEIRAR MIGRAÇÃO + SEED
            // ═══════════════════════════════════════════════════════════════════
            log.info("3️⃣  Enfileirando migração Flyway + MainSeed...");
            
            MigrationQueueTask migrationTask = 
                    migrationQueueService.enqueueTenantMigrationWithSeed(tenantCriado.getId());
            
            log.info("✅ Tarefa enfileirada (TaskID: {})", migrationTask.getTaskId());
            
            // ═══════════════════════════════════════════════════════════════════
            // FASE 4: INICIAR PROCESSAMENTO ASSÍNCRONO
            // ═══════════════════════════════════════════════════════════════════
            log.info("4️⃣  Iniciando processamento assíncrono...");
            
            migrationQueueService.processMigrationQueue();
            log.info("✅ Processamento iniciado em background");
            
            log.info("");
            log.info("╔════════════════════════════════════════════════════════════════╗");
            log.info("║  ✅ NOVO TENANT PROVISIONADO COM SUCESSO                      ║");
            log.info("╚════════════════════════════════════════════════════════════════╝");
            
            // Retorna resultado completo
            return new NewTenantProvisionResult(
                    tenantCriado,
                    datasourceResponse,
                    migrationTask
            );
            
        } catch (Exception e) {
            log.error("❌ Erro ao provisionar novo tenant", e);
            throw e;
        }
    }
    
    /**
     * Classe para encapsular resultado do provisionamento
     */
    public static class NewTenantProvisionResult {
        private final Tenant tenant;
        private final TenantDatasourceResponse datasource;
        private final MigrationQueueTask migrationTask;
        
        public NewTenantProvisionResult(
                Tenant tenant,
                TenantDatasourceResponse datasource,
                MigrationQueueTask migrationTask) {
            this.tenant = tenant;
            this.datasource = datasource;
            this.migrationTask = migrationTask;
        }
        
        public Tenant getTenant() { return tenant; }
        public TenantDatasourceResponse getDatasource() { return datasource; }
        public MigrationQueueTask getMigrationTask() { return migrationTask; }
    }
}
