package com.api.erp.v1.main.dynamic.features.address.domain.repository;

import com.api.erp.v1.main.dynamic.features.address.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
