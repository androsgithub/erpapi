# 📄 Quick Reference - Estrutura Visual

## Árvore Completa de Packages (Proposta Final)

```
src/main/java/com/api/erp/
│
├── ErpApplication.java
│   └── @SpringBootApplication
│       @EnableAspectJAutoProxy
│       @EnableAsync
│
├── v1/
│   │
│   ├── 📁 config/                       # ⭐ CONFIGURAÇÕES GLOBAIS (sem lógica)
│   │   │
│   │   ├── aspects/
│   │   │   ├── LoggingAspect.java
│   │   │   ├── TransactionAspect.java
│   │   │   ├── PerformanceAspect.java
│   │   │   └── SecurityAspect.java
│   │   │
│   │   ├── database/
│   │   │   ├── DataSourceConfig.java
│   │   │   ├── JpaConfig.java
│   │   │   └── HibernateConfig.java
│   │   │
│   │   ├── security/
│   │   │   ├── SecurityConfig.java
│   │   │   ├── JwtTokenProvider.java
│   │   │   ├── AuthenticationFilter.java
│   │   │   └── UserDetailsServiceImpl.java
│   │   │
│   │   ├── swagger/
│   │   │   ├── SwaggerConfig.java
│   │   │   └── OpenAPIConfig.java
│   │   │
│   │   ├── web/
│   │   │   ├── WebConfig.java
│   │   │   ├── CorsConfig.java
│   │   │   ├── ExceptionHandler.java
│   │   │   └── GlobalExceptionHandler.java
│   │   │
│   │   └── aop/
│   │       ├── LoggingAspectConfig.java
│   │       └── TransactionAspectConfig.java
│   │
│   ├── 📁 features/                     # ⭐ FEATURES ESPECÍFICAS (modular)
│   │   │
│   │   ├── produto/
│   │   │   ├── config/                  # ⚪ OPCIONAL - config específica
│   │   │   │   ├── ProdutoConfig.java
│   │   │   │   └── ProdutoCacheConfig.java
│   │   │   │
│   │   │   ├── application/
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   │   ├── CriarProdutoRequest.java
│   │   │   │   │   │   ├── AtualizarProdutoRequest.java
│   │   │   │   │   │   └── FiltrarProdutoRequest.java
│   │   │   │   │   └── response/
│   │   │   │   │       ├── ProdutoResponse.java
│   │   │   │   │       └── ProdutoDetailResponse.java
│   │   │   │   │
│   │   │   │   └── mapper/
│   │   │   │       └── ProdutoMapper.java
│   │   │   │
│   │   │   ├── domain/                  # 🎯 LÓGICA DE NEGÓCIO PURA
│   │   │   │   ├── entity/
│   │   │   │   │   └── Produto.java
│   │   │   │   │
│   │   │   │   ├── service/
│   │   │   │   │   ├── ProdutoDomainService.java
│   │   │   │   │   └── ProdutoCalculationService.java
│   │   │   │   │
│   │   │   │   ├── repository/          # 🔴 INTERFACE (port)
│   │   │   │   │   └── ProdutoRepository.java
│   │   │   │   │
│   │   │   │   ├── validator/
│   │   │   │   │   ├── ProdutoValidator.java
│   │   │   │   │   └── EstoqueValidator.java
│   │   │   │   │
│   │   │   │   └── specification/
│   │   │   │       └── ProdutoSpecification.java
│   │   │   │
│   │   │   ├── infrastructure/         # 🔧 IMPLEMENTAÇÕES TÉCNICAS (adapter)
│   │   │   │   ├── service/
│   │   │   │   │   ├── ProdutoServiceImpl.java
│   │   │   │   │   └── ProdutoEstoqueService.java
│   │   │   │   │
│   │   │   │   ├── validator/
│   │   │   │   │   └── ProdutoInfraValidator.java
│   │   │   │   │
│   │   │   │   ├── decorator/
│   │   │   │   │   ├── ProdutoCacheDecorator.java
│   │   │   │   │   └── ProdutoLoggingDecorator.java
│   │   │   │   │
│   │   │   │   ├── proxy/
│   │   │   │   │   └── ProdutoRepositoryJpaImpl.java  # 🔴 ADAPTER (JPA)
│   │   │   │   │
│   │   │   │   └── event/
│   │   │   │       ├── ProdutoCriadoEvent.java
│   │   │   │       └── EstoqueAlteradoEvent.java
│   │   │   │
│   │   │   └── presentation/           # 🌐 API REST
│   │   │       ├── controller/
│   │   │       │   └── ProdutoController.java
│   │   │       │
│   │   │       └── dto/                # (Reutiliza de application/dto)
│   │   │
│   │   ├── cliente/
│   │   │   ├── config/
│   │   │   │   └── ClienteConfig.java
│   │   │   │
│   │   │   ├── application/
│   │   │   ├── domain/
│   │   │   ├── infrastructure/
│   │   │   ├── presentation/
│   │   │   │
│   │   │   └── 📁 tenants/             # ⚡ CUSTOMIZAÇÕES POR TENANT
│   │   │       └── hece/
│   │   │           ├── validator/
│   │   │           │   └── ClienteHECEValidator.java
│   │   │           │
│   │   │           └── rules/
│   │   │               └── ClienteHECEBusinessRules.java
│   │   │
│   │   ├── fornecedor/
│   │   ├── usuario/
│   │   ├── permissao/
│   │   ├── customfield/
│   │   └── personalizado/
│   │
│   ├── 📁 tenant/                      # ⭐ MULTI-TENANCY
│   │   │
│   │   ├── application/
│   │   │   ├── dto/
│   │   │   │   └── TenantDTO.java
│   │   │   │
│   │   │   └── service/
│   │   │       └── TenantApplicationService.java
│   │   │
│   │   ├── domain/
│   │   │   ├── entity/
│   │   │   │   └── Tenant.java          # ⭐ ENTIDADE PRINCIPAL
│   │   │   │
│   │   │   ├── service/
│   │   │   │   └── TenantDomainService.java
│   │   │   │
│   │   │   └── repository/              # 🔴 REPOSITÓRIO DO MASTER
│   │   │       └── TenantRepository.java
│   │   │
│   │   ├── infrastructure/
│   │   │   │
│   │   │   ├── 📁 config/               # 🔴 CONFIGURAÇÕES CRÍTICAS
│   │   │   │   ├── TenantsMasterDatabaseConfig.java
│   │   │   │   ├── TenantsMasterRepositoriesConfig.java   # @EnableJpaRepositories
│   │   │   │   ├── TenantsConfiguration.java
│   │   │   │   ├── FeaturesRepositoriesConfig.java        # @EnableJpaRepositories
│   │   │   │   └── TenantsMultitenantDataSourceConfig.java
│   │   │   │
│   │   │   ├── 📁 datasource/          # 🔧 ROTEAMENTO DE DATASOURCES
│   │   │   │   │
│   │   │   │   ├── manual/
│   │   │   │   │   ├── TenantContextHolder.java
│   │   │   │   │   ├── TenantResolutionStrategy.java
│   │   │   │   │   ├── MultiTenantRoutingDataSource.java
│   │   │   │   │   └── DataSourceFactory.java
│   │   │   │   │
│   │   │   │   └── dynamic/
│   │   │   │       ├── DynamicDataSourceBuilder.java
│   │   │   │       └── DataSourceCache.java
│   │   │   │
│   │   │   ├── 📁 interceptor/         # 🌐 INTERCEPTORES
│   │   │   │   ├── TenantInterceptor.java
│   │   │   │   ├── TenantResolverInterceptor.java
│   │   │   │   └── TenantFilterConfig.java
│   │   │   │
│   │   │   ├── listener/
│   │   │   │   └── TenantEventListener.java
│   │   │   │
│   │   │   └── service/
│   │   │       └── TenantRepositoryJpaImpl.java
│   │   │
│   │   └── presentation/
│   │       ├── controller/
│   │       │   └── TenantController.java
│   │       │
│   │       └── dto/
│   │           └── TenantResponse.java
│   │
│   ├── 📁 shared/                      # ⭐ CÓDIGO COMPARTILHADO
│   │   │
│   │   ├── 📁 core/                    # 🎯 GLOBALMENTE GLOBAL
│   │   │   │
│   │   │   ├── domain/
│   │   │   │   ├── exceptions/
│   │   │   │   │   ├── BusinessException.java
│   │   │   │   │   ├── NotFoundException.java
│   │   │   │   │   └── ValidationException.java
│   │   │   │   │
│   │   │   │   ├── events/
│   │   │   │   │   ├── DomainEvent.java
│   │   │   │   │   └── EventPublisher.java
│   │   │   │   │
│   │   │   │   ├── value-objects/
│   │   │   │   │   ├── Money.java
│   │   │   │   │   ├── Email.java
│   │   │   │   │   └── CPF.java
│   │   │   │   │
│   │   │   │   ├── constants/
│   │   │   │   │   └── ValidationConstants.java
│   │   │   │   │
│   │   │   │   └── base/
│   │   │   │       ├── BaseEntity.java
│   │   │   │       ├── BaseRepository.java
│   │   │   │       └── BaseDomainService.java
│   │   │   │
│   │   │   ├── infrastructure/
│   │   │   │   ├── persistence/
│   │   │   │   │   ├── BaseRepositoryImpl.java
│   │   │   │   │   └── SpecificationBase.java
│   │   │   │   │
│   │   │   │   ├── context/
│   │   │   │   │   ├── UserContext.java
│   │   │   │   │   └── ContextProvider.java
│   │   │   │   │
│   │   │   │   ├── event/
│   │   │   │   │   └── ApplicationEventPublisher.java
│   │   │   │   │
│   │   │   │   └── utils/
│   │   │   │       ├── DateUtils.java
│   │   │   │       ├── CurrencyUtils.java
│   │   │   │       └── ValidationUtils.java
│   │   │   │
│   │   │   └── presentation/
│   │   │       ├── response/
│   │   │       │   ├── APIResponse.java
│   │   │       │   ├── ErrorResponse.java
│   │   │       │   └── PaginatedResponse.java
│   │   │       │
│   │   │       ├── dto/
│   │   │       │   ├── PageDTO.java
│   │   │       │   └── SortDTO.java
│   │   │       │
│   │   │       └── controller/
│   │   │           └── BaseController.java
│   │   │
│   │   └── 📁 features/                # 🔄 FEATURES REUTILIZÁVEIS
│   │       │
│   │       ├── endereco/               # Usada por cliente, fornecedor, etc
│   │       │   ├── application/
│   │       │   │   ├── dto/request/CriarEnderecoRequest.java
│   │       │   │   ├── dto/response/EnderecoResponse.java
│   │       │   │   └── mapper/EnderecoMapper.java
│   │       │   │
│   │       │   ├── domain/
│   │       │   │   ├── entity/Endereco.java
│   │       │   │   ├── service/EnderecoDomainService.java
│   │       │   │   ├── repository/EnderecoRepository.java
│   │       │   │   └── validator/EnderecoValidator.java
│   │       │   │
│   │       │   ├── infrastructure/
│   │       │   │   ├── service/EnderecoInfraService.java
│   │       │   │   └── proxy/EnderecoRepositoryJpaImpl.java
│   │       │   │
│   │       │   └── presentation/
│   │       │       └── controller/EnderecoController.java
│   │       │
│   │       ├── contato/               # Usada por cliente, fornecedor, usuario, etc
│   │       ├── permissao/             # Sistema de permissões
│   │       └── unidademedida/         # Usada por produto
│   │
│   ├── 📁 docs/                       # ⭐ DOCUMENTAÇÃO
│   │   │
│   │   ├── config/
│   │   │   └── OpenAPIConfiguration.java
│   │   │
│   │   ├── api/
│   │   │   ├── produto-api.md
│   │   │   ├── cliente-api.md
│   │   │   ├── fornecedor-api.md
│   │   │   └── README.md
│   │   │
│   │   └── technical/
│   │       ├── ARCHITECTURE.md
│   │       ├── DESIGN_DECISIONS.md
│   │       └── DEPLOYMENT.md
│   │
│   └── 📁 observability/              # ⭐ OBSERVABILIDADE
│       │
│       ├── application/
│       │   └── dto/
│       │
│       ├── config/
│       │   ├── LoggingConfig.java
│       │   └── MetricsConfig.java
│       │
│       ├── domain/
│       │   ├── entity/
│       │   │   ├── AuditLog.java
│       │   │   └── EventLog.java
│       │   │
│       │   └── service/
│       │       ├── AuditService.java
│       │       └── MetricsService.java
│       │
│       ├── presentation/
│       │   └── controller/
│       │       └── MetricsController.java
│       │
│       └── strategy/
│           ├── FileLoggingStrategy.java
│           ├── DatabaseLoggingStrategy.java
│           └── KafkaLoggingStrategy.java
│
└── resources/
    ├── application.yml
    ├── application-dev.yml
    ├── application-prod.yml
    │
    ├── db/
    │   └── migration/
    │       ├── master/                  # Migrações do banco master
    │       │   ├── V1__CreateTenantTable.sql
    │       │   ├── V2__CreatePermissionTable.sql
    │       │   └── ...
    │       │
    │       ├── observability/           # Migrações do erpapi_logs
    │       │   ├── V1__CreateAuditTable.sql
    │       │   └── ...
    │       │
    │       ├── taxengine/               # Migrações do erpapi_tax
    │       │   └── ...
    │       │
    │       └── tenant/                  # Template para bancos de tenants
    │           ├── V1__CreateClienteTable.sql
    │           ├── V2__CreateProdutoTable.sql
    │           └── ...
    │
    ├── messages/
    │   ├── messages.properties           # Mensagens padrão
    │   └── validation.properties
    │
    └── templates/                       # Se houver (Thymeleaf ou similar)
        └── ...
```

