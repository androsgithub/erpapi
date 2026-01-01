package com.api.erp.v1.features.endereco.domain.entity;

import com.api.erp.v1.shared.domain.valueobject.CEP;
import com.api.erp.v1.shared.domain.valueobject.CustomData;
import com.api.erp.v1.shared.infrastructure.persistence.converters.CustomDataAttributeConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rua;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    @Column(name = "cep")
    private CEP cep;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    @Convert(converter = CustomDataAttributeConverter.class)
    @Column(columnDefinition = "json")
    private CustomData customData;

    public Endereco() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Endereco(String rua, String numero, String bairro, String cidade, String estado, String cep, CustomData customData) {
        this();
        this.rua = rua;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = new CEP(cep);
        this.customData = customData;
    }

    @Override
    public String toString() {
        return rua + ", " + numero +
               (complemento != null ? ", " + complemento : "") +
               " - " + bairro + " - " + cidade + "/" + estado + " - " + cep.getFormatado();
    }
}
