package com.api.erp.v1.main.features.user.infrastructure.service;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.features.permission.domain.entity.Permission;
import com.api.erp.v1.main.features.permission.domain.entity.Role;
import com.api.erp.v1.main.features.permission.domain.repository.PermissionRepository;
import com.api.erp.v1.main.features.permission.domain.repository.RoleRepository;
import com.api.erp.v1.main.features.user.application.dto.request.AdicionarPermissionsRequest;
import com.api.erp.v1.main.features.user.application.dto.request.AdicionarRolesRequest;
import com.api.erp.v1.main.features.user.application.dto.request.CreateUserRequest;
import com.api.erp.v1.main.features.user.application.dto.request.UpdateUserRequest;
import com.api.erp.v1.main.features.user.domain.entity.StatusUser;
import com.api.erp.v1.main.features.user.domain.entity.User;
import com.api.erp.v1.main.features.user.domain.repository.UserRepository;
import com.api.erp.v1.main.features.user.domain.service.IPasswordEncoder;
import com.api.erp.v1.main.features.user.domain.service.IUserService;
import com.api.erp.v1.main.features.user.domain.validator.IUserValidator;
import com.api.erp.v1.main.shared.common.error.ErrorHandler;
import com.api.erp.v1.main.shared.common.error.TenantErrorMessage;
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

@Service
@Slf4j
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final IPasswordEncoder passwordEncoder;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final IUserValidator validator;

    @Autowired
    public UserService(UserRepository userRepository,
                       IPasswordEncoder passwordEncoder,
                       PermissionRepository permissionRepository,
                       RoleRepository roleRepository,
                       @Qualifier("userValidatorProxy") IUserValidator validator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.validator = validator;
    }


    @Override
    public User criar(CreateUserRequest request) {
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

        // Creates user
        User user = User.builder().tenantId(request.tenantId()).nomeCompleto(request.nomeCompleto()).email(new Email(request.email())).cpf(new CPF(request.cpf())).senhaHash(passwordEncoder.encode(request.senha())).status(StatusUser.ATIVO).build();

        return userRepository.save(user);
    }

    @Override
    public User atualizar(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id).orElseThrow(()-> new ErrorHandler(UserErrorMessage.USER_NOT_FOUND));

        if (request.getNomeCompleto() != null) {
            user = User.builder().id(user.getId()).nomeCompleto(request.getNomeCompleto()).email(user.getEmail()).cpf(user.getCpf()).senhaHash(user.getSenha_hash()).status(user.getStatus()).build();
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User buscarPorId(Long id) {
        return userRepository.findById(id).orElseThrow(()-> new ErrorHandler(UserErrorMessage.USER_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> listarTodos() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> listarPendentes() {
        return userRepository.findByStatus(StatusUser.PENDENTE_APROVACAO);
    }

    @Override
    @Transactional
    public void inativar(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new ErrorHandler(UserErrorMessage.USER_NOT_FOUND));

        user.inativar();
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User aprovar(Long userId, Long gestorId) {
        throw new BusinessException("Tenant does not require manager approval");
    }

    @Override
    @Transactional
    public User rejeitar(Long userId, Long gestorId, String motivo) {
        throw new BusinessException("Tenant does not require manager approval");
    }

    @Override
    @Transactional
    public void adicionarPermissions(Long userId, AdicionarPermissionsRequest request) {


        User user = userRepository.findById(userId).orElseThrow(()-> new ErrorHandler(UserErrorMessage.USER_NOT_FOUND));


        Long tenantId = TenantContext.getTenantId();
        Long tenantGroupId = TenantContext.getGroupId();

        for (Long permissionId : request.permissionIds()) {

            Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new NotFoundException("Permission not found"));

            boolean exists = userRepository.existsByIdAndPermissions_IdAndTenantIdAndTenantGroupId(
                    userId,
                    permissionId,
                    tenantId,
                    tenantGroupId
            );

            if (exists) continue;

            user.getPermissions().add(permission);
            userRepository.save(user);
        }
    }


    @Override
    @Transactional
    public void removerPermission(Long userId, Long permissionId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new ErrorHandler(UserErrorMessage.USER_NOT_FOUND));


        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new NotFoundException("Permissão não encontrada"));

        user.getPermissions().remove(permission);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> listarPermissions(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new ErrorHandler(UserErrorMessage.USER_NOT_FOUND));

        return user.getPermissions().stream().toList();
    }

    @Override
    @Transactional
    public void adicionarRoles(Long userId, AdicionarRolesRequest request) {

        User user = userRepository.findById(userId).orElseThrow(()-> new ErrorHandler(UserErrorMessage.USER_NOT_FOUND));


        Long tenantId = TenantContext.getTenantId();
        Long tenantGroupId = TenantContext.getGroupId();

        for (Long roleId : request.roleIds()) {

            Role role = roleRepository.findById(roleId).orElseThrow(() -> new NotFoundException("Role not found"));

            boolean exists = userRepository.existsByIdAndRoles_IdAndTenantIdAndTenantGroupId(userId, roleId, tenantId, tenantGroupId);

            if (exists) continue;

            user.getRoles().add(role);
            userRepository.save(user);
        }
    }


    @Override
    @Transactional
    public void removerRole(Long userId, Long roleId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;
        user.getRoles().removeIf(rl -> rl.getId().equals(roleId));
        userRepository.save(user);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Role> listarRoles(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return List.of();
        return user.getRoles().stream().toList();
    }

}
