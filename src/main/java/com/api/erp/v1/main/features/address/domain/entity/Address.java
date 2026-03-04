package com.api.erp.v1.main.features.address.domain.entity;

import com.api.erp.v1.main.shared.domain.entity.BaseEntity;
import com.api.erp.v1.main.shared.domain.valueobject.CEP;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "tb_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE tb_address SET deleted = true, deleted_at = now() WHERE id = ?")
public class Address extends BaseEntity {

    @Column(name = "rua", nullable = false, length = 255)
    private String rua;

    @Column(name = "numero", nullable = false, length = 50)
    private String numero;

    @Column(name = "complemento", length = 255)
    private String complemento;

    @Column(name = "bairro", nullable = false, length = 100)
    private String bairro;

    @Column(name = "cidade", nullable = false, length = 100)
    private String cidade;

    @Column(name = "estado", nullable = false, length = 2)
    private String estado;

    @Column(name = "cep", nullable = false, length = 8)
    private CEP cep;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private AddressType tipo;

    @Column(name = "principal", nullable = false)
    private Boolean principal;

}
