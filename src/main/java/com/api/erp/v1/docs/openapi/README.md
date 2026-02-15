# Documentação OpenAPI - Estrutura Reorganizada

## Visão Geral

As anotações de documentação Swagger (OpenAPI) foram movidas de interfaces de controller para interfaces de documentação dedicadas, resultando em uma separação clara entre lógica de negócio e documentação.

## Estrutura de Pastas

```
src/main/java/com/api/erp/v1/docs/openapi/
├── tenant/
│   ├── TenantOpenApiDocumentation.java
│   └── TenantDatabaseOpenApiDocumentation.java
├── features/
│   ├── cliente/
│   │   └── ClienteOpenApiDocumentation.java
│   ├── usuario/
│   │   └── UsuarioOpenApiDocumentation.java
│   ├── permissao/
│   │   ├── PermissaoOpenApiDocumentation.java
│   │   └── RoleOpenApiDocumentation.java
│   ├── produto/
│   │   ├── ProdutoOpenApiDocumentation.java
│   │   ├── ComposicaoOpenApiDocumentation.java
│   │   └── ListaExpandidaOpenApiDocumentation.java
│   ├── unidademedida/
│   │   └── UnidadeMedidaOpenApiDocumentation.java
│   ├── endereco/
│   │   └── EnderecoOpenApiDocumentation.java
│   ├── contato/
│   │   ├── ContatosOpenApiDocumentation.java
│   │   └── ContatosUsuarioOpenApiDocumentation.java
│   └── customfield/
│       └── CustomFieldDefinitionOpenApiDocumentation.java
```

## Como Funciona

### Interfaces de Controller Original (Limpas)

As interfaces originais agora contêm apenas as assinaturas dos métodos, sem anotações Swagger:

```java
// Arquivo: ITenantController.java
public interface ITenantController {
    ResponseEntity<TenantResponse> obter();
    ResponseEntity<TenantResponse> criar(@RequestBody CriarTenantRequest request);
    // ... outros métodos
}
```

### Interfaces de Documentação (Com Anotações)

As interfaces de documentação herdam das interfaces originais e adicionam todas as anotações Swagger:

```java
// Arquivo: TenantOpenApiDocumentation.java
@Tag(
    name = "Tenant",
    description = "Endpoints responsáveis pela gestão e configurações da tenant (multi-tenant)"
)
public interface TenantOpenApiDocumentation extends ITenantController {

    @Override
    @Operation(
        summary = "Obter dados da tenant",
        description = "Retorna os dados cadastrais da tenant vinculada ao tenant do usuário autenticado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dados da tenant retornados com sucesso"),
        // ... mais responses
    })
    ResponseEntity<TenantResponse> obter();
    
    // ... outros métodos com anotações
}
```

### Controllers Concretos

Os controllers concretos implementam as interfaces de controller original:

```java
// Arquivo: TenantController.java
@RestController
@RequestMapping("/api/v1/tenant")
public class TenantController implements ITenantController {
    // implementação dos métodos
}
```

## Benefícios

1. **Separação de Responsabilidades**: Lógica de negócio separada da documentação
2. **Interfaces Limpas**: Interfaces de controller sem poluição de anotações
3. **Fácil Manutenção**: Documentação centralizada e organizada
4. **Escalabilidade**: Fácil adicionar novas interfaces de documentação
5. **Reutilização**: As interfaces de controller podem ser usadas em múltiplos contextos

## Uso

Para gerar a documentação Swagger/OpenAPI:

1. O Spring Boot/Springdoc-OpenAPI irá automaticamente detectar as anotações nas interfaces de documentação
2. Um arquivo OpenAPI (swagger.json ou OpenAPI 3.0) será gerado com base nas anotações encontradas
3. O Swagger UI estará disponível em: `http://localhost:8080/swagger-ui.html`

## Adicionando Nova Documentação

Quando criar um novo controller:

1. Crie a interface de controller sem anotações (ex: `INovoController.java`)
2. Crie a interface de documentação que estenda a interface (ex: `NovoOpenApiDocumentation.java`)
3. Adicione todas as anotações Swagger @Tag, @Operation, @ApiResponse, etc. na interface de documentação
4. Implemente a interface de controller no controller concreto
5. Coloque a interface de documentação em `docs/openapi/features/novofeature/`