---

## 🎨 Legenda

| Símbolo | Significado |
|---------|------------|
| 📁 | Diretório/Package |
| 🎯 | Lógica de negócio (Core domain logic) |
| 🔧 | Implementação técnica (Infrastructure) |
| 🌐 | API/Web layer |
| 🔴 | CRÍTICO - Atenção especial |
| ⭐ | Importante/Principal |
| ⚡ | Customizável por tenant |
| ⚪ | Opcional |
| 🔄 | Compartilhado/Reutilizável |

---

## 📊 Matriz de Onde Colocar Classes

| Tipo de Classe | Package | Exemplo |
|---|---|---|
| **Entity JPA** | `feature.domain.entity` | `Produto.java`, `Cliente.java` |
| **Repository Interface** | `feature.domain.repository` | `ProdutoRepository.java` |
| **Repository Impl (JPA)** | `feature.infrastructure.proxy` | `ProdutoRepositoryJpaImpl.java` |
| **Domain Service** | `feature.domain.service` | `ProdutoDomainService.java` |
| **Domain Validator** | `feature.domain.validator` | `ProdutoValidator.java` |
| **REST Controller** | `feature.presentation.controller` | `ProdutoController.java` |
| **Request DTO** | `feature.application.dto.request` | `CriarProdutoRequest.java` |
| **Response DTO** | `feature.application.dto.response` | `ProdutoResponse.java` |
| **Mapper** | `feature.application.mapper` | `ProdutoMapper.java` |
| **Cache Decorator** | `feature.infrastructure.decorator` | `ProdutoCacheDecorator.java` |
| **Infra Service** | `feature.infrastructure.service` | `ProdutoEstoqueService.java` |
| **Domain Event** | `shared.core.domain.events` | `ProdutoCriadoEvent.java` |
| **Exception** | `shared.core.domain.exceptions` | `ProdutoNaoEncontradoException.java` |
| **Value Object** | `shared.core.domain.value-objects` | `Money.java`, `CPF.java` |
| **Aspect** | `config.aspects` | `LoggingAspect.java` |
| **Security Config** | `config.security` | `SecurityConfig.java` |
| **SwaggerConfig** | `config.swagger` | `SwaggerConfig.java` |
| **Base Classes** | `shared.core.domain.base` | `BaseEntity.java`, `BaseRepository.java` |

