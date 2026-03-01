package com.api.erp.v1.main.features.user.infrastructure.proxy;

import com.api.erp.v1.main.features.permission.domain.entity.Permission;
import com.api.erp.v1.main.features.permission.domain.entity.Role;
import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.tenant.domain.entity.configs.UserConfig;
import com.api.erp.v1.main.tenant.domain.service.ITenantService;
import com.api.erp.v1.main.features.user.application.dto.request.AdicionarPermissionsRequest;
import com.api.erp.v1.main.features.user.application.dto.request.AdicionarRolesRequest;
import com.api.erp.v1.main.features.user.application.dto.request.CreateUserRequest;
import com.api.erp.v1.main.features.user.application.dto.request.UpdateUserRequest;
import com.api.erp.v1.main.features.user.domain.entity.User;
import com.api.erp.v1.main.features.user.domain.service.IUserService;
import com.api.erp.v1.main.features.user.infrastructure.decorator.UserServiceApplyDecorate;
import com.api.erp.v1.main.features.user.infrastructure.service.UserService;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import com.dros.observability.core.annotation.TrackFlow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceProxy implements IUserService {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private UserService userServiceDefault;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private ITenantService tenantService;

    private IUserService resolverService() {
        IUserService response = userServiceDefault;
        UserConfig userConfig = new UserConfig();

        String strTenantId = securityService.getAuthTenantId();
        
        // Se não tem autenticação, usa serviço padrão
        if (strTenantId == null || strTenantId.isEmpty()) {
            log.debug("[SERVICE] No authentication, using default service");
            return userServiceDefault;
        }
        
        Long tenantId = Long.valueOf(strTenantId);
        Tenant tenant = tenantService.getDadosTenant(tenantId);
        String tenantType = tenant.getConfig().getInternalTenantConfig().getTenantType().name();
        String beanName = "userService" + tenantType;
        userConfig = tenant.getConfig().getUserConfig();

        try {
            IUserService service = applicationContext.getBean(beanName, IUserService.class);
            log.debug("[SERVICE] Service resolvido para tenant {}: {}", tenantId, beanName);
            response = service;
        } catch (Exception e) {
            log.debug("[SERVICE] Service {} not found, using default", beanName);
        }

        return UserServiceApplyDecorate.aplicarDecorators(response, userConfig);
    }

    @Override
    @TrackFlow("SRVC_CREATE_USER")
    public User criar(CreateUserRequest request) {
        return resolverService().criar(request);
    }

    @Override
    @TrackFlow("SRVC_UPDATE_USER")
    public User atualizar(Long id, UpdateUserRequest request) {
        return resolverService().atualizar(id, request);
    }

    @Override
    @TrackFlow("SRVC_SEARCHBYID_USER")
    public User buscarPorId(Long id) {
        return resolverService().buscarPorId(id);
    }

    @Override
    @TrackFlow("SRVC_LIST_USER")
    public List<User> listarTodos() {
        return resolverService().listarTodos();
    }

    @Override
    @TrackFlow("SRVC_LIST_PENDING_USER")
    public List<User> listarPendentes() {
        return resolverService().listarPendentes();
    }

    @Override
    @TrackFlow("SRVC_DISABLE_USER")
    public void inativar(Long id) {
//        return resolverService().inativar(id);
    }

    @Override
    @TrackFlow("SRVC_APROVE_USER")
    public User aprovar(Long userId, Long gestorId) {
        return resolverService().aprovar(userId, gestorId);
    }

    @Override
    @TrackFlow("SRVC_REJECT_USER")
    public User rejeitar(Long userId, Long gestorId, String motivo) {
        return resolverService().rejeitar(userId, gestorId, motivo);
    }

    @Override
    @TrackFlow("SRVC_ADD_USER_PERMS")
    public void adicionarPermissions(Long userId, AdicionarPermissionsRequest request) {
        resolverService().adicionarPermissions(userId, request);
    }

    @Override
    @TrackFlow("SRVC_REMOVE_USER_PERMS")
    public void removerPermission(Long userId, Long permissionId) {
        resolverService().removerPermission(userId, permissionId);
    }

    @Override
    @TrackFlow("SRVC_LIST_USER_PERMS")
    public List<Permission> listarPermissions(Long userId) {
        return resolverService().listarPermissions(userId);
    }

    @Override
    @TrackFlow("SRVC_ADD_USER_ROLES")
    public void adicionarRoles(Long userId, AdicionarRolesRequest request) {
        resolverService().adicionarRoles(userId, request);
    }

    @Override
    @TrackFlow("SRVC_REMOVE_USER_ROLE")
    public void removerRole(Long userId, Long roleId) {
        resolverService().removerRole(userId, roleId);
    }

    @Override
    @TrackFlow("SRVC_LIST_USER_ROLES")
    public List<Role> listarRoles(Long userId) {
        return resolverService().listarRoles(userId);
    }
}
