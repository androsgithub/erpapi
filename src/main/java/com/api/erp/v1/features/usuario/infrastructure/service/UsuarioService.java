package com.api.erp.v1.features.usuario.infrastructure.service;

import com.api.erp.v1.features.permissao.domain.entity.Permissao;
import com.api.erp.v1.features.permissao.domain.entity.Role;
import com.api.erp.v1.features.permissao.domain.repository.PermissaoRepository;
import com.api.erp.v1.features.permissao.domain.repository.RoleRepository;
import com.api.erp.v1.features.usuario.application.dto.request.AdicionarPermissoesRequest;
import com.api.erp.v1.features.usuario.application.dto.request.AdicionarRolesRequest;
import com.api.erp.v1.features.usuario.application.dto.request.CreateUsuarioRequest;
import com.api.erp.v1.features.usuario.application.dto.request.UpdateUsuarioRequest;
import com.api.erp.v1.features.usuario.domain.entity.StatusUsuario;
import com.api.erp.v1.features.usuario.domain.entity.Usuario;
import com.api.erp.v1.features.usuario.domain.entity.UsuarioPermissao;
import com.api.erp.v1.features.usuario.domain.entity.UsuarioRole;
import com.api.erp.v1.features.usuario.domain.repository.UsuarioPermissaoRepository;
import com.api.erp.v1.features.usuario.domain.repository.UsuarioRepository;
import com.api.erp.v1.features.usuario.domain.repository.UsuarioRoleRepository;
import com.api.erp.v1.features.usuario.domain.service.IPasswordEncoder;
import com.api.erp.v1.features.usuario.domain.service.IUsuarioService;
import com.api.erp.v1.features.usuario.domain.validator.IUsuarioValidator;
import com.api.erp.v1.shared.domain.entity.TenantScope;
import com.api.erp.v1.shared.domain.exception.BusinessException;
import com.api.erp.v1.shared.domain.exception.NotFoundException;
import com.api.erp.v1.shared.domain.valueobject.CPF;
import com.api.erp.v1.shared.domain.valueobject.Email;
import com.api.erp.v1.tenant.infrastructure.config.datasource.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService implements IUsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final IPasswordEncoder passwordEncoder;
    private final PermissaoRepository permissaoRepository;
    private final RoleRepository roleRepository;
    private final UsuarioPermissaoRepository usuarioPermissaoRepository;
    private final UsuarioRoleRepository usuarioRoleRepository;
    private final IUsuarioValidator validator;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, IPasswordEncoder passwordEncoder, PermissaoRepository permissaoRepository, RoleRepository roleRepository, UsuarioPermissaoRepository usuarioPermissaoRepository, UsuarioRoleRepository usuarioRoleRepository, @Qualifier("usuarioValidatorProxy") IUsuarioValidator validator) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissaoRepository = permissaoRepository;
        this.roleRepository = roleRepository;
        this.usuarioPermissaoRepository = usuarioPermissaoRepository;
        this.usuarioRoleRepository = usuarioRoleRepository;
        this.validator = validator;
    }


    @Override
    public Usuario criar(CreateUsuarioRequest request) {
        // Validações
        validator.validar(request);

        // Verifica duplicidade
        if (usuarioRepository.existsByEmail(new Email(request.email()))) {
            throw new BusinessException("Email já cadastrado");
        }

        if (usuarioRepository.existsByCpf(new CPF(request.cpf()))) {
            throw new BusinessException("CPF já cadastrado");
        }

        // Cria usuário
        Usuario usuario = Usuario.builder().tenantId(request.tenantId()).nomeCompleto(request.nomeCompleto()).email(new Email(request.email())).cpf(new CPF(request.cpf())).senhaHash(passwordEncoder.encode(request.senha())).status(StatusUsuario.ATIVO).build();

        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario atualizar(Long id, UpdateUsuarioRequest request) {
        Usuario usuario = buscarPorId(id);

        if (request.getNomeCompleto() != null) {
            usuario = Usuario.builder().id(usuario.getId()).nomeCompleto(request.getNomeCompleto()).email(usuario.getEmail()).cpf(usuario.getCpf()).senhaHash(usuario.getSenha_hash()).status(usuario.getStatus()).build();
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public List<Usuario> listarPendentes() {
        return usuarioRepository.findByStatus(StatusUsuario.PENDENTE_APROVACAO);
    }

    @Override
    public void inativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.inativar();
        usuarioRepository.save(usuario);
    }

    @Override
    public Usuario aprovar(Long usuarioId, Long gestorId) {
        throw new BusinessException("Empresa não requer aprovação de gestor");
    }

    @Override
    public Usuario rejeitar(Long usuarioId, Long gestorId, String motivo) {
        throw new BusinessException("Empresa não requer aprovação de gestor");
    }

    @Override
    @Transactional
    public void adicionarPermissoes(Long usuarioId, AdicionarPermissoesRequest request) {

        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new NotFoundException("Usuário não existe"));

        Long tenantId = TenantContext.getTenantId();
        Long tenantGroupId = TenantContext.getGroupId();

        for (Long permissaoId : request.permissaoIds()) {

            Permissao permissao = permissaoRepository.findById(permissaoId).orElseThrow(() -> new NotFoundException("Permissão não encontrada"));

            boolean exists = usuarioPermissaoRepository.existsByUsuarioIdAndPermissaoIdAndTenantIdAndTenantGroupId(
                    usuarioId,
                    permissaoId,
                    tenantId,
                    tenantGroupId
            );

            if (exists) continue;

            UsuarioPermissao up = new UsuarioPermissao();

            up.setUsuario(usuario);
            up.setPermissao(permissao);
            up.setTenantId(tenantId);
            up.setTenantGroupId(tenantGroupId);
            up.setScope(TenantScope.TENANT);

            usuarioPermissaoRepository.save(up);
        }
    }


    @Override
    public void removerPermissao(Long usuarioId, Long permissaoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new NotFoundException("Usuário não existe"));

        Permissao permissao = permissaoRepository.findById(permissaoId).orElseThrow(() -> new NotFoundException("Permissão não encontrada"));

        usuario.getPermissoes().remove(permissao);
        usuarioRepository.save(usuario);
    }

    @Override
    public List<Permissao> listarPermissoes(Long usuarioId) {
        ArrayList arrayList = usuarioRepository.findById(usuarioId).map(up -> new ArrayList(up.getPermissoes())).orElseGet(ArrayList::new);
        return arrayList;
    }

    @Override
    @Transactional
    public void adicionarRoles(Long usuarioId, AdicionarRolesRequest request) {

        Usuario usuario = buscarPorId(usuarioId);

        Long tenantId = TenantContext.getTenantId();
        Long tenantGroupId = TenantContext.getGroupId();

        for (Long roleId : request.roleIds()) {

            Role role = roleRepository.findById(roleId).orElseThrow(() -> new NotFoundException("Role não encontrada"));

            boolean exists = usuarioRoleRepository.existsByUsuarioIdAndRoleIdAndTenantIdAndTenantGroupId(usuarioId, roleId, tenantId, tenantGroupId);

            if (exists) continue;

            UsuarioRole ur = new UsuarioRole();

            ur.setUsuario(usuario);
            ur.setRole(role);
            ur.setTenantId(tenantId);
            ur.setTenantGroupId(tenantGroupId);
            ur.setScope(TenantScope.TENANT); // se tiver

            usuarioRoleRepository.save(ur);
        }
    }


    @Override
    @Transactional
    public void removerRole(Long usuarioId, Long roleId) {

        Long tenantId = TenantContext.getTenantId();
        Long tenantGroupId = TenantContext.getGroupId();

        UsuarioRole usuarioRole = usuarioRoleRepository.findByUsuarioIdAndRoleIdAndTenantIdAndTenantGroupId(usuarioId, roleId, tenantId, tenantGroupId).orElseThrow(() -> new NotFoundException("Role não vinculada ao usuário"));

        usuarioRoleRepository.delete(usuarioRole);
    }


    @Override
    public List<Role> listarRoles(Long usuarioId) {
        return usuarioRoleRepository.listarRoles(usuarioId, TenantContext.getTenantId(), TenantContext.getGroupId());
    }

}
