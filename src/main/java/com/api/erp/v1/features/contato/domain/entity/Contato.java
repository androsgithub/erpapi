package com.api.erp.v1.features.contato.domain.entity;

import com.api.erp.v1.shared.domain.valueobject.CustomData;
import com.api.erp.v1.shared.infrastructure.persistence.converters.CustomDataAttributeConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Entidade que representa um contato de uma entidade (Usuário, Empresa, Cliente, etc.)
 * 
 * Seguindo o padrão DDD, esta entidade é responsável por gerenciar as informações
 * de contato de uma entidade do sistema.
 */
@Entity
@Table(name = "contatos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoContato tipo;

    @Column(nullable = false, length = 255)
    private String valor;

    @Column(length = 255)
    private String descricao;

    @Column(nullable = false)
    @Builder.Default
    private boolean principal = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean ativo = true;

    @Convert(converter = CustomDataAttributeConverter.class)
    @Column(columnDefinition = "json")
    private CustomData customData;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dataAtualizacao = LocalDateTime.now();

    @OneToMany(
            mappedBy = "contato",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnore
    private Set<UsuarioContato> usuariosContatos;

    /**
     * Construtor simplificado para criar um contato básico
     */
    public Contato(TipoContato tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
        this.principal = false;
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Marca este contato como principal
     */
    public void marcarComoPrincipal() {
        this.principal = true;
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Desmarca este contato como principal
     */
    public void desmarcarComoPrincipal() {
        this.principal = false;
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Ativa o contato (soft delete inverso)
     */
    public void ativar() {
        this.ativo = true;
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Desativa o contato (soft delete)
     */
    public void desativar() {
        this.ativo = false;
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Verifica se o contato está ativo
     */
    public boolean estaAtivo() {
        return this.ativo;
    }

    /**
     * Verifica se o contato é o principal
     */
    public boolean ehPrincipal() {
        return this.principal;
    }

    @Override
    public String toString() {
        return String.format("Contato {tipo=%s, valor=%s, principal=%s, ativo=%s}", 
            tipo, valor, principal, ativo);
    }
}
