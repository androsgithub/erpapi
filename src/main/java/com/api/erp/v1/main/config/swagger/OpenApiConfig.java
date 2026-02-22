package com.api.erp.v1.main.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("ERP API")
                .version("1.0.0")
                .description("API de Gestão de Usuários com autenticação por Bearer Token")
                .contact(new Contact()
                    .name("Equipe ERP")
                    .email("support@empresa.com")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth").addList("X-Tenant-ID"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Bearer token para autenticação"))
                .addSecuritySchemes("X-Tenant-ID",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("X-Tenant-ID")
                        .description("Header obrigatório para requisições tenant-specific. " +
                                     "Identifica unicamente o tenant em um ambiente multitenant. " +
                                     "Não é necessário para endpoints administrativos.")));
    }
}
