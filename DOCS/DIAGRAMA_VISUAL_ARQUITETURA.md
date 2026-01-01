# 🎯 Arquitetura de Refatoração - Diagrama Visual

## 📊 Fluxo de Requisição - Antes vs Depois

### ANTES ❌

```
┌─────────────────────────────────────────────────────────────┐
│                       HTTP Request                           │
│                  POST /api/v1/contatos                       │
│              { tipo: "EMAIL", valor: "..." }                │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│                    ContatoController                         │
│  criar(CreateContatoRequest request) {                       │
│    ContatoResponse response =                                │
│      contatoService.criar(request);  ← Já recebe DTO!       │
│    return response;                                          │
│  }                                                           │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│                   ContatoService                             │
│  criar(CreateContatoRequest request)                         │
│    → ContatoResponse {                                       │
│    Contato contato = ...;  ← cria entidade                 │
│    return converterParaResponse(contato);                    │
│         ↓                                                    │
│    CONVERSÃO AQUI! ← Acoplamento                           │
│    return new ContatoResponse(                              │
│      contato.getId(),      id,                              │
│      contato.getTipo().name(),  tipo (ENUM),               │
│      contato.getValor(),    valor,                          │
│      contato.getDescricao(), descricao,                     │
│      contato.isPrincipal(), principal,                      │
│      contato.isAtivo(),     ativo,                          │
│      contato.getDataCriacao(), dataCriacao,                │
│      contato.getDataAtualizacao() dataAtualizacao           │
│    );                                                        │
│  }                                                           │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│                   HTTP Response 201                          │
│          { id: 1, tipo: "EMAIL", valor: "..." }            │
└─────────────────────────────────────────────────────────────┘

⚠️ PROBLEMA: Service retorna DTO → Acoplamento HTTP
```

---

### DEPOIS ✅

```
┌─────────────────────────────────────────────────────────────┐
│                       HTTP Request                           │
│                  POST /api/v1/contatos                       │
│              { tipo: "EMAIL", valor: "..." }                │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│                    ContatoController                         │
│  criar(CreateContatoRequest request) {                       │
│    var contato =                                             │
│      contatoService.criar(request);  ← Entidade             │
│    return                                                    │
│      contatoMapper.toResponse(contato);  ← Mapper converte   │
│  }                                                           │
└─────────────────────────────────────────────────────────────┘
                    ↓              ↓
        ┌───────────┴──────┬───────┘
        ↓                  ↓
┌──────────────────┐  ┌──────────────────────────┐
│  ContatoService  │  │   ContatoMapper          │
│  criar(...){     │  │   toResponse(contato){   │
│   Contato c=...; │  │     return new           │
│   return        │  │       ContatoResponse(   │
│     contato;     │  │   contato.getId(),       │
│  } ← APENAS      │  │   contato.getTipo()...  │
│    ENTIDADE      │  │   );                     │
│                  │  │   } ← CONVERSÃO AQUI    │
│  (Desacoplado)   │  │   (Centralizado)        │
└──────────────────┘  └──────────────────────────┘
        ↓                  ↓
        └───────────┬──────┘
                    ↓
┌─────────────────────────────────────────────────────────────┐
│                   HTTP Response 201                          │
│          { id: 1, tipo: "EMAIL", valor: "..." }            │
└─────────────────────────────────────────────────────────────┘

✅ BENEFÍCIO: Service retorna Entidade → Desacoplado
            Mapper centraliza conversão → Reutilizável
```

---

## 🏗️ Arquitetura em Camadas

### Estrutura Lógica