---

## 🔍 Exemplo de Import em Feature

```java
// ✅ CORRETO - Usando compartilhado
package com.api.erp.v1.features.cliente.domain.service;

import com.api.erp.v1.main.features.cliente.domain.entity.Cliente;
import com.api.erp.v1.main.features.cliente.domain.repository.ClienteRepository;
import com.api.erp.v1.shared.features.endereco.domain.service.EnderecoDomainService;  // ✅
import com.api.erp.v1.shared.core.domain.exceptions.BusinessException;                 // ✅
import com.api.erp.v1.shared.core.domain.events.DomainEvent;                           // ✅
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteDomainService {
    private final ClienteRepository repository;
    private final EnderecoDomainService enderecoDomainService;  // Cross-feature

    public Cliente criar(Cliente cliente, EnderecoRequest endereco) {
        // Usar shared feature
        var enderecoCriado = enderecoDomainService.criar(...);
        cliente.setEndereco(enderecoCriado);
        return repository.save(cliente);
    }
}

// ❌ ERRADO - Importar de feature específica
// import com.api.erp.v1.features.cliente.domain.entity.Produto;  // Não fazer! Produto é outra feature
```

---

## 🧪 Estrutura de Testes Paralela

```
src/test/java/com/api/erp/
│
├── v1/
│   ├── features/
│   │   ├── produto/
│   │   │   ├── domain/service/
│   │   │   │   └── ProdutoDomainServiceTest.java
│   │   │   ├── infrastructure/
│   │   │   │   └── ProdutoRepositoryTest.java
│   │   │   └── presentation/
│   │   │       └── ProdutoControllerTest.java
│   │   │
│   │   └── cliente/
│   │
│   ├── shared/
│   │   ├── features/endereco/
│   │   │   └── domain/service/EnderecoServiceTest.java
│   │   │
│   │   └── core/
│   │       └── domain/exceptions/ExceptionTest.java
│   │
│   ├── tenant/
│   │   └── infrastructure/datasource/MultiTenantRoutingDataSourceTest.java
│   │
│   └── integration/
│       ├── ClienteIntegrationTest.java
│       ├── MultiTenantIntegrationTest.java
│       └── SecurityIntegrationTest.java
│
└── resources/
    └── application-test.yml
```

---

## 🚀 Summary

| Aspecto | Decisão |
|---------|---------|
| **Linguagem na estrutura** | Package names em **inglês** (com exceção de Tenant que é PT) |
| **Nomeação de classes feature** | CamelCase + sufixo (UserRepository, UserService, UserController) |
| **DTOs** | Sempre em `application.dto.request` e `application.dto.response` |
| **Validators** | **domain/validator** para lógica, **infrastructure/validator** para infra |
| **Exceptions** | `shared.core.domain.exceptions` para global, ou `feature.domain` para feature-specific |
| **Multi-tenancy** | `tenant.infrastructure.config` com @EnableJpaRepositories |
| **Aspectos** | `config.aspects` |

---

