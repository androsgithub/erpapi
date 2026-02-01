package com.api.erp.v1.shared.infrastructure.config.startup.seed;

import com.api.erp.v1.features.endereco.domain.entity.EnderecoPermissions;
import com.api.erp.v1.features.permissao.domain.entity.*;
import com.api.erp.v1.features.permissao.domain.repository.PermissaoRepository;
import com.api.erp.v1.features.permissao.domain.repository.RoleRepository;
import com.api.erp.v1.features.produto.domain.entity.ComposicaoPermissions;
import com.api.erp.v1.features.produto.domain.entity.ListaExpandidaPermissions;
import com.api.erp.v1.features.produto.domain.entity.ProdutoPermissions;
import com.api.erp.v1.features.usuario.domain.entity.UsuarioPermissions;
import com.api.erp.v1.shared.infrastructure.config.startup.util.PermissionReflectionUtil;
import com.api.erp.v1.tenant.domain.entity.TenantPermissions;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PermissaoSeed {

    private static final List<Class<?>> PERMISSION_CLASSES = List.of(
            UsuarioPermissions.class,
            TenantPermissions.class,
            EnderecoPermissions.class,
            PermissaoPermissions.class,
            RolePermissions.class,
            ProdutoPermissions.class,
            ComposicaoPermissions.class,
            ListaExpandidaPermissions.class
    );


    private static final Logger logger = LoggerFactory.getLogger(PermissaoSeed.class);

    private final PermissaoRepository permissaoRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public void executar() {
        logger.info("Inicializando permissões e roles padrão...");


        /* ===================== PERMISSÕES ===================== */

        for (Class<?> permissionClass : PERMISSION_CLASSES) {
            criarPermissoesDaClasse(permissionClass);
        }

        /* ===================== ROLES ===================== */

        criarRole(
                "USUARIO",
                Set.of()
        );

        criarRole(
                "GESTOR",
                Set.of()
        );

        criarRole(
                "ADMIN",
                Set.of()
        );

        logger.info("Permissões e roles inicializadas com sucesso.");
    }

    /* ===================== MÉTODOS AUXILIARES ===================== */

    private Permissao criarPermissao(String codigo, String nome, String modulo, TipoAcao acao) {
        return permissaoRepository.findByCodigo(codigo)
                .orElseGet(() -> {
                    logger.info("Criando permissão: {}", codigo);
                    return permissaoRepository.save(
                            Permissao.builder()
                                    .codigo(codigo)
                                    .nome(nome)
                                    .modulo(modulo)
                                    .acao(acao)
                                    .build()
                    );
                });
    }

    private Role criarRole(String nome, Set<Permissao> permissoes) {
        return roleRepository.findByNome(nome)
                .orElseGet(() -> {
                    logger.info("Criando role: {}", nome);
                    return roleRepository.save(
                            Role.builder()
                                    .nome(nome)
                                    .permissoes(permissoes)
                                    .build()
                    );
                });
    }

    private void criarPermissoesDaClasse(Class<?> permissionClass) {

        List<String> codigos = PermissionReflectionUtil.extrairPermissoes(permissionClass);

        String modulo = extrairNomeModulo(permissionClass);

        for (String codigo : codigos) {

            TipoAcao acao = extrairTipoAcao(codigo);

            criarPermissao(
                    codigo,
                    gerarNomeAmigavel(codigo),
                    modulo,
                    acao
            );
        }

    }
    private String extrairNomeModulo(Class<?> clazz) {
        return clazz.getSimpleName()
                .replace("Permissions", "");
    }
    private TipoAcao extrairTipoAcao(String codigo) {

        if (codigo.endsWith(".criar")) return TipoAcao.CRIAR;
        if (codigo.endsWith(".atualizar")) return TipoAcao.EDITAR;
        if (codigo.endsWith(".visualizar")) return TipoAcao.VISUALIZAR;
        if (codigo.endsWith(".deletar")) return TipoAcao.EXCLUIR;

        return TipoAcao.OUTRO;
    }

    private String gerarNomeAmigavel(String codigo) {
        String[] partes = codigo.split("\\.");
        String acao = partes[1];

        return acao.substring(0, 1).toUpperCase() + acao.substring(1)
                + " " + partes[0];
    }




}

