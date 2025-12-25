package com.api.erp.v1.features.permissao.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Entity
@Table(name = "permissao")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Permissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String modulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAcao acao;

    @Embedded
    private ContextoPermissao contexto;

    @Column(nullable = false)
    private boolean ativo;

    public Map<String, String> getContexto(){
        return contexto.getContexto();
    }
}
