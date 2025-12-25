package com.api.erp.v1.features.permissao.application.service;

import com.api.erp.v1.features.permissao.application.dto.request.AssociarPermissaoRequest;
import com.api.erp.v1.features.permissao.application.dto.request.CreatePermissaoRequest;
import com.api.erp.v1.features.permissao.application.dto.request.CreateRoleRequest;
import com.api.erp.v1.features.permissao.application.dto.response.PermissaoResponse;
import com.api.erp.v1.features.permissao.application.dto.response.RoleResponse;
import com.api.erp.v1.features.permissao.domain.entity.Permissao;
import com.api.erp.v1.features.permissao.domain.entity.Role;
import com.api.erp.v1.features.permissao.domain.repository.PermissaoRepository;
import com.api.erp.v1.features.permissao.domain.repository.RoleRepository;
import com.api.erp.v1.features.permissao.domain.repository.UsuarioPermissaoRepository;
import com.api.erp.v1.features.permissao.domain.service.GerenciamentoPermissaoService;
import com.api.erp.v1.features.usuario.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GerenciamentoPermissaoServiceImpl implements GerenciamentoPermissaoService {

    private final PermissaoRepository permissaoRepository;
    private final RoleRepository roleRepository;
    private final UsuarioPermissaoRepository usuarioPermissaoRepository;
    private final UsuarioRepository usuarioRepository; // Assuming this exists

    @Override
    @Transactional
    public PermissaoResponse createPermissao(CreatePermissaoRequest request) {
        // Add validation logic here
        Permissao permissao = Permissao.builder()
                .codigo(request.codigo())
                .nome(request.nome())
                .modulo(request.modulo())
                .acao(request.acao())
                .ativo(request.ativo())
                .build();
        permissao = permissaoRepository.save(permissao);
        return toPermissaoResponse(permissao);
    }

    @Override
    @Transactional
    public RoleResponse createRole(CreateRoleRequest request) {
        // Add validation logic here
        Role role = Role.builder()
                .nome(request.nome())
                .build();
        role = roleRepository.save(role);
        return toRoleResponse(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissaoResponse> getAllPermissoes() {
        return permissaoRepository.findAll().stream().map(this::toPermissaoResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream().map(this::toRoleResponse).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void associarPermissao(AssociarPermissaoRequest request) {
        // Lógica para associar permissões/roles a um usuário
    }

    private PermissaoResponse toPermissaoResponse(Permissao permissao) {
        return new PermissaoResponse(
                permissao.getId(),
                permissao.getCodigo(),
                permissao.getNome(),
                permissao.getModulo(),
                permissao.getAcao(),
                null, // Simplified context
                permissao.isAtivo()
        );
    }

    private RoleResponse toRoleResponse(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getNome(),
                null
        );
    }
}
