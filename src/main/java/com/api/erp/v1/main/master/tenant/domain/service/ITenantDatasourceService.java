package com.api.erp.v1.main.master.tenant.domain.service;

import com.api.erp.v1.main.master.tenant.application.dto.request.create.TenantDatasourceRequest;
import com.api.erp.v1.main.master.tenant.application.dto.request.update.UpdateTenantDatasourceWithPasswordRequest;
import com.api.erp.v1.main.master.tenant.application.dto.response.TenantDatasourceResponse;

import java.util.Optional;


public interface ITenantDatasourceService {
    TenantDatasourceResponse configurarDatasource(Long tenantId, TenantDatasourceRequest request);
    Optional<TenantDatasourceResponse> obterDatasource(Long tenantId);
    TenantDatasourceResponse atualizarDatasource(Long tenantId, TenantDatasourceRequest request);
    
    /**
     * Atualizar datasource com validação de senha atual.
     * 
     * Segurança: Verifica se a currentPassword corresponde à senha armazenada
     * antes de permitir a atualização.
     * 
     * @param tenantId ID do tenant
     * @param request DTO com currentPassword + newPassword + datasource config
     * @return TenantDatasourceResponse atualizado
     * @throws InvalidPasswordVerificationException se currentPassword não corresponder
     */
    TenantDatasourceResponse atualizarDatasourceComVerificacao(
            Long tenantId, 
            UpdateTenantDatasourceWithPasswordRequest request);
    
    boolean testarConexao(TenantDatasourceRequest request);
}
