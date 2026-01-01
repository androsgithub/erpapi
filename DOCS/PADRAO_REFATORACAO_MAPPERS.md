# Padrão de Refatoração com Mappers - ERP API

## 📋 Visão Geral

Este documento descreve o padrão arquitetural para refatoração de Controllers e mapeamento de DTOs no ERP API, utilizando **MapStruct** para conversão automática entre entidades de domínio e DTOs de resposta.

---

## 🎯 Objetivos

✅ Remover completamente métodos `toResponse` dos controllers
✅ Criar uma camada dedicada de Mapper
✅ Utilizar MapStruct para gerar code em tempo de compilação
✅ Manter controllers enxutos (apenas orquestração)
✅ Não acoplar o domínio a DTOs
✅ Garantir código limpo, testável e escalável

---

## 🏗️ Arquitetura

### Fluxo de Dados

```
Request HTTP
    ↓
Controller (orquestra)
    ↓
Service (retorna entidades)
    ↓
Mapper (converte para DTO)
    ↓
Response HTTP
```

### Responsabilidades por Camada

| Camada | Responsabilidade |
|--------|------------------|
| **Controller** | Orquestra chamadas ao service e mapper. Apenas recebe e valida requests, chama service e retorna resposta via mapper |
| **Service** | Implementa lógica de negócio. Retorna **apenas entidades de domínio** |
| **Mapper** | Converte entidades → DTOs. Centraliza toda lógica de transformação |
| **Domain** | Desconhecido de DTOs. Puro e agnóstico a representação HTTP |

---

## 📦 Estrutura de Pacotes

```
features/
├── contato/
│   ├── application/
│   │   ├── mapper/                    ← NOVO: Mappers com MapStruct
│   │   │   ├── ContatoMapper.java
│   │   │   └── UsuarioContatoMapper.java
│   │   ├── service/
│   │   │   ├── IContatoService.java   ← REFATORADO
│   │   │   └── ContatoService.java    ← REFATORADO
│   │   └── dto/
│   │       ├── ContatoResponse.java
│   │       └── response/
│   ├── domain/
│   │   ├── entity/
│   │   └── repository/
│   └── presentation/
│       └── controller/
│           └── ContatoController.java ← REFATORADO
```

---

## 🔧 Implementação

### 1️⃣ Adicionar Dependência MapStruct

**pom.xml:**
```xml
<!-- MapStruct -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.6.0</version>
</dependency>

<!-- Annotation Processor -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.6.0</version>
    <scope>provided</scope>
</dependency>
```

**Atualizar Maven Compiler Plugin:**
```xml
<annotationProcessorPaths>
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </path>
    <path>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-configuration-processor</artifactId>
    </path>
    <path>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>1.6.0</version>
    </path>
</annotationProcessorPaths>
```

### 2️⃣ Criar Interfaces de Mapper

**ContatoMapper.java:**
```java
@Mapper(componentModel = "spring")
public interface ContatoMapper {
    
    ContatoResponse toResponse(Contato contato);
    
    List<ContatoResponse> toResponseList(List<Contato> contatos);
    
    Set<ContatoResponse> toResponseSet(Set<Contato> contatos);
}
```

**UsuarioContatoMapper.java:**
```java
@Mapper(componentModel = "spring", uses = ContatoMapper.class)
public interface UsuarioContatoMapper {
    
    @Mapping(target = "usuarioContatoId", source = "id")
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "contatos", source = "contatos")
    UsuarioContatosResponse toResponse(UsuarioContato usuarioContato);
    
    List<UsuarioContatosResponse> toResponseList(List<UsuarioContato> usuarioContatos);
}
```

**Anotações importantes:**
- `@Mapper(componentModel = "spring")`: Gera bean Spring automaticamente
- `uses = ContatoMapper.class`: Injeta dependência de outro mapper
- `@Mapping`: Mapeia campos com nomes diferentes

### 3️⃣ Refatorar Service para Retornar Entidades

**Antes (❌ Acoplado):**
```java
public ContatoResponse criar(CreateContatoRequest request) {
    Contato contato = ... // lógica
    return converterParaResponse(contato); // DTO aqui!
}

private ContatoResponse converterParaResponse(Contato contato) {
    return new ContatoResponse(...);
}
```

**Depois (✅ Limpo):**
```java
public Contato criar(CreateContatoRequest request) {
    Contato contato = ... // lógica
    return contatoRepository.save(contato); // apenas entidade
}
```

**Interface IContatoService - ANTES:**
```java
List<ContatoResponse> buscarTodos();
ContatoResponse criar(CreateContatoRequest request);
```

**Interface IContatoService - DEPOIS:**
```java
List<Contato> buscarTodos();
Contato criar(CreateContatoRequest request);
```

### 4️⃣ Refatorar Controller

**Antes (❌ Conversão no Controller):**
```java
@PostMapping
public ResponseEntity<ContatoResponse> criar(@RequestBody CreateContatoRequest request) {
    ContatoResponse response = contatoService.criar(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

**Depois (✅ Orquestração clara):**
```java
@RestController
@RequiredArgsConstructor  // Injeção via constructor
public class ContatoController {
    
    private final ContatoMapper contatoMapper;
    private IContatoService contatoService;
    
