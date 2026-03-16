package com.api.erp.v1.main.master.user.application.dto.response;

import com.api.erp.v1.main.master.tenant.application.dto.TenantResponse;

import java.util.Set;

public record AuthenticationResponse(AuthStatus status, Set<TenantResponse> tenants, TokenResponse auth) {

}
