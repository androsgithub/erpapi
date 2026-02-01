package com.api.erp.v1.tenant.domain.entity.configs;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantConfig {

    @Embedded
    @Builder.Default
    private UsuarioConfig usuarioConfig = new UsuarioConfig();

    @Embedded
    @Builder.Default
    private ContatoConfig contatoConfig = new ContatoConfig();

    @Embedded
    @Builder.Default
    private ClienteConfig clienteConfig = new ClienteConfig();

    @Embedded
    @Builder.Default
    private EnderecoConfig enderecoConfig = new EnderecoConfig();

    @Embedded
    @Builder.Default
    private PermissaoConfig permissaoConfig = new PermissaoConfig();

    @Embedded
    @Builder.Default
    private UnidadeMedidaConfig unidadeMedidaConfig = new UnidadeMedidaConfig();

    @Embedded
    @Builder.Default
    private ProdutoConfig produtoConfig = new ProdutoConfig();

    @Embedded
    @Builder.Default
    private InternalTenantConfig internalTenantConfig = new InternalTenantConfig();
}


