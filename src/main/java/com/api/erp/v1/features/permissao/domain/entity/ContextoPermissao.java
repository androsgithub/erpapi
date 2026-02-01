package com.api.erp.v1.features.permissao.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Map;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContextoPermissao implements Serializable {

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tb_permissao_contexto", joinColumns = @JoinColumn(name = "permissao_id"))
    @MapKeyColumn(name = "contexto_chave")
    @Column(name = "contexto_valor")
    private Map<String, String> contexto;
}
