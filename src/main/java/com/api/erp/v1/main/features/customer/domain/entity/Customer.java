package com.api.erp.v1.main.features.customer.domain.entity;

import com.api.erp.v1.main.features.contact.domain.entity.Contact;
import com.api.erp.v1.main.features.address.domain.entity.Address;
import com.api.erp.v1.main.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.List;

@Entity
@Table(name = "tb_customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE tb_customer SET deleted = true, deleted_at = now() WHERE id = ?")
public class Customer extends BaseEntity {

    @Column(name = "nome", nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CustomerStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoCustomer tipo;

    @Embedded
    private CustomerDadosFiscais dadosFiscais;

    @Embedded
    private CustomerDadosFinanceiros dadosFinanceiros;

    @Embedded
    private CustomerPreferencias preferencias;

    @OneToMany
    @JoinTable(name = "TB_CUSTOMER_CONTACT", joinColumns = @JoinColumn(name = "customer_id"), inverseJoinColumns = @JoinColumn(name = "contact_id"))
    private List<Contact> contacts;

    @OneToMany
    @JoinTable(name = "TB_CUSTOMER_ADDRESS", joinColumns = @JoinColumn(name = "customer_id"), inverseJoinColumns = @JoinColumn(name = "address_id"))
    private List<Address> addresss;
}
