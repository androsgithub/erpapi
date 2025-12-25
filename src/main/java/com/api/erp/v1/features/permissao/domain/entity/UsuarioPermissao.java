package com.api.erp.v1.features.permissao.domain.entity;

import com.api.erp.v1.features.usuario.domain.entity.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usuario_permissao")
public class UsuarioPermissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "usuario_permissao_direta",
            joinColumns = @JoinColumn(name = "usuario_permissao_id"),
            inverseJoinColumns = @JoinColumn(name = "permissao_id")
    )
    private Set<Permissao> permissoesDiretas = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "usuario_role",
            joinColumns = @JoinColumn(name = "usuario_permissao_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;
}
