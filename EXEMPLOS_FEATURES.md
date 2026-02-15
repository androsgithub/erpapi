# 📖 Exemplos de Implementação por Feature

## Template para Nova Feature

Use este template quando criar uma nova feature (ex: Produto, Cliente, etc).

### Estrutura Padrão
```
features/minha-feature/
├── config/                          ← OPCIONAL - Apenas se houver customização
├── application/
│   ├── dto/
│   │   ├── request/
│   │   │   └── CriarMinhaFeatureRequest.java
│   │   └── response/
│   │       └── MinhaFeatureResponse.java
│   └── mapper/
│       └── MinhaFeatureMapper.java
├── domain/
│   ├── entity/
│   │   └── MinhaFeature.java
│   ├── service/
│   │   └── MinhaFeatureDomainService.java
│   ├── repository/
│   │   └── MinhaFeatureRepository.java       ← Interface (port)
│   ├── validator/
│   │   └── MinhaFeatureValidator.java
│   └── controller/                          ← Domain-driven controller (port)
│       └── CriarMinhaFeatureUsecase.java
├── infrastructure/
│   ├── service/
│   │   └── MinhaFeatureInfraService.java
│   ├── validator/
│   │   └── MinhaFeatureInfraValidator.java
│   ├── decorator/
│   │   └── MinhaFeatureCacheDecorator.java (se houver cache)
│   └── proxy/
│       └── MinhaFeatureRepositoryProxy.java   ← Implementação (adapter)
└── presentation/
    ├── controller/
    │   └── MinhaFeatureController.java        ← REST Controller
    └── (DTOs já estão em application)
```

---

## 📚 Exemplo 1: Feature Simples (ENDERECO - Compartilhada)

### Localização
```
shared/features/endereco/
```

### Classes Principais

#### 1️⃣ Entity (domain/entity/Endereco.java)
```java
package com.api.erp.v1.shared.features.endereco.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "tb_endereco")
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cep;
    private String cidade;
    private String estado;
    
    // sem getters/setters (Lombok @Data)
}
```

#### 2️⃣ Repository Interface (domain/repository/EnderecoRepository.java)
```java
package com.api.erp.v1.shared.features.endereco.domain.repository;

import com.api.erp.v1.shared.features.endereco.domain.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    // Métodos customizados se necessário
}
```

#### 3️⃣ Domain Service (domain/service/EnderecoDomainService.java)
```java
package com.api.erp.v1.shared.features.endereco.domain.service;

import com.api.erp.v1.shared.features.endereco.domain.entity.Endereco;
import com.api.erp.v1.shared.features.endereco.domain.repository.EnderecoRepository;
import com.api.erp.v1.shared.features.endereco.domain.validator.EnderecoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnderecoDomainService {
    private final EnderecoRepository repository;
    private final EnderecoValidator validator;
    
    public Endereco criarEndereco(Endereco endereco) {
        validator.validar(endereco);
        return repository.save(endereco);
    }
    
    public Endereco obterEndereco(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new EnderecoNaoEncontradoException(id));
    }
    
    public Endereco atualizarEndereco(Long id, Endereco endereco) {
        var existente = obterEndereco(id);
        // mapear campos
        return repository.save(existente);
    }
}
```

#### 4️⃣ Validator (domain/validator/EnderecoValidator.java)
```java
package com.api.erp.v1.shared.features.endereco.domain.validator;

import com.api.erp.v1.shared.features.endereco.domain.entity.Endereco;
import org.springframework.stereotype.Component;

@Component
public class EnderecoValidator {
    
    public void validar(Endereco endereco) {
        if (endereco.getLogradouro() == null || endereco.getLogradouro().isBlank()) {
            throw new IllegalArgumentException("Logradouro é obrigatório");
        }
        if (endereco.getCep() == null || !endereco.getCep().matches("\\d{5}-?\\d{3}")) {
            throw new IllegalArgumentException("CEP inválido");
        }
    }
}
```

