package com.api.erp.v1.main.features.produto.presentation.controller;

import com.api.erp.v1.main.features.produto.application.dto.ListaExpandidaResponseDTO;
import com.api.erp.v1.main.features.produto.domain.controller.IListaExpandidaController;
import com.api.erp.v1.docs.openapi.features.produto.ListaExpandidaOpenApiDocumentation;
import com.api.erp.v1.main.features.produto.domain.entity.ListaExpandidaPermissions;
import com.api.erp.v1.main.features.produto.domain.service.IListaExpandidaService;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
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

    @GetMapping("/produto/{produtoId}")
    @RequiresPermission(ListaExpandidaPermissions.GERAR)
    public ResponseEntity<ListaExpandidaResponseDTO> gerarListaExpandida(
            @PathVariable Long produtoId,
            @RequestParam(defaultValue = "1") BigDecimal quantidade) {
        ListaExpandidaResponseDTO resposta = service.gerarListaExpandida(produtoId, quantidade);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/compras/produto/{produtoId}")
    @RequiresPermission(ListaExpandidaPermissions.GERAR_COMPRA)
    public ResponseEntity<ListaExpandidaResponseDTO> gerarListaCompras(
            @PathVariable Long produtoId,
            @RequestParam(defaultValue = "1") BigDecimal quantidade) {
        ListaExpandidaResponseDTO resposta = service.gerarListaCompras(produtoId, quantidade);
        return ResponseEntity.ok(resposta);
    }
}
