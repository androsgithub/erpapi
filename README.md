# Módulo de Usuários - ERP

## Visão Geral
Módulo de gestão de usuários com suporte a multi-tenancy e regras customizáveis por empresa.

## Estrutura do Projeto

```
src/
├── main/java/com/empresa/erp/
│   ├── shared/domain/
│   │   ├── exception/
│   │   │   ├── BusinessException.java
│   │   │   └── NotFoundException.java
│   │   └── valueobject/
│   │       ├── CPF.java
│   │       └── Email.java
│   └── features/usuario/
│       ├── domain/
│       │   ├── entity/
│       │   │   ├── Usuario.java
│       │   │   └── StatusUsuario.java
│       │   ├── factory/
│       │   │   └── EmpresaConfig.java
│       │   ├── repository/
│       │   │   └── UsuarioRepository.java
│       │   ├── service/
│       │   │   └── UsuarioService.java
│       │   └── validator/
│       │       └── UsuarioValidator.java
│       ├── application/
│       │   ├── dto/request/
│       │   │   ├── CreateUsuarioRequest.java
│       │   │   └── UpdateUsuarioRequest.java
│       │   ├── dto/response/
│       │   │   └── UsuarioResponse.java
│       │   ├── service/
│       │   │   ├── PasswordEncoder.java
│       │   │   └── UsuarioServiceImpl.java
│       │   └── validator/
│       │       └── BasicUsuarioValidator.java
│       ├── infrastructure/
│       │   ├── decorator/
│       │   │   ├── EmailCorporativoValidatorDecorator.java
│       │   │   ├── GestorAprovacaoServiceDecorator.java
│       │   │   └── NotificacaoService.java
│       │   └── factory/
│       │       ├── EmpresaConfigRepository.java
│       │       ├── UsuarioServiceFactory.java
│       │       └── ValidatorFactory.java
│       └── presentation/
│           └── controller/
│               └── UsuarioController.java
└── test/java/com/empresa/erp/
    ├── shared/domain/valueobject/
    │   ├── CPFTest.java
    │   └── EmailTest.java
    ├── features/usuario/
    │   ├── domain/entity/
    │   │   └── UsuarioTest.java
    │   ├── application/
    │   │   ├── service/
    │   │   │   └── UsuarioServiceImplTest.java
    │   │   └── validator/
    │   │       └── BasicUsuarioValidatorTest.java
    │   └── infrastructure/
    │       ├── decorator/
    │       │   ├── EmailCorporativoValidatorDecoratorTest.java
    │       │   └── GestorAprovacaoServiceDecoratorTest.java
    │       └── factory/
    │           ├── ValidatorFactoryTest.java
    │           └── UsuarioServiceFactoryTest.java
```

## Padrões de Projeto Implementados

### 1. Factory Pattern
**Propósito:** Criar instâncias de serviços customizados por empresa.

- `UsuarioServiceFactory`: Cria serviços com decorators apropriados
- `ValidatorFactory`: Compõe validadores dinamicamente

### 2. Decorator Pattern
**Propósito:** Adicionar comportamentos dinamicamente sem modificar código base.

- `GestorAprovacaoServiceDecorator`: Adiciona fluxo de aprovação
- `EmailCorporativoValidatorDecorator`: Valida domínio de email

### 3. Builder Pattern
**Propósito:** Construir objetos complexos de forma fluente.

```java
Usuario usuario = Usuario.builder()
    .nomeCompleto("João Silva")
    .email(new Email("joao@empresa.com"))
    .cpf(new CPF("123.456.789-09"))
    .status(StatusUsuario.ATIVO)
    .build();
```

### 4. Repository Pattern
**Propósito:** Abstrair acesso a dados.

## Próximas Implementações
1. Integração com JPA (entidades e repositories)
2. Configuração Spring Boot (beans e profiles)
3. Segurança (JWT e autorização)
4. Auditoria e logs
5. Documentação Swagger/OpenAPI
