package com.api.erp.v1.features.permissao.infrastructure.service;

import com.api.erp.v1.features.permissao.application.dto.request.AssociarPermissaoRequest;
import com.api.erp.v1.features.permissao.application.dto.request.CreatePermissaoRequest;
import com.api.erp.v1.features.permissao.application.dto.request.CreateRoleRequest;
import com.api.erp.v1.features.permissao.domain.entity.Permissao;
import com.api.erp.v1.features.permissao.domain.entity.Role;
import com.api.erp.v1.features.permissao.domain.repository.PermissaoRepository;
import com.api.erp.v1.features.permissao.domain.repository.RoleRepository;
import com.api.erp.v1.features.permissao.domain.repository.UsuarioPermissaoRepository;
import com.api.erp.v1.features.permissao.domain.service.IGerenciamentoPermissaoService;
import com.api.erp.v1.features.usuario.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
public class GerenciamentoPermissaoService implements IGerenciamentoPermissaoService {

    private final PermissaoRepository permissaoRepository;
    private final RoleRepository roleRepository;
    private final UsuarioPermissaoRepository usuarioPermissaoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public Permissao createPermissao(CreatePermissaoRequest request) {
        Permissao permissao = Permissao.builder()
                .codigo(request.codigo())
                .nome(request.nome())
                .modulo(request.modulo())
                .acao(request.acao())
                .ativo(request.ativo())
                .build();
        return permissaoRepository.save(permissao);
    }

    @Override
    @Transactional
    public Role createRole(CreateRoleRequest request) {
        Role role = Role.builder()
                .nome(request.nome())
                .build();
        return roleRepository.save(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permissao> getAllPermissoes() {
        return permissaoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional
    public void associarPermissao(AssociarPermissaoRequest request) {
        // Lógica para associar permissões/roles a um usuário
    }
}
