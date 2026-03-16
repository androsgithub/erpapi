package com.api.erp.v1.main.master.user.infrastructure.proxy;

import com.api.erp.v1.main.master.permission.domain.entity.Permission;
import com.api.erp.v1.main.master.permission.domain.entity.Role;
import com.api.erp.v1.main.master.user.application.dto.request.AddPermissionsRequest;
import com.api.erp.v1.main.master.user.application.dto.request.AdicionarRolesRequest;
import com.api.erp.v1.main.master.user.application.dto.request.CreateUserRequest;
import com.api.erp.v1.main.master.user.application.dto.request.UpdateUserRequest;
import com.api.erp.v1.main.master.user.domain.entity.User;
import com.api.erp.v1.main.master.user.domain.service.IUserService;
import com.api.erp.v1.main.master.user.infrastructure.service.UserService;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import com.api.erp.v1.main.master.tenant.infrastructure.service.TenantBeanResolver;
import com.dros.observability.core.annotation.TrackFlow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserServiceProxy
 * <p>
 * Proxy que resolve qual UserService usar baseado em tb_tnt_features.
 * Feature key: "userService"
 * <p>
 * Fluxo:
 * 1. Obtém o tenant atual via SecurityService
 * 2. Resolve o bean via TenantBeanResolver (consulta tb_tnt_features)
 * 3. Aplica decoradores conforme configuração do tenant
 * 4. Delega para o serviço resolvido
 */
@Service
@Slf4j
public class UserServiceProxy implements IUserService {
    
    static final String FEATURE_KEY = "userService";
    
    private final UserService userServiceDefault;
    private final SecurityService securityService;
    private final TenantBeanResolver tenantBeanResolver;

    public UserServiceProxy(
            UserService userServiceDefault,
            SecurityService securityService,
            TenantBeanResolver tenantBeanResolver) {
        this.userServiceDefault = userServiceDefault;
        this.securityService = securityService;
        this.tenantBeanResolver = tenantBeanResolver;
    }

    /**
     * Resolve o UserService para o tenant atual
     * <p>
     * Se not authenticated, retorna o serviço padrão.
     * Se autenticado, consulta tb_tnt_features com TenantBeanResolver.
     */
    private IUserService resolverService() {
        String strTenantId = securityService.getAuthTenantId();
        
        // Se não tem autenticação, usa serviço padrão
        if (strTenantId == null || strTenantId.isEmpty()) {
            log.debug("[UserServiceProxy] No authentication, using default service");
            return userServiceDefault;
        }
        
        Long tenantId = Long.valueOf(strTenantId);
        
        try {
            // Resolve via TenantBeanResolver (consulta tb_tnt_features)
            IUserService service = tenantBeanResolver.resolve(tenantId, FEATURE_KEY, IUserService.class);
            log.debug("[UserServiceProxy] Service resolvido para tenant {}: {} (feature key={})", 
                      tenantId, service.getClass().getSimpleName(), FEATURE_KEY);
            return service;
        } catch (TenantBeanResolver.TenantFeatureNotFoundException | 
                 TenantBeanResolver.TenantBeanNotFoundException e) {
            log.warn("[UserServiceProxy] Falha ao resolver bean via TenantBeanResolver, usando default: {}", 
                     e.getMessage());
            return userServiceDefault;
        }
    }

    @Override
    @TrackFlow("SRVC_CREATE_USER")
    public User create(CreateUserRequest request) {
        return resolverService().create(request);
    }

    @Override
    @TrackFlow("SRVC_UPDATE_USER")
    public User update(Long id, UpdateUserRequest request) {
        return resolverService().update(id, request);
    }

    @Override
    @TrackFlow("SRVC_SEARCHBYID_USER")
    public User findById(Long id) {
        return resolverService().findById(id);
    }

    @Override
    @TrackFlow("SRVC_LIST_USER")
    public List<User> listAll() {
        return resolverService().listAll();
    }

    @Override
    @TrackFlow("SRVC_LIST_PENDING_USER")
    public List<User> listPending() {
        return resolverService().listPending();
    }

    @Override
    @TrackFlow("SRVC_DISABLE_USER")
    public void disable(Long id) {
        resolverService().disable(id);
    }

    @Override
    @TrackFlow("SRVC_APROVE_USER")
    public User approve(Long userId, Long gestorId) {
        return resolverService().approve(userId, gestorId);
    }

    @Override
    @TrackFlow("SRVC_REJECT_USER")
    public User reject(Long userId, Long gestorId, String motivo) {
        return resolverService().reject(userId, gestorId, motivo);
    }

    @Override
    @TrackFlow("SRVC_ADD_USER_PERMS")
    public void addPermissions(Long userId, AddPermissionsRequest request) {
        resolverService().addPermissions(userId, request);
    }

    @Override
    @TrackFlow("SRVC_REMOVE_USER_PERMS")
    public void removePermission(Long userId, Long permissionId) {
        resolverService().removePermission(userId, permissionId);
    }

    @Override
    @TrackFlow("SRVC_LIST_USER_PERMS")
    public List<Permission> listPermissions(Long userId) {
        return resolverService().listPermissions(userId);
    }

    @Override
    @TrackFlow("SRVC_ADD_USER_ROLES")
    public void addRoles(Long userId, AdicionarRolesRequest request) {
        resolverService().addRoles(userId, request);
    }

    @Override
    @TrackFlow("SRVC_REMOVE_USER_ROLE")
    public void removeRole(Long userId, Long roleId) {
        resolverService().removeRole(userId, roleId);
    }

    @Override
    @TrackFlow("SRVC_LIST_USER_ROLES")
    public List<Role> listRoles(Long userId) {
        return resolverService().listRoles(userId);
    }
}
