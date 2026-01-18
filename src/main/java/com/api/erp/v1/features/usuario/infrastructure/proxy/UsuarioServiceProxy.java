package com.api.erp.v1.features.usuario.infrastructure.proxy;

import com.api.erp.v1.features.permissao.domain.entity.Permissao;
import com.api.erp.v1.features.permissao.domain.entity.Role;
import com.api.erp.v1.tenant.domain.entity.Tenant;
import com.api.erp.v1.tenant.domain.entity.UsuarioConfig;
import com.api.erp.v1.tenant.domain.service.ITenantService;
import com.api.erp.v1.features.usuario.application.dto.request.AdicionarPermissoesRequest;
import com.api.erp.v1.features.usuario.application.dto.request.AdicionarRolesRequest;
import com.api.erp.v1.features.usuario.application.dto.request.CreateUsuarioRequest;
import com.api.erp.v1.features.usuario.application.dto.request.UpdateUsuarioRequest;
import com.api.erp.v1.features.usuario.domain.entity.Usuario;
import com.api.erp.v1.features.usuario.domain.service.IUsuarioService;
import com.api.erp.v1.features.usuario.infrastructure.decorator.UsuarioServiceApplyDecorate;
import com.api.erp.v1.features.usuario.infrastructure.service.UsuarioService;
import com.api.erp.v1.shared.infrastructure.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UsuarioServiceProxy implements IUsuarioService {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private UsuarioService usuarioServiceDefault;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private ITenantService tenantService;

    private IUsuarioService resolverService() {
        IUsuarioService response = usuarioServiceDefault;
        UsuarioConfig usuarioConfig = new UsuarioConfig();

        String strTenantId = securityService.getAuthTenantId();
        
        // Se não tem autenticação, usa serviço padrão
        if (strTenantId == null || strTenantId.isEmpty()) {
            log.debug("[SERVICE] Sem autenticação, usando serviço padrão");
            return usuarioServiceDefault;
        }
        
        Long tenantId = Long.valueOf(strTenantId);
        Tenant tenant = tenantService.getDadosTenant(tenantId);
        String tenantType = tenant.getConfig().getInternalTenantConfig().getTenantType().name();
        String beanName = "usuarioService_" + tenantType;
        usuarioConfig = tenant.getConfig().getUsuarioConfig();

        try {
            IUsuarioService service = applicationContext.getBean(beanName, IUsuarioService.class);
            log.debug("[SERVICE] Service resolvido para tenant {}: {}", tenantId, beanName);
            response = service;
        } catch (Exception e) {
            log.debug("[SERVICE] Service {} não encontrado, usando padrão", beanName);
        }

        return UsuarioServiceApplyDecorate.aplicarDecorators(response, usuarioConfig);
    }

    @Override
    public Usuario criar(CreateUsuarioRequest request) {
        return resolverService().criar(request);
    }

    @Override
    public Usuario atualizar(Long id, UpdateUsuarioRequest request) {
        return resolverService().atualizar(id, request);
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return resolverService().buscarPorId(id);
    }

    @Override
    public List<Usuario> listarTodos() {
        return resolverService().listarTodos();
    }

    @Override
    public List<Usuario> listarPendentes() {
        return resolverService().listarPendentes();
    }

    @Override
    public void inativar(Long id) {

    }

    @Override
    public Usuario aprovar(Long usuarioId, Long gestorId) {
        return resolverService().aprovar(usuarioId, gestorId);
    }

    @Override
    public Usuario rejeitar(Long usuarioId, Long gestorId, String motivo) {
        return resolverService().rejeitar(usuarioId, gestorId, motivo);
    }

    @Override
    public void adicionarPermissoes(Long usuarioId, AdicionarPermissoesRequest request) {
        resolverService().adicionarPermissoes(usuarioId, request);
    }

    @Override
    public void removerPermissao(Long usuarioId, Long permissaoId) {
        resolverService().removerPermissao(usuarioId, permissaoId);
    }

    @Override
    public List<Permissao> listarPermissoes(Long usuarioId) {
        return resolverService().listarPermissoes(usuarioId);
    }

    @Override
    public void adicionarRoles(Long usuarioId, AdicionarRolesRequest request) {
        resolverService().adicionarRoles(usuarioId, request);
    }

    @Override
    public void removerRole(Long usuarioId, Long roleId) {
        resolverService().removerRole(usuarioId, roleId);
    }

    @Override
    public List<Role> listarRoles(Long usuarioId) {
        return resolverService().listarRoles(usuarioId);
    }
}
