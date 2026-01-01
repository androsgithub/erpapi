package com.api.erp.v1.features.empresa.domain.service;

import com.api.erp.v1.features.empresa.application.dto.*;
import com.api.erp.v1.features.empresa.domain.entity.Empresa;
import com.api.erp.v1.features.empresa.domain.entity.EmpresaConfig;

public interface IEmpresaService {
    Empresa getDadosEmpresa();
    Empresa updateDadosEmpresa(EmpresaRequest empresaRequest);
    Empresa updateClienteConfig(ClienteConfigRequest clienteConfigRequest);
    Empresa updateContatoConfig(ContatoConfigRequest contatoConfigRequest);
    Empresa updateEnderecoConfig(EnderecoConfigRequest enderecoConfigRequest);
    Empresa updatePermissaoConfig(PermissaoConfigRequest permissaoConfigRequest);
    Empresa updateTenantConfig(TenantConfigRequest tenantConfigRequest);

    Empresa updateUsuarioConfig(UsuarioConfigRequest usuarioConfigRequest);
    EmpresaConfig getEmpresaConfig();
}
