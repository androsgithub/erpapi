package com.api.erp.v1.main.dynamic.features.address.domain.entity;

import com.api.erp.v1.main.shared.domain.entity.TenantScopeEntity;
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
public class Address extends TenantScopeEntity {

    @Column(name = "street", nullable = false, length = 255)
    private String street;

    @Column(name = "number", nullable = false, length = 50)
    private String number;

    @Column(name = "complement", length = 255)
    private String complement;

    @Column(name = "district", nullable = false, length = 100)
    private String district;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 2)
    private String state;

    @Column(name = "postal_code", nullable = false, length = 8)
    private CEP postalCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AddressType type;

    @Column(name = "is_primary", nullable = false)
    private Boolean primary;

}