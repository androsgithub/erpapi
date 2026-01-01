package com.api.erp.v1.shared.infrastructure.bootstrap;

import com.api.erp.v1.features.camposcustom.domain.entity.CamposCustomPermissions;
import com.api.erp.v1.features.cliente.domain.entity.ClientePermissions;
import com.api.erp.v1.features.contato.domain.entity.ContatoPermissions;
import com.api.erp.v1.features.empresa.domain.entity.EmpresaPermissions;
import com.api.erp.v1.features.endereco.domain.entity.EnderecoPermissions;
import com.api.erp.v1.features.permissao.domain.entity.*;
import com.api.erp.v1.features.permissao.domain.repository.PermissaoRepository;
import com.api.erp.v1.features.permissao.domain.repository.RoleRepository;
import com.api.erp.v1.features.produto.domain.entity.ComposicaoPermissions;
import com.api.erp.v1.features.produto.domain.entity.ListaExpandidaPermissions;
import com.api.erp.v1.features.produto.domain.entity.ProdutoPermissions;
import com.api.erp.v1.features.usuario.domain.entity.UsuarioPermissions;
import com.api.erp.v1.shared.infrastructure.bootstrap.util.PermissionReflectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermissaoSeed {

    private static final List<Class<?>> PERMISSION_CLASSES = List.of(
            UsuarioPermissions.class,
            EmpresaPermissions.class,
            EnderecoPermissions.class,
            PermissaoPermissions.class,
            RolePermissions.class,
            ProdutoPermissions.class,
            ComposicaoPermissions.class,
            ListaExpandidaPermissions.class,
            ContatoPermissions.class,
            ClientePermissions.class,
            CamposCustomPermissions.class
    );

    private final PermissaoRepository permissaoRepository;
    private final RoleRepository roleRepository;

    public void executar() {
        log.info("[PERMISSAO SEED] Inicializando permissões e roles padrão...");


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

        log.info("[PERMISSAO SEED] Permissões e roles inicializadas com sucesso.");
    }

    /* ===================== MÉTODOS AUXILIARES ===================== */

    private Permissao criarPermissao(String codigo, String nome, String modulo, TipoAcao acao) {
        return permissaoRepository.findByCodigo(codigo)
                .orElseGet(() -> {
                    log.info("[PERMISSAO SEED] Criando permissão: {}", codigo);
                    return permissaoRepository.save(
                            Permissao.builder()
                                    .codigo(codigo)
                                    .nome(nome)
                                    .modulo(modulo)
                                    .acao(acao)
                                    .ativo(true)
                                    .build()
                    );
                });
    }

    private Role criarRole(String nome, Set<Permissao> permissoes) {
        return roleRepository.findByNome(nome)
                .orElseGet(() -> {
                    log.info("[PERMISSAO SEED] Criando role: {}", nome);
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

