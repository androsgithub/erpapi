package com.api.erp.v1.main.master.tenant.infrastructure.service;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.migration.domain.TenantMigrationEvent;
import com.api.erp.v1.main.migration.service.TenantMigrationQueue;
import com.api.erp.v1.main.master.tenant.application.dto.CreateNewTenantWithDatasourceRequest;
import com.api.erp.v1.main.master.tenant.application.dto.CreateTenantRequest;
import com.api.erp.v1.main.master.tenant.application.dto.TenantDatasourceRequest;
import com.api.erp.v1.main.master.tenant.application.dto.TenantDatasourceResponse;
import com.api.erp.v1.main.master.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.master.tenant.domain.repository.TenantDatasourceRepository;
import com.api.erp.v1.main.master.tenant.domain.service.ITenantDatasourceService;
import com.api.erp.v1.main.master.tenant.domain.service.ITenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * INFRASTRUCTURE - New Tenant Provisioning Service
 * 
 * Orchestrates the entire process of creating a new complete tenant with datasource,
 * migrations and seeders, hiding controller complexity.
 * 
 * Responsibilities:
 * - Create tenant no master database
 * - Configure datasource
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
    private final TenantMigrationQueue migrationQueue;
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
            // PHASE 1: CRIAR TENANT NO MASTER DATABASE
            // ═══════════════════════════════════════════════════════════════════
            log.info("1️⃣  Criando tenant no master database...");
            
            CreateTenantRequest criarRequest = new CreateTenantRequest(
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
            log.info("✅ Tenant criado: {} (ID: {})", tenantCriado.getName(), tenantCriado.getId());
            
            // ═══════════════════════════════════════════════════════════════════
            // PHASE 2: CONFIGURAR DATASOURCE DO TENANT
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
            // PHASE 3: ENFILEIRAR MIGRAÇÃO + SEED (NOVO SISTEMA)
            // ═══════════════════════════════════════════════════════════════════
            log.info("3️⃣  Enqueueing Flyway migration + MainSeed...");
            
            TenantMigrationEvent migrationEvent = migrationQueue.enqueueEvent(
                    tenantCriado.getId(),
                    tenantCriado.getName(),
                    datasourceResponse != null ? tenantDatasourceRepository
                            .findByTenantIdAndActiveTrue(tenantCriado.getId()).orElse(null) : null,
                    TenantMigrationEvent.MigrationEventSource.MANUAL_REQUEST
            );
            
            log.info("✅ Evento enfileirado (EventID: {})", migrationEvent.getEventId());
            log.info("⏳ Status: {} (will be processed automatically)", migrationEvent.getStatus().getLabel());
            
            log.info("");
            log.info("╔════════════════════════════════════════════════════════════════╗");
            log.info("║         ✅ NOVO TENANT PROVISIONADO COM SUCESSO                ║");
            log.info("╚════════════════════════════════════════════════════════════════╝");
            
            // Returns resultado completo
            return new NewTenantProvisionResult(
                    tenantCriado,
                    datasourceResponse,
                    migrationEvent
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
        private final TenantMigrationEvent migrationEvent;
        
        public NewTenantProvisionResult(
                Tenant tenant,
                TenantDatasourceResponse datasource) {
            this.tenant = tenant;
            this.datasource = datasource;
            this.migrationEvent = null;
        }
        
        public NewTenantProvisionResult(
                Tenant tenant,
                TenantDatasourceResponse datasource,
                TenantMigrationEvent migrationEvent) {
            this.tenant = tenant;
            this.datasource = datasource;
            this.migrationEvent = migrationEvent;
        }
        
        public Tenant getTenant() { return tenant; }
        public TenantDatasourceResponse getDatasource() { return datasource; }
        public TenantMigrationEvent getMigrationEvent() { return migrationEvent; }
    }
}
