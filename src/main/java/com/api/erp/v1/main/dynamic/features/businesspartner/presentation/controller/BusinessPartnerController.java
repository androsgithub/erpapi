package com.api.erp.v1.main.dynamic.features.businesspartner.presentation.controller;

import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.request.CreateBusinessPartnerDto;
import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.response.BusinessPartnerCompleteResponseDto;
import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.response.BusinessPartnerSimpleResponseDto;
import com.api.erp.v1.main.dynamic.features.businesspartner.application.mapper.IBusinessPartnerCompleteMapper;
import com.api.erp.v1.main.dynamic.features.businesspartner.application.mapper.IBusinessPartnerSimpleMapper;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.controller.IBusinessPartnerController;
import com.api.erp.v1.docs.openapi.features.businesspartner.BusinessPartnerOpenApiDocumentation;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.entity.BusinessPartner;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.entity.BusinessPartnerPermissions;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.service.IBusinessPartnerService;
import com.api.erp.v1.main.shared.infrastructure.documentation.RequiresXTenantId;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import com.api.erp.v1.main.dynamic.features.address.application.dto.request.CreateAddressRequest;
import com.api.erp.v1.main.dynamic.features.address.application.dto.response.AddressResponse;
import com.api.erp.v1.main.dynamic.features.address.application.mapper.AddressMapper;
import com.api.erp.v1.main.dynamic.features.address.domain.entity.AddressPermissions;
import com.api.erp.v1.main.dynamic.features.businesspartner.application.service.BusinessPartnerAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/business-partners")
public class BusinessPartnerController implements IBusinessPartnerController, BusinessPartnerOpenApiDocumentation {

    @Qualifier("businessPartnerServiceProxy")
    private final IBusinessPartnerService businessPartnerService;
    private final IBusinessPartnerCompleteMapper businesspartnerCompleteMapper;
    private final IBusinessPartnerSimpleMapper businesspartnerSimpleMapper;
    private final BusinessPartnerAddressService businesspartnerAddressService;
    private final AddressMapper addressMapper;

    @Autowired
    public BusinessPartnerController(IBusinessPartnerService businessPartnerService, IBusinessPartnerCompleteMapper businesspartnerCompleteMapper, IBusinessPartnerSimpleMapper businesspartnerSimpleMapper, BusinessPartnerAddressService businesspartnerAddressService, AddressMapper addressMapper) {
        this.businessPartnerService = businessPartnerService;
        this.businesspartnerCompleteMapper = businesspartnerCompleteMapper;
        this.businesspartnerSimpleMapper = businesspartnerSimpleMapper;
        this.businesspartnerAddressService = businesspartnerAddressService;
        this.addressMapper = addressMapper;
    }


    @GetMapping
    @RequiresXTenantId
    @RequiresPermission(BusinessPartnerPermissions.VIEW)
    public Page<BusinessPartnerSimpleResponseDto> listar(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size,
                                                 @RequestParam(defaultValue = "nome") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return businesspartnerSimpleMapper.toResponsePage(businessPartnerService.pegarTodos(pageable));
    }

    @GetMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(BusinessPartnerPermissions.VIEW)
    public BusinessPartnerCompleteResponseDto pegar(@PathVariable Long id) {
        BusinessPartner businesspartner = businessPartnerService.pegarPorId(id);
        return businesspartnerCompleteMapper.toResponse(businesspartner);
    }

    @DeleteMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(BusinessPartnerPermissions.DELETE)
    public void deletar(@PathVariable Long id) {
        businessPartnerService.deletar(id);
    }

    @PostMapping
    @RequiresXTenantId
    @RequiresPermission(BusinessPartnerPermissions.CREATE)
    public BusinessPartnerCompleteResponseDto criar(@RequestBody CreateBusinessPartnerDto dto) {
        BusinessPartner businesspartner = businessPartnerService.criar(dto);
        return businesspartnerCompleteMapper.toResponse(businesspartner);
    }

    @PutMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(BusinessPartnerPermissions.UPDATE)
    public BusinessPartnerCompleteResponseDto atualizar(@PathVariable Long id, @RequestBody CreateBusinessPartnerDto dto) {
        BusinessPartner businesspartner = businessPartnerService.atualizar(id, dto);
        return businesspartnerCompleteMapper.toResponse(businesspartner);
    }

    // ===== NEW ADDRESS ENDPOINTS =====
    
    @PostMapping("/{businesspartnerId}/addresses")
    @RequiresXTenantId
    @RequiresPermission(AddressPermissions.CREATE)
    public ResponseEntity<AddressResponse> createAddress(
            @PathVariable Long businesspartnerId,
            @RequestBody CreateAddressRequest request) {
        var address = businesspartnerAddressService.create(businesspartnerId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressMapper.toResponse(address));
    }
    
    @GetMapping("/{businesspartnerId}/addresses")
    @RequiresXTenantId
    @RequiresPermission(AddressPermissions.VIEW)
    public ResponseEntity<java.util.List<AddressResponse>> listAddresses(
            @PathVariable Long businesspartnerId) {
        var addresses = businesspartnerAddressService.list(businesspartnerId);
        return ResponseEntity.ok(addressMapper.toResponseList(addresses));
    }
    
    @GetMapping("/{businesspartnerId}/addresses/{addressId}")
    @RequiresXTenantId
    @RequiresPermission(AddressPermissions.VIEW)
    public ResponseEntity<AddressResponse> getAddress(
            @PathVariable Long businesspartnerId,
            @PathVariable Long addressId) {
        var address = businesspartnerAddressService.get(businesspartnerId, addressId);
        return ResponseEntity.ok(addressMapper.toResponse(address));
    }
    
    @PutMapping("/{businesspartnerId}/addresses/{addressId}")
    @RequiresXTenantId
    @RequiresPermission(AddressPermissions.UPDATE)
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long businesspartnerId,
            @PathVariable Long addressId,
            @RequestBody CreateAddressRequest request) {
        var address = businesspartnerAddressService.update(businesspartnerId, addressId, request);
        return ResponseEntity.ok(addressMapper.toResponse(address));
    }
    
    @DeleteMapping("/{businesspartnerId}/addresses/{addressId}")
    @RequiresXTenantId
    @RequiresPermission(AddressPermissions.DELETE)
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long businesspartnerId,
            @PathVariable Long addressId) {
        businesspartnerAddressService.delete(businesspartnerId, addressId);
        return ResponseEntity.noContent().build();
    }
}
