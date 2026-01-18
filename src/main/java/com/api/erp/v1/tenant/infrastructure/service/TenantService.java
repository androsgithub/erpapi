package com.api.erp.v1.tenant.infrastructure.service;

import com.api.erp.v1.features.endereco.domain.repository.EnderecoRepository;
import com.api.erp.v1.features.permissao.infrastructure.factory.PermissaoConfigUpdateEvent;
import com.api.erp.v1.tenant.application.dto.*;
import com.api.erp.v1.tenant.domain.entity.*;
import com.api.erp.v1.tenant.domain.repository.TenantRepository;
import com.api.erp.v1.tenant.domain.repository.TenantSchemaRepository;
import com.api.erp.v1.tenant.domain.service.ITenantService;
import com.api.erp.v1.shared.domain.exception.NotFoundException;
import com.api.erp.v1.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.shared.domain.valueobject.Email;
import com.api.erp.v1.shared.domain.valueobject.Telefone;
import com.api.erp.v1.shared.infrastructure.service.SecurityService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class TenantService implements ITenantService {
    @Autowired
    private TenantRepository tenantRepository;
    private final EnderecoRepository enderecoRepository;
    private final TenantSchemaRepository tenantDatasourceRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final SecurityService securityService;

    @Override
    public Tenant getDadosTenant(Long tenantId) {
        return tenantRepository.findById(tenantId).orElseThrow(() -> new NotFoundException("Tenant do tenant não encontrada"));
    }

    @Override
    public Tenant updateDadosTenant(Long tenantId, TenantRequest empresaRequest) {
        Tenant empresa = tenantRepository.findById(tenantId).orElse(null);
        empresa.setNome(empresaRequest.nome());
        empresa.setEmail(empresaRequest.email());
        empresa.setTelefone(empresaRequest.telefone());
        // Endereco comentado no Tenant entity
        // Endereco endereco = enderecoRepository.findById(empresaRequest.enderecoId()).orElse(null);
        // empresa.setEndereco(endereco);

        return tenantRepository.save(empresa);
    }

    @Override
    public Tenant updateClienteConfig(Long tenantId, ClienteConfigRequest clienteConfigRequest) {
        log.info("[EMPRESA SERVICE] Atualizando configuração de Cliente");
        Tenant empresa = tenantRepository.findById(Long.valueOf(tenantId)).orElse(null);
        if (empresa == null) {
            throw new IllegalStateException("Tenant do tenant não encontrada");
        }

        ClienteConfig clienteConfig = new ClienteConfig();
        clienteConfig.setClienteAuditEnabled(clienteConfigRequest.clienteAuditEnabled());
        clienteConfig.setClienteCacheEnabled(clienteConfigRequest.clienteCacheEnabled());
        clienteConfig.setClienteValidationEnabled(clienteConfigRequest.clienteValidationEnabled());
        clienteConfig.setClienteNotificationEnabled(clienteConfigRequest.clienteNotificationEnabled());
        clienteConfig.setClienteTenantCustomizationEnabled(clienteConfigRequest.clienteTenantCustomizationEnabled());
        empresa.getConfig().setClienteConfig(clienteConfig);

        Tenant empresaSalva = tenantRepository.save(empresa);

        // Obtém usuário atual para auditoria
        String usuario = obterUsuarioAtual();

        // Publica evento para recarregar decorators de Cliente
        log.info("[EMPRESA SERVICE] Publicando ClienteConfigUpdateEvent");

        return empresaSalva;
    }

    @Override
    public Tenant updateContatoConfig(Long tenantId, ContatoConfigRequest contatoConfigRequest) {
        log.info("[EMPRESA SERVICE] Atualizando configuração de Contato");
        Tenant empresa = tenantRepository.findById(Long.valueOf(tenantId)).orElse(null);
        if (empresa == null) {
            throw new IllegalStateException("Tenant do tenant não encontrada");
        }

        ContatoConfig contatoConfig = new ContatoConfig();
        contatoConfig.setContatoAuditEnabled(contatoConfigRequest.contatoAuditEnabled());
        contatoConfig.setContatoCacheEnabled(contatoConfigRequest.contatoCacheEnabled());
        contatoConfig.setContatoValidationEnabled(contatoConfigRequest.contatoValidationEnabled());
        contatoConfig.setContatoNotificationEnabled(contatoConfigRequest.contatoNotificationEnabled());
        empresa.getConfig().setContatoConfig(contatoConfig);

        Tenant empresaSalva = tenantRepository.save(empresa);

        return empresaSalva;
    }

    @Override
    public Tenant updateEnderecoConfig(Long tenantId, EnderecoConfigRequest enderecoConfigRequest) {
        log.info("[EMPRESA SERVICE] Atualizando configuração de Endereco");


        Tenant empresa = tenantRepository.findById(Long.valueOf(tenantId)).orElse(null);
        if (empresa == null) {
            throw new IllegalStateException("Tenant do tenant não encontrada");
        }

        EnderecoConfig enderecoConfig = new EnderecoConfig();
        enderecoConfig.setEnderecoAuditEnabled(enderecoConfigRequest.enderecoAuditEnabled());
        enderecoConfig.setEnderecoCacheEnabled(enderecoConfigRequest.enderecoCacheEnabled());
        enderecoConfig.setEnderecoValidationEnabled(enderecoConfigRequest.enderecoValidationEnabled());
        empresa.getConfig().setEnderecoConfig(enderecoConfig);

        Tenant empresaSalva = tenantRepository.save(empresa);

        return empresaSalva;
    }

    @Override
    public Tenant updateUsuarioConfig(Long tenantId, UsuarioConfigRequest usuarioConfigRequest) {
        log.info("[EMPRESA SERVICE] Atualizando configuração de Usuario");
        Tenant empresa = tenantRepository.findById(Long.valueOf(tenantId)).orElse(null);
        if (empresa == null) {
            throw new IllegalStateException("Tenant do tenant não encontrada");
        }

        UsuarioConfig usuarioConfig = new UsuarioConfig();
        usuarioConfig.setUsuarioApprovalRequired(usuarioConfigRequest.usuarioApprovalRequired());
        usuarioConfig.setUsuarioCorporateEmailRequired(usuarioConfigRequest.usuarioCorporateEmailRequired());
        usuarioConfig.setAllowedEmailDomains(usuarioConfigRequest.allowedEmailDomains());
        empresa.getConfig().setUsuarioConfig(usuarioConfig);

        Tenant empresaSalva = tenantRepository.save(empresa);

        return empresaSalva;
    }

    @Override
    public Tenant updatePermissaoConfig(Long tenantId, PermissaoConfigRequest permissaoConfigRequest) {
        log.info("[EMPRESA SERVICE] Atualizando configuração de Permissao");
        Tenant empresa = tenantRepository.findById(Long.valueOf(tenantId)).orElse(null);
        if (empresa == null) {
            throw new IllegalStateException("Tenant do tenant não encontrada");
        }

        PermissaoConfig permissaoConfig = new PermissaoConfig();
        permissaoConfig.setPermissaoAuditEnabled(permissaoConfigRequest.permissaoAuditEnabled());
        permissaoConfig.setPermissaoCacheEnabled(permissaoConfigRequest.permissaoCacheEnabled());
        permissaoConfig.setPermissaoValidationEnabled(permissaoConfigRequest.permissaoValidationEnabled());
        empresa.getConfig().setPermissaoConfig(permissaoConfig);

        Tenant empresaSalva = tenantRepository.save(empresa);

        // Obtém usuário atual para auditoria
        String usuario = obterUsuarioAtual();

        // Publica evento para recarregar decorators de Permissao
        log.info("[EMPRESA SERVICE] Publicando PermissaoConfigUpdateEvent");
        eventPublisher.publishEvent(new PermissaoConfigUpdateEvent(
                this,
                permissaoConfig,
                empresa.getId(),
                usuario
        ));

        return empresaSalva;
    }

    @Override
    public Tenant updateInternalTenantConfig(Long tenantId, InternalTenantConfigRequest internalTenantConfigRequest) {
        log.info("[EMPRESA SERVICE] Atualizando configuração de Tenant");
        Tenant empresa = tenantRepository.findById(Long.valueOf(tenantId)).orElse(null);
        if (empresa == null) {
            throw new IllegalStateException("Tenant do tenant não encontrada");
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
    private String obterUsuarioAtual() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                return auth.getName();
            }
        } catch (Exception e) {
            log.debug("[EMPRESA SERVICE] Erro ao obter usuário atual", e);
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

        // Validar se já existe empresa com esse CNPJ
        // Aqui você pode adicionar uma validação se necessário

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

        // Criar configuração padrão da empresa
        TenantConfig empresaConfig = new TenantConfig();

        // Configurar TenantConfig com o tipo de tenant
        InternalTenantConfig tenantConfig = new InternalTenantConfig();
        tenantConfig.setTenantType(request.tenantType());
        tenantConfig.setTenantSubdomain(request.tenantSubdomain() != null ? request.tenantSubdomain() : request.tenantType().getCode());
        tenantConfig.setTenantFeaturesEnabled(true);
        empresaConfig.setInternalTenantConfig(tenantConfig);

        // Adicionar outras configurações padrão
        empresaConfig.setClienteConfig(new ClienteConfig());
        empresaConfig.setContatoConfig(new ContatoConfig());
        empresaConfig.setEnderecoConfig(new EnderecoConfig());
        empresaConfig.setPermissaoConfig(new PermissaoConfig());
        empresaConfig.setUsuarioConfig(new UsuarioConfig());

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
                .orElseThrow(() -> new IllegalArgumentException("Tenant não encontrada: " + tenantId));

        // Marcar como inativa ao invés de deletar (soft delete)
        empresa.setAtiva(false);
        tenantRepository.save(empresa);

        // Também desativar o datasource associado
        tenantDatasourceRepository.findByTenantIdAndIsActiveTrue(tenantId)
                .ifPresent(tenantDs -> {
                    tenantDs.setIsActive(false);
                    tenantDatasourceRepository.save(tenantDs);
                    log.info("[EMPRESA SERVICE] Datasource desativado para empresa: {}", tenantId);
                });

        log.info("[EMPRESA SERVICE] Tenant deletada (inativada) com sucesso: {}", tenantId);
    }
}
