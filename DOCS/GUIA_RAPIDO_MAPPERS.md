# 🚀 Quick Start - Aplicando o Padrão de Mappers em Outras Features

Este é um checklist rápido para aplicar o padrão de refatoração em novas features do ERP.

---

## 📋 Pré-requisitos

- ✅ MapStruct já adicionado no pom.xml
- ✅ Feature possui: Controller → Service → Domain
- ✅ Feature possui DTOs de response
- ✅ Lida com entidades de domínio

---

## 🔧 Passo a Passo

### 1️⃣ Criar Diretório de Mappers

```bash
mkdir -p src/main/java/com/api/erp/v1/features/{FEATURE}/application/mapper
```

**Substitua `{FEATURE}` por:** usuario, empresa, endereco, etc.

---

### 2️⃣ Criar Mappers (Copie o Template)

**XyzMapper.java** (onde XYZ é sua entidade)

```java
package com.api.erp.v1.features.{feature}.application.mapper;

import com.api.erp.v1.features.{feature}.application.dto.XyzResponse;
import com.api.erp.v1.features.{feature}.domain.entity.Xyz;
import org.mapstruct.Mapper;

/**
 * Mapper para conversão da entidade Xyz para XyzResponse
 * Utiliza MapStruct para gerar implementação em tempo de compilação
 */
@Mapper(componentModel = "spring")
public interface XyzMapper {

    /**
     * Converte uma entidade Xyz para XyzResponse
     */
    XyzResponse toResponse(Xyz xyz);

    /**
     * Converte uma coleção de Xyz para XyzResponse
     */
    java.util.List<XyzResponse> toResponseList(java.util.List<Xyz> xyzs);

    /**
     * Converte um conjunto de Xyz para XyzResponse
     */
    java.util.Set<XyzResponse> toResponseSet(java.util.Set<Xyz> xyzs);
}
```

---

### 3️⃣ Refatorar Service

#### A. Atualizar Imports

**Remover:**
```java
import com.api.erp.v1.features.{feature}.application.dto.XyzResponse;
```

**Manter:**
```java
import com.api.erp.v1.features.{feature}.domain.entity.Xyz;
```

#### B. Refatorar Métodos

**Antes:**
```java
public XyzResponse criar(CreateXyzRequest request) {
    Xyz xyz = ... // lógica
    return converterParaResponse(xyz);
}

private XyzResponse converterParaResponse(Xyz xyz) {
    return new XyzResponse(...);
}
```

**Depois:**
```java
public Xyz criar(CreateXyzRequest request) {
    Xyz xyz = ... // lógica
    return xyz;  // Apenas entidade
}

// REMOVA converterParaResponse()
```

#### C. Atualizar Interface de Service

**IXyzService.java - Antes:**
```java
public interface IXyzService {
    XyzResponse criar(CreateXyzRequest request);
    XyzResponse buscarPorId(Long id);
    List<XyzResponse> buscarTodos();
}
```

**IXyzService.java - Depois:**
```java
public interface IXyzService {
    Xyz criar(CreateXyzRequest request);
    Xyz buscarPorId(Long id);
    List<Xyz> buscarTodos();
}
```

**Adicione documentação:**
```java
/**
 * Interface para o serviço de Xyz
 * 
 * IMPORTANTE: Retorna apenas entidades de domínio (Xyz)
 * A conversão para DTOs de response é responsabilidade do controller/mapper
 */
```

---

### 4️⃣ Refatorar Controller

#### A. Adicionar Injeção de Mapper

**Antes:**
```java
@RestController
@RequiredArgsConstructor
public class XyzController {
    private final XyzServiceFactory serviceFactory;
    // ...
}
```

**Depois:**
```java
@RestController
@RequiredArgsConstructor
public class XyzController {
    private final XyzServiceFactory serviceFactory;
    private final XyzMapper xyzMapper;  // ← NOVO
    // ...
}
```

#### B. Refatorar Cada Endpoint

**Padrão para métodos que retornam UM objeto:**

```java
@GetMapping("/{id}")
public ResponseEntity<XyzResponse> buscar(@PathVariable Long id) {
    var xyz = xyzService.buscarPorId(id);              // Entidade
    return ResponseEntity.ok(xyzMapper.toResponse(xyz)); // Mapper
}
```

**Padrão para métodos que retornam LISTA:**

```java
@GetMapping
public ResponseEntity<List<XyzResponse>> listar() {
    var xyzs = xyzService.buscarTodos();                   // Lista de entidades
    return ResponseEntity.ok(xyzMapper.toResponseList(xyzs)); // Mapper
}
```

**Padrão para métodos que retornam CONJUNTO (Set):**

```java
@GetMapping("/ativos")
public ResponseEntity<Set<XyzResponse>> listarAtivos() {
    var xyzs = xyzService.buscarAtivos();                  // Set de entidades
    return ResponseEntity.ok(xyzMapper.toResponseSet(xyzs)); // Mapper
}
```

#### C. Remover Métodos privados `toResponse`

Se existem métodos privados como:
```java
private XyzResponse toResponse(Xyz xyz) {
    return new XyzResponse(...);
}
```

**REMOVA-OS!** Agora o mapper faz isso.

---

### 5️⃣ Validar Compilação

```bash
mvn clean install
```

✅ Se compilar sem erros, sucesso!  
❌ Se houver erros, verifique:
- Imports corretos no service
- Nomes de mappers/classes
- DTOs compatíveis

---

## 📝 Checklist por Feature

### Feature: usuario

