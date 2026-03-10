package com.api.erp.v1.main.tenant.infrastructure.service;

import com.api.erp.v1.main.features.address.domain.repository.AddressRepository;
import com.api.erp.v1.main.features.permission.infrastructure.factory.PermissionConfigUpdateEvent;
import com.api.erp.v1.main.shared.common.error.ErrorHandler;
import com.api.erp.v1.main.shared.common.error.TenantErrorMessage;
import com.api.erp.v1.main.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.main.shared.domain.valueobject.Email;
import com.api.erp.v1.main.shared.domain.valueobject.Telefone;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import com.api.erp.v1.main.tenant.application.dto.CreateTenantRequest;
import com.api.erp.v1.main.tenant.application.dto.TenantRequest;
import com.api.erp.v1.main.tenant.application.dto.UnifiedTenantConfigRequest;
import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.tenant.domain.entity.TenantDadosFiscais;
import com.api.erp.v1.main.tenant.domain.entity.configs.*;
import com.api.erp.v1.main.tenant.domain.repository.TenantDatasourceRepository;
import com.api.erp.v1.main.tenant.domain.repository.TenantRepository;
import com.api.erp.v1.main.tenant.domain.service.ITenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final AddressRepository addressRepository;
    private final TenantDatasourceRepository tenantDatasourceRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final SecurityService securityService;

    @Autowired
    public TenantService(TenantRepository tenantRepository, AddressRepository addressRepository, TenantDatasourceRepository tenantDatasourceRepository, ApplicationEventPublisher eventPublisher, SecurityService securityService) {
        this.tenantRepository = tenantRepository;
        this.addressRepository = addressRepository;
        this.tenantDatasourceRepository = tenantDatasourceRepository;
        this.eventPublisher = eventPublisher;
        this.securityService = securityService;
    }

    @Override
    public Tenant getDadosTenant(Long tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ErrorHandler(TenantErrorMessage.TENANT_NOT_FOUND));
    }

    @Override
    public Tenant updateDadosTenant(Long tenantId, TenantRequest empresaRequest) {
        Tenant empresa = tenantRepository.findById(tenantId).orElse(null);
        empresa.setNome(empresaRequest.nome());
        empresa.setEmail(empresaRequest.email());
        empresa.setTelefone(empresaRequest.telefone());
        // Address comentado no Tenant entity
        // Address address = addressRepository.findById(empresaRequest.addressId()).orElse(null);
        // empresa.setAddress(address);

        return tenantRepository.save(empresa);
    }

    /**
     * Gets current user from security context.
     * Returns "SYSTEM" if no authenticated user.
     */
    private String obterUserAtual() {
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

    @Override
    @Cacheable(
            value = "tenant-config",
            unless = "#result == null"
    )
    public TenantConfig getTenantConfig(Long tenantId) {
        Tenant empresa = tenantRepository.findById(tenantId).orElse(null);
        if (empresa == null) {
            return new TenantConfig();
        }
        return empresa.getConfig();
    }

    @Override
    public boolean isTenantAtiva(Long tenantId) {
        return tenantRepository.existsByIdAndAtivaTrue(tenantId);
    }

    @Override
    @Transactional(transactionManager = "masterTransactionManager")
    public Tenant criarTenant(CreateTenantRequest request) {
        log.info("[EMPRESA SERVICE] Criando nova empresa: {}", request.nome());

        // Check if company with this CNPJ already exists
        // Here you can add a validation if necessary

        // Create nova empresa
        Tenant empresa = new Tenant();
        empresa.setNome(request.nome());
        empresa.setEmail(new Email(request.email()));
        empresa.setTelefone(new Telefone(request.telefone()));
        empresa.setAtiva(true);

        // Adicionar dados fiscais
        TenantDadosFiscais empresaDadosFiscais = new TenantDadosFiscais();
        empresaDadosFiscais.setCnpj(new CNPJ(request.cnpj()));
        empresaDadosFiscais.setRazaoSocial(request.razaoSocial());
        empresaDadosFiscais.setContribuinteICMS(request.contribuinteICMS());
        empresaDadosFiscais.setRegimeTributario(request.regimeTributario());
        empresa.setDadosFiscais(empresaDadosFiscais);

        // Create default company configuration
        TenantConfig empresaConfig = new TenantConfig();

        // Configure TenantConfig com o tipo de tenant
        InternalTenantConfig tenantConfig = new InternalTenantConfig();
        tenantConfig.setTenantType(request.tenantType());
        tenantConfig.setTenantSubdomain(request.tenantSubdomain() != null ? request.tenantSubdomain() : request.tenantType().getCode());
        tenantConfig.setTenantFeaturesEnabled(true);
        empresaConfig.setInternalTenantConfig(tenantConfig);

        // Add other default configurations
        empresaConfig.setBusinessPartnerConfig(new BusinessPartnerConfig());
        empresaConfig.setContactConfig(new ContactConfig());
        empresaConfig.setAddressConfig(new AddressConfig());
        empresaConfig.setPermissionConfig(new PermissionConfig());
        empresaConfig.setUserConfig(new UserConfig());

        empresa.setConfig(empresaConfig);

        // Salvar empresa
        Tenant empresaSalva = tenantRepository.save(empresa);

        log.info("[EMPRESA SERVICE] Tenant criada com sucesso: {} (ID: {})",
                request.nome(), empresaSalva.getId());

        // Publicar evento de criação para disparar migração automática
        try {
            com.api.erp.v1.main.migration.domain.TenantCreatedEvent tenantCreatedEvent =
                    new com.api.erp.v1.main.migration.domain.TenantCreatedEvent(this, empresaSalva);
            eventPublisher.publishEvent(tenantCreatedEvent);
            log.debug("[EMPRESA SERVICE] Evento TenantCreatedEvent publicado para tenant: {}", empresaSalva.getId());
        } catch (Exception e) {
            log.warn("[EMPRESA SERVICE] Error publishing tenant creation event: {}", e.getMessage());
            // Continua mesmo se o evento não for publicado
        }

        return empresaSalva;
    }

    @Override
    public List<Tenant> listarTenants() {
        log.info("[EMPRESA SERVICE] Listando todas as empresas ativas");
        return tenantRepository.findAll()
                .stream()
                .filter(Tenant::isAtiva)
                .toList();
    }

    @Override
    public Tenant updateConfig(Long tenantId, UnifiedTenantConfigRequest request) {
        log.info("[TENANT SERVICE] ═══════════════════════════════════════════════════");
        log.info("[TENANT SERVICE] Updating Unified Tenant Configuration");
        log.info("[TENANT SERVICE] Tenant ID: {}", tenantId);
        log.info("[TENANT SERVICE] ═══════════════════════════════════════════════════");

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ErrorHandler(TenantErrorMessage.TENANT_NOT_FOUND));

        TenantConfig tenantConfig = tenant.getConfig();

        // BUSINESSPARTNER CONFIG
        if (hasBusinessPartnerConfig(request)) {
            log.info("[TENANT SERVICE] Updating BusinessPartner Config");
            BusinessPartnerConfig businesspartnerConfig = new BusinessPartnerConfig();
            if (request.businesspartnerValidationEnabled() != null)
                businesspartnerConfig.setBusinessPartnerValidationEnabled(request.businesspartnerValidationEnabled());
            if (request.businesspartnerAuditEnabled() != null)
                businesspartnerConfig.setBusinessPartnerAuditEnabled(request.businesspartnerAuditEnabled());
            if (request.businesspartnerCacheEnabled() != null)
                businesspartnerConfig.setBusinessPartnerCacheEnabled(request.businesspartnerCacheEnabled());
            if (request.businesspartnerNotificationEnabled() != null)
                businesspartnerConfig.setBusinessPartnerNotificationEnabled(request.businesspartnerNotificationEnabled());
            if (request.businesspartnerTenantCustomizationEnabled() != null)
                businesspartnerConfig.setBusinessPartnerTenantCustomizationEnabled(request.businesspartnerTenantCustomizationEnabled());
            tenantConfig.setBusinessPartnerConfig(businesspartnerConfig);
        }

        // USER CONFIG
        if (hasUserConfig(request)) {
            log.info("[TENANT SERVICE] Updating User Config");
            UserConfig userConfig = new UserConfig();
            if (request.userApprovalRequired() != null)
                userConfig.setUserApprovalRequired(request.userApprovalRequired());
            if (request.userCorporateEmailRequired() != null)
                userConfig.setUserCorporateEmailRequired(request.userCorporateEmailRequired());
            if (request.allowedEmailDomains() != null)
                userConfig.setAllowedEmailDomains(request.allowedEmailDomains());
            tenantConfig.setUserConfig(userConfig);
        }

        // PERMISSION CONFIG
        if (hasPermissionConfig(request)) {
            log.info("[TENANT SERVICE] Updating Permission Config");
            PermissionConfig permissionConfig = new PermissionConfig();
            if (request.permissionValidationEnabled() != null)
                permissionConfig.setPermissionValidationEnabled(request.permissionValidationEnabled());
            if (request.permissionCacheEnabled() != null)
                permissionConfig.setPermissionCacheEnabled(request.permissionCacheEnabled());
            if (request.permissionAuditEnabled() != null)
                permissionConfig.setPermissionAuditEnabled(request.permissionAuditEnabled());
            tenantConfig.setPermissionConfig(permissionConfig);

            // Publish event for Permission decorators
            String user = obterUserAtual();
            eventPublisher.publishEvent(new PermissionConfigUpdateEvent(
                    this,
                    permissionConfig,
                    tenantId,
                    user
            ));
        }

        // INTERNAL TENANT CONFIG
        if (hasInternalTenantConfig(request)) {
            log.info("[TENANT SERVICE] Updating Internal Tenant Config");
            InternalTenantConfig internalTenantConfig = new InternalTenantConfig();
            if (request.tenantType() != null)
                internalTenantConfig.setTenantType(request.tenantType());
            if (request.tenantSubdomain() != null)
                internalTenantConfig.setTenantSubdomain(request.tenantSubdomain());
            if (request.tenantCustomCode() != null)
                internalTenantConfig.setTenantCustomCode(request.tenantCustomCode());
            if (request.tenantFeaturesEnabled() != null)
                internalTenantConfig.setTenantFeaturesEnabled(request.tenantFeaturesEnabled());
            tenantConfig.setInternalTenantConfig(internalTenantConfig);
        }

        // ADDRESS CONFIG
        if (hasAddressConfig(request)) {
            log.info("[TENANT SERVICE] Updating Address Config");
            AddressConfig addressConfig = new AddressConfig();
            if (request.addressValidationEnabled() != null)
                addressConfig.setAddressValidationEnabled(request.addressValidationEnabled());
            if (request.addressAuditEnabled() != null)
                addressConfig.setAddressAuditEnabled(request.addressAuditEnabled());
            if (request.addressCacheEnabled() != null)
                addressConfig.setAddressCacheEnabled(request.addressCacheEnabled());
            tenantConfig.setAddressConfig(addressConfig);
        }

        // CONTACT CONFIG
        if (hasContactConfig(request)) {
            log.info("[TENANT SERVICE] Updating Contact Config");
            ContactConfig contactConfig = new ContactConfig();
            if (request.contactValidationEnabled() != null)
                contactConfig.setContactValidationEnabled(request.contactValidationEnabled());
            if (request.contactAuditEnabled() != null)
                contactConfig.setContactAuditEnabled(request.contactAuditEnabled());
            if (request.contactCacheEnabled() != null)
                contactConfig.setContactCacheEnabled(request.contactCacheEnabled());
            if (request.contactNotificationEnabled() != null)
                contactConfig.setContactNotificationEnabled(request.contactNotificationEnabled());
            tenantConfig.setContactConfig(contactConfig);
        }

        Tenant tenantSalvo = tenantRepository.save(tenant);
        log.info("[TENANT SERVICE] ✅ Configurations updated successfully for tenant: {}", tenantId);

        return tenantSalvo;
    }

    // HELPER METHODS para detectar quais configs foram preenchidas
    private boolean hasBusinessPartnerConfig(UnifiedTenantConfigRequest request) {
        return request.businesspartnerValidationEnabled() != null ||
                request.businesspartnerAuditEnabled() != null ||
                request.businesspartnerCacheEnabled() != null ||
                request.businesspartnerNotificationEnabled() != null ||
                request.businesspartnerTenantCustomizationEnabled() != null;
    }

    private boolean hasUserConfig(UnifiedTenantConfigRequest request) {
        return request.userApprovalRequired() != null ||
                request.userCorporateEmailRequired() != null ||
                request.allowedEmailDomains() != null;
    }

    private boolean hasPermissionConfig(UnifiedTenantConfigRequest request) {
        return request.permissionValidationEnabled() != null ||
                request.permissionCacheEnabled() != null ||
                request.permissionAuditEnabled() != null;
    }

    private boolean hasInternalTenantConfig(UnifiedTenantConfigRequest request) {
        return request.tenantType() != null ||
                request.tenantSubdomain() != null ||
                request.tenantCustomCode() != null ||
                request.tenantFeaturesEnabled() != null;
    }

    private boolean hasAddressConfig(UnifiedTenantConfigRequest request) {
        return request.addressValidationEnabled() != null ||
                request.addressAuditEnabled() != null ||
                request.addressCacheEnabled() != null;
    }

    private boolean hasContactConfig(UnifiedTenantConfigRequest request) {
        return request.contactValidationEnabled() != null ||
                request.contactAuditEnabled() != null ||
                request.contactCacheEnabled() != null ||
                request.contactFormatValidationEnabled() != null ||
                request.contactNotificationEnabled() != null;
    }

    @Override
    @Transactional(transactionManager = "masterTransactionManager")
    public void deletarTenant(Long tenantId) {
        log.info("[EMPRESA SERVICE] Deletando empresa: {}", tenantId);

        Tenant empresa = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));

        // Mark as inactive instead of deleting (soft delete)
        empresa.setAtiva(false);
        tenantRepository.save(empresa);

        // Also deactivate the associated datasource
        tenantDatasourceRepository.findByTenant_IdAndIsActive(tenantId, true)
                .ifPresent(tenantDs -> {
                    tenantDs.setIsActive(false);
                    tenantDatasourceRepository.save(tenantDs);
                    log.info("[EMPRESA SERVICE] Datasource desativado para empresa: {}", tenantId);
                });

        log.info("[EMPRESA SERVICE] Tenant deletada (inativada) com sucesso: {}", tenantId);
    }
}
