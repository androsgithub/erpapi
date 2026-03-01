//package com.api.erp.v1.main.features.user.domain.entity;
//
//import com.api.erp.v1.main.features.contact.domain.entity.Contact;
//import com.api.erp.v1.main.shared.domain.entity.BaseEntity;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//@Entity
//@Table(name = "tb_user_contact")
//@Getter
//@Setter
//public class UserContact extends BaseEntity {
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "contact_id")
//    private Contact contact;
//}
//
