package com.api.erp.v1.features.empresa.domain.entity;

import com.api.erp.v1.features.endereco.domain.entity.Endereco;
import com.api.erp.v1.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.shared.domain.valueobject.Email;
import com.api.erp.v1.shared.domain.valueobject.Telefone;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    @Column(name = "email")
    private Email email;
    @Column(name = "telefone")
    private Telefone telefone;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(
            joinColumns = @JoinColumn(name = "endereco_id"),
            inverseJoinColumns = @JoinColumn(name = "id")
    )
    @JsonIgnore
    private Endereco endereco;

    @Embedded
    @Builder.Default
    private EmpresaConfig config = new EmpresaConfig();

    @Embedded
    @Builder.Default
    private EmpresaDadosFiscais dadosFiscais = new EmpresaDadosFiscais();

    private boolean ativa;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;


}
