package com.api.erp.v1.main.master.user.infrastructure.service;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.master.permission.domain.entity.Permission;
import com.api.erp.v1.main.master.permission.domain.entity.Role;
import com.api.erp.v1.main.master.permission.domain.repository.PermissionRepository;
import com.api.erp.v1.main.master.permission.domain.repository.RoleRepository;
import com.api.erp.v1.main.master.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.master.tenant.domain.service.ITenantService;
import com.api.erp.v1.main.master.user.application.dto.request.AddPermissionsRequest;
import com.api.erp.v1.main.master.user.application.dto.request.AdicionarRolesRequest;
import com.api.erp.v1.main.master.user.application.dto.request.CreateUserRequest;
import com.api.erp.v1.main.master.user.application.dto.request.UpdateUserRequest;
import com.api.erp.v1.main.master.user.domain.entity.StatusUser;
import com.api.erp.v1.main.master.user.domain.entity.User;
import com.api.erp.v1.main.master.user.domain.repository.UserRepository;
import com.api.erp.v1.main.master.user.domain.service.IPasswordEncoder;
import com.api.erp.v1.main.master.user.domain.service.IUserService;
import com.api.erp.v1.main.master.user.domain.validator.IUserValidator;
import com.api.erp.v1.main.shared.common.error.ErrorHandler;
import com.api.erp.v1.main.shared.common.error.UserErrorMessage;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import com.api.erp.v1.main.shared.domain.valueobject.CPF;
import com.api.erp.v1.main.shared.domain.valueobject.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final IPasswordEncoder passwordEncoder;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final IUserValidator validator;
    private final ITenantService tenantService;

    @Autowired
    public UserService(UserRepository userRepository, IPasswordEncoder passwordEncoder, PermissionRepository permissionRepository, RoleRepository roleRepository, @Qualifier("userValidatorProxy") IUserValidator validator, ITenantService tenantService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.validator = validator;
        this.tenantService = tenantService;
    }


    @Override
    public User create(CreateUserRequest request) {
        // Validações
        validator.validar(request);

        // Verifica duplicidade
        if (userRepository.existsByEmail(new Email(request.email()))) {
            log.warn("Email already registered: {}", request.email());
            throw new BusinessException("Email already registered");
        }

        if (userRepository.existsByCpf(new CPF(request.cpf()))) {
            log.warn("CPF already registered: {}", request.cpf());
            throw new BusinessException("CPF already registered");
        }
        Tenant tenant = tenantService.getDadosTenant(request.tenantId());

        // Creates user
        User user = User.builder()
                .tenants(Set.of(tenant))
                .name(request.nomeCompleto())
                .email(new Email(request.email()))
                .cpf(new CPF(request.cpf()))
                .passwordHash(passwordEncoder.encode(request.senha()))
                .status(StatusUser.ENABLED).build();

        return userRepository.save(user);
    }

    @Override
    public User update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new ErrorHandler(UserErrorMessage.USER_NOT_FOUND));

        if (request.name().isBlank()) {
            user = User.builder()
                    .id(user.getId())
                    .name(request.name())
                    .email(user.getEmail())
                    .cpf(user.getCpf())
                    .passwordHash(user.getPasswordHash())
                    .status(user.getStatus())
                    .build();
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ErrorHandler(UserErrorMessage.USER_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> listAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> listPending() {
        return userRepository.findByStatus(StatusUser.APPROVE_PENDING);
    }

    @Override
    @Transactional
    public void disable(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ErrorHandler(UserErrorMessage.USER_NOT_FOUND));

        user.setStatus(StatusUser.APPROVE_PENDING);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User approve(Long userId, Long gestorId) {
        throw new ErrorHandler(UserErrorMessage.APPROVE_MANAGEMENT_NOT_ALLOWED);
    }

    @Override
    @Transactional
    public User reject(Long userId, Long gestorId, String motivo) {
        throw new ErrorHandler(UserErrorMessage.APPROVE_MANAGEMENT_NOT_ALLOWED);
    }

    @Override
    @Transactional
    public void addPermissions(Long userId, AddPermissionsRequest request) {


        User user = userRepository.findById(userId).orElseThrow(() -> new ErrorHandler(UserErrorMessage.USER_NOT_FOUND));

        for (Long permissionId : request.permissionIds()) {

            Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new NotFoundException("Permission not found"));

            boolean exists = userRepository.existsByIdAndPermissions_Id(userId, permissionId);

            if (exists) continue;

            user.getPermissions().add(permission);
            userRepository.save(user);
        }
    }


    @Override
    @Transactional
    public void removePermission(Long userId, Long permissionId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ErrorHandler(UserErrorMessage.USER_NOT_FOUND));


        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new NotFoundException("Permissão não encontrada"));

        user.getPermissions().remove(permission);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> listPermissions(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ErrorHandler(UserErrorMessage.USER_NOT_FOUND));

        return user.getPermissions().stream().toList();
    }

    @Override
    @Transactional
    public void addRoles(Long userId, AdicionarRolesRequest request) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ErrorHandler(UserErrorMessage.USER_NOT_FOUND));

        for (Long roleId : request.roleIds()) {

            Role role = roleRepository.findById(roleId).orElseThrow(() -> new NotFoundException("Role not found"));

            boolean exists = userRepository.existsByIdAndRoles_Id(userId, roleId);

            if (exists) continue;

            user.getRoles().add(role);
            userRepository.save(user);
        }
    }


    @Override
    @Transactional
    public void removeRole(Long userId, Long roleId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;
        user.getRoles().removeIf(rl -> rl.getId().equals(roleId));
        userRepository.save(user);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Role> listRoles(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return List.of();
        return user.getRoles().stream().toList();
    }

}