```
┌─────────────────────────────────────────────────────────────┐
│          Presentation Layer (REST API HTTP)                  │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │                ContatoController                      │   │
│  │  - Recebe Request HTTP                              │   │
│  │  - Chama Service (entidade)                          │   │
│  │  - Chama Mapper (DTO)                                │   │
│  │  - Retorna Response HTTP                             │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│          Application Layer (Lógica de Negócio)              │
│                                                              │
│  ┌────────────────────────────────────────────────────┐     │
│  │              ContatoService                        │     │
│  │  - Implementa lógica de negócio pura             │     │
│  │  - Retorna apenas ENTIDADES (Contato)            │     │
│  │  - Agnóstico a HTTP/Apresentação                 │     │
│  │  - Pode ser reusado em CLI, Batch, Eventos, etc  │     │
│  └────────────────────────────────────────────────────┘     │
│                                                              │
│  ┌────────────────────────────────────────────────────┐     │
│  │               ContatoMapper (NOVO)                │     │
│  │  - Converte Entidade → DTO                       │     │
│  │  - Centraliza toda transformação                  │     │
│  │  - Reutilizável em qualquer contexto            │     │
│  │  - Gerado por MapStruct em tempo de compilação  │     │
│  └────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│          Domain Layer (Lógica Pura de Domínio)              │
│                                                              │
│  ┌────────────────────────────────────────────────────┐     │
│  │               Contato (Entidade)                   │     │
│  │  - Representa conceito de domínio puro           │     │
│  │  - SEM conhecimento de HTTP, DTO ou Apresentação │     │
│  │  - Rules de negócio encapsuladas                 │     │
│  │  - Agnóstico a tecnologia                        │     │
│  └────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│       Infrastructure Layer (Data Access, Persistence)        │
│                                                              │
│  ┌────────────────────────────────────────────────────┐     │
│  │           ContatoRepository                        │     │
│  │  - Persistência de Contato                        │     │
│  │  - Queries SQL/DB                                 │     │
│  └────────────────────────────────────────────────────┘     │
│                                                              │
│  ┌────────────────────────────────────────────────────┐     │
│  │               Database (PostgreSQL)               │     │
│  │  - Tabela: contatos                              │     │
│  │  - Dados persistidos                             │     │
│  └────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔀 Fluxo de Dados Detalhado

### Endpoint: POST /api/v1/contatos

```
1. HTTP Request chega
   ├─ Método: POST
   ├─ URL: /api/v1/contatos
   ├─ Body: { "tipo": "EMAIL", "valor": "user@example.com" }
   └─ Headers: Content-Type: application/json

2. ContatoController.criar(@RequestBody CreateContatoRequest)
   ├─ Valida request via @Valid (automático)
   ├─ Chama: contatoService.criar(request)
   └─ Recebe: Contato (entidade)

3. ContatoService.criar(CreateContatoRequest)
   ├─ Valida dados do domínio
   ├─ Cria entidade: new Contato(tipo, valor)
   ├─ Persiste: contatoRepository.save(contato)
   └─ Retorna: Contato (entidade pura)

4. ContatoMapper.toResponse(Contato)
   ├─ Recebe: Contato entidade
   ├─ Mapeia cada campo:
   │  ├─ contato.getId()       → ContatoResponse.id
   │  ├─ contato.getTipo()     → ContatoResponse.tipo
   │  ├─ contato.getValor()    → ContatoResponse.valor
   │  ├─ contato.getDescricao()→ ContatoResponse.descricao
   │  ├─ contato.isPrincipal() → ContatoResponse.principal
   │  ├─ contato.isAtivo()     → ContatoResponse.ativo
   │  ├─ contato.getDataCriacao()→ ContatoResponse.dataCriacao
   │  └─ contato.getDataAtualizacao()→ ContatoResponse.dataAtualizacao
   └─ Retorna: ContatoResponse (DTO)

5. ContatoController retorna ResponseEntity
   ├─ Status: 201 CREATED
   ├─ Body: { "id": 1, "tipo": "EMAIL", "valor": "user@example.com", ... }
   └─ Headers: Location: /api/v1/contatos/1

6. HTTP Response é enviado ao cliente
   ├─ Status Code: 201
   ├─ Body: JSON serializado
   └─ Client recebe resposta validada
```

---

## 🔄 Ciclo de Vida de um Contato

```
┌──────────────────┐
│  CreateRequest   │
│ {tipo, valor}    │
└────────┬─────────┘
         │ Controller valida
         ↓
┌──────────────────────┐
│    Contato           │  ← Entidade de Domínio
│  (no Service)        │  ├─ id
│                      │  ├─ tipo (ENUM)
│                      │  ├─ valor (String)
│                      │  ├─ descricao
│                      │  ├─ principal (boolean)
│                      │  ├─ ativo (boolean)
│                      │  ├─ dataCriacao
│                      │  └─ dataAtualizacao
└────────┬─────────────┘
         │ Repository.save()
         ↓
┌──────────────────────┐
│  Database Row        │
│  (Persistido)        │
└────────┬─────────────┘
         │ Repository.findById()
         ↓
┌──────────────────────┐
│    Contato           │  ← Entidade reconstituída
│  (do Database)       │  (com ID do banco)
└────────┬─────────────┘
         │ Mapper.toResponse()
         ↓
┌──────────────────────┐
│  ContatoResponse     │  ← DTO para HTTP
│  (JSON/DTO)          │  └─ Apenas para presentation
└────────┬─────────────┘
         │ ResponseEntity
         ↓
┌──────────────────────┐
│  HTTP Response       │
│  JSON Serializado    │
└──────────────────────┘
```

---

## 📦 Responsabilidades por Camada

### Controller (Presentation)
```
✅ Receber HTTP Request
✅ Validar entrada (@Valid)
✅ Chamar Service
✅ Chamar Mapper
✅ Retornar HTTP Response
✅ Tratar exceções HTTP

❌ Lógica de negócio
❌ Conversão manual
❌ Persistência
❌ Conhecer detalhes internos
```

### Service (Application)
```
✅ Implementar lógica de negócio
✅ Validar regras de domínio
✅ Orquestrar repository
✅ Retornar entidades
✅ Ser agnóstico a HTTP

