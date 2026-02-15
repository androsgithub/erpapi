package com.api.erp.v1.main.features.contato.infrastructure.proxy;

import com.api.erp.v1.main.features.contato.application.dto.CreateContatoRequest;
import com.api.erp.v1.main.features.contato.domain.entity.Contato;
import com.api.erp.v1.main.features.contato.domain.service.IContatoService;
import com.api.erp.v1.main.features.contato.infrastructure.decorator.ContatoServiceApplyDecorate;
import com.api.erp.v1.main.features.contato.infrastructure.service.ContatoService;
import com.api.erp.v1.main.tenant.domain.entity.configs.ContatoConfig;
import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.tenant.domain.service.ITenantService;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ContatoServiceProxy implements IContatoService {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ContatoService contatoServiceDefault;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private ITenantService tenantService;

    private IContatoService resolverService() {
        IContatoService response = contatoServiceDefault;
        ContatoConfig contatoConfig = new ContatoConfig();
        try {
            String strTenantId = securityService.getAuthTenantId();
            Long tenantId = Long.valueOf(strTenantId);
            Tenant tenant = tenantService.getDadosTenant(tenantId);
            String tenantType = tenant.getConfig().getInternalTenantConfig().getTenantType().name();
            String beanName = "contatoService_" + tenantType;

            contatoConfig = tenant.getConfig().getContatoConfig();

            try {
                IContatoService service = applicationContext.getBean(beanName, IContatoService.class);
                log.debug("[CLIENTE SERVICE] Service resolvido para tenant {}: {}", tenantId, beanName);
                response = service;
            } catch (Exception e) {
                log.debug("[CLIENTE SERVICE] Service {} não encontrado, usando padrão", beanName);
            }
        } catch (Exception e) {
            log.debug("[CLIENTE SERVICE] Erro ao resolver tenant, usando padrão: {}", e.getMessage());
        }
        return ContatoServiceApplyDecorate.aplicarDecorators(response, contatoConfig);
    }

    @Override
    public Contato criar(CreateContatoRequest request) {
        return resolverService().criar(request);
    }

    @Override
    public Contato buscarPorId(Long id) {
        return resolverService().buscarPorId(id);
    }

    @Override
    public List<Contato> buscarTodos() {
        return resolverService().buscarTodos();
    }

    @Override
    public List<Contato> buscarAtivos() {
        return resolverService().buscarAtivos();
    }

    @Override
    public List<Contato> buscarInativos() {
        return resolverService().buscarInativos();
    }

    @Override
    public List<Contato> buscarPorTipo(String tipo) {
        return resolverService().buscarPorTipo(tipo);
    }

    @Override
    public Contato buscarPrincipal() {
        return resolverService().buscarPrincipal();
    }

    @Override
    public Contato atualizar(Long id, CreateContatoRequest request) {
        return resolverService().atualizar(id, request);
    }

    @Override
    public Contato ativar(Long id) {
        return resolverService().ativar(id);
    }

    @Override
    public Contato desativar(Long id) {
        return resolverService().desativar(id);
    }

    @Override
    public void deletar(Long id) {
        resolverService().deletar(id);
    }
}
