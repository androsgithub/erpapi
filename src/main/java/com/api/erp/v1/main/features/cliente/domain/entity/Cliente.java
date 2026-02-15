package com.api.erp.v1.main.features.cliente.domain.entity;

import com.api.erp.v1.main.features.contato.domain.entity.Contato;
import com.api.erp.v1.main.features.endereco.domain.entity.Endereco;
import com.api.erp.v1.main.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.List;

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

    @OneToMany
    @JoinTable(name = "TB_CLIENTE_CONTATO", joinColumns = @JoinColumn(name = "cliente_id"), inverseJoinColumns = @JoinColumn(name = "contato_id"))
    private List<Contato> contatos;

    @OneToMany
    @JoinTable(name = "TB_CLIENTE_ENDERECO", joinColumns = @JoinColumn(name = "cliente_id"), inverseJoinColumns = @JoinColumn(name = "endereco_id"))
    private List<Endereco> enderecos;
}
