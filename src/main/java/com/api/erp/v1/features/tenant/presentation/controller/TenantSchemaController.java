package com.api.erp.v1.features.tenant.presentation.controller;

import com.api.erp.v1.features.tenant.application.dto.DatasourceTesteResponse;
import com.api.erp.v1.features.tenant.application.dto.TenantDatasourceRequest;
import com.api.erp.v1.features.tenant.application.dto.TenantDatasourceResponse;
import com.api.erp.v1.features.tenant.domain.controller.ITenantDatabaseController;
import com.api.erp.v1.features.tenant.domain.entity.TenantPermissions;
import com.api.erp.v1.features.tenant.domain.service.ITenantDatasourceService;
import com.api.erp.v1.shared.infrastructure.security.annotations.RequiresPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/tenant/database")
public class TenantSchemaController implements ITenantDatabaseController {
    @Autowired
    private ITenantDatasourceService tenantDataSourceService;

    @PostMapping("/{tenantSlug}/datasource")
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantDatasourceResponse> configurarDatasource(
            @PathVariable String tenantSlug,
            @RequestBody TenantDatasourceRequest request) {
        log.info("[EMPRESA CONTROLLER] Configurando datasource para tenant: {}", tenantSlug);
        TenantDatasourceResponse response = tenantDataSourceService.configurarDatasource(tenantSlug, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{tenantId}/datasource")
    @RequiresPermission(TenantPermissions.BUSCAR)
    public ResponseEntity<TenantDatasourceResponse> obterDatasource(@PathVariable String tenantSlug) {
        log.info("[EMPRESA CONTROLLER] Obtendo configuração de datasource do tenant: {}", tenantSlug);
        return tenantDataSourceService.obterDatasource(tenantSlug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{tenantId}/datasource")
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantDatasourceResponse> atualizarDatasource(
            @PathVariable String tenantSlug,
            @RequestBody TenantDatasourceRequest request) {
        log.info("[EMPRESA CONTROLLER] Atualizando datasource do tenant: {}", tenantSlug);
        TenantDatasourceResponse response = tenantDataSourceService.atualizarDatasource(tenantSlug, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{tenantId}/datasource/teste")
    @RequiresPermission(TenantPermissions.BUSCAR)
    public ResponseEntity<?> testarConexaoDatasource(
            @PathVariable String tenantSlug,
            @RequestBody TenantDatasourceRequest request) {
        log.info("[EMPRESA CONTROLLER] Testando conexão de datasource para tenant: {}", tenantSlug);
        boolean sucesso = tenantDataSourceService.testarConexao(request);

        if (sucesso) {
            return ResponseEntity.ok(new DatasourceTesteResponse("Conexão estabelecida com sucesso", true));
        } else {
            return ResponseEntity.badRequest().body(new DatasourceTesteResponse("Falha ao conectar com o banco de dados", false));
        }
    }
}
