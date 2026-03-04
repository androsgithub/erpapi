package com.api.erp.v1.main.features.address.shared_api;

import com.api.erp.v1.main.features.address.domain.entity.Address;

/**
 * Public interface with generic Address helper methods.
 * Does NOT contain business logic specific to any feature.
 * Must be injected into contextualized Services (BusinessPartnerAddressService, SupplierAddressService, etc).
 */
public interface IAddressInternalService {
    
    /**
     * Retrieve an address by ID.
     */
    Address getById(Long id);
    
    /**
     * Validate that an address belongs to a specific owner.
     */
    boolean belongsToOwner(Long addressId, Long ownerId, String ownerType);
}
