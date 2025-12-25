package com.api.erp.v1.features.permissao.application.service;

import com.api.erp.v1.features.permissao.domain.entity.Permissao;
import com.api.erp.v1.features.permissao.domain.entity.Role;
import com.api.erp.v1.features.permissao.domain.entity.UsuarioPermissao;
import com.api.erp.v1.features.permissao.domain.repository.UsuarioPermissaoRepository;
import com.api.erp.v1.features.permissao.domain.service.PermissaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissaoServiceImpl implements PermissaoService {

    private final UsuarioPermissaoRepository usuarioPermissaoRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(Long usuarioId, String permissaoCodigo, Map<String, String> contexto) {
        UsuarioPermissao usuarioPermissao = usuarioPermissaoRepository.findByUsuarioId(usuarioId).orElse(null);

        if (usuarioPermissao == null) {
            log.warn("Nenhuma permissão encontrada para o usuário {}", usuarioId);
            return false;
        }

        // 1. Verificar permissões diretas
        for (Permissao p : usuarioPermissao.getPermissoesDiretas()) {
            if (p.getCodigo().equals(permissaoCodigo)) {
                // Adicionar lógica de contexto aqui se necessário
                return true;
            }
        }

        // 2. Verificar permissões herdadas de roles
        Set<Role> rolesAProcessar = new HashSet<>(usuarioPermissao.getRoles());
        Set<Role> rolesProcessadas = new HashSet<>();

        while (!rolesAProcessar.isEmpty()) {
            Role role = rolesAProcessar.iterator().next();
            rolesAProcessar.remove(role);
            rolesProcessadas.add(role);

            // Verifica permissões do role atual
            for (Permissao p : role.getPermissoes()) {
                if (p.getCodigo().equals(permissaoCodigo)) {
                    // Adicionar lógica de contexto aqui se necessário
                    return true;
                }
            }
        }

        return false; // Se não encontrou a permissão
    }
}