❌ Converter para DTO
❌ Conhecer sobre HTTP
❌ Acesso direto a Database
❌ Lógica de apresentação
```

### Mapper (Application)
```
✅ Converter Entidade → DTO
✅ Mapear todos os campos
✅ Ser reutilizável
✅ Usar MapStruct
✅ Centralizar transformação

❌ Lógica de negócio
❌ Persistência
❌ Validação
❌ Side effects
```

### Domain (Domínio)
```
✅ Representar conceitos puro
✅ Encapsular regras de negócio
✅ Ser agnóstico a tecnologia
✅ Ter identidade clara
✅ Validar estado interno

❌ Conhecer DTO
❌ Conhecer HTTP
❌ Persistência direta
❌ Anotações de framework
```

### Repository (Infrastructure)
```
✅ Persistir entidades
✅ Recuperar do banco
✅ Queries SQL
✅ Transações
✅ Mapeamento ORM

❌ Lógica de negócio
❌ Conversão para DTO
❌ Validação
```

---

## 🎯 Benefícios Visuais

### Antes: Acoplamento ❌

```
┌─────────────────────┐
│   HTTP/JSON         │
│  (Apresentação)     │
└──────────┬──────────┘
           │
           ↓
┌─────────────────────┐
│    ContatoResponse  │ ← DTO
│    (Response DTO)   │
└──────────┬──────────┘
           │ Acoplado!
           ↓
┌─────────────────────┐
│  ContatoService     │ ← Service conhece DTO!
│  (Lógica Negócio)   │  Acoplado a Apresentação
└──────────┬──────────┘
           │
           ↓
┌─────────────────────┐
│    Contato          │
│   (Entidade)        │
└─────────────────────┘

⚠️ PROBLEMA: Service está acoplado a DTO
            Service não pode ser reusado
            Conversão espalhada
```

### Depois: Desacoplamento ✅

```
┌─────────────────────┐
│   HTTP/JSON         │
│  (Apresentação)     │
└──────────┬──────────┘
           │
           ↓
┌─────────────────────┐
│  ContatoMapper      │ ← Mapper (novo)
│  (Conversão)        │  Responsável por DTO
└──────────┬──────────┘
           │
           ↓
┌─────────────────────┐
│  ContatoService     │ ← Service puro
│  (Lógica Negócio)   │  Agnóstico a HTTP
└──────────┬──────────┘
           │
           ↓
┌─────────────────────┐
│    Contato          │
│   (Entidade)        │
└─────────────────────┘

✅ BENEFÍCIO: Service desacoplado
             Service reutilizável
             Conversão centralizada
```

---

## 📈 Estatísticas de Código

### Antes

```
ContatoService.java      264 linhas
  ├─ criar()             20 linhas
  ├─ buscarPorId()       10 linhas
  ├─ buscarTodos()       8 linhas
  ├─ ... outros métodos
  └─ converterParaResponse() ← 15 linhas (MÉTODO PRIVADO)

ContatoController.java   378 linhas (apenas chama service)

Total: 642 linhas
Conversão de DTO: Espalhada em múltiplos lugares
Acoplamento: Alto (Service conhece DTO)
```

### Depois

```
ContatoService.java      214 linhas (-50 linhas)
  ├─ criar()             15 linhas (-5)
  ├─ buscarPorId()       5 linhas (-5)
  ├─ buscarTodos()       3 linhas (-5)
  ├─ ... outros métodos
  └─ ❌ converterParaResponse() REMOVIDO!

ContatoController.java   ~400 linhas (com mapper)
  ├─ Mesmo tamanho mas mais claro
  ├─ Responsabilidade bem definida
  └─ Fácil de testar

ContatoMapper.java       ~40 linhas (NOVO)
  ├─ toResponse()        1 linha (gerado)
  ├─ toResponseList()    1 linha (gerado)
  └─ toResponseSet()     1 linha (gerado)

Total: 654 linhas (-50 linhas de código redundante)
Conversão de DTO: Centralizada em Mapper
Acoplamento: Zero (Service ignora DTO)
```

---

## 🚀 Escalabilidade

```
Feature Contato (PRONTO):
├─ ContatoMapper ✅
├─ ContatoService ✅
└─ ContatoController ✅

Feature Usuario (PRÓXIMA):
├─ UsuarioMapper ← Mesmo padrão
├─ UsuarioService ← Mesmo padrão
└─ UsuarioController ← Mesmo padrão

Feature Empresa:
├─ EmpresaMapper ← Copie padrão
├─ EmpresaService ← Copie padrão
└─ EmpresaController ← Copie padrão

... e assim por diante para todas as features
```

---

**Refatoração Completa e Pronta para Escalar! 🎉**