    @PostMapping
    public ResponseEntity<ContatoResponse> criar(@RequestBody CreateContatoRequest request) {
        var contato = contatoService.criar(request);  // Entidade
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(contatoMapper.toResponse(contato)); // Mapper converte
    }
    
    @GetMapping
    public ResponseEntity<List<ContatoResponse>> listar() {
        var contatos = contatoService.buscarTodos(); // Lista de entidades
        return ResponseEntity.ok(contatoMapper.toResponseList(contatos)); // Mapper converte
    }
}
```

---

## 📐 Padrões de Mapeamento

### Mapeamento Simples

```java
@Mapper(componentModel = "spring")
public interface ContatoMapper {
    
    // Tipos compatíveis → automático
    ContatoResponse toResponse(Contato contato);
}
```

**Gerado automaticamente pelo MapStruct:**
```java
contato.getId()        → ContatoResponse.id
contato.getTipo().name() → ContatoResponse.tipo
contato.getValor()    → ContatoResponse.valor
```

### Mapeamento com Nomes Diferentes

```java
@Mapper(componentModel = "spring")
public interface UsuarioContatoMapper {
    
    @Mapping(target = "usuarioContatoId", source = "id")
    @Mapping(target = "usuarioId", source = "usuario.id")
    UsuarioContatosResponse toResponse(UsuarioContato usuarioContato);
}
```

### Mapeamento com Mappers Compostos

```java
@Mapper(componentModel = "spring", uses = ContatoMapper.class)
public interface UsuarioContatoMapper {
    
    @Mapping(target = "contatos", source = "contatos")
    UsuarioContatosResponse toResponse(UsuarioContato usuarioContato);
    // MapStruct usa ContatoMapper para converter Set<Contato> → Set<ContatoResponse>
}
```

---

## 🎨 Regras Obrigatórias

### ✅ DO (Faça)

1. **Controller:**
   - ✅ Injeta mappers via constructor
   - ✅ Chama service e recebe entidades
   - ✅ Passa entidade ao mapper
   - ✅ Retorna DTO mapeado

2. **Service:**
   - ✅ Retorna apenas entidades de domínio
   - ✅ Desconhecido de DTOs
   - ✅ Implementa lógica de negócio pura

3. **Mapper:**
   - ✅ Fica em `application.mapper`
   - ✅ Usa `@Mapper(componentModel = "spring")`
   - ✅ Usa `@Mapping` para casos complexos
   - ✅ Sem lógica de negócio, apenas conversão

### ❌ DON'T (Não Faça)

1. **Controller:**
   - ❌ Lógica de negócio
   - ❌ Conversão manual entity → DTO
   - ❌ Métodos `toResponse` privados

2. **Service:**
   - ❌ Retornar DTOs
   - ❌ Conhecer sobre representação HTTP
   - ❌ Importar classes de response

3. **Domain:**
   - ❌ Importar classes de DTO
   - ❌ Anotações HTTP/Jackson no domínio

---

## 🔄 Fluxo Completo de Implementação

### Para cada feature do ERP:

1. **Criar Mapper em `application/mapper/`**
   ```java
   @Mapper(componentModel = "spring")
   public interface XyzMapper { ... }
   ```

2. **Refatorar Service**
   - Remover todas as importações de Response
   - Retornar apenas entidades
   - Atualizar interface

3. **Refatorar Controller**
   - Injetar Mapper
   - Remover métodos `toResponse`
   - Usar `mapper.toResponse()` nos endpoints

4. **Testar**
   - Verificar se compilação OK
   - Testar endpoints
   - Validar respostas

---

## 🧪 Exemplo Completo - Feature Contato

### Antes (Antigo):
```
ContatoService.java (retorna ContatoResponse)
ContatoController.java (cria DTO, chama service)
```

### Depois (Refatorado):
```
ContatoService.java (retorna Contato)
ContatoMapper.java (Contato → ContatoResponse)
ContatoController.java (orquestra service + mapper)
```

---

## 📊 Benefícios da Refatoração

| Antes | Depois |
|-------|--------|
| Lógica de conversão espalhada | Centralizada em Mapper |
| Services retornam DTOs | Services retornam entidades |
| Controllers com métodos `toResponse` | Controllers enxutos |
| Baixa reutilização | Mappers reutilizáveis em qualquer camada |
| Difícil de testar | Fácil de mockar mapper em testes |
| Acoplamento alto | Desacoplamento: domain ⊥ DTOs |
| Code repetido | Code gerado por MapStruct |

---

## 🚀 Próximos Passos

1. **Aplicar para todas as features:**
   - usuario
   - empresa
   - endereco
   - permissao
   - produto
   - unidademedida

2. **Considerar:**
   - Mapper base abstrato para métodos comuns
   - Config centralizada de MapStruct
   - Stratégias para conversões complexas

3. **Testes:**
   - Testar mappers isoladamente
   - Validar respostas HTTP
   - Mockar mappers em testes de controller

---

## 📚 Referências

- [MapStruct Official Documentation](https://mapstruct.org/)
- [DDD: Separar Domain de DTOs](https://www.baeldung.com/java-dto-pattern)
- [Clean Architecture: Controller Thin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

---

**Versão:** 1.0  
**Atualizado:** Dezembro 2025  
**Aplicado em:** Feature Contato como exemplo
