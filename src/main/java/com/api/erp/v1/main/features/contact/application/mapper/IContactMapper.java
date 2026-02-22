package com.api.erp.v1.main.features.contact.application.mapper;

import com.api.erp.v1.main.features.contact.application.dto.response.ContactResponse;
import com.api.erp.v1.main.features.contact.domain.entity.Contact;
import com.api.erp.v1.main.shared.application.mapper.BaseMapper;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring", uses = BaseMapper.class)
public interface IContactMapper {
    ContactResponse toResponse(Contact contact);

    java.util.List<ContactResponse> toResponseList(java.util.List<Contact> contacts);

    Set<ContactResponse> toResponseSet(Set<Contact> contacts);
}
