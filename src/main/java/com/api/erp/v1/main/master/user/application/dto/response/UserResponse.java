package com.api.erp.v1.main.master.user.application.dto.response;

import com.api.erp.v1.main.dynamic.features.contact.application.dto.response.ContactResponse;
import com.api.erp.v1.main.master.user.domain.entity.StatusUser;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class UserResponse {
    private long id;
    private String nomeCompleto;
    private String email;
    private String cpf;
    private StatusUser status;
    private LocalDateTime dataCriacao;
    private Set<ContactResponse> contacts;
    private long tenantId;
}
