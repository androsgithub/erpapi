package com.api.erp.v1.features.empresa.infrastructure.service;

import com.api.erp.v1.features.cliente.infrastructure.event.ClienteConfigUpdateEvent;
import com.api.erp.v1.features.contato.infrastructure.event.ContatoConfigUpdateEvent;
import com.api.erp.v1.features.empresa.application.dto.*;
import com.api.erp.v1.features.empresa.domain.entity.*;
import com.api.erp.v1.features.empresa.infrastructure.event.TenantConfigUpdateEvent;
import com.api.erp.v1.features.endereco.infrastructure.event.EnderecoConfigUpdateEvent;
import com.api.erp.v1.features.permissao.infrastructure.factory.PermissaoConfigUpdateEvent;
import com.api.erp.v1.features.empresa.domain.repository.EmpresaRepository;
import com.api.erp.v1.features.empresa.domain.service.IEmpresaService;
import com.api.erp.v1.features.endereco.domain.entity.Endereco;
import com.api.erp.v1.features.endereco.domain.repository.EnderecoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmpresaService implements IEmpresaService {

    private final EmpresaRepository empresaRepository;
    private final EnderecoRepository enderecoRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Empresa getDadosEmpresa() {
        return empresaRepository.findById(1L).orElse(null);
    }

    @Override
    public Empresa updateDadosEmpresa(EmpresaRequest empresaRequest) {
        Empresa empresa = empresaRepository.findById(1L).orElse(null);
        empresa.setNome(empresaRequest.nome());
        empresa.setEmail(empresaRequest.email());
        empresa.setTelefone(empresaRequest.telefone());
        Endereco endereco = enderecoRepository.findById(empresaRequest.enderecoId()).orElse(null);
        empresa.setEndereco(endereco);

        return empresaRepository.save(empresa);
    }

    @Override
    public Empresa updateClienteConfig(ClienteConfigRequest clienteConfigRequest) {
        log.info("[EMPRESA SERVICE] Atualizando configuração de Cliente");
        
        Empresa empresa = empresaRepository.findById(1L).orElse(null);
        if (empresa == null) {
            throw new IllegalStateException("Empresa padrão não encontrada");
        }

        ClienteConfig clienteConfig = new ClienteConfig();
        clienteConfig.setClienteAuditEnabled(clienteConfigRequest.clienteAuditEnabled());
        clienteConfig.setClienteCacheEnabled(clienteConfigRequest.clienteCacheEnabled());
        clienteConfig.setClienteValidationEnabled(clienteConfigRequest.clienteValidationEnabled());
        clienteConfig.setClienteNotificationEnabled(clienteConfigRequest.clienteNotificationEnabled());
        clienteConfig.setClienteTenantCustomizationEnabled(clienteConfigRequest.clienteTenantCustomizationEnabled());
        empresa.getConfig().setClienteConfig(clienteConfig);

        Empresa empresaSalva = empresaRepository.save(empresa);
        
        // Obtém usuário atual para auditoria
        String usuario = obterUsuarioAtual();
        
        // Publica evento para recarregar decorators de Cliente
        log.info("[EMPRESA SERVICE] Publicando ClienteConfigUpdateEvent");
        eventPublisher.publishEvent(new ClienteConfigUpdateEvent(
            this,
            clienteConfig,
            empresa.getId(),
            usuario
        ));

        return empresaSalva;
    }

    @Override
    public Empresa updateContatoConfig(ContatoConfigRequest contatoConfigRequest) {
        log.info("[EMPRESA SERVICE] Atualizando configuração de Contato");
        
        Empresa empresa = empresaRepository.findById(1L).orElse(null);
        if (empresa == null) {
            throw new IllegalStateException("Empresa padrão não encontrada");
        }
        
        ContatoConfig contatoConfig = new ContatoConfig();
        contatoConfig.setContatoAuditEnabled(contatoConfigRequest.contatoAuditEnabled());
        contatoConfig.setContatoCacheEnabled(contatoConfigRequest.contatoCacheEnabled());
        contatoConfig.setContatoValidationEnabled(contatoConfigRequest.contatoValidationEnabled());
        contatoConfig.setContatoFormatValidationEnabled(contatoConfigRequest.contatoFormatValidationEnabled());
        contatoConfig.setContatoNotificationEnabled(contatoConfigRequest.contatoNotificationEnabled());
        empresa.getConfig().setContatoConfig(contatoConfig);

        Empresa empresaSalva = empresaRepository.save(empresa);
        
        // Obtém usuário atual para auditoria
        String usuario = obterUsuarioAtual();
        
        // Publica evento para recarregar decorators de Contato
        log.info("[EMPRESA SERVICE] Publicando ContatoConfigUpdateEvent");
        eventPublisher.publishEvent(new ContatoConfigUpdateEvent(
            this,
            contatoConfig,
            empresa.getId(),
            usuario
        ));

        return empresaSalva;
    }

    @Override
    public Empresa updateEnderecoConfig(EnderecoConfigRequest enderecoConfigRequest) {
        log.info("[EMPRESA SERVICE] Atualizando configuração de Endereco");
        
        Empresa empresa = empresaRepository.findById(1L).orElse(null);
        if (empresa == null) {
            throw new IllegalStateException("Empresa padrão não encontrada");
        }
        
        EnderecoConfig enderecoConfig = new EnderecoConfig();
        enderecoConfig.setEnderecoAuditEnabled(enderecoConfigRequest.enderecoAuditEnabled());
        enderecoConfig.setEnderecoCacheEnabled(enderecoConfigRequest.enderecoCacheEnabled());
        enderecoConfig.setEnderecoValidationEnabled(enderecoConfigRequest.enderecoValidationEnabled());
        empresa.getConfig().setEnderecoConfig(enderecoConfig);

        Empresa empresaSalva = empresaRepository.save(empresa);
        
        // Obtém usuário atual para auditoria
        String usuario = obterUsuarioAtual();
        
        // Publica evento para recarregar decorators de Endereco
        log.info("[EMPRESA SERVICE] Publicando EnderecoConfigUpdateEvent");
        eventPublisher.publishEvent(new EnderecoConfigUpdateEvent(
            this,
            enderecoConfig,
            empresa.getId(),
            usuario
        ));

        return empresaSalva;
    }

    @Override
    public Empresa updateUsuarioConfig(UsuarioConfigRequest usuarioConfigRequest) {
        log.info("[EMPRESA SERVICE] Atualizando configuração de Usuario");

        Empresa empresa = empresaRepository.findById(1L).orElse(null);
        if (empresa == null) {
            throw new IllegalStateException("Empresa padrão não encontrada");
        }

        UsuarioConfig usuarioConfig = new UsuarioConfig();
        usuarioConfig.setUsuarioApprovalRequired(usuarioConfigRequest.usuarioApprovalRequired());
        usuarioConfig.setUsuarioCorporateEmailRequired(usuarioConfigRequest.usuarioCorporateEmailRequired());
        usuarioConfig.setAllowedEmailDomains(usuarioConfigRequest.allowedEmailDomains());
        empresa.getConfig().setUsuarioConfig(usuarioConfig);

        Empresa empresaSalva = empresaRepository.save(empresa);

//        // Obtém usuário atual para auditoria
//        String usuario = obterUsuarioAtual();
//
//        // Publica evento para recarregar decorators de Endereco
//        log.info("[EMPRESA SERVICE] Publicando EnderecoConfigUpdateEvent");
//        eventPublisher.publishEvent(new EnderecoConfigUpdateEvent(
//                this,
//                enderecoConfig,
//                empresa.getId(),
//                usuario
//        ));

        return empresaSalva;
    }

    @Override
    public Empresa updatePermissaoConfig(PermissaoConfigRequest permissaoConfigRequest) {
        log.info("[EMPRESA SERVICE] Atualizando configuração de Permissao");
        
        Empresa empresa = empresaRepository.findById(1L).orElse(null);
        if (empresa == null) {
            throw new IllegalStateException("Empresa padrão não encontrada");
        }
        
        PermissaoConfig permissaoConfig = new PermissaoConfig();
        permissaoConfig.setPermissaoAuditEnabled(permissaoConfigRequest.permissaoAuditEnabled());
        permissaoConfig.setPermissaoCacheEnabled(permissaoConfigRequest.permissaoCacheEnabled());
        permissaoConfig.setPermissaoValidationEnabled(permissaoConfigRequest.permissaoValidationEnabled());
        empresa.getConfig().setPermissaoConfig(permissaoConfig);

        Empresa empresaSalva = empresaRepository.save(empresa);
        
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
    public Empresa updateTenantConfig(TenantConfigRequest tenantConfigRequest) {
        log.info("[EMPRESA SERVICE] Atualizando configuração de Permissao");

        Empresa empresa = empresaRepository.findById(1L).orElse(null);
        if (empresa == null) {
            throw new IllegalStateException("Empresa padrão não encontrada");
        }

        TenantConfig tenantConfig = new TenantConfig();
        tenantConfig.setTenantCustomCode(tenantConfigRequest.tenantCustomCode());
        tenantConfig.setTenantType(tenantConfigRequest.tenantType());
        tenantConfig.setTenantSubdomain(tenantConfigRequest.tenantSubdomain());
        tenantConfig.setTenantFeaturesEnabled(tenantConfigRequest.tenantFeaturesEnabled());
        empresa.getConfig().setTenantConfig(tenantConfig);

        Empresa empresaSalva = empresaRepository.save(empresa);

        // Obtém usuário atual para auditoria
        String usuario = obterUsuarioAtual();

        // Publica evento para recarregar decorators de Permissao
        log.info("[EMPRESA SERVICE] Publicando PermissaoConfigUpdateEvent");
        eventPublisher.publishEvent(new TenantConfigUpdateEvent(
                this,
                tenantConfig,
                empresa.getId(),
                usuario
        ));

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
    public EmpresaConfig getEmpresaConfig() {
        Empresa empresa = empresaRepository.findById(1L).orElse(null);
        if (empresa == null) {
            return new EmpresaConfig();
        }
        return empresa.getConfig();
    }
}
