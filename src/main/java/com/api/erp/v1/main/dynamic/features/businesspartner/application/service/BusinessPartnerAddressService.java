package com.api.erp.v1.main.dynamic.features.businesspartner.application.service;

import com.api.erp.v1.main.dynamic.features.address.domain.entity.Address;
import com.api.erp.v1.main.dynamic.features.address.domain.service.IAddressService;
import com.api.erp.v1.main.dynamic.features.address.domain.validator.IAddressValidator;
import com.api.erp.v1.main.dynamic.features.address.application.dto.request.CreateAddressRequest;
import com.api.erp.v1.main.dynamic.features.address.shared_api.IAddressInternalService;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.entity.BusinessPartner;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.repository.BusinessPartnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Contextualized service with BUSINESSPARTNER + ADDRESS logic.
 * Contains validations specific to businesspartner and their addresses.
 * Uses IAddressValidator for generic validations (existing pattern).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessPartnerAddressService {
    
    private final BusinessPartnerRepository businesspartnerRepository;
    private final IAddressService addressService;
    private final IAddressValidator addressValidator;
    private final IAddressInternalService addressInternalService;
    
    /**
     * Create new address for a businesspartner.
     */
    public Address create(Long businesspartnerId, CreateAddressRequest request) {
        log.info("Creating address for businesspartner: {}", businesspartnerId);
        
        // 1. Validate that businesspartner exists
        BusinessPartner businesspartner = validateBusinessPartnerExists(businesspartnerId);
        
        // 2. Validate generic address data
        addressValidator.validarCriacao(request);
        
        // 3. Validate businesspartner business rules (example: address limit)
        validateBusinessPartnerBusinessRules(businesspartner, request);
        
        // 4. Create address delegating to IAddressService
        Address address = addressService.criar(request);
        
        log.info("Address created. ID: {}, BusinessPartner: {}", address.getId(), businesspartnerId);
        return address;
    }
    
    /**
     * List all addresses of a businesspartner.
     */
    public List<Address> list(Long businesspartnerId) {
        log.debug("Listing addresses for businesspartner: {}", businesspartnerId);
        
        BusinessPartner businesspartner = validateBusinessPartnerExists(businesspartnerId);
        
        return businesspartner.getAddresss() != null ? businesspartner.getAddresss() : List.of();
    }
    
    /**
     * Retrieve a specific address of a businesspartner.
     */
    public Address get(Long businesspartnerId, Long addressId) {
        log.debug("Fetching address {} for businesspartner {}", addressId, businesspartnerId);
        
        validateBusinessPartnerExists(businesspartnerId);
        validateAddressBelongsToBusinessPartner(businesspartnerId, addressId);
        
        return addressService.buscarPorId(addressId);
    }
    
    /**
     * Update an address of a businesspartner.
     */
    public Address update(Long businesspartnerId, Long addressId, CreateAddressRequest request) {
        log.info("Updating address {} for businesspartner {}", addressId, businesspartnerId);
        
        validateBusinessPartnerExists(businesspartnerId);
        validateAddressBelongsToBusinessPartner(businesspartnerId, addressId);
        addressValidator.validarAtualizacao(addressId, request);
        
        Address updatedAddress = addressService.atualizar(addressId, request);
        
        log.info("Address updated. ID: {}", addressId);
        return updatedAddress;
    }
    
    /**
     * Delete an address of a businesspartner.
     */
    public void delete(Long businesspartnerId, Long addressId) {
        log.info("Deleting address {} for businesspartner {}", addressId, businesspartnerId);
        
        validateBusinessPartnerExists(businesspartnerId);
        validateAddressBelongsToBusinessPartner(businesspartnerId, addressId);
        
        addressService.deletar(addressId);
        
        log.info("Address deleted. ID: {}", addressId);
    }
    
    // ===== VALIDATIONS =====
    
    private BusinessPartner validateBusinessPartnerExists(Long businesspartnerId) {
        return businesspartnerRepository.findById(businesspartnerId)
                .orElseThrow(() -> {
                    log.warn("BusinessPartner not found: {}", businesspartnerId);
                    return new RuntimeException("BusinessPartner not found: " + businesspartnerId);
                });
    }
    
    private void validateAddressBelongsToBusinessPartner(Long businesspartnerId, Long addressId) {
        BusinessPartner businesspartner = validateBusinessPartnerExists(businesspartnerId);
        
        boolean pertence = businesspartner.getAddresss().stream()
                .anyMatch(addr -> addr.getId().equals(addressId));
        
        if (!pertence) {
            log.warn("Address {} does not belong to businesspartner {}", addressId, businesspartnerId);
            throw new RuntimeException(
                    "Address " + addressId + " does not belong to businesspartner " + businesspartnerId);
        }
    }
    
    /**
     * Validations of businesspartner-specific business rules.
     * Example: address limit per businesspartner, zone validations, etc.
     */
    private void validateBusinessPartnerBusinessRules(BusinessPartner businesspartner, CreateAddressRequest request) {
        // TODO: Add businesspartner-specific validations
        // Example:
        // if (businesspartner.getAddresss().size() >= MAX_ADDRESSES_PER_BUSINESSPARTNER) {
        //     throw new RuntimeException("BusinessPartner reached address limit");
        // }
    }
}
