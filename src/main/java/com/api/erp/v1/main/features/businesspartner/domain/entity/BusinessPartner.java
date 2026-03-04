package com.api.erp.v1.main.features.businesspartner.domain.entity;

import com.api.erp.v1.main.features.contact.domain.entity.Contact;
import com.api.erp.v1.main.features.address.domain.entity.Address;
import com.api.erp.v1.main.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.List;

@Entity
@Table(name = "tb_business_partner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE tb_business_partner SET deleted = true, deleted_at = now() WHERE id = ?")
public class BusinessPartner extends BaseEntity {

    @Column(name = "nome", nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BusinessPartnerStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoBusinessPartner tipo;

    @Embedded
    private BusinessPartnerDadosFiscais dadosFiscais;

    @Embedded
    private BusinessPartnerDadosFinanceiros dadosFinanceiros;

    @Embedded
    private BusinessPartnerPreferencias preferencias;

    @OneToMany
    @JoinTable(name = "TB_BUSINESS_PARTNER_CONTACT", joinColumns = @JoinColumn(name = "business_partner_id"), inverseJoinColumns = @JoinColumn(name = "contact_id"))
    private List<Contact> contacts;

    @OneToMany
    @JoinTable(name = "TB_BUSINESS_PARTNER_ADDRESS", joinColumns = @JoinColumn(name = "business_partner_id"), inverseJoinColumns = @JoinColumn(name = "address_id"))
    private List<Address> addresss;
}
