package com.api.erp.v1.main.master.user.application.mapper;

import com.api.erp.v1.main.dynamic.features.contact.application.mapper.ContactMapper;
import com.api.erp.v1.main.master.permission.application.mapper.PermissionMapper;
import com.api.erp.v1.main.master.permission.application.mapper.RoleMapper;
import com.api.erp.v1.main.master.user.application.dto.response.UserPermissionsResponse;
import com.api.erp.v1.main.master.user.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class IUserPermissionsMapper {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final ContactMapper contactMapper;

    public IUserPermissionsMapper(RoleMapper roleMapper, PermissionMapper permissionMapper, ContactMapper contactMapper) {
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
        this.contactMapper = contactMapper;
    }


    public UserPermissionsResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        UserPermissionsResponse response = new UserPermissionsResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail() != null ? user.getEmail().getValor() : null);
        response.setCpf(user.getCpf() != null ? user.getCpf().getFormatado() : null);
        response.setStatus(user.getStatus());
        response.setContacts(contactMapper.toResponse(user.getContacts()));
        response.setPermissions(permissionMapper.toResponse(user.getPermissions()));
        response.setRoles(roleMapper.toResponse(user.getRoles()));

        return response;
    }
}



