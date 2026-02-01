package com.api.erp.v1.features.endereco.infrastructure.proxy;

import com.api.erp.v1.features.endereco.application.dto.request.CreateEnderecoRequest;
import com.api.erp.v1.tenant.domain.entity.Tenant;
import com.api.erp.v1.tenant.domain.entity.configs.EnderecoConfig;
import com.api.erp.v1.tenant.domain.service.ITenantService;
import com.api.erp.v1.features.endereco.domain.entity.Endereco;
import com.api.erp.v1.features.endereco.domain.service.IEnderecoService;
import com.api.erp.v1.features.endereco.infrastructure.decorator.EnderecoServiceApplyDecorate;
import com.api.erp.v1.features.endereco.infrastructure.service.EnderecoService;
import com.api.erp.v1.shared.infrastructure.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EnderecoServiceProxy implements IEnderecoService {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private EnderecoService enderecoServiceDefault;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private ITenantService tenantService;

    private IEnderecoService resolverService() {
        IEnderecoService response = enderecoServiceDefault;
        EnderecoConfig enderecoConfig = new EnderecoConfig();
        try {
            String strTenantId = securityService.getAuthTenantId();
            Long tenantId = Long.valueOf(strTenantId);
            Tenant tenant = tenantService.getDadosTenant(tenantId);
            String tenantType = tenant.getConfig().getInternalTenantConfig().getTenantType().name();
            String beanName = "enderecoService_" + tenantType;
            enderecoConfig = tenant.getConfig().getEnderecoConfig();

            try {
                IEnderecoService service = applicationContext.getBean(beanName, IEnderecoService.class);
                log.debug("[CLIENTE SERVICE] Service resolvido para tenant {}: {}", tenantId, beanName);
                response = service;
            } catch (Exception e) {
                log.debug("[CLIENTE SERVICE] Service {} não encontrado, usando padrão", beanName);
            }
        } catch (Exception e) {
            log.debug("[CLIENTE SERVICE] Erro ao resolver tenant, usando padrão: {}", e.getMessage());
        }
        return EnderecoServiceApplyDecorate.aplicarDecorators(response, enderecoConfig);
    }

    @Override
    public Endereco criar(CreateEnderecoRequest request) {
        return resolverService().criar(request);
    }

    @Override
    public Endereco buscarPorId(Long id) {
        return resolverService().buscarPorId(id);
    }

    @Override
    public List<Endereco> buscarTodos() {
        return resolverService().buscarTodos();
    }

    @Override
    public Endereco atualizar(Long id, CreateEnderecoRequest request) {
        return resolverService().atualizar(id, request);
    }

    @Override
    public void deletar(Long id) {
        resolverService().deletar(id);
    }
}
