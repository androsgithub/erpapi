package com.api.erp.v1.features.cliente.domain.entity;

import com.api.erp.v1.features.contato.domain.entity.ClienteContato;
import com.api.erp.v1.features.endereco.domain.entity.ClienteEndereco;
import com.api.erp.v1.shared.domain.valueobject.CustomData;
import com.api.erp.v1.shared.infrastructure.persistence.converters.CustomDataAttributeConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClienteStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCliente tipo;

    @Embedded
    private ClienteDadosFiscais dadosFiscais;

    @Embedded
    private ClienteDadosFinanceiros dadosFinanceiros;

    @Embedded
    private ClientePreferencias preferencias;

    @OneToMany(
            mappedBy = "cliente",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<ClienteContato> contatos;

    @OneToMany(
            mappedBy = "cliente",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<ClienteEndereco> enderecos;


    @Convert(converter = CustomDataAttributeConverter.class)
    @Column(columnDefinition = "json")
    private CustomData customData;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }
}
