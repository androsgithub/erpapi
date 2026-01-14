package com.api.erp.v1.features.unidademedida.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidade de domínio que representa uma Unidade de Medida.
 * 
 * Responsabilidades:
 * - Armazenar dados da unidade de medida
 * - Validar estado da entidade através de métodos de comportamento
 * 
 * SRP: Uma única responsabilidade - representar uma unidade de medida
 */
@Entity
@Table(name = "unidade_medida")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnidadeMedida {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 10)
    private String sigla;
    
    @Column(nullable = false, length = 100)
    private String descricao;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;
    
    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();
    
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dataAtualizacao = LocalDateTime.now();
    
    /**
     * Ativa a unidade de medida
     */
    public void ativar() {
        this.ativo = true;
        atualizarDataAtualizacao();
    }
    
    /**
     * Desativa a unidade de medida
     */
    public void desativar() {
        this.ativo = false;
        atualizarDataAtualizacao();
    }
    
    /**
     * Atualiza a data de atualização para o momento atual
     */
    public void atualizarDataAtualizacao() {
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    /**
     * Verifica se a unidade está ativa
     */
    public boolean estaAtiva() {
        return this.ativo;
    }
    
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
