package com.api.erp.v1.features.produto.infrastructure.proxy;

import com.api.erp.v1.features.tenant.domain.entity.Tenant;
import com.api.erp.v1.features.tenant.domain.entity.ProdutoConfig;
import com.api.erp.v1.features.tenant.domain.service.ITenantService;
import com.api.erp.v1.features.produto.application.dto.ListaExpandidaResponseDTO;
import com.api.erp.v1.features.produto.domain.service.IListaExpandidaService;
import com.api.erp.v1.features.produto.infrastructure.decorator.ListaExpandidaServiceApplyDecorate;
import com.api.erp.v1.features.produto.infrastructure.service.ListaExpandidaService;
import com.api.erp.v1.shared.infrastructure.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class ListaExpandidaServiceProxy implements IListaExpandidaService {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ListaExpandidaService listaExpandidaServiceDefault;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private ITenantService tenantService;

    private IListaExpandidaService resolverService() {
        IListaExpandidaService response = listaExpandidaServiceDefault;
        ProdutoConfig produtoConfig = new ProdutoConfig();
        try {
            String strTenantId = securityService.getAuthTenantId();
            Long tenantId = Long.valueOf(strTenantId);
            Tenant tenant = tenantService.getDadosTenant(tenantId);
            String tenantType = tenant.getConfig().getInternalTenantConfig().getTenantType().name();
            String beanName = "listaExpandidaService_" + tenantType;
            produtoConfig = tenant.getConfig().getProdutoConfig();

            try {
                IListaExpandidaService service = applicationContext.getBean(beanName, IListaExpandidaService.class);
                log.debug("[SERVICE] Service resolvido para tenant {}: {}", tenantId, beanName);
                response = service;
            } catch (Exception e) {
                log.debug("[SERVICE] Service {} não encontrado, usando padrão", beanName);
            }
        } catch (Exception e) {
            log.debug("[SERVICE] Erro ao resolver tenant, usando padrão: {}", e.getMessage());
        }
        return ListaExpandidaServiceApplyDecorate.aplicarDecorators(response, produtoConfig);
    }


    @Override
    public ListaExpandidaResponseDTO gerarListaExpandida(Long produtoId, BigDecimal quantidade) {
        return resolverService().gerarListaExpandida(produtoId, quantidade);
    }

    @Override
    public ListaExpandidaResponseDTO gerarListaCompras(Long produtoId, BigDecimal quantidade) {
        return resolverService().gerarListaCompras(produtoId, quantidade);
    }
}
