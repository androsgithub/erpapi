package com.api.erp.v1.features.usuario.infrastructure.service;

import com.api.erp.v1.features.permissao.domain.entity.Permissao;
import com.api.erp.v1.features.permissao.domain.entity.Role;
import com.api.erp.v1.features.permissao.domain.entity.UsuarioPermissao;
import com.api.erp.v1.features.permissao.domain.repository.PermissaoRepository;
import com.api.erp.v1.features.permissao.domain.repository.RoleRepository;
import com.api.erp.v1.features.permissao.domain.repository.UsuarioPermissaoRepository;
import com.api.erp.v1.features.usuario.application.dto.request.AdicionarPermissoesRequest;
import com.api.erp.v1.features.usuario.application.dto.request.AdicionarRolesRequest;
import com.api.erp.v1.features.usuario.application.dto.request.CreateUsuarioRequest;
import com.api.erp.v1.features.usuario.application.dto.request.UpdateUsuarioRequest;
import com.api.erp.v1.features.usuario.domain.entity.StatusUsuario;
import com.api.erp.v1.features.usuario.domain.entity.Usuario;
import com.api.erp.v1.features.usuario.domain.repository.UsuarioRepository;
import com.api.erp.v1.features.usuario.domain.service.IPasswordEncoder;
import com.api.erp.v1.features.usuario.domain.service.IUsuarioService;
import com.api.erp.v1.features.usuario.domain.validator.IUsuarioValidator;
import com.api.erp.v1.shared.domain.exception.BusinessException;
import com.api.erp.v1.shared.domain.exception.NotFoundException;
import com.api.erp.v1.shared.domain.valueobject.CPF;
import com.api.erp.v1.shared.domain.valueobject.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService implements IUsuarioService {
    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private IPasswordEncoder passwordEncoder;
    @Autowired
    private PermissaoRepository permissaoRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UsuarioPermissaoRepository usuarioPermissaoRepository;
    @Autowired
    @Qualifier("usuarioValidatorProxy")
    private IUsuarioValidator validator;


    @Override
    public Usuario criar(CreateUsuarioRequest request) {
        // Validações
        validator.validar(request);

        // Verifica duplicidade
        if (repository.existsByEmail(new Email(request.email()))) {
            throw new BusinessException("Email já cadastrado");
        }

        if (repository.existsByCpf(new CPF(request.cpf()))) {
            throw new BusinessException("CPF já cadastrado");
        }

        // Cria usuário
        Usuario usuario = Usuario.builder()
                .tenantId(request.tenantId())
                .nomeCompleto(request.nomeCompleto())
                .email(new Email(request.email()))
                .cpf(new CPF(request.cpf()))
                .senhaHash(passwordEncoder.encode(request.senha()))
                .status(StatusUsuario.ATIVO)
                .build();

        return repository.save(usuario);
    }

    @Override
    public Usuario atualizar(Long id, UpdateUsuarioRequest request) {
        Usuario usuario = buscarPorId(id);

        if (request.getNomeCompleto() != null) {
            usuario = Usuario.builder()
                    .id(usuario.getId())
                    .nomeCompleto(request.getNomeCompleto())
                    .email(usuario.getEmail())
                    .cpf(usuario.getCpf())
                    .senhaHash(usuario.getSenha_hash())
                    .status(usuario.getStatus())
                    .build();
        }

        return repository.save(usuario);
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    @Override
    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    @Override
    public List<Usuario> listarPendentes() {
        return repository.findByStatus(StatusUsuario.PENDENTE_APROVACAO);
    }

    @Override
    public void inativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.inativar();
        repository.save(usuario);
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
    public void adicionarPermissoes(Long usuarioId, AdicionarPermissoesRequest request) {
        Usuario usuario = buscarPorId(usuarioId);

        UsuarioPermissao usuarioPermissao = usuarioPermissaoRepository.findByUsuarioId(usuarioId)
                .orElse(UsuarioPermissao.builder()
                        .usuario(usuario)
                        .dataInicio(request.dataInicio())
                        .dataFim(request.dataFim())
                        .build());

        for (Long permissaoId : request.permissaoIds()) {
            Permissao permissao = permissaoRepository.findById(permissaoId)
                    .orElseThrow(() -> new NotFoundException("Permissão não encontrada"));

            usuarioPermissao.getPermissoesDiretas().add(permissao);
        }

        usuarioPermissaoRepository.save(usuarioPermissao);
    }

    @Override
    public void removerPermissao(Long usuarioId, Long permissaoId) {
        UsuarioPermissao usuarioPermissao = usuarioPermissaoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuário sem permissões atribuídas"));

        Permissao permissao = permissaoRepository.findById(permissaoId)
                .orElseThrow(() -> new NotFoundException("Permissão não encontrada"));

        usuarioPermissao.getPermissoesDiretas().remove(permissao);
        usuarioPermissaoRepository.save(usuarioPermissao);
    }

    @Override
    public List<Permissao> listarPermissoes(Long usuarioId) {
        ArrayList arrayList = usuarioPermissaoRepository.findByUsuarioId(usuarioId)
                .map(up -> new ArrayList(up.getPermissoesDiretas()))
                .orElseGet(ArrayList::new);
        return arrayList;
    }


    @Override
    public void adicionarRoles(Long usuarioId, AdicionarRolesRequest request) {
        Usuario usuario = buscarPorId(usuarioId);

        UsuarioPermissao usuarioPermissao = usuarioPermissaoRepository.findByUsuarioId(usuarioId)
                .orElse(UsuarioPermissao.builder()
                        .usuario(usuario)
                        .dataInicio(request.dataInicio())
                        .dataFim(request.dataFim())
                        .build());

        for (Long roleId : request.roleIds()) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new NotFoundException("Role não encontrada"));

            usuarioPermissao.getRoles().add(role);
        }

        usuarioPermissaoRepository.save(usuarioPermissao);
    }

    @Override
    public void removerRole(Long usuarioId, Long roleId) {
        UsuarioPermissao usuarioPermissao = usuarioPermissaoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuário sem roles atribuídas"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role não encontrada"));

        usuarioPermissao.getRoles().remove(role);
        usuarioPermissaoRepository.save(usuarioPermissao);
    }

    @Override
    public List<Role> listarRoles(Long usuarioId) {
        return usuarioPermissaoRepository.findByUsuarioId(usuarioId)
                .map(up -> new ArrayList<>(up.getRoles()))
                .orElseGet(ArrayList::new);
    }

}
