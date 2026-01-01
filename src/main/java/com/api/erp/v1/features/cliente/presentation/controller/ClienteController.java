package com.api.erp.v1.features.cliente.presentation.controller;

import com.api.erp.v1.features.cliente.application.dto.request.CreateClienteDto;
import com.api.erp.v1.features.cliente.application.dto.response.ClienteCompleteResponseDto;
import com.api.erp.v1.features.cliente.application.dto.response.ClienteSimpleResponseDto;
import com.api.erp.v1.features.cliente.application.mapper.IClienteCompleteMapper;
import com.api.erp.v1.features.cliente.application.mapper.IClienteSimpleMapper;
import com.api.erp.v1.features.cliente.domain.controller.IClienteController;
import com.api.erp.v1.features.cliente.domain.entity.Cliente;
import com.api.erp.v1.features.cliente.domain.entity.ClientePermissions;
import com.api.erp.v1.features.cliente.domain.service.IClienteService;
import com.api.erp.v1.shared.infrastructure.security.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController implements IClienteController {

    @Autowired
    private IClienteService clienteService;
    @Autowired
    private IClienteCompleteMapper clienteCompleteMapper;
    @Autowired
    private IClienteSimpleMapper clienteSimpleMapper;


    @GetMapping
    @RequiresPermission(ClientePermissions.VISUALIZAR)
    public Page<ClienteSimpleResponseDto> listar(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size,
                                                 @RequestParam(defaultValue = "nome") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return clienteSimpleMapper.toResponsePage(clienteService.pegarTodos(pageable));
    }

    @GetMapping("/{id}")
    @RequiresPermission(ClientePermissions.VISUALIZAR)
    public ClienteCompleteResponseDto pegar(@PathVariable Long id) {
        Cliente cliente = clienteService.pegarPorId(id);
        return clienteCompleteMapper.toResponse(cliente);
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(ClientePermissions.DELETAR)
    public void deletar(Long id) {
        clienteService.deletar(id);
    }

    @PostMapping
    @RequiresPermission(ClientePermissions.CRIAR)
    public ClienteCompleteResponseDto criar(@RequestBody CreateClienteDto dto) {
        Cliente cliente = clienteService.criar(dto);
        return clienteCompleteMapper.toResponse(cliente);
    }

    @PutMapping("/{id}")
    @RequiresPermission(ClientePermissions.ATUALIZAR)
    public ClienteCompleteResponseDto atualizar(@PathVariable Long id, @RequestBody CreateClienteDto dto) {
        Cliente cliente = clienteService.atualizar(id, dto);
        return clienteCompleteMapper.toResponse(cliente);
    }

}
