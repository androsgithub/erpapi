package com.api.erp.v1.tenant.domain.entity;

import com.api.erp.v1.features.endereco.domain.entity.Endereco;
import com.api.erp.v1.shared.domain.valueobject.Email;
import com.api.erp.v1.shared.domain.valueobject.Telefone;
import com.api.erp.v1.shared.infrastructure.persistence.converters.EmailConverter;
import com.api.erp.v1.shared.infrastructure.persistence.converters.TelefoneConverter;
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
@Table(name="tb_tenant")
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    @Convert(converter = EmailConverter.class)
    @Column(name = "email")
    private Email email;

    @Convert(converter = TelefoneConverter.class)
    @Column(name = "telefone")
    private Telefone telefone;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_id")
    @JsonIgnore
    private Endereco endereco;

    @Embedded
    @Builder.Default
    private TenantConfig config = new TenantConfig();

    @Embedded
    @Builder.Default
    private TenantDadosFiscais dadosFiscais = new TenantDadosFiscais();

    private boolean ativa;
    
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;


}
