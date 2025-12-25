# 🌍 Camada de Domínio (Domain Layer)

## 📋 Visão Geral

A camada de domínio contém a **lógica de negócio pura**, implementando os conceitos e regras do domínio de forma independente de frameworks ou tecnologias específicas.

## 🎯 Responsabilidades

- Encapsular lógica de negócio
- Definir entidades e value objects
- Implementar validações de negócio
- Definir interfaces de repositório
- Orquestrar domínios complexos via serviços
- Manter coesão entre objetos relacionados

## 🏗️ Estrutura

```
features/{feature}/domain/
├── entity/              # Entidades JPA com comportamento
├── service/             # Serviços de domínio
├── repository/          # Interfaces de repositório
├── valueobject/         # Value Objects
├── factory/             # Factories de criação
├── event/               # Eventos de domínio
└── specification/       # Specifications (Criteria)
```

## 🔑 Conceitos-Chave

### 1️⃣ Entity (Entidade)

Uma entidade é um objeto com **identidade única** que perdura ao longo do tempo.

```java
@Entity
@Table(name = "produtos")
public class Produto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // ← Identidade única
    
    @Column(nullable = false, unique = true)
    private String codigo;
    
    @Column(nullable = false)
    private String descricao;
    
    @Enumerated(EnumType.STRING)
    private StatusProduto status;
    
    // ← Comportamentos (não apenas getters/setters)
    public void ativar() {
        if (this.status == StatusProduto.BLOQUEADO) {
            throw new BusinessException("Não é possível ativar produto bloqueado");
        }
        this.status = StatusProduto.ATIVO;
    }
    
    public boolean estaDisponivel() {
        return status == StatusProduto.ATIVO;
    }
    
    public void bloquear(String motivo) {
        this.status = StatusProduto.BLOQUEADO;
        // ... registrar motivo
    }
}
```

**Características**:
- ✓ Tem identidade (@Id)
- ✓ Mutável (estado pode mudar)
- ✓ Persistido em banco de dados
- ✓ Contém lógica de negócio
- ✓ Responsável por manter suas invariantes

### 2️⃣ Value Object (Objeto de Valor)

Um value object é um objeto **imutável** que representa um conceito sem identidade.

```java
public class Email {
    
    private final String valor;  // ← Imutável (final)
    
    public Email(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException("Email não pode estar em branco");
        }
        
        if (!isValido(valor)) {
            throw new BusinessException("Email inválido: " + valor);
        }
        
        this.valor = valor.toLowerCase();
    }
    
    public String getValor() {
        return valor;
    }
    
    // ← Igualdade por valor, não por identidade
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Email)) return false;
        Email email = (Email) o;
        return valor.equals(email.valor);
    }
    
    @Override
    public int hashCode() {
        return valor.hashCode();
    }
    
    private static boolean isValido(String email) {
        return Pattern.matches(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            email
        );
    }
}
```

**Características**:
- ✓ Sem identidade (não tem @Id)
- ✓ Imutável (final fields)
- ✓ Igualdade por valor (não por referência)
- ✓ Autovalidação no construtor
- ✓ Encapsula validação de um conceito específico

### 3️⃣ Repository (Repositório)

Interface que define contrato para acesso a dados.

```java
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    // ← Métodos de domínio (linguagem ubíqua)
    Optional<Produto> findByCodigo(String codigo);
    List<Produto> findByStatus(StatusProduto status);
    List<Produto> findByUnidadeMedidaId(Long unidadeMedidaId);
    
    // ← Método customizado para lógica complexa
    List<Produto> findProdutosAtivosParaProducao();
}
```

**Características**:
- ✓ Interface (não implementação)
- ✓ Métodos em linguagem ubíqua (do domínio)
- ✓ Não expõe detalhes de persistência
- ✓ Implementação fica em infrastructure

### 4️⃣ Domain Service (Serviço de Domínio)

Orquestra lógica que envolve múltiplas entidades.

```java
@Component
public class ProdutoService {
    
    private final ProdutoRepository produtoRepository;
    private final UnidadeMedidaRepository unidadeRepository;
    
    // ← Lógica que envolve múltiplas entidades
    public void associarUnidade(Long produtoId, Long unidadeId) {
        Produto produto = produtoRepository.findById(produtoId)
            .orElseThrow(() -> new NotFoundException("Produto não encontrado"));
        
        UnidadeMedida unidade = unidadeRepository.findById(unidadeId)
            .orElseThrow(() -> new NotFoundException("Unidade não encontrada"));
        
        // Validações de negócio
        if (!produto.estaDisponivel()) {
            throw new BusinessException("Produto não está disponível");
        }
        
        // Regra de negócio: tipos de unidade devem ser compatíveis
        if (!saoCompativeis(produto.getTipo(), unidade.getTipo())) {
            throw new BusinessException("Unidade não é compatível com tipo de produto");
        }
        
        produto.setUnidade(unidade);
        produtoRepository.save(produto);
    }
    
    private boolean saoCompativeis(TipoProduto tipo, TipoUnidade unidade) {
        // Lógica de compatibilidade
        return true;
    }
}
```

