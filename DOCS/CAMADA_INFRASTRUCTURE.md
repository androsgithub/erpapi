# ⚙️ Camada de Infraestrutura (Infrastructure Layer)

## 📋 Visão Geral

A camada de infraestrutura é responsável por **implementar detalhes técnicos**, como persistência em banco de dados, integração com sistemas externos, cache e configurações técnicas.

## 🎯 Responsabilidades

- Implementar repositórios JPA
- Gerenciar configurações do banco de dados
- Implementar adapters para sistemas externos
- Cache e performance
- Auditoria e eventos
- Configurações técnicas (Spring, etc)

## 🏗️ Estrutura

```
features/{feature}/infrastructure/
├── repository/         # Implementações JPA
├── adapter/           # Adapters para externos
├── persistence/       # Event listeners, auditoria
├── config/           # Configurações
└── cache/            # Cache strategies
```

## 🔑 Conceitos-Chave

### 1️⃣ JPA Repository

Implementa interface de repositório do domínio.

```java
@Repository
public interface JpaProdutoRepository 
    extends JpaRepository<Produto, Long>, 
            JpaSpecificationExecutor<Produto> {
    
    // Derivados do nome (Spring Data)
    Optional<Produto> findByCodigo(String codigo);
    List<Produto> findByStatus(StatusProduto status);
    List<Produto> findByUnidadeMedidaId(Long unidadeId);
    
    // Customizados com @Query
    @Query("""
        SELECT p FROM Produto p 
        WHERE p.status = 'ATIVO' 
        AND p.tipo = 'FABRICAVEL'
        ORDER BY p.descricao
    """)
    List<Produto> findProdutosFabricaveis();
    
    // Com Specification para queries complexas
    @Query("""
        SELECT p FROM Produto p 
        WHERE (:codigo IS NULL OR p.codigo LIKE %:codigo%)
        AND (:status IS NULL OR p.status = :status)
    """)
    Page<Produto> findComFiltro(
        @Param("codigo") String codigo,
        @Param("status") StatusProduto status,
        Pageable pageable
    );
}
```

### 2️⃣ Specifications

Para queries complexas e reutilizáveis.

```java
@Component
public class ProdutoSpecifications {
    
    public static Specification<Produto> comCodigo(String codigo) {
        return (root, query, cb) -> 
            cb.like(root.get("codigo"), "%" + codigo + "%");
    }
    
    public static Specification<Produto> comStatus(StatusProduto status) {
        return (root, query, cb) -> 
            cb.equal(root.get("status"), status);
    }
    
    public static Specification<Produto> comUnidade(Long unidadeId) {
        return (root, query, cb) -> 
            cb.equal(root.get("unidadeMedida").get("id"), unidadeId);
    }
    
    public static Specification<Produto> todos() {
        return (root, query, cb) -> cb.conjunction();
    }
    
    // Composição
    public static Specification<Produto> ativosComUnidade(Long unidadeId) {
        return todos()
            .and(comStatus(StatusProduto.ATIVO))
            .and(comUnidade(unidadeId));
    }
}

// Uso no repository
List<Produto> ativos = repository.findAll(
    ProdutoSpecifications.comStatus(StatusProduto.ATIVO),
    Sort.by("descricao")
);
```

### 3️⃣ Entity Listeners (Auditoria)

```java
@Entity
@EntityListeners(AuditListener.class)
public class Produto {
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;
    
    @Column(name = "criado_por")
    private String criadoPor;
    
    @Column(name = "atualizado_por")
    private String atualizadoPor;
}

@Component
public class AuditListener {
    
    @PrePersist
    public void prePersist(Object entity) {
        if (entity instanceof Auditavel) {
            Auditavel auditavel = (Auditavel) entity;
            String usuarioId = getCurrentUserId();
            LocalDateTime agora = LocalDateTime.now();
            
            auditavel.setCriadoPor(usuarioId);
            auditavel.setDataCriacao(agora);
            auditavel.setAtualizadoPor(usuarioId);
            auditavel.setDataAtualizacao(agora);
        }
    }
    
    @PreUpdate
    public void preUpdate(Object entity) {
        if (entity instanceof Auditavel) {
            Auditavel auditavel = (Auditavel) entity;
            auditavel.setAtualizadoPor(getCurrentUserId());
            auditavel.setDataAtualizacao(LocalDateTime.now());
        }
    }
    
    private String getCurrentUserId() {
        // Obter usuário do contexto
        return SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();
    }
}
```

### 4️⃣ Adapters

Para integração com sistemas externos.