#### 5️⃣ DTO e Mapper (application/)
```java
// application/dto/request/CriarEnderecoRequest.java
package com.api.erp.v1.shared.features.endereco.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriarEnderecoRequest {
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cep;
    private String cidade;
    private String estado;
}

// application/dto/response/EnderecoResponse.java
package com.api.erp.v1.shared.features.endereco.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoResponse {
    private Long id;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cep;
    private String cidade;
    private String estado;
}

// application/mapper/EnderecoMapper.java
package com.api.erp.v1.shared.features.endereco.application.mapper;

import com.api.erp.v1.shared.features.endereco.application.dto.request.CriarEnderecoRequest;
import com.api.erp.v1.shared.features.endereco.application.dto.response.EnderecoResponse;
import com.api.erp.v1.shared.features.endereco.domain.entity.Endereco;
import org.springframework.stereotype.Component;

@Component
public class EnderecoMapper {
    
    public Endereco toDomain(CriarEnderecoRequest request) {
        return Endereco.builder()
            .logradouro(request.getLogradouro())
            .numero(request.getNumero())
            .complemento(request.getComplemento())
            .bairro(request.getBairro())
            .cep(request.getCep())
            .cidade(request.getCidade())
            .estado(request.getEstado())
            .build();
    }
    
    public EnderecoResponse toResponse(Endereco endereco) {
        return EnderecoResponse.builder()
            .id(endereco.getId())
            .logradouro(endereco.getLogradouro())
            .numero(endereco.getNumero())
            .complemento(endereco.getComplemento())
            .bairro(endereco.getBairro())
            .cep(endereco.getCep())
            .cidade(endereco.getCidade())
            .estado(endereco.getEstado())
            .build();
    }
}
```

#### 6️⃣ REST Controller (presentation/controller/EnderecoController.java)
```java
package com.api.erp.v1.shared.features.endereco.presentation.controller;

import com.api.erp.v1.shared.features.endereco.application.dto.request.CriarEnderecoRequest;
import com.api.erp.v1.shared.features.endereco.application.dto.response.EnderecoResponse;
import com.api.erp.v1.shared.features.endereco.application.mapper.EnderecoMapper;
import com.api.erp.v1.shared.features.endereco.domain.service.EnderecoDomainService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/enderecos")
@Tag(name = "Endereço", description = "Gerenciamento de Endereços")
@RequiredArgsConstructor
public class EnderecoController {
    private final EnderecoDomainService domainService;
    private final EnderecoMapper mapper;
    
    @PostMapping
    public ResponseEntity<EnderecoResponse> criar(@RequestBody CriarEnderecoRequest request) {
        var endereco = mapper.toDomain(request);
        var salvo = domainService.criarEndereco(endereco);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(salvo));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EnderecoResponse> obter(@PathVariable Long id) {
        var endereco = domainService.obterEndereco(id);
        return ResponseEntity.ok(mapper.toResponse(endereco));
    }
}
```

---

## 📚 Exemplo 2: Feature Complexa com Configuração (CLIENTE)

### Pode ter Customizações por Tenant

#### Localização
```
features/cliente/
├── config/
│   └── ClienteConfig.java             ← Beans específicos de Cliente
├── tenants/
│   └── hece/
│       └── validator/
│           └── ClienteHECEValidator.java  ← Regras específicas do tenant HECE
├── application/
├── domain/
├── infrastructure/
└── presentation/
```

#### ClienteConfig.java (config/)
```java
package com.api.erp.v1.features.cliente.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.api.erp.v1.features.cliente.infrastructure.service.ClienteCacheService;

@Configuration
public class ClienteConfig {
    
    @Bean
    public ClienteCacheService clienteCacheService() {
        return new ClienteCacheService();
    }
}
```

#### ClienteHECEValidator.java (tenants/hece/)

