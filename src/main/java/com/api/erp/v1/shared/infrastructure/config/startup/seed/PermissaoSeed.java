package com.api.erp.v1.shared.infrastructure.config.startup.seed;

import com.api.erp.v1.features.endereco.domain.entity.EnderecoPermissions;
import com.api.erp.v1.features.permissao.domain.entity.*;
import com.api.erp.v1.features.permissao.domain.repository.PermissaoRepository;
import com.api.erp.v1.features.permissao.domain.repository.RoleRepository;
import com.api.erp.v1.features.produto.domain.entity.ComposicaoPermissions;
import com.api.erp.v1.features.produto.domain.entity.ListaExpandidaPermissions;
import com.api.erp.v1.features.produto.domain.entity.ProdutoPermissions;
import com.api.erp.v1.features.usuario.domain.entity.UsuarioPermissions;
import com.api.erp.v1.shared.domain.entity.TenantScope;
import com.api.erp.v1.shared.infrastructure.config.startup.util.PermissionReflectionUtil;
import com.api.erp.v1.tenant.domain.entity.TenantPermissions;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermissaoSeed {

    private static final List<Class<?>> PERMISSION_CLASSES = List.of(UsuarioPermissions.class, TenantPermissions.class, EnderecoPermissions.class, PermissaoPermissions.class, RolePermissions.class, ProdutoPermissions.class, ComposicaoPermissions.class, ListaExpandidaPermissions.class);

    private static final Logger logger = LoggerFactory.getLogger(PermissaoSeed.class);

    private final PermissaoRepository permissaoRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public void executar() {
        logger.info("Inicializando permissões e roles padrão...");

        /* ===================== PERMISSÕES ===================== */

        // 1. Buscar todas as permissões existentes de uma vez
        List<Permissao> permissoesExistentes = permissaoRepository.findAll();
        Map<String, Permissao> permissoesPorCodigo = permissoesExistentes.stream().collect(Collectors.toMap(Permissao::getCodigo, p -> p));

        // 2. Coletar todas as novas permissões a serem criadas
        List<Permissao> novasPermissoes = new ArrayList<>();

        for (Class<?> permissionClass : PERMISSION_CLASSES) {
            List<String> codigos = PermissionReflectionUtil.extrairPermissoes(permissionClass);
            String modulo = extrairNomeModulo(permissionClass);

            for (String codigo : codigos) {
                if (!permissoesPorCodigo.containsKey(codigo)) {
                    TipoAcao acao = extrairTipoAcao(codigo);

                    Permissao novaPermissao = Permissao.builder().codigo(codigo).nome(gerarNomeAmigavel(codigo)).modulo(modulo).acao(acao).build();
                    novaPermissao.setScope(TenantScope.GLOBAL);

                    novasPermissoes.add(novaPermissao);
                }
            }
        }

        // 3. Salvar todas as novas permissões em lote (1 query)
        if (!novasPermissoes.isEmpty()) {
            logger.info("Criando {} novas permissões em lote", novasPermissoes.size());
            List<Permissao> permissoesSalvas = permissaoRepository.saveAll(novasPermissoes);
            permissoesSalvas.forEach(p -> permissoesPorCodigo.put(p.getCodigo(), p));
        }

        /* ===================== ROLES ===================== */

        // 4. Buscar todas as roles existentes de uma vez
        List<Role> rolesExistentes = roleRepository.findAll();
        Set<String> nomesRolesExistentes = rolesExistentes.stream().map(Role::getNome).collect(Collectors.toSet());

        // 5. Criar roles faltantes em lote
        List<Role> novasRoles = new ArrayList<>();
        List<String> nomesRoles = List.of("USUARIO", "GESTOR", "ADMIN");

        for (String nomeRole : nomesRoles) {
            if (!nomesRolesExistentes.contains(nomeRole)) {
                Role novaRole = Role.builder().nome(nomeRole).permissoes(List.of()).build();
                novasRoles.add(novaRole);
            }
        }

        if (!novasRoles.isEmpty()) {
            logger.info("Criando {} novas roles em lote", novasRoles.size());
            roleRepository.saveAll(novasRoles);
        }

        logger.info("Permissões e roles inicializadas com sucesso.");
    }

    /* ===================== MÉTODOS AUXILIARES ===================== */

    private String extrairNomeModulo(Class<?> clazz) {
        return clazz.getSimpleName().replace("Permissions", "");
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
        return acao.substring(0, 1).toUpperCase() + acao.substring(1) + " " + partes[0];
    }
}