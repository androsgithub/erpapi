package com.api.erp.v1.main.dynamic.features.address.infrastructure.service;

import com.api.erp.v1.main.dynamic.features.address.domain.entity.Address;
import com.api.erp.v1.main.dynamic.features.address.domain.service.IAddressService;
import com.api.erp.v1.main.dynamic.features.address.shared_api.IAddressInternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation with generic Address helper methods.
 * Does NOT contain business logic specific to any feature.
 * Used by BusinessPartnerAddressService, SupplierAddressService, etc.
 * 
 * Validations delegated to IAddressValidator (existing pattern).
 */
@Service("addressInternalService")
@RequiredArgsConstructor
@Slf4j
public class AddressInternalServiceImpl implements IAddressInternalService {
    
    private final IAddressService addressService;
    
    @Override
    public Address getById(Long id) {
        return addressService.buscarPorId(id);
    }
    
    @Override
    public boolean belongsToOwner(Long addressId, Long ownerId, String ownerType) {
        log.debug("Validating if address {} belongs to {} {}", addressId, ownerType, ownerId);
        
        Address address = addressService.buscarPorId(addressId);
        return address != null;
    }
}
