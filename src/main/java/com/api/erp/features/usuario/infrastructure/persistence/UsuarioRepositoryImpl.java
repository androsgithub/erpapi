package com.api.erp.features.usuario.infrastructure.persistence;

import com.api.erp.features.usuario.domain.entity.Usuario;
import com.api.erp.features.usuario.domain.entity.StatusUsuario;
import com.api.erp.features.usuario.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {
    
    // Simple in-memory implementation - replace with database access as needed
    private final Map<UUID, Usuario> usuarios = new HashMap<>();
    
    @Override
    public Usuario save(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario = Usuario.builder()
                    .id(UUID.randomUUID())
                    .nomeCompleto(usuario.getNomeCompleto())
                    .email(usuario.getEmail())
                    .cpf(usuario.getCpf())
                    .senhaHash(usuario.getSenhaHash())
                    .status(usuario.getStatus())
                    .build();
        }
        usuarios.put(usuario.getId(), usuario);
        return usuario;
    }
    
    @Override
    public Optional<Usuario> findById(UUID id) {
        return Optional.ofNullable(usuarios.get(id));
    }
    
    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarios.values().stream()
                .filter(u -> u.getEmail().getValor().equalsIgnoreCase(email))
                .findFirst();
    }
    
    @Override
    public Optional<Usuario> findByCpf(String cpf) {
        return usuarios.values().stream()
                .filter(u -> u.getCpf().getValor().equals(cpf))
                .findFirst();
    }
    
    @Override
    public List<Usuario> findAll() {
        return new ArrayList<>(usuarios.values());
    }
    
    @Override
    public List<Usuario> findByStatus(StatusUsuario status) {
        return usuarios.values().stream()
                .filter(u -> u.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return usuarios.values().stream()
                .anyMatch(u -> u.getEmail().getValor().equalsIgnoreCase(email));
    }
    
    @Override
    public boolean existsByCpf(String cpf) {
        return usuarios.values().stream()
                .anyMatch(u -> u.getCpf().getValor().equals(cpf));
    }
    
    @Override
    public void deleteById(UUID id) {
        usuarios.remove(id);
    }
}
