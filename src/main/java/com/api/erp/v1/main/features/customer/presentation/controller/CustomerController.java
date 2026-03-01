package com.api.erp.v1.main.features.customer.presentation.controller;

import com.api.erp.v1.main.features.customer.application.dto.request.CreateCustomerDto;
import com.api.erp.v1.main.features.customer.application.dto.response.CustomerCompleteResponseDto;
import com.api.erp.v1.main.features.customer.application.dto.response.CustomerSimpleResponseDto;
import com.api.erp.v1.main.features.customer.application.mapper.ICustomerCompleteMapper;
import com.api.erp.v1.main.features.customer.application.mapper.ICustomerSimpleMapper;
import com.api.erp.v1.main.features.customer.domain.controller.ICustomerController;
import com.api.erp.v1.docs.openapi.features.customer.CustomerOpenApiDocumentation;
import com.api.erp.v1.main.features.customer.domain.entity.Customer;
import com.api.erp.v1.main.features.customer.domain.entity.CustomerPermissions;
import com.api.erp.v1.main.features.customer.domain.service.ICustomerService;
import com.api.erp.v1.main.shared.infrastructure.documentation.RequiresXTenantId;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/src/test/java/com/api/v1/customers")
public class CustomerController implements ICustomerController, CustomerOpenApiDocumentation {

    @Qualifier("customerServiceProxy")
    private final ICustomerService customerService;
    private final ICustomerCompleteMapper customerCompleteMapper;
    private final ICustomerSimpleMapper customerSimpleMapper;

    @Autowired
    public CustomerController(ICustomerService customerService, ICustomerCompleteMapper customerCompleteMapper, ICustomerSimpleMapper customerSimpleMapper) {
        this.customerService = customerService;
        this.customerCompleteMapper = customerCompleteMapper;
        this.customerSimpleMapper = customerSimpleMapper;
    }


    @GetMapping
    @RequiresXTenantId
    @RequiresPermission(CustomerPermissions.VISUALIZAR)
    public Page<CustomerSimpleResponseDto> listar(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size,
                                                 @RequestParam(defaultValue = "nome") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return customerSimpleMapper.toResponsePage(customerService.pegarTodos(pageable));
    }

    @GetMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(CustomerPermissions.VISUALIZAR)
    public CustomerCompleteResponseDto pegar(@PathVariable Long id) {
        Customer customer = customerService.pegarPorId(id);
        return customerCompleteMapper.toResponse(customer);
    }

    @DeleteMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(CustomerPermissions.DELETAR)
    public void deletar(@PathVariable Long id) {
        customerService.deletar(id);
    }

    @PostMapping
    @RequiresXTenantId
    @RequiresPermission(CustomerPermissions.CRIAR)
    public CustomerCompleteResponseDto criar(@RequestBody CreateCustomerDto dto) {
        Customer customer = customerService.criar(dto);
        return customerCompleteMapper.toResponse(customer);
    }

    @PutMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(CustomerPermissions.ATUALIZAR)
    public CustomerCompleteResponseDto atualizar(@PathVariable Long id, @RequestBody CreateCustomerDto dto) {
        Customer customer = customerService.atualizar(id, dto);
        return customerCompleteMapper.toResponse(customer);
    }

}
