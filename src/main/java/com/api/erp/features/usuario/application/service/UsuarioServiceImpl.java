package com.api.erp.features.usuario.application.service;

import com.api.erp.features.usuario.application.dto.request.CreateUsuarioRequest;
import com.api.erp.features.usuario.application.dto.request.UpdateUsuarioRequest;
import com.api.erp.features.usuario.domain.entity.Usuario;
import com.api.erp.features.usuario.domain.entity.StatusUsuario;
import com.api.erp.features.usuario.domain.repository.UsuarioRepository;
import com.api.erp.features.usuario.domain.service.UsuarioService;
import com.api.erp.features.usuario.domain.validator.UsuarioValidator;
import com.api.erp.shared.domain.valueobject.CPF;
import com.api.erp.shared.domain.valueobject.Email;
import com.api.erp.shared.domain.exception.BusinessException;
import com.api.erp.shared.domain.exception.NotFoundException;
import java.util.List;
import java.util.UUID;

public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository repository;
    private final UsuarioValidator validator;
    private final PasswordEncoder passwordEncoder;
    
    public UsuarioServiceImpl(UsuarioRepository repository, UsuarioValidator validator, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public Usuario criar(CreateUsuarioRequest request) {
        // Validações
        validator.validar(request);
        
        // Verifica duplicidade
        if (repository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado");
        }
        
        if (repository.existsByCpf(request.getCpf())) {
            throw new BusinessException("CPF já cadastrado");
        }
        
        // Cria usuário
        Usuario usuario = Usuario.builder()
            .nomeCompleto(request.getNomeCompleto())
            .email(new Email(request.getEmail()))
            .cpf(new CPF(request.getCpf()))
            .senhaHash(passwordEncoder.encode(request.getSenha()))
            .status(StatusUsuario.ATIVO)
            .build();
        
        return repository.save(usuario);
    }
    
    @Override
    public Usuario atualizar(UUID id, UpdateUsuarioRequest request) {
        Usuario usuario = buscarPorId(id);
        
        if (request.getNomeCompleto() != null) {
            usuario = Usuario.builder()
                .id(usuario.getId())
                .nomeCompleto(request.getNomeCompleto())
                .email(usuario.getEmail())
                .cpf(usuario.getCpf())
                .senhaHash(usuario.getSenhaHash())
                .status(usuario.getStatus())
                .build();
        }
        
        return repository.save(usuario);
    }
    
    @Override
    public Usuario buscarPorId(UUID id) {
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
    public void inativar(UUID id) {
        Usuario usuario = buscarPorId(id);
        usuario.inativar();
        repository.save(usuario);
    }

    @Override
    public Usuario aprovar(UUID usuarioId, UUID gestorId) {
        throw new BusinessException("Empresa não requer aprovação de gestor");
    }

    @Override
    public Usuario rejeitar(UUID usuarioId, UUID gestorId, String motivo) {
        throw new BusinessException("Empresa não requer aprovação de gestor");
    }
}