```java
@Component
public class ViaCepAdapter {
    
    private final RestTemplate restTemplate;
    
    public EnderecoData buscarPorCep(String cep) {
        String url = "https://viacep.com.br/ws/{cep}/json/";
        
        try {
            ResponseEntity<EnderecoData> response = restTemplate.getForEntity(
                url,
                EnderecoData.class,
                cep
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
        } catch (RestClientException ex) {
            log.error("Erro ao consultar CEP: {}", cep, ex);
        }
        
        return null;
    }
}

// DTO para adapter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoData {
    private String logradouro;
    private String bairro;
    private String localidade;
    private String uf;
    private String erro;
}
```

### 5️⃣ Cache

```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            "unidades",
            "conversoes",
            "usuarios",
            "empresas"
        );
    }
}

// Uso
@Component
public class UnidadeMedidaService {
    
    @Cacheable(value = "unidades", key = "#id")
    public UnidadeMedida obter(Long id) {
        return repository.findById(id).orElseThrow();
    }
    
    @CacheEvict(value = "unidades", key = "#entity.id")
    public void atualizar(UnidadeMedida entity) {
        repository.save(entity);
    }
    
    @CacheEvict(value = "unidades", allEntries = true)
    public void limparCache() {
    }
}
```

### 6️⃣ Configurações JPA

```java
@Configuration
public class JpaConfig {
    
    @Bean
    public JpaProperties jpaProperties() {
        JpaProperties props = new JpaProperties();
        props.setDatabase(Database.MYSQL);
        props.setShowSql(false);
        props.setDatabasePlatform("org.hibernate.dialect.MySQL8Dialect");
        return props;
    }
    
    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(false);
        adapter.setGenerateDdl(false);
        adapter.setDatabase(Database.MYSQL);
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL8Dialect");
        return adapter;
    }
}
```

## 🔄 Padrão Repository

```java
// Domínio
public interface ProdutoRepository extends Repository<Produto, Long> {
    Produto save(Produto produto);
    Optional<Produto> findById(Long id);
    List<Produto> findByStatus(StatusProduto status);
}

// Infraestrutura
@Repository
public class JpaProdutoRepository 
    extends SimpleJpaRepository<Produto, Long>
    implements ProdutoRepository {
    
    public JpaProdutoRepository(EntityManager em) {
        super(Produto.class, em);
    }
    
    @Override
    public List<Produto> findByStatus(StatusProduto status) {
        return findAll(where(comStatus(status)));
    }
}
```

## 🔗 Persistência Multi-Tenancy

```java
@Component
public class TenantAwareRepositoryImpl {
    
    @PersistenceContext
    private EntityManager em;
    
    public <T> List<T> findAllForTenant(
        Class<T> entityClass,
        Long tenantId
    ) {
        return em.createQuery(
            "SELECT e FROM " + entityClass.getName() + " e " +
            "WHERE e.empresa.id = :tenantId",
            entityClass
        )
        .setParameter("tenantId", tenantId)
        .getResultList();
    }
}
```

## 🧪 Testes de Infraestrutura

```java
@DataJpaTest
@ActiveProfiles("test")
class JpaProdutoRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private JpaProdutoRepository repository;
    
    @Test
    void deveSalvarEObterProduto() {
        Produto produto = new Produto();
        produto.setCodigo("TEST001");
        produto.setDescricao("Produto Teste");
        produto.setStatus(StatusProduto.ATIVO);
        
        entityManager.persistAndFlush(produto);
        
        Optional<Produto> resultado = repository.findByCodigo("TEST001");
        
        assertTrue(resultado.isPresent());
        assertEquals("Produto Teste", resultado.get().getDescricao());
    }
    
    @Test
    void deveEncontrarProdutosAtivos() {
        // Arrange
        salvarProduto("PROD001", StatusProduto.ATIVO);
        salvarProduto("PROD002", StatusProduto.INATIVO);
        salvarProduto("PROD003", StatusProduto.ATIVO);
        
        // Act
        List<Produto> resultado = repository.findByStatus(StatusProduto.ATIVO);
        
        // Assert
        assertEquals(2, resultado.size());
    }
}
```

## 🚀 Boas Práticas

1. **Repositories como interfaces**: Não expor JPA details
2. **Queries customizadas**: Para lógica complexa use @Query
3. **Lazy loading cautela**: Use @Fetch(FetchMode.JOIN) se necessário
4. **Índices no banco**: Para queries frequentes
5. **Transações apropriadas**: readOnly = true para queries
6. **Cache estrategicamente**: Para dados imutáveis
7. **Auditoria automática**: Com listeners
8. **Versionamento**: Para soft deletes
9. **Constraints no BD**: Não apenas no código
10. **N+1 Query problem**: Use joins ou eager loading

## 📚 Referências Relacionadas

- [CAMADA_DOMAIN.md](CAMADA_DOMAIN.md) - Domínio
- [TESTES.md](TESTES.md) - Testes de infraestrutura
- [SHARED_DOMAIN.md](SHARED_DOMAIN.md) - Componentes compartilhados

---

**Última atualização:** Dezembro de 2025
