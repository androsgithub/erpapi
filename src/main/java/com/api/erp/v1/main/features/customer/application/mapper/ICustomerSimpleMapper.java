package com.api.erp.v1.main.features.customer.application.mapper;

import com.api.erp.v1.main.features.customer.application.dto.response.CustomerSimpleResponseDto;
import com.api.erp.v1.main.features.customer.domain.entity.Customer;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ICustomerSimpleMapper {

    CustomerSimpleResponseDto toResponse(Customer customer);

    List<CustomerSimpleResponseDto> toResponseList(List<Customer> customers);

    default Page<CustomerSimpleResponseDto> toResponsePage(Page<Customer> page) {
        return page.map(this::toResponse);
    }
}
