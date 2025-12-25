# 🏗️ Arquitetura Geral - ERP API

## Visão Geral

O ERP API segue uma arquitetura em **camadas** (Layered Architecture) combinada com princípios de **Domain-Driven Design (DDD)** para promover separação de responsabilidades, testabilidade e manutenibilidade.

## 📊 Diagrama de Arquitetura

```
┌─────────────────────────────────────────────┐
│         CAMADA DE APRESENTAÇÃO               │
│  (Controllers, REST, WebSocket, Static)     │
├─────────────────────────────────────────────┤
│         CAMADA DE APLICAÇÃO                  │
│  (Service, DTO, Use Cases, Validators)      │
├─────────────────────────────────────────────┤
│         CAMADA DE DOMÍNIO                    │
│  (Entities, Value Objects, Repositories)    │
├─────────────────────────────────────────────┤
│         CAMADA DE INFRAESTRUTURA             │
│  (BD, Cache, External Services, Config)     │
└─────────────────────────────────────────────┘
```

## 🏢 Estrutura de Features

O projeto é organizado em torno de **features** (funcionalidades) de negócio. Cada feature segue a mesma estrutura:

```
feature/
├── domain/                    # Camada de Domínio (DDD)
│   ├── entity/               # Entidades do domínio
│   ├── service/              # Serviços de domínio
│   ├── repository/           # Interface de repositório
│   └── valueobject/          # Value Objects
│
├── application/              # Camada de Aplicação
│   ├── service/              # Implementação de serviços
│   ├── dto/                  # Data Transfer Objects
│   │   ├── request/
│   │   └── response/
│   ├── validator/            # Validadores
│   └── usecase/              # Casos de uso
│
├── infrastructure/           # Camada de Infraestrutura
│   ├── repository/           # Implementação de repositórios (JPA)
│   ├── persistence/          # Configurações de persistência
│   └── adapter/              # Adaptadores
│
└── presentation/             # Camada de Apresentação
    ├── controller/           # Controllers REST
    └── response/             # Response formatters
```

## 🎯 Princípios de Design

### 1. **Separação de Responsabilidades**
Cada camada tem uma responsabilidade bem definida:
- **Domain**: Regras de negócio core
- **Application**: Orquestração de casos de uso
- **Presentation**: Interface HTTP
- **Infrastructure**: Detalhes técnicos

### 2. **Domain-Driven Design (DDD)**
- Entidades com identidade única
- Value Objects imutáveis
- Repositórios como interfaces de acesso aos dados
- Serviços de domínio para lógica complexa

### 3. **SOLID Principles**
- **S**ingle Responsibility: Cada classe tem uma responsabilidade
- **O**pen/Closed: Aberto para extensão, fechado para modificação
- **L**iskov Substitution: Usar interfaces
- **I**nterface Segregation: Interfaces específicas
- **D**ependency Inversion: Depender de abstrações

### 4. **Clean Code**
- Nomes descritivos
- Funções pequenas e focadas
- Sem duplicação (DRY)
- Fácil de testar

## 🔄 Fluxo de Requisição

```
1. HTTP Request chega no Controller (presentation)
                    ↓
2. Controller valida entrada e cria DTO
                    ↓
3. ApplicationService orquestra lógica
                    ↓
4. Domain Services executam regras de negócio
                    ↓
5. Repository persiste/recupera dados
                    ↓
6. Response é formatada e retornada ao cliente
```

## 📦 Features Principais

### 1. **Produto** (`feature/produto`)
- Gerenciamento de catálogo de produtos
- Composição de produtos
- Classificação fiscal
- Tipos de produto (Comprado/Fabricável)

### 2. **Usuário** (`feature/usuario`)
- Gerenciamento de usuários
- Autenticação e autorização
- Gestão de status (Ativo/Inativo/Bloqueado)
- Aprovação de usuários

### 3. **Empresa** (`feature/empresa`)
- Gerenciamento de informações de empresas
- Multi-tenancy
- Configurações por empresa

### 4. **Endereço** (`feature/endereco`)
- Gestão de endereços de empresas
- Endereços de clientes
- Validação de endereços

### 5. **Permissão** (`feature/permissao`)
- Sistema granular de permissões
- Roles baseadas
- Controle de acesso por ação (CRUD)

### 6. **Unidade de Medida** (`feature/unidademedida`)
- Catálogo de unidades de medida
- Conversão entre unidades
- Unidade padrão por produto

## 🔐 Camadas de Segurança

```
┌─────────────────────────────┐
│   Spring Security          │ ← Autenticação
├─────────────────────────────┤
│   Permission Filters       │ ← Autorização
├─────────────────────────────┤
│   Role-Based Access Control│ ← RBAC
├─────────────────────────────┤
│   Validators               │ ← Validação de regras
└─────────────────────────────┘
```

## 🛠️ Stack Tecnológico

| Componente | Tecnologia |
|-----------|-----------|
| Framework | Spring Boot 4.0.1 |
| Linguagem | Java 17 |
| Build | Maven |
| ORM | Spring Data JPA / Hibernate |
| Segurança | Spring Security |
| Validação | Jakarta Bean Validation |
| Persistência | Database (configurável) |
| WebSocket | Spring WebSocket |

## 📚 Conceitos-Chave

### Entity
Objeto com identidade única, persistido no banco de dados.
```java
@Entity
public class Produto {
    @Id private Long id;
    // ...
}
```

### Value Object
Objeto imutável sem identidade, representa um conceito do domínio.
```java
public class Email {
    private final String valor;
    // Sem @Id, sem identidade
}
```

### Repository
Interface que define contrato para acesso aos dados.
```java
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    // métodos customizados
}
```

### Service (Domain)
Lógica de domínio que envolve múltiplas entidades.
```java
public class ProdutoService {
    public void associarUnidade(Produto produto, UnidadeMedida unidade) {
        // validações e lógica de negócio
    }
}
```

### ApplicationService
Orquestra domain services para implementar um caso de uso.
```java
@Service
public class CriarProdutoService {
    public void executar(CriarProdutoRequest dto) {
        // orquestra a criação
    }
}
```

## 🔗 Referências de Entidades

```
┌─────────────┐
│   Produto   │ ◄─── Referencia ────► UnidadeMedida
└─────────────┘
       │
       │ Relacionado
       ▼
┌─────────────────────┐
│ ProdutoComposicao   │
│ Classificacao Fiscal│
└─────────────────────┘

┌─────────────┐
│  Empresa    │ ◄─── Tem ────► Endereço(s)
└─────────────┘
       │
       │ Tem
       ▼
┌─────────────┐
│  Usuário    │
└─────────────┘
       │
       │ Tem
       ▼
┌──────────────────┐
│ UsuarioPermissao │
└──────────────────┘
```

## 🎓 Recursos Adicionais

- Ver [ESTRUTURA_PROJETO.md](ESTRUTURA_PROJETO.md) para detalhes de pastas
- Ver [CAMADA_DOMAIN.md](CAMADA_DOMAIN.md) para detalhes da camada de domínio
- Ver [CAMADA_APPLICATION.md](CAMADA_APPLICATION.md) para detalhes de aplicação
- Ver [PADROES_PROJETO.md](PADROES_PROJETO.md) para padrões utilizados

---

**Última atualização:** Dezembro de 2025
