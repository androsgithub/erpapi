package com.api.erp.v1.main.tenant.infrastructure.service;

import com.api.erp.v1.main.features.address.domain.repository.AddressRepository;
import com.api.erp.v1.main.features.permission.infrastructure.factory.PermissionConfigUpdateEvent;
import com.api.erp.v1.main.shared.common.error.ErrorHandler;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import com.api.erp.v1.main.shared.common.error.TenantErrorMessage;
import com.api.erp.v1.main.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.main.shared.domain.valueobject.Email;
import com.api.erp.v1.main.shared.domain.valueobject.Telefone;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import com.api.erp.v1.main.tenant.application.dto.*;
import com.api.erp.v1.main.tenant.domain.entity.configs.*;
import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.tenant.domain.entity.TenantDadosFiscais;
import com.api.erp.v1.main.tenant.domain.repository.TenantDatasourceRepository;
import com.api.erp.v1.main.tenant.domain.repository.TenantRepository;
import com.api.erp.v1.main.tenant.domain.service.ITenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public Tenant updateCustomerConfig(Long tenantId, CustomerConfigRequest customerConfigRequest) {
        log.info("[TENANT SERVICE] Updating Customer configuration");
            Tenant empresa = tenantRepository.findById(tenantId).orElse(null);
        if (empresa == null) {
            throw new ErrorHandler(TenantErrorMessage.TENANT_NOT_FOUND);
        }

        CustomerConfig customerConfig = new CustomerConfig();
        customerConfig.setCustomerAuditEnabled(customerConfigRequest.customerAuditEnabled());
        customerConfig.setCustomerCacheEnabled(customerConfigRequest.customerCacheEnabled());
        customerConfig.setCustomerValidationEnabled(customerConfigRequest.customerValidationEnabled());
        customerConfig.setCustomerNotificationEnabled(customerConfigRequest.customerNotificationEnabled());
        customerConfig.setCustomerTenantCustomizationEnabled(customerConfigRequest.customerTenantCustomizationEnabled());
        empresa.getConfig().setCustomerConfig(customerConfig);

        Tenant empresaSalva = tenantRepository.save(empresa);

        // Get current user for audit
        String user = obterUserAtual();

        // Publica evento para recarregar decorators de Customer
        log.info("[TENANT SERVICE] Publishing CustomerConfigUpdateEvent");

        return empresaSalva;
    }

    @Override
    public Tenant updateContactConfig(Long tenantId, ContactConfigRequest contactConfigRequest) {
        log.info("[TENANT SERVICE] Updating Contact configuration");
        Tenant empresa = tenantRepository.findById(tenantId).orElse(null);
        if (empresa == null) {
            throw new ErrorHandler(TenantErrorMessage.TENANT_NOT_FOUND);
        }

        ContactConfig contactConfig = new ContactConfig();
        contactConfig.setContactAuditEnabled(contactConfigRequest.contactAuditEnabled());
        contactConfig.setContactCacheEnabled(contactConfigRequest.contactCacheEnabled());
        contactConfig.setContactValidationEnabled(contactConfigRequest.contactValidationEnabled());
        contactConfig.setContactNotificationEnabled(contactConfigRequest.contactNotificationEnabled());
        empresa.getConfig().setContactConfig(contactConfig);

        Tenant empresaSalva = tenantRepository.save(empresa);

        return empresaSalva;
    }

    @Override
    public Tenant updateAddressConfig(Long tenantId, AddressConfigRequest addressConfigRequest) {
        log.info("[TENANT SERVICE] Updating Address configuration");


        Tenant empresa = tenantRepository.findById(tenantId).orElse(null);
        if (empresa == null) {
            throw new ErrorHandler(TenantErrorMessage.TENANT_NOT_FOUND);
        }

        AddressConfig addressConfig = new AddressConfig();
        addressConfig.setAddressAuditEnabled(addressConfigRequest.addressAuditEnabled());
        addressConfig.setAddressCacheEnabled(addressConfigRequest.addressCacheEnabled());
        addressConfig.setAddressValidationEnabled(addressConfigRequest.addressValidationEnabled());
        empresa.getConfig().setAddressConfig(addressConfig);

        Tenant empresaSalva = tenantRepository.save(empresa);

        return empresaSalva;
    }

    @Override
    public Tenant updateUserConfig(Long tenantId, UserConfigRequest userConfigRequest) {
        log.info("[TENANT SERVICE] Updating User configuration");
        Tenant empresa = tenantRepository.findById(tenantId).orElse(null);
        if (empresa == null) {
            throw new ErrorHandler(TenantErrorMessage.TENANT_NOT_FOUND);
        }

        UserConfig userConfig = new UserConfig();
        userConfig.setUserApprovalRequired(userConfigRequest.userApprovalRequired());
        userConfig.setUserCorporateEmailRequired(userConfigRequest.userCorporateEmailRequired());
        userConfig.setAllowedEmailDomains(userConfigRequest.allowedEmailDomains());
        empresa.getConfig().setUserConfig(userConfig);

        Tenant empresaSalva = tenantRepository.save(empresa);

        return empresaSalva;
    }

    @Override
    public Tenant updatePermissionConfig(Long tenantId, PermissionConfigRequest permissionConfigRequest) {
        log.info("[TENANT SERVICE] Updating Permission configuration");
        Tenant empresa = tenantRepository.findById(tenantId).orElse(null);
        if (empresa == null) {
            throw new ErrorHandler(TenantErrorMessage.TENANT_NOT_FOUND);
        }

        PermissionConfig permissionConfig = new PermissionConfig();
        permissionConfig.setPermissionAuditEnabled(permissionConfigRequest.permissionAuditEnabled());
        permissionConfig.setPermissionCacheEnabled(permissionConfigRequest.permissionCacheEnabled());
        permissionConfig.setPermissionValidationEnabled(permissionConfigRequest.permissionValidationEnabled());
        empresa.getConfig().setPermissionConfig(permissionConfig);

        Tenant empresaSalva = tenantRepository.save(empresa);

        // Get current user for audit
        String user = obterUserAtual();

        // Publish event to reload Permission decorators
        log.info("[TENANT SERVICE] Publishing PermissionConfigUpdateEvent");
        eventPublisher.publishEvent(new PermissionConfigUpdateEvent(
                this,
                permissionConfig,
                empresa.getId(),
                user
        ));

        return empresaSalva;
    }

    @Override
    public Tenant updateInternalTenantConfig(Long tenantId, InternalTenantConfigRequest internalTenantConfigRequest) {
        log.info("[TENANT SERVICE] Updating Tenant configuration");
        Tenant empresa = tenantRepository.findById(tenantId).orElse(null);
        if (empresa == null) {
            throw new ErrorHandler(TenantErrorMessage.TENANT_NOT_FOUND);
        }

        InternalTenantConfig tenantConfig = new InternalTenantConfig();
        tenantConfig.setTenantCustomCode(internalTenantConfigRequest.tenantCustomCode());
        tenantConfig.setTenantType(internalTenantConfigRequest.tenantType());
        tenantConfig.setTenantSubdomain(internalTenantConfigRequest.tenantSubdomain());
        tenantConfig.setTenantFeaturesEnabled(internalTenantConfigRequest.tenantFeaturesEnabled());
        empresa.getConfig().setInternalTenantConfig(tenantConfig);

        Tenant empresaSalva = tenantRepository.save(empresa);


        return empresaSalva;
    }

    /**
     * Obtém o usuário atual do contexto de segurança.
     * Retorna "SYSTEM" se não houver usuário autenticado.
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
    public Tenant criarTenant(CriarTenantRequest request) {
        log.info("[EMPRESA SERVICE] Criando nova empresa: {}", request.nome());

        // Check if company with this CNPJ already exists
        // Here you can add a validation if necessary

        // Criar nova empresa
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

        // Configurar TenantConfig com o tipo de tenant
        InternalTenantConfig tenantConfig = new InternalTenantConfig();
        tenantConfig.setTenantType(request.tenantType());
        tenantConfig.setTenantSubdomain(request.tenantSubdomain() != null ? request.tenantSubdomain() : request.tenantType().getCode());
        tenantConfig.setTenantFeaturesEnabled(true);
        empresaConfig.setInternalTenantConfig(tenantConfig);

        // Add other default configurations
        empresaConfig.setCustomerConfig(new CustomerConfig());
        empresaConfig.setContactConfig(new ContactConfig());
        empresaConfig.setAddressConfig(new AddressConfig());
        empresaConfig.setPermissionConfig(new PermissionConfig());
        empresaConfig.setUserConfig(new UserConfig());

        empresa.setConfig(empresaConfig);

        // Salvar empresa
        Tenant empresaSalva = tenantRepository.save(empresa);

        log.info("[EMPRESA SERVICE] Tenant criada com sucesso: {} (ID: {})",
                request.nome(), empresaSalva.getId());

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
