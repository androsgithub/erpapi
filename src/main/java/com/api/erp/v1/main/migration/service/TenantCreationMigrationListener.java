package com.api.erp.v1.main.migration.service;

import com.api.erp.v1.main.migration.domain.TenantCreatedEvent;
import com.api.erp.v1.main.migration.domain.TenantMigrationEvent;
import com.api.erp.v1.main.master.tenant.domain.repository.TenantDatasourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * SERVICE - Listener para Enfileiramento Automático de Novo Tenant
 * 
 * Responsável por:
 * - Escutar eventos de criação de novo tenant
 * - Enfileirar automaticamente para migração
 * - Disparar o consumidor se necessário
 * 
 * Integração automática:
 * Quando TenantService.criarTenant() cria um novo tenant, este listener
 * é acionado automaticamente e enfileira o tenant na fila unificada.
 * 
 * @author ERP System
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TenantCreationMigrationListener {
    
    private final TenantMigrationQueue migrationQueue;
    private final TenantDatasourceRepository tenantDatasourceRepository;
    
    /**
     * Enfileira um tenant para migração quando ele é criado
     */
    @EventListener
    public void onTenantCreated(TenantCreatedEvent event) {
        var tenant = event.getTenant();
        Long tenantId = tenant.getId();
        String tenantName = tenant.getName();
        
        log.info("");
        log.info("✅ [{}] Novo tenant criado: {}", tenantId, tenantName);
        log.info("   Enqueueing for migration...");
        
        try {
            // Busca o datasource do tenant
            var datasource = tenantDatasourceRepository
                    .findByTenantIdAndActiveTrue(tenantId).orElse(null);
            
            if (datasource == null) {
                log.warn("⚠️ [{}] Tenant {} does not have datasource configured yet", 
                        tenantId, tenantName);
                log.info("   ℹ️ Datasource must be created before migration");
                return;
            }
            
            // Enfileira o novo tenant
            TenantMigrationEvent migrationEvent = migrationQueue.enqueueEvent(
                    tenantId,
                    tenantName,
                    datasource,
                    TenantMigrationEvent.MigrationEventSource.TENANT_CREATION
            );
            
            log.info("📥 [{}] Tenant enqueued for migration", migrationEvent.getEventId());
            log.info("   Status: {} (will be processed shortly)", 
                    migrationEvent.getStatus().getLabel());
            log.info("");
            
        } catch (Exception e) {
            log.error("❌ [{}] Erro ao enfileirar tenant para migração: {}", 
                    tenantId, e.getMessage(), e);
            log.warn("⚠️ [{}] Tenant foi criado, mas a migração não foi enfileirada", tenantId);
            log.warn("   Dica: Use endpoints de admin para enfileirar manualmente");
        }
    }
}
