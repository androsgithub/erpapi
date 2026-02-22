package com.api.erp.v1.main.shared.infrastructure.documentation;

import com.api.erp.v1.main.shared.common.constant.HeaderConst;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para marcar endpoints que requerem o header X-Tenant-ID (obrigatório).
 * 
 * Combina:
 * - SecurityRequirement com os schemes "bearerAuth" e "X-Tenant-ID"
 * - Parameter documentation para o header
 * 
 * Uso:
 * @GetMapping("/dados")
 * @RequiresXTenantId
 * public ResponseEntity<?> getDados() { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@SecurityRequirement(name = "bearerAuth")
@SecurityRequirement(name = "X-Tenant-ID")
public @interface RequiresXTenantId {
}
