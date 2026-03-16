package com.api.erp.v1.main.master.user.application.dto.response;

import com.api.erp.v1.main.dynamic.features.contact.application.dto.response.ContactResponse;
import com.api.erp.v1.main.master.permission.application.dto.response.PermissionResponse;
import com.api.erp.v1.main.master.permission.application.dto.response.RoleResponse;
import com.api.erp.v1.main.master.user.domain.entity.StatusUser;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserPermissionsResponse {
    private long id;
    private String name;
    private String email;
    private String cpf;
    private StatusUser status;
    private Set<ContactResponse> contacts;
    private Set<PermissionResponse> permissions;
    private Set<RoleResponse> roles;
}