```java
package com.api.erp.v1.features.cliente.tenants.hece.validator;

import com.api.erp.v1.main.features.cliente.domain.entity.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteHECEValidator {

    public void validarClienteHECE(Cliente cliente) {
        // Regras específicas do tenant HECE
        if (!cliente.getCpfCnpj().matches("[0-9]{11}|[0-9]{14}")) {
            throw new IllegalArgumentException("CPF/CNPJ inválido para HECE");
        }
    }
}
```

---

## 📚 Exemplo 3: Service com Injeção de Múltiplas Features

### Cliente precisa de Endereco

```java
package com.api.erp.v1.features.cliente.domain.service;

import com.api.erp.v1.main.features.cliente.domain.entity.Cliente;
import com.api.erp.v1.main.features.cliente.domain.repository.ClienteRepository;
import com.api.erp.v1.shared.features.endereco.domain.service.EnderecoDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteDomainService {
    private final ClienteRepository repository;
    private final EnderecoDomainService enderecoDomainService;  // ✅ Cross-feature dependency

    public Cliente criarClienteComEndereco(Cliente cliente, EnderecoRequest enderecoRequest) {
        // Criar endereço na shared feature
        var endereco = enderecoDomainService.criarEndereco(
                mapper.toDomainEndereco(enderecoRequest)
        );

        // Vincular ao cliente
        cliente.setEnderecoId(endereco.getId());

        return repository.save(cliente);
    }
}
```

---

## 🔧 Configuração do FeaturesRepositoriesConfig

Quando adicionar uma new feature, atualize:

```java
@Configuration
@EnableJpaRepositories(
    basePackages = {
        // ... features existentes
        "com.api.erp.v1.features.minha-nova-feature.domain.repository"  // ADICIONAR
    },
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager"
)
public class FeaturesRepositoriesConfig {
}
```

---

## 📊 Fluxo de Requisição (Request → Response)

```
1. HTTP Request chega em ClienteController.java
   ↓
2. Controller recebe CriarClienteRequest (DTO)
   ↓
3. Valida @Valid (Bean Validation)
   ↓
4. Mapper converte para Domain Entity
   ↓
5. Domain Service executa lógica de negócio
   ↓
6. Domain Service chama Validator (domain/validator)
   ↓
7. If OK → Repository.save() (detecta tenant via TenantContextHolder)
   ↓
8. Repository salva em TenantDynamicDataSource (banco correto)
   ↓
9. Domain Service retorna Entity
   ↓
10. Mapper converte para Response DTO
   ↓
11. Controller retorna ResponseEntity com HTTP 201/200
```

---

## 🎯 Regras Importantes

### ✅ DO's
- ✅ Coloque **lógica de negócio** em `domain/service`
- ✅ Use **interfaces** em `domain/repository` (ports)
- ✅ Implemente em `infrastructure/` (adapters)
- ✅ DTOs apenas em `application/dto`
- ✅ Controllers apenas em `presentation/controller`
- ✅ **Valide** em `domain/validator` (primeira camada)

### ❌ DON'Ts
- ❌ Não coloque Entity em DTOs (risco de LazyLoad)
- ❌ Não chame repository diretamente do controller (pule por domain service)
- ❌ Não misture features (cliente não acessa direto database de product)
- ❌ Não desacople muito (cada feature é independente MAS pode usar shared)
- ❌ Não coloque lógica em entities (anemic models)

---

## 📋 Checklist para Nova Feature

- ☐ Feature criada em `features/nome-feature/`
- ☐ Estrutura completa (config, application, domain, infrastructure, presentation)
- ☐ Repository interface em `domain/repository/`
- ☐ Validator em `domain/validator/`
- ☐ DomainService em `domain/service/`
- ☐ Mapper em `application/mapper/`
- ☐ DTO request e response em `application/dto/`
- ☐ Controller em `presentation/controller/`
- ☐ FeaturesRepositoriesConfig atualizado com basePackage
- ☐ Testes unitários para domain services
- ☐ Testes de integração para repository
- ☐ Documentação em Swagger (@Tag, @Operation)