- [ ] Criar `UsuarioMapper.java`
- [ ] Atualizar `IUsuarioService.java` (retorna entidades)
- [ ] Refatorar `UsuarioService.java`
- [ ] Refatorar `UsuarioController.java` (injeta mapper)
- [ ] Compilar e testar

### Feature: empresa

- [ ] Criar `EmpresaMapper.java`
- [ ] Atualizar `IEmpresaService.java`
- [ ] Refatorar `EmpresaService.java`
- [ ] Refatorar `EmpresaController.java`
- [ ] Compilar e testar

### Feature: endereco

- [ ] Criar `EnderecoMapper.java`
- [ ] Atualizar `IEnderecoService.java`
- [ ] Refatorar `EnderecoService.java`
- [ ] Refatorar `EnderecoController.java`
- [ ] Compilar e testar

### Feature: permissao

- [ ] Criar `RoleMapper.java`
- [ ] Criar `PermissaoMapper.java`
- [ ] Atualizar interfaces de service
- [ ] Refatorar services
- [ ] Refatorar controllers
- [ ] Compilar e testar

### Feature: produto

- [ ] Criar `ProdutoMapper.java`
- [ ] Atualizar `IProdutoService.java`
- [ ] Refatorar `ProdutoService.java`
- [ ] Refatorar `ProdutoController.java`
- [ ] Compilar e testar

### Feature: unidademedida

- [ ] Criar `UnidadeMedidaMapper.java`
- [ ] Atualizar `IUnidadeMedidaService.java`
- [ ] Refatorar `UnidadeMedidaService.java`
- [ ] Refatorar `UnidadeMedidaController.java`
- [ ] Compilar e testar

---

## 🎨 Casos Especiais

### Quando há múltiplas entidades relacionadas

**Exemplo:** Usuário com Permissões e Roles

```java
@Mapper(componentModel = "spring", uses = {RoleMapper.class, PermissaoMapper.class})
public interface UsuarioMapper {
    
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "permissoes", source = "permissoes")
    UsuarioResponse toResponse(Usuario usuario);
}
```

A anotação `uses = {...}` injeta os mappers das entidades relacionadas.

### Quando há transformação de tipos

**Exemplo:** Entidade com Enum → DTO com String

```java
@Mapper(componentModel = "spring")
public interface XyzMapper {
    
    @Mapping(target = "status", source = "status")
    XyzResponse toResponse(Xyz xyz);
    
    default String statusToString(StatusEnum status) {
        return status.name();
    }
}
```

---

## 🔗 Exemplo Pronto para Copiar

### Feature: usuario

**Crie UsuarioMapper.java:**

```java
package com.api.erp.v1.features.usuario.application.mapper;

import com.api.erp.v1.features.usuario.application.dto.response.UsuarioResponse;
import com.api.erp.v1.features.usuario.domain.entity.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    
    UsuarioResponse toResponse(Usuario usuario);
    
    java.util.List<UsuarioResponse> toResponseList(java.util.List<Usuario> usuarios);
    
    java.util.Set<UsuarioResponse> toResponseSet(java.util.Set<Usuario> usuarios);
}
```

**Refatore UsuarioService:**

```java
public class UsuarioService implements IUsuarioService {
    
    // Remova imports de UsuarioResponse
    // Remova método converterParaResponse()
    
    @Transactional
    public Usuario criar(CreateUsuarioRequest request) {
        Usuario usuario = ... // lógica
        return usuarioRepository.save(usuario);
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
}
```

**Refatore UsuarioController:**

```java
@RestController
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioServiceFactory serviceFactory;
    private final UsuarioMapper usuarioMapper;  // ← NOVO
    
    @PostMapping
    public UsuarioResponse criar(@RequestBody CreateUsuarioRequest request) {
        var usuario = serviceFactory.create().criar(request);
        return usuarioMapper.toResponse(usuario);
    }
    
    @GetMapping
    public List<UsuarioResponse> listar() {
        var usuarios = serviceFactory.create().listarTodos();
        return usuarioMapper.toResponseList(usuarios);
    }
}
```

---

## ✅ Validação Final

Após refatorar cada feature:

```bash
# 1. Compilar
mvn clean compile

# 2. Rodar testes
mvn test

# 3. Build completo
mvn clean install

# 4. Testar endpoint manualmente
curl -X GET http://localhost:8080/api/v1/{feature}

# 5. Verificar resposta JSON
# Deve ter estrutura de DTO correto
```

---

## 📞 Troubleshooting

### Erro: "Cannot find method in generated mapper"

**Solução:**
- Adicione método na interface mapper
- Recompile com `mvn clean compile`

### Erro: "XyzMapper is not a Spring Bean"

**Solução:**
- Verifique `@Mapper(componentModel = "spring")`
- Verifique se classe está em pacote `mapper`
- Recompile

### Erro: "Cannot convert type A to type B"

**Solução:**
- Adicione `@Mapping` customizado
- Ou crie método `default` na interface mapper

---

## 🎯 Ordem Recomendada de Implementação

1. **contato** ✅ (Já feito - use como referência)
2. usuario (Próximo - muitas relações)
3. empresa (Depois)
4. endereco (Simples)
5. permissao (Complexo - múltiplas entidades)
6. produto (Complexo)
7. unidademedida (Simples)

---

## 📚 Documentação Completa

Leia [PADRAO_REFATORACAO_MAPPERS.md](PADRAO_REFATORACAO_MAPPERS.md) para:
- Conceitos detalhados
- Boas práticas
- Exemplos avançados
- Troubleshooting completo

---

**Bom trabalho! 🚀**

Qualquer dúvida, consulte a documentação completa ou use a feature contato como referência.
