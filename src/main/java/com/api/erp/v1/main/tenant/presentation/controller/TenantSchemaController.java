package com.api.erp.v1.main.tenant.presentation.controller;

import com.api.erp.v1.main.tenant.application.dto.DatasourceTesteResponse;
import com.api.erp.v1.main.tenant.application.dto.TenantDatasourceRequest;
import com.api.erp.v1.main.tenant.application.dto.TenantDatasourceResponse;
import com.api.erp.v1.main.tenant.domain.controller.ITenantDatabaseController;
import com.api.erp.v1.docs.openapi.tenant.TenantDatabaseOpenApiDocumentation;
import com.api.erp.v1.main.tenant.domain.entity.TenantPermissions;
import com.api.erp.v1.main.tenant.domain.service.ITenantDatasourceService;
import com.api.erp.v1.main.tenant.infrastructure.config.datasource.TenantContext;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/tenant/database")
public class TenantSchemaController implements ITenantDatabaseController, TenantDatabaseOpenApiDocumentation {
    @Autowired
    private ITenantDatasourceService tenantDataSourceService;

    @PostMapping("/datasource")
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantDatasourceResponse> configurarDatasource(
            @RequestBody TenantDatasourceRequest request) {
        Long tenantId = TenantContext.getTenantId();
        log.info("[EMPRESA CONTROLLER] Configurando datasource para tenant: {}", tenantId);
        TenantDatasourceResponse response = tenantDataSourceService.configurarDatasource(tenantId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/datasource")
    @RequiresPermission(TenantPermissions.BUSCAR)
    public ResponseEntity<TenantDatasourceResponse> obterDatasource() {
        Long tenantId = TenantContext.getTenantId();
        log.info("[EMPRESA CONTROLLER] Obtendo configuração de datasource do tenant: {}", tenantId);
        return tenantDataSourceService.obterDatasource(tenantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/datasource")
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantDatasourceResponse> atualizarDatasource(
            @RequestBody TenantDatasourceRequest request) {
        Long tenantId = TenantContext.getTenantId();
        log.info("[EMPRESA CONTROLLER] Atualizando datasource do tenant: {}", tenantId);
        TenantDatasourceResponse response = tenantDataSourceService.atualizarDatasource(tenantId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/datasource/teste")
    @RequiresPermission(TenantPermissions.BUSCAR)
    public ResponseEntity<?> testarConexaoDatasource(
            @RequestBody TenantDatasourceRequest request) {
        Long tenantId = TenantContext.getTenantId();
        log.info("[EMPRESA CONTROLLER] Testando conexão de datasource para tenant: {}", tenantId);
        boolean sucesso = tenantDataSourceService.testarConexao(request);

        if (sucesso) {
            return ResponseEntity.ok(new DatasourceTesteResponse("Conexão estabelecida com sucesso", true));
        } else {
            return ResponseEntity.badRequest().body(new DatasourceTesteResponse("Falha ao conectar com o banco de dados", false));
        }
    }
}
