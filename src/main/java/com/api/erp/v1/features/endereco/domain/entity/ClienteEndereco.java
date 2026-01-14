package com.api.erp.v1.features.endereco.domain.entity;

import com.api.erp.v1.features.cliente.domain.entity.Cliente;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_cliente_endereco", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cliente_id", "endereco_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteEndereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false, updatable = false)
    @JsonIgnore
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "endereco_id", nullable = false, updatable = false)
    private Endereco endereco;

    @Column(name = "principal", nullable = false)
    private Boolean principal = false;
}

