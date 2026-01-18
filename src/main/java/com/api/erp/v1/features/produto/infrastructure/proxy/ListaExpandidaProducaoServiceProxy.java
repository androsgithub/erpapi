package com.api.erp.v1.features.produto.infrastructure.proxy;

import com.api.erp.v1.tenant.domain.entity.Tenant;
import com.api.erp.v1.tenant.domain.entity.ProdutoConfig;
import com.api.erp.v1.tenant.domain.service.ITenantService;
import com.api.erp.v1.features.produto.domain.entity.Produto;
import com.api.erp.v1.features.produto.domain.service.IListaExpandidaProducaoService;
import com.api.erp.v1.features.produto.infrastructure.decorator.ListaExpandidaProducaoServiceApplyDecorate;
import com.api.erp.v1.features.produto.infrastructure.service.ListaExpandidaProducaoService;
import com.api.erp.v1.shared.infrastructure.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ListaExpandidaProducaoServiceProxy implements IListaExpandidaProducaoService {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ListaExpandidaProducaoService listaExpandidaProducaoServiceDefault;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private ITenantService tenantService;

    private IListaExpandidaProducaoService resolverService() {
        IListaExpandidaProducaoService response = listaExpandidaProducaoServiceDefault;
        ProdutoConfig produtoConfig = new ProdutoConfig();
        try {
            String strTenantId = securityService.getAuthTenantId();
            Long tenantId = Long.valueOf(strTenantId);
            Tenant tenant = tenantService.getDadosTenant(tenantId);
            String tenantType = tenant.getConfig().getInternalTenantConfig().getTenantType().name();
            String beanName = "listaExpandidaProducaoService_" + tenantType;
            produtoConfig = tenant.getConfig().getProdutoConfig();

            try {
                IListaExpandidaProducaoService service = applicationContext.getBean(beanName, IListaExpandidaProducaoService.class);
                log.debug("[SERVICE] Service resolvido para tenant {}: {}", tenantId, beanName);
                response = service;
            } catch (Exception e) {
                log.debug("[SERVICE] Service {} não encontrado, usando padrão", beanName);
            }
        } catch (Exception e) {
            log.debug("[SERVICE] Erro ao resolver tenant, usando padrão: {}", e.getMessage());
        }
        return ListaExpandidaProducaoServiceApplyDecorate.aplicarDecorators(response, produtoConfig);
    }

    @Override
    public Map<Produto, BigDecimal> calcularListaExpandida(Produto produto, BigDecimal quantidadeRequerida) {
        return resolverService().calcularListaExpandida(produto, quantidadeRequerida);
    }

    @Override
    public Map<Produto, BigDecimal> obterListaCompras(Produto produto, BigDecimal quantidadeRequerida) {
        return resolverService().obterListaCompras(produto, quantidadeRequerida);
    }

    @Override
    public List<Map.Entry<Produto, BigDecimal>> obterListaOrdenada(Produto produto, BigDecimal quantidadeRequerida) {
        return resolverService().obterListaOrdenada(produto, quantidadeRequerida);
    }
}
