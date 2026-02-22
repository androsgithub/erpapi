package com.api.erp.v1.main.features.user.application.dto.response;

import com.api.erp.v1.main.features.contact.application.dto.response.ContactResponse;
import com.api.erp.v1.main.features.permission.application.dto.response.PermissionResponse;
import com.api.erp.v1.main.features.permission.application.dto.response.RoleResponse;
import com.api.erp.v1.main.features.user.domain.entity.StatusUser;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class UserPermissionsResponse {
    private long id;
    private String nomeCompleto;
    private String email;
    private String cpf;
    private StatusUser status;
    private LocalDateTime dataCriacao;
    private Set<ContactResponse> contacts;
    private Set<PermissionResponse> permissions;
    private Set<RoleResponse> roles;
}

