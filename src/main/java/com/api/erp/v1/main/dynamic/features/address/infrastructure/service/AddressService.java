package com.api.erp.v1.main.dynamic.features.address.infrastructure.service;

import com.api.erp.v1.main.dynamic.features.address.application.dto.request.CreateAddressRequest;
import com.api.erp.v1.main.dynamic.features.address.domain.entity.Address;
import com.api.erp.v1.main.dynamic.features.address.domain.repository.AddressRepository;
import com.api.erp.v1.main.dynamic.features.address.domain.service.IAddressService;
import com.api.erp.v1.main.dynamic.features.address.domain.validator.IAddressValidator;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import com.api.erp.v1.main.shared.domain.valueobject.CEP;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService implements IAddressService {

    private final AddressRepository addressRepository;
    private final IAddressValidator validator;

    public Address criar(CreateAddressRequest request) {
        validator.validarCriacao(request);

        Address address = Address.builder()
                .street(request.rua())
                .number(request.numero())
                .complement(request.complemento())
                .district(request.bairro())
                .city(request.cidade())
                .state(request.estado())
                .postalCode(new CEP(request.cep()))
                .type(request.tipo())
                .primary(request.principal())
                .build();

        return addressRepository.save(address);
    }

    public Address buscarPorId(Long id) {
        validator.validarId(id);
        return addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Endereço não encontrado com id: " + id));
    }

    public List<Address> buscarTodos() {
        return addressRepository.findAll();
    }

    public Address atualizar(Long id, CreateAddressRequest request) {
        validator.validarAtualizacao(id, request);

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Endereço não encontrado com id: " + id));

        address.setStreet(request.rua());
        address.setNumber(request.numero());
        address.setComplement(request.complemento());
        address.setDistrict(request.bairro());
        address.setCity(request.cidade());
        address.setState(request.estado());
        address.setPostalCode(new CEP(request.cep()));
        address.setType(request.tipo());
        address.setPrimary(request.principal());

        return addressRepository.save(address);
    }

    public void deletar(Long id) {
        validator.validarId(id);
        addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Endereço não encontrado com id: " + id));
        addressRepository.deleteById(id);
    }
}
