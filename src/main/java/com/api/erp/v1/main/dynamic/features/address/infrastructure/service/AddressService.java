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
                .rua(request.rua())
                .numero(request.numero())
                .complemento(request.complemento())
                .bairro(request.bairro())
                .cidade(request.cidade())
                .estado(request.estado())
                .cep(new CEP(request.cep()))
                .tipo(request.tipo())
                .principal(request.principal())
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

        address.setRua(request.rua());
        address.setNumero(request.numero());
        address.setComplemento(request.complemento());
        address.setBairro(request.bairro());
        address.setCidade(request.cidade());
        address.setEstado(request.estado());
        address.setCep(new CEP(request.cep()));
        address.setTipo(request.tipo());
        address.setPrincipal(request.principal());

        return addressRepository.save(address);
    }

    public void deletar(Long id) {
        validator.validarId(id);
        addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Endereço não encontrado com id: " + id));
        addressRepository.deleteById(id);
    }
}
