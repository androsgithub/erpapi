package com.api.erp.v1.tenant.presentation.documentation;

import com.api.erp.v1.shared.common.constant.HeaderConst;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Parameters({
        @Parameter(
                name = HeaderConst.TENANT_ID_HEADER,
                in = ParameterIn.HEADER,
                required = true,
                description = "Identificador do tenant"
        )
})
public @interface TenantIdentifierHeader {
}