**Características**:
- ✓ Encapsula lógica de domínio complexa
- ✓ Trabalha com repositórios
- ✓ Valida regras de negócio
- ✓ Implementa conceitos que não pertencem a uma entidade específica

### 5️⃣ Factory (Fábrica)

Encapsula a criação complexa de entidades.

```java
public class ProdutoFactory {
    
    public static Produto criar(CriarProdutoRequest dto) {
        
        // Validações que não cabem na entidade
        if (!dto.isValido()) {
            throw new BusinessException("Dados inválidos para criar produto");
        }
        
        // Lógica de criação
        Produto produto = new Produto();
        produto.setCodigo(dto.getCodigo());
        produto.setDescricao(dto.getDescricao());
        produto.setStatus(StatusProduto.ATIVO);
        produto.setTipo(dto.getTipo());
        
        // Valores padrão
        produto.setMargemLucro(new BigDecimal("30.00"));
        produto.setDataCriacao(LocalDateTime.now());
        
        return produto;
    }
}
```

### 6️⃣ Specification (Especificação)

Define critérios complexos de query de forma reutilizável.

```java
public class ProdutoSpecification {
    
    public static Specification<Produto> ativa() {
        return (root, query, cb) -> 
            cb.equal(root.get("status"), StatusProduto.ATIVO);
    }
    
    public static Specification<Produto> porUnidade(Long unidadeId) {
        return (root, query, cb) -> 
            cb.equal(root.get("unidadeMedida").get("id"), unidadeId);
    }
    
    public static Specification<Produto> comCodigoOuDescricao(String termo) {
        return (root, query, cb) -> cb.or(
            cb.like(root.get("codigo"), "%" + termo + "%"),
            cb.like(root.get("descricao"), "%" + termo + "%")
        );
    }
    
    // Composição de specifications
    public static Specification<Produto> ativasPorUnidade(Long unidadeId) {
        return ativa().and(porUnidade(unidadeId));
    }
}
```

## 🔄 Fluxo de Criação de Entidade

```
Request
   ↓
Validações de entrada (DTO)
   ↓
Factory cria entidade (lógica de criação)
   ↓
Domain Service valida regras de negócio
   ↓
Repository persiste
   ↓
Response
```

## ✅ Validações no Domínio

```java
// Validação dentro da entidade (invariante)
public class Produto {
    
    public void setPreco(BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Preço deve ser positivo");
        }
        this.preco = preco;
    }
}

// Validação no serviço de domínio (regra de negócio)
public class ProdutoService {
    
    public void descontinuar(Long produtoId) {
        Produto produto = repository.findById(produtoId).orElseThrow();
        
        if (temVendasPendentes(produtoId)) {
            throw new BusinessException("Não é possível descontinuar produto com vendas pendentes");
        }
        
        produto.setStatus(StatusProduto.DESCONTINUADO);
    }
}
```

## 🧪 Testes de Domínio

```java
// Testes de entidade
class ProdutoTest {
    
    @Test
    void deveBloqueiarProdutoAtivo() {
        Produto produto = new Produto();
        produto.setStatus(StatusProduto.ATIVO);
        
        produto.bloquear("Teste");
        
        assertEquals(StatusProduto.BLOQUEADO, produto.getStatus());
    }
    
    @Test
    void naoDeveBloqueiarProdutoJaBloqueado() {
        Produto produto = new Produto();
        produto.setStatus(StatusProduto.BLOQUEADO);
        
        assertThrows(BusinessException.class, () -> produto.bloquear("Teste"));
    }
}

// Testes de serviço
class ProdutoServiceTest {
    
    @Test
    void deveAssociarUnidade() {
        Produto produto = novo();
        UnidadeMedida unidade = novaUnidade();
        
        service.associarUnidade(produto.getId(), unidade.getId());
        
        assertTrue(service.verificarAssociacao(produto.getId(), unidade.getId()));
    }
}
```

## 🚀 Boas Práticas

1. **Linguagem Ubíqua**: Use nomes do domínio (não técnicos)
2. **Encapsulamento**: Entidades com comportamento, não apenas dados
3. **Imutabilidade**: Value Objects devem ser imutáveis
4. **Coesão**: Mantenha coisas relacionadas juntas
5. **Independência**: Domínio não depende de frameworks
6. **Validação**: Valide em dois pontos:
   - Construtor/setters (invariantes)
   - Serviços de domínio (regras complexas)
7. **Nenhuma lógica de persistência**: Deixe para repositories
8. **DTOs separados**: Use DTOs na aplicação, não no domínio

## 🔗 Integração com Outras Camadas

```
Domain (core)
    ↑
    │ (implementa)
    │
Infrastructure (JPA repositories, external adapters)
    ↑
    │ (orquestra)
    │
Application (use cases, DTOs, validators)
    ↑
    │ (expõe)
    │
Presentation (controllers, HTTP responses)
```

## 📚 Referências Relacionadas

- [SHARED_DOMAIN.md](SHARED_DOMAIN.md) - Componentes compartilhados
- [PADROES_PROJETO.md](PADROES_PROJETO.md) - Padrões DDD
- [FEATURE_PRODUTO.md](FEATURE_PRODUTO.md) - Exemplo de aplicação

---

**Última atualização:** Dezembro de 2025
