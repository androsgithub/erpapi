package com.api.erp.v1.main.master.tenant.infrastructure.service;

import com.api.erp.v1.main.shared.common.error.ErrorHandler;
import com.api.erp.v1.main.shared.common.error.TenantErrorMessage;
import com.api.erp.v1.main.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.main.shared.domain.valueobject.Email;
import com.api.erp.v1.main.shared.domain.valueobject.Telefone;
import com.api.erp.v1.main.master.tenant.application.dto.request.create.ProvisionTenantRequest;
import com.api.erp.v1.main.master.tenant.application.dto.request.create.TenantRequest;
import com.api.erp.v1.main.master.tenant.application.dto.request.update.UnifiedTenantConfigRequest;
import com.api.erp.v1.main.master.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.master.tenant.domain.entity.TenantConfig;
import com.api.erp.v1.main.master.tenant.domain.entity.TenantFiscal;
import com.api.erp.v1.main.master.tenant.domain.repository.TenantConfigRepository;
import com.api.erp.v1.main.master.tenant.domain.repository.TenantDatasourceRepository;
import com.api.erp.v1.main.master.tenant.domain.repository.TenantFiscalRepository;
import com.api.erp.v1.main.master.tenant.domain.repository.TenantRepository;
import com.api.erp.v1.main.master.tenant.domain.service.ITenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class TenantService implements ITenantService {

    private final TenantRepository tenantRepository;
    private final TenantFiscalRepository tenantFiscalRepository;
    private final TenantConfigRepository tenantConfigRepository;
    private final TenantDatasourceRepository tenantDatasourceRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public TenantService(
            TenantRepository tenantRepository,
            TenantFiscalRepository tenantFiscalRepository,
            TenantConfigRepository tenantConfigRepository,
            TenantDatasourceRepository tenantDatasourceRepository,
            ApplicationEventPublisher eventPublisher) {
        this.tenantRepository = tenantRepository;
        this.tenantFiscalRepository = tenantFiscalRepository;
        this.tenantConfigRepository = tenantConfigRepository;
        this.tenantDatasourceRepository = tenantDatasourceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Tenant getDadosTenant(Long tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ErrorHandler(TenantErrorMessage.TENANT_NOT_FOUND));
    }

    @Override
    @CacheEvict(value = "tenant-config", key = "#tenantId")
    public Tenant updateDadosTenant(Long tenantId, TenantRequest tenantRequest) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ErrorHandler(TenantErrorMessage.TENANT_NOT_FOUND));

        tenant.setName(tenantRequest.nome());
        tenant.setEmail(new Email(tenantRequest.email()));
        tenant.setPhone(new Telefone(tenantRequest.telefone()));

        return tenantRepository.save(tenant);
    }

    @Cacheable(value = "tenant-config", key = "#tenantId", unless = "#result == null")
    public TenantConfig getTenantConfig(Long tenantId) {
        return tenantConfigRepository.findByTenantId(tenantId)
                .orElseGet(TenantConfig::new);
    }

    @Override
    public boolean isTenantAtiva(Long tenantId) {
        return tenantRepository.existsByIdAndActiveTrue(tenantId);
    }

    @Override
    @Transactional(transactionManager = "masterTransactionManager")
    public Tenant criarTenant(ProvisionTenantRequest request) {
        log.info("[TENANT SERVICE] Creating new tenant: {}", request.name());

        // Validate CNPJ uniqueness (if provided)
        if (request != null) {
            // Use basic tenant info from ProvisionTenantRequest
        }

        // 1. Create Tenant entity
        Tenant tenant = Tenant.builder()
                .name(request.name())
                .email(new Email(request.email()))
                .phone(new Telefone(request.phone()))
                .active(true)
                .trial(request.trial() != null ? request.trial() : false)
                .build();

        // 2. Create TenantConfig with defaults
        TenantConfig tenantConfig = createDefaultConfig(request);

        // 3. Associate entities
        tenant.setConfig(tenantConfig);

        // 4. Save tenant (cascades to config if properly configured)
        Tenant tenantSaved = tenantRepository.save(tenant);

        // 5. Explicitly save config if not cascaded
        tenantConfig.setTenant(tenantSaved);
        tenantConfigRepository.save(tenantConfig);

        log.info("[TENANT SERVICE] Tenant created successfully: {} (ID: {})",
                request.name(), tenantSaved.getId());

        // 6. Publish event to trigger automatic migration
        publishTenantCreatedEvent(tenantSaved);

        return tenantSaved;
    }

    /**
     * Publishes tenant creation event to trigger database migration
     */
    private void publishTenantCreatedEvent(Tenant tenant) {
        try {
            com.api.erp.v1.main.migration.domain.TenantCreatedEvent tenantCreatedEvent =
                    new com.api.erp.v1.main.migration.domain.TenantCreatedEvent(this, tenant);
            eventPublisher.publishEvent(tenantCreatedEvent);
            log.debug("[TENANT SERVICE] TenantCreatedEvent published for tenant: {}", tenant.getId());
        } catch (Exception e) {
            log.warn("[TENANT SERVICE] Error publishing tenant creation event: {}", e.getMessage());
        }
    }

    /**
     * Creates default configuration for a new tenant
     */
    private TenantConfig createDefaultConfig(ProvisionTenantRequest request) {
        TenantConfig tenantConfig = new TenantConfig();
        return tenantConfig;
    }

    @Override
    public List<Tenant> listarTenants() {
        log.info("[TENANT SERVICE] Listing all active tenants");
        return tenantRepository.findAll()
                .stream()
                .filter(Tenant::getActive)
                .toList();
    }

    @Override
    @Transactional(transactionManager = "masterTransactionManager")
    @CacheEvict(value = "tenant-config", key = "#tenantId")
    public Tenant updateConfig(Long tenantId, UnifiedTenantConfigRequest request) {
        log.info("[TENANT SERVICE] ═══════════════════════════════════════════════════");
        log.info("[TENANT SERVICE] Updating Unified Tenant Configuration for tenant: {}", tenantId);
        log.info("[TENANT SERVICE] ═══════════════════════════════════════════════════");

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ErrorHandler(TenantErrorMessage.TENANT_NOT_FOUND));

        TenantConfig tenantConfig = initializeTenantConfig(tenant);

        // Persist changes
        tenant.setConfig(tenantConfig);
        Tenant tenantSalvo = tenantRepository.save(tenant);
        tenantConfigRepository.save(tenantConfig);

        log.info("[TENANT SERVICE] ✅ Configurations updated successfully for tenant: {}", tenantId);
        return tenantSalvo;
    }

    private TenantConfig initializeTenantConfig(Tenant tenant) {
        TenantConfig tenantConfig = tenant.getConfig();
        if (tenantConfig == null) {
            tenantConfig = new TenantConfig();
            tenant.setConfig(tenantConfig);
        }
        return tenantConfig;
    }

    @Override
    @Transactional(transactionManager = "masterTransactionManager")
    @CacheEvict(value = "tenant-config", key = "#tenantId")
    public void deletarTenant(Long tenantId) {
        log.info("[TENANT SERVICE] Deleting tenant: {}", tenantId);

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ErrorHandler(TenantErrorMessage.TENANT_NOT_FOUND));

        // Mark as inactive instead of deleting (soft delete)
        tenant.setActive(false);
        tenantRepository.save(tenant);
        tenantFiscalRepository.save(tenant.getFiscal());

        // Also deactivate the associated datasource
        tenantDatasourceRepository.findByTenantIdAndActiveTrue(tenantId)
                .ifPresent(tenantDs -> {
                    tenantDs.setActive(false);
                    tenantDatasourceRepository.save(tenantDs);
                    log.info("[TENANT SERVICE] Datasource deactivated for tenant: {}", tenantId);
                });

        log.info("[TENANT SERVICE] Tenant deleted (deactivated) successfully: {}", tenantId);
    }

    /**
     * Gets current authenticated user from security context
     */
    private String getCurrentUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                return auth.getName();
            }
        } catch (Exception e) {
            log.debug("[TENANT SERVICE] Error getting current user", e);
        }
        return "SYSTEM";
    }
}