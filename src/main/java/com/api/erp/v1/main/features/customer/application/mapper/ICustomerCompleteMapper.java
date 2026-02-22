package com.api.erp.v1.main.features.customer.application.mapper;

import com.api.erp.v1.main.features.customer.application.dto.response.CustomerCompleteResponseDto;
import com.api.erp.v1.main.features.customer.domain.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", uses = CustomerDadosFiscaisMapper.class)
public interface ICustomerCompleteMapper {

    @Mapping(target = "dadosFiscais", source = "dadosFiscais")
//    @Mapping(target = "contacts", expression = "java(mapearContacts(customer.getContacts()))")
//    @Mapping(target = "addresss", expression = "java(mapearAddresss(customer.getAddresss()))")
    CustomerCompleteResponseDto toResponse(Customer customer);

    List<CustomerCompleteResponseDto> toResponseList(List<Customer> customers);

//    default Set<ContactResponse> mapearContacts(Set<CustomerContact> customerContacts) {
//        if (customerContacts == null || customerContacts.isEmpty()) {
//            return Set.of();
//        }
//
//        return customerContacts
//                .stream()
//                .filter(uc -> uc.getContact() != null)
//                .map(uc -> new ContactResponse(uc.getContact().getId(),
//                        uc.getContact().getTipo() != null ? uc.getContact().getTipo().toString() : null,
//                        uc.getContact().getValor(),
//                        uc.getContact().getDescricao(),
//                        uc.getContact().isPrincipal(),
//                        uc.getContact().isAtivo(),
//                        uc.getCustomer().getCustomData(),
//                        uc.getContact().getCreatedAt().toLocalDateTime(),
//                        uc.getContact().getUpdatedAt().toLocalDateTime())).collect(Collectors.toSet());
//    }

//    default Set<AddressResponse> mapearAddresss(Set<CustomerAddress> customerAddresss) {
//        if (customerAddresss == null || customerAddresss.isEmpty()) {
//            return Set.of();
//        }
//
//        return customerAddresss
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
    default Page<CustomerCompleteResponseDto> toResponsePage(Page<Customer> page) {
        return page.map(this::toResponse);
    }
}
