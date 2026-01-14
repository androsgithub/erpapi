package com.api.erp.v1.features.produto.infrastructure.proxy;

import com.api.erp.v1.features.tenant.domain.entity.Tenant;
import com.api.erp.v1.features.tenant.domain.entity.ProdutoConfig;
import com.api.erp.v1.features.tenant.domain.service.ITenantService;
import com.api.erp.v1.features.produto.application.dto.ProdutoRequestDTO;
import com.api.erp.v1.features.produto.domain.entity.Produto;
import com.api.erp.v1.features.produto.domain.entity.TipoProduto;
import com.api.erp.v1.features.produto.domain.service.IProdutoService;
import com.api.erp.v1.features.produto.infrastructure.decorator.ProdutoServiceApplyDecorate;
import com.api.erp.v1.features.produto.infrastructure.service.ProdutoService;
import com.api.erp.v1.shared.infrastructure.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProdutoServiceProxy implements IProdutoService {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ProdutoService produtoServiceDefault;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private ITenantService tenantService;

    private IProdutoService resolverService() {
        IProdutoService response = produtoServiceDefault;
        ProdutoConfig produtoConfig = new ProdutoConfig();
        try {
            String strTenantId = securityService.getAuthTenantId();
            Long tenantId = Long.valueOf(strTenantId);
            Tenant tenant = tenantService.getDadosTenant(tenantId);
            String tenantType = tenant.getConfig().getInternalTenantConfig().getTenantType().name();
            String beanName = "produtoService_" + tenantType;
            produtoConfig = tenant.getConfig().getProdutoConfig();

            try {
                IProdutoService service = applicationContext.getBean(beanName, IProdutoService.class);
                log.debug("[CLIENTE SERVICE] Service resolvido para tenant {}: {}", tenantId, beanName);
                response = service;
            } catch (Exception e) {
                log.debug("[CLIENTE SERVICE] Service {} não encontrado, usando padrão", beanName);
            }
        } catch (Exception e) {
            log.debug("[CLIENTE SERVICE] Erro ao resolver tenant, usando padrão: {}", e.getMessage());
        }
        return ProdutoServiceApplyDecorate.aplicarDecorators(response, produtoConfig);
    }


    @Override
    public Produto criar(ProdutoRequestDTO dto) {
        return resolverService().criar(dto);
    }

    @Override
    public Produto atualizar(Long id, ProdutoRequestDTO produtoModificado) {
        return resolverService().atualizar(id, produtoModificado);
    }

    @Override
    public Produto obter(Long id) {
        return resolverService().obter(id);
    }

    @Override
    public Page<Produto> listar(Pageable pageable) {
        return resolverService().listar(pageable);
    }

    @Override
    public Page<Produto> listarPorTipo(TipoProduto tipo, Pageable pageable) {
        return resolverService().listarPorTipo(tipo, pageable);
    }

    @Override
    public Produto ativar(Long id) {
        return resolverService().ativar(id);
    }

    @Override
    public Produto desativar(Long id) {
        return resolverService().desativar(id);
    }

    @Override
    public Produto bloquear(Long id) {
        return resolverService().bloquear(id);
    }

    @Override
    public Produto descontinuar(Long id) {
        return resolverService().descontinuar(id);
    }

    @Override
    public void deletar(Long id) {
        resolverService().deletar(id);
    }
}
