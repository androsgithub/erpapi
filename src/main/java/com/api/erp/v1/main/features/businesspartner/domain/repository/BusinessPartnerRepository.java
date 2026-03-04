package com.api.erp.v1.main.features.businesspartner.domain.repository;

import com.api.erp.v1.main.features.businesspartner.domain.entity.BusinessPartner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessPartnerRepository extends JpaRepository<BusinessPartner, Long> {
}
