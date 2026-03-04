package com.api.erp.v1.main.features.businesspartner.application.mapper;

import com.api.erp.v1.main.features.businesspartner.application.dto.response.BusinessPartnerCompleteResponseDto;
import com.api.erp.v1.main.features.businesspartner.domain.entity.BusinessPartner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", uses = BusinessPartnerDadosFiscaisMapper.class)
public interface IBusinessPartnerCompleteMapper {

    @Mapping(target = "dadosFiscais", source = "dadosFiscais")
//    @Mapping(target = "contacts", expression = "java(mapearContacts(businesspartner.getContacts()))")
//    @Mapping(target = "addresss", expression = "java(mapearAddresss(businesspartner.getAddresss()))")
    BusinessPartnerCompleteResponseDto toResponse(BusinessPartner businesspartner);

    List<BusinessPartnerCompleteResponseDto> toResponseList(List<BusinessPartner> businesspartners);

//    default Set<ContactResponse> mapearContacts(Set<BusinessPartnerContact> businesspartnerContacts) {
//        if (businesspartnerContacts == null || businesspartnerContacts.isEmpty()) {
//            return Set.of();
//        }
//
//        return businesspartnerContacts
//                .stream()
//                .filter(uc -> uc.getContact() != null)
//                .map(uc -> new ContactResponse(uc.getContact().getId(),
//                        uc.getContact().getTipo() != null ? uc.getContact().getTipo().toString() : null,
//                        uc.getContact().getValor(),
//                        uc.getContact().getDescricao(),
//                        uc.getContact().isPrincipal(),
//                        uc.getContact().isAtivo(),
//                        uc.getBusinessPartner().getCustomData(),
//                        uc.getContact().getCreatedAt().toLocalDateTime(),
//                        uc.getContact().getUpdatedAt().toLocalDateTime())).collect(Collectors.toSet());
//    }

//    default Set<AddressResponse> mapearAddresss(Set<BusinessPartnerAddress> businesspartnerAddresss) {
//        if (businesspartnerAddresss == null || businesspartnerAddresss.isEmpty()) {
//            return Set.of();
//        }
//
//        return businesspartnerAddresss
//                .stream()
//                .filter(uc -> uc.getAddress() != null)
//                .map(uc -> new AddressResponse(
//                        uc.getAddress().getId(),
//                        uc.getAddress().getRua().toString(),
//                        uc.getAddress().getNumero(),
//                        uc.getAddress().getComplemento(),
//                        uc.getAddress().getBairro(),
//                        uc.getAddress().getCidade(),
//                        uc.getAddress().getEstado(),
//                        uc.getAddress().getCep(),
//                        uc.getAddress().getTipo(),
//                        uc.getAddress().getPrincipal()

    /// /                        uc.getAddress().getCustomData(),
    /// /                        uc.getAddress().getCreatedAt(),
    /// /                        uc.getAddress().getUpdatedAt(),
    /// /                        uc.getAddress().getCreatedBy(),
    /// /                        uc.getAddress().getUpdatedBy())).collect(Collectors.toSet()
//                        );
//    }
    default Page<BusinessPartnerCompleteResponseDto> toResponsePage(Page<BusinessPartner> page) {
        return page.map(this::toResponse);
    }
}
