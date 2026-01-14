package com.api.erp.v1.features.cliente.domain.entity;

import com.api.erp.v1.features.contato.domain.entity.ClienteContato;
import com.api.erp.v1.features.endereco.domain.entity.ClienteEndereco;
import com.api.erp.v1.shared.domain.entity.BaseEntity;
import com.api.erp.v1.shared.domain.valueobject.CustomData;
import com.api.erp.v1.shared.infrastructure.persistence.converters.CustomDataAttributeConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.Set;

@Entity
@Table(name = "tb_cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE tb_cliente SET deleted = true, deleted_at = now() WHERE id = ?")
public class Cliente extends BaseEntity {

    @Column(name = "nome", nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClienteStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
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
}
