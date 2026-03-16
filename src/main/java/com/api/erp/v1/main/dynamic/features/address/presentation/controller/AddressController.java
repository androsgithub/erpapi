//package com.api.erp.v1.main.features.address.presentation.controller;
//
//import com.api.erp.v1.main.features.address.application.dto.request.CreateAddressRequest;
//import com.api.erp.v1.main.features.address.application.dto.response.AddressResponse;
//import com.api.erp.v1.main.features.address.application.mapper.AddressMapper;
//import com.api.erp.v1.main.features.address.domain.controller.IAddressController;
//import com.api.erp.v1.docs.openapi.features.address.AddressOpenApiDocumentation;
//import com.api.erp.v1.main.features.address.domain.entity.AddressPermissions;
//import com.api.erp.v1.main.features.address.domain.service.IAddressService;
//import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
//import com.api.erp.v1.main.shared.infrastructure.documentation.RequiresXTenantId;
//import io.swagger.v3.oas.annotations.Parameter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/address")
//@RequiredArgsConstructor
//public class AddressController implements IAddressController, AddressOpenApiDocumentation {
//
//    private final IAddressService addressService;
//    private final AddressMapper addressMapper;
//
//    @PostMapping
//    @RequiresXTenantId
//    @RequiresPermission(AddressPermissions.CREATE)
//    public ResponseEntity<AddressResponse> criar(@RequestBody CreateAddressRequest request) {
//        var address = addressService.criar(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(addressMapper.toResponse(address));
//    }
//
//    @GetMapping("/{id}")
//    @RequiresXTenantId
//    @RequiresPermission(AddressPermissions.VIEW)
//    public ResponseEntity<AddressResponse> buscar(
//            @PathVariable @Parameter(description = "ID do endereço") Long id) {
//        var address = addressService.buscarPorId(id);
//        return ResponseEntity.ok(addressMapper.toResponse(address));
//    }
//
//    @GetMapping
//    @RequiresXTenantId
//    @RequiresPermission(AddressPermissions.VIEW)
//    public ResponseEntity<List<AddressResponse>> listar() {
//        var addresss = addressService.buscarTodos();
//        return ResponseEntity.ok(addressMapper.toResponse(addresss));
//    }
//
//    @PutMapping("/{id}")
//    @RequiresXTenantId
//    @RequiresPermission(AddressPermissions.UPDATE)
//    public ResponseEntity<AddressResponse> atualizar(
//            @PathVariable @Parameter(description = "ID do endereço") Long id,
//            @RequestBody CreateAddressRequest request) {
//        var address = addressService.atualizar(id, request);
//        return ResponseEntity.ok(addressMapper.toResponse(address));
//    }
//
//    @DeleteMapping("/{id}")
//    @RequiresXTenantId
//    @RequiresPermission(AddressPermissions.DELETE)
//    public ResponseEntity<Void> deletar(
//            @PathVariable @Parameter(description = "ID do endereço") Long id) {
//        addressService.deletar(id);
//        return ResponseEntity.noContent().build();
//    }
//}
