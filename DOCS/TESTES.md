# 🧪 Testes e Test Containers

## 📋 Visão Geral

Este documento descreve a estratégia de testes no projeto, incluindo testes unitários, integração e test containers.

## 🏗️ Pirâmide de Testes

```
        /\\
       /  \\          End-to-End (5%)
      /────\\
     /  E2E  \\
    /──────────\\
   /            \\    Integração (15%)
  /  Integração  \\
 /────────────────\\
/                  \\  Unitário (80%)
/    Unitário       \\
/____________________\\
```

## 🎯 Testes Unitários (80%)

Testam uma unidade isolada.

### Testes de Entidade

```java
@SpringBootTest
public class ProdutoTest {
    
    @Test
    void deveCriarProdutoValido() {
        // Arrange
        Produto produto = Produto.builder()
            .codigo(\"PROD001\")
            .descricao(\"Produto Teste\")
            .status(StatusProduto.ATIVO)
            .build();
        
        // Act & Assert
        assertNotNull(produto.getId());
        assertEquals(StatusProduto.ATIVO, produto.getStatus());
    }
    
    @Test
    void naoDeveBloqueiarProdutoJaBloqueado() {
        // Arrange
        Produto produto = Produto.builder()
            .status(StatusProduto.BLOQUEADO)
            .build();
        
        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            produto.bloquear(\"Motivo\");
        });
    }
}
```

### Testes de Service (Domain)

```java
@SpringBootTest
public class ProdutoServiceTest {
    
    @MockBean
    private ProdutoRepository repository;
    
    @Autowired
    private ProdutoService service;
    
    @Test
    void deveAssociarUnidadeMedida() {
        // Arrange
        Produto produto = new Produto();
        UnidadeMedida unidade = new UnidadeMedida();
        
        // Act
        service.associarUnidade(produto, unidade);
        
        // Assert
        assertEquals(unidade, produto.getUnidadeMedida());
    }
}
```

## 🔗 Testes de Integração (15%)

Testam múltiplas componentes integradas.

### Application Service Test

```java
@SpringBootTest
public class CriarProdutoServiceTest {
    
    @Autowired
    private CriarProdutoService service;
    
    @Autowired
    private ProdutoRepository repository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    void deveCriarProdutoCompleto() {
        // Arrange
        CriarProdutoRequest request = new CriarProdutoRequest(
            \"PROD001\", \"Produto\", 1L, TipoProduto.COMPRADO
        );
        
        // Act
        ProdutoResponse response = service.executar(request);
        
        // Assert
        assertNotNull(response.getId());
        assertEquals(\"PROD001\", response.getCodigo());
        
        // Verificar persistência
        Produto criado = repository.findByCodigo(\"PROD001\").orElseThrow();
        assertEquals(\"PROD001\", criado.getCodigo());
    }
}
```

### JPA Repository Test

```java
@DataJpaTest
public class JpaProdutoRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private JpaProdutoRepository repository;
    
    @Test
    void deveEncontrarPorCodigo() {
        // Arrange
        Produto produto = new Produto(\"PROD001\", \"Produto\");
        entityManager.persistAndFlush(produto);
        
        // Act
        Optional<Produto> resultado = repository.findByCodigo(\"PROD001\");
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(\"PROD001\", resultado.get().getCodigo());
    }
}
```

### Controller Test

```java
@SpringBootTest
@AutoConfigureMockMvc
public class ProdutoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private CriarProdutoService criarService;
    
    @Test
    void deveCriarProdutoViaHttp() throws Exception {
        // Arrange
        ProdutoResponse response = new ProdutoResponse();
        response.setId(1L);
        response.setCodigo(\"PROD001\");
        
        when(criarService.executar(any()))
            .thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(post(\"/api/v1/produtos\")
            .contentType(MediaType.APPLICATION_JSON)
            .content(\"{\\\"codigo\\\":\\\"PROD001\\\"}\"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath(\"$.id\").value(1));
    }
}
```

## 🐳 Test Containers

Para testes com banco de dados real.

### Configuração

```java
@SpringBootTest
public class IntegrationTestBase {
    
    @Container
    static PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>(\"postgres:13\")
            .withDatabaseName(\"testdb\")
            .withUsername(\"test\")
            .withPassword(\"test\");
    
    @DynamicPropertySource
    static void registerDatabaseProperties(DynamicPropertyRegistry registry) {
        registry.add(\"spring.datasource.url\", 
            postgres::getJdbcUrl);
        registry.add(\"spring.datasource.username\", 
            postgres::getUsername);
        registry.add(\"spring.datasource.password\", 
            postgres::getPassword);
    }
}
```

### Teste com Test Container

```java
@SpringBootTest
public class ProdutoRepositoryTestContainerTest 
    extends IntegrationTestBase {
    
    @Autowired
    private ProdutoRepository repository;
    
    @Test
    void devePersisteEmBancoDeDadosReal() {
        // Arrange
        Produto produto = Produto.builder()
            .codigo(\"PROD001\")
            .descricao(\"Produto\")
            .status(StatusProduto.ATIVO)
            .build();
        
        // Act
        Produto salvo = repository.save(produto);
        
        // Assert
        assertTrue(salvo.getId() > 0);
        
        Optional<Produto> recuperado = repository.findByCodigo(\"PROD001\");
        assertTrue(recuperado.isPresent());
    }
}
```

## 📊 Cobertura de Testes

### Executar com Cobertura

```bash
./mvnw clean test jacoco:report
# Resultado: target/site/jacoco/index.html
```

### JaCoCo Configuration

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.7</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## 🎯 Mocks e Stubs

### Mock - Simular comportamento

```java
@Mock
private ProdutoRepository mockRepository;

@Test
void deveUsarMock() {
    when(mockRepository.findById(1L))
        .thenReturn(Optional.of(new Produto()));
    
    ProdutoResponse response = service.obter(1L);
    
    assertNotNull(response);
    verify(mockRepository, times(1)).findById(1L);
}
```

### Stub - Retorno pré-definido

```java
@Test
void deveUsarStub() {
    when(emailService.enviar(any()))
        .thenReturn(true); // Stub
    
    // Usar service
}
```

### Spy - Espiar objeto real

```java
@Test
void deveUsarSpy() {
    ProdutoService spyService = spy(new ProdutoService());
    
    spyService.criar(request);
    
    verify(spyService).criar(request);
}
```

## ✅ Boas Práticas

1. **Independência**: Testes não dependem uns dos outros
2. **Isolamento**: Mock dependências externas
3. **Rapidez**: Testes rápidos (unitários)
4. **Legibilidade**: Nome descritivo e AAA
5. **Cobertura**: Objetivo 80%+
6. **Assertions**: Uma por teste (ou relacionadas)
7. **Setup/Teardown**: Use @Before/@After
8. **Data de Teste**: Fixture ou builder
9. **Sem Hard Code**: Use constantes
10. **Documentação**: Comentários quando necessário

## 🚀 Executar Testes

```bash
# Todos os testes
./mvnw test

# Teste específico
./mvnw test -Dtest=ProdutoServiceTest

# Método específico
./mvnw test -Dtest=ProdutoServiceTest#deveCriarProduto

# Com falhas paradas
./mvnw test -X -DfailIfNoTests=false

# Pular testes
./mvnw clean install -DskipTests
```

## 📚 Referências Relacionadas

- [JUnit 5 Documentation](https://junit.org/junit5/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [TestContainers](https://www.testcontainers.org/)

---

**Última atualização:** Dezembro de 2025
