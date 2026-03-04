package com.api.erp.v1.main.features.product.presentation.controller;

import com.api.erp.v1.main.features.product.application.dto.ListaExpandidaResponseDTO;
import com.api.erp.v1.main.features.product.domain.controller.IListaExpandidaController;
import com.api.erp.v1.docs.openapi.features.product.ListaExpandidaOpenApiDocumentation;
import com.api.erp.v1.main.features.product.domain.entity.ExpandedListPermissions;
import com.api.erp.v1.main.features.product.domain.service.IListaExpandidaService;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import com.api.erp.v1.main.shared.infrastructure.documentation.RequiresXTenantId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/lista-expandida")
public class ListaExpandidaController implements IListaExpandidaController, ListaExpandidaOpenApiDocumentation {

    @Autowired
    @Qualifier("listaExpandidaServiceProxy")
    private IListaExpandidaService service;

    @GetMapping("/product/{productId}")
    @RequiresXTenantId
    @RequiresPermission(ExpandedListPermissions.GENERATE)
    public ResponseEntity<ListaExpandidaResponseDTO> gerarListaExpandida(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") BigDecimal quantidade) {
        ListaExpandidaResponseDTO resposta = service.gerarListaExpandida(productId, quantidade);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/compras/product/{productId}")
    @RequiresXTenantId
    @RequiresPermission(ExpandedListPermissions.GENERATE_PURCHASE)
    public ResponseEntity<ListaExpandidaResponseDTO> gerarListaCompras(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") BigDecimal quantidade) {
        ListaExpandidaResponseDTO resposta = service.gerarListaCompras(productId, quantidade);
        return ResponseEntity.ok(resposta);
    }
}
