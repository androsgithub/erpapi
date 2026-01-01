# 📊 Diagrama Visual - Arquitetura de Decorators de Cliente

## 1. Fluxo de Inicialização

```
┌─────────────────────────────────────────────────────────┐
│                 APPLICATION START                       │
└───────────────────┬─────────────────────────────────────┘
                    │
        ┌───────────┴───────────┐
        ▼                       ▼
   Spring Boot          Registra Beans
   carrega config
        │
        ▼
  ┌──────────────────────────────┐
  │ ClienteServiceConfiguration  │
  │  - Cria Factory              │
  │  - Cria Holder               │
  │  - Cria Proxy (@Primary)     │
  └──────────┬───────────────────┘
             │
             ▼
  ┌──────────────────────────────┐
  │  ProxyInitializer            │
  │  1. factory.create()         │
  │  2. holder.updateService()   │
  └──────────┬───────────────────┘
             │
             ▼
  ┌──────────────────────────────┐
  │  ClienteServiceHolder        │
  │  serviceReference = Chain    │
  │  [✓] Ready                   │
  └──────────────────────────────┘
```

## 2. Fluxo de Request Normal

```
HTTP Request: POST /api/v1/clientes
             │
             ▼
  ┌──────────────────────────────┐
  │  ClienteController           │
  │  Injeta: IClienteService     │
  │  (Recebe ClienteServiceProxy)│
  └──────────┬───────────────────┘
             │ proxy.criar(dto)
             ▼
  ┌──────────────────────────────┐
  │  ClienteServiceProxy         │
  │  public Cliente criar(dto) { │
  │    return holder.getService()│
  │      .criar(dto);            │
  │  }                           │
  └──────────┬───────────────────┘
             │ holder.getService() [READ LOCK]
             ▼
  ┌──────────────────────────────────────────┐
  │  ClienteServiceHolder                    │
  │  readLock.lock()                         │
  │  └─ serviceReference.get()               │
  │     └─ Retorna instância com decorators  │
  │  readLock.unlock()                       │
  └──────────┬───────────────────────────────┘
             │
             ▼
  ┌────────────────────────────────────────────────────┐
  │              DECORATOR CHAIN                       │
  │  ┌──────────────────────────────────────────────┐  │
  │  │  NotificationDecorator.criar(dto)            │  │
  │  │    Log: "Evento: Cliente criado"             │  │
  │  │    │                                         │  │
  │  │    ▼                                         │  │
  │  │  ┌──────────────────────────────────────┐   │  │
  │  │  │ AuditDecorator.criar(dto)            │   │  │
  │  │  │   Log: "Criando cliente..."          │   │  │
  │  │  │   │                                  │   │  │
  │  │  │   ▼                                  │   │  │
  │  │  │ ┌──────────────────────────────────┐ │   │  │
  │  │  │ │ CacheDecorator.criar(dto)        │ │   │  │
  │  │  │ │   (Não usa cache para write)     │ │   │  │
  │  │  │ │   │                              │ │   │  │
  │  │  │ │   ▼                              │ │   │  │
  │  │  │ │ ┌──────────────────────────────┐ │ │   │  │
  │  │  │ │ │ ValidationDecorator.criar()  │ │ │   │  │
  │  │  │ │ │   Valida DTO                 │ │ │   │  │
  │  │  │ │ │   ✓ Nome não vazio           │ │ │   │  │
  │  │  │ │ │   ✓ CNPJ válido              │ │ │   │  │
  │  │  │ │ │   │                          │ │ │   │  │
  │  │  │ │ │   ▼                          │ │ │   │  │
  │  │  │ │ │ ┌────────────────────────┐   │ │ │   │  │
  │  │  │ │ │ │ ClienteService.criar() │   │ │ │   │  │
  │  │  │ │ │ │ (Core Service)         │   │ │ │   │  │
  │  │  │ │ │ │ 1. repository.save()   │   │ │ │   │  │
  │  │  │ │ │ │ 2. Return novo cliente │   │ │ │   │  │
  │  │  │ │ │ └──────┬────────────────┘   │ │ │   │  │
  │  │  │ │ │        │ return              │ │ │   │  │
  │  │  │ │ │        ▼                     │ │ │   │  │
  │  │  │ │ │ CacheDecorator cache.put()  │ │ │   │  │
  │  │  │ │ │ return cliente               │ │ │   │  │
  │  │  │ │ └──────────┬────────────────── │ │ │   │  │
  │  │  │ │            │ return            │ │ │   │  │
  │  │  │ │            ▼                   │ │ │   │  │
  │  │  │ AuditDecorator log success       │ │ │   │  │
  │  │  │ return cliente                   │ │ │   │  │
  │  │  └──────────────┬────────────────────│ │ │   │  │
  │  │                 │ return            │ │ │   │  │
  │  │                 ▼                   │ │ │   │  │
  │  NotificationDecorator publish event   │ │ │   │  │
  │  return cliente                        │ │ │   │  │
  │  └─────────────────┬────────────────────│─│─┘   │  │
  │                    │ return            │ │     │  │
  │                    ▼                   ▼ ▼     │  │
  └────────────────────────────────────────────────┘
             │
             ▼
  ┌──────────────────────────────┐
  │  ClienteController           │
  │  return mapper.toResponse()  │
  └──────────┬───────────────────┘
             │
             ▼
  ┌──────────────────────────────┐
  │  HTTP Response: 201 Created  │
  │  Body: ClienteResponse       │
  └──────────────────────────────┘
```

## 3. Fluxo de Atualização de Configuração

```
HTTP Request: PUT /api/v1/empresas/config/cliente
             │
             ▼
  ┌──────────────────────────────┐
  │  EmpresaController           │
  │  updateClienteConfig(config) │
  └──────────┬───────────────────┘
             │
             ▼
  ┌──────────────────────────────────────┐
  │  EmpresaService                      │
  │  1. empresaRepository.save()         │
  │  2. eventPublisher.publishEvent()    │
  │     └─ ClienteConfigUpdateEvent     │
  └──────────┬──────────────────────────┘
             │
             ▼
  ┌──────────────────────────────────────┐
  │  ClienteConfigUpdateListener         │
  │  @EventListener                      │
  │  @Async  ← Não bloqueia a transação │
  │  onClienteConfigUpdate(event)       │
  └──────────┬──────────────────────────┘
             │ [Executa em thread diferente]
             ▼
  ┌──────────────────────────────────────┐
  │  recarregarDecorators()              │
  │  1. factory.create()                 │
  │     └─ Lê config do DB               │
  │     └─ Cria novos decorators         │
  │  2. holder.updateService() [WRITE]   │
  │     └─ Aguarda ReadLocks             │
  │     └─ serviceReference.set()        │
  └──────────┬──────────────────────────┘
             │
             ▼
  ┌──────────────────────────────────────┐
  │  Próxima Requisição já usa           │
  │  a nova configuração!                │
  └──────────────────────────────────────┘
```

## 4. Thread-Safety Diagram

### Cenário: Múltiplas Requisições + Update Simultâneo

```
Timeline:

T=0ms    ┌────────────────────────────────────────────────┐
         │ Thread-1: GET /api/v1/clientes/1               │
         │ holder.getService() [acquires READ LOCK]       │
         │                                                 │
         │ Thread-2: GET /api/v1/clientes/2              │
         │ holder.getService() [acquires READ LOCK]       │
         │ (ReadLock permite múltiplas leituras!)         │
         └────────┬────────────────────────────────────────┘
                  │
T=30ms   ┌────────────────────────────────────────────────┐
         │ Thread-3: PUT /api/v1/empresas/config/cliente  │
         │ eventPublisher.publishEvent() [ASYNC]          │
         │ (Retorna imediatamente, evento em thread-bg)   │
         │                                                 │
         │ Thread-BG: ClienteConfigUpdateListener         │
         │ holder.updateService() [waits for WRITE LOCK]  │
         │ ⏳ Aguardando ReadLocks liberarem...           │
         └────────┬────────────────────────────────────────┘
                  │
T=50ms   ┌────────────────────────────────────────────────┐
         │ Thread-1: termina transação                     │
         │ holder.readLock.unlock()                       │
         │                                                 │
         │ Thread-2: termina transação                     │
         │ holder.readLock.unlock()                       │
         └────────┬────────────────────────────────────────┘
                  │
T=55ms   ┌────────────────────────────────────────────────┐
         │ Thread-BG: AGORA consegue WriteLock!           │
         │ serviceReference.set(novoService)              │
         │ holder.writeLock.unlock()                      │
         │                                                 │
         │ ✅ Configuração atualizada                      │
         └────────┬────────────────────────────────────────┘
                  │
T=60ms   ┌────────────────────────────────────────────────┐
         │ Thread-4: GET /api/v1/clientes/3               │
         │ holder.getService() [acquires READ LOCK]       │
         │ └─ Retorna NOVO serviço com novos decorators   │
         │ (Nova config já está em uso!)                 │
         └────────────────────────────────────────────────┘

Garantias:
✅ Thread-1 e Thread-2 completam normalmente
✅ Thread-3 retorna imediatamente (async)
✅ Thread-4 obtém novo serviço após update
✅ Zero downtime, zero race conditions
```

## 5. Estrutura de Classes

```
┌──────────────────────────────────────────────────────────┐
│                    IClienteService                       │
│                    (Interface)                           │
├──────────────────────────────────────────────────────────┤
│  + pegarTodos(Pageable): Page<Cliente>                  │
│  + criar(CreateClienteDto): Cliente                     │
│  + atualizar(Long, CreateClienteDto): Cliente           │
│  + pegarPorId(Long): Cliente                            │
│  + deletar(Long): void                                   │
└────────────┬──────────────────────────┬───────────────────┘
             │                          │
             │ implements               │ implements
             ▼                          ▼
    ┌─────────────────────┐    ┌──────────────────┐
    │ ClienteService      │    │ ClienteServiceProxy
    │ (Implementação Core)│    │ (Proxy Dinâmico)
    │                     │    │                  │
    │ - repository        │    │ - holder: ref    │
    │ - validator         │    │                  │
    │ - criar()           │    │ + getService()   │
    │ - pegarPorId()      │    │ + criar()        │
    └─────────────────────┘    └────────┬─────────┘
                                        │ delega
                                        ▼
                        ┌───────────────────────────┐
                        │ ClienteServiceHolder      │
                        │ (Gerenciador da Ref)      │
                        │                           │
                        │ - serviceReference        │
                        │ - lock: ReadWriteLock     │
                        │ + getService()            │
                        │ + updateService()         │
                        └───────────┬───────────────┘
                                    │
                        ┌───────────┴──────────┐
                        │                      │
                        ▼                      ▼
            ┌─────────────────────┐  ┌──────────────────┐
            │ Decorators (Chain)  │  │ Factory          │
            │                     │  │                  │
            │ ┌─────────────────┐ │  │ + create():      │
            │ │ValidationDeco.. │ │  │   aplicarDecs()  │
            │ ├─────────────────┤ │  │                  │
            │ │CacheDecorator.. │ │  │ ClientConfig:    │
            │ ├─────────────────┤ │  │ - isValidation   │
            │ │AuditDecorator.. │ │  │ - isAudit        │
            │ ├─────────────────┤ │  │ - isCache        │
            │ │Notification..   │ │  │ - isNotification │
            │ └─────────────────┘ │  │                  │
            └─────────────────────┘  └──────────────────┘
```

## 6. Sequência de Decorators

```
Request Entrada
      │
      ▼
┌─────────────────────────────────────────┐
│ NotificationDecorator.criar(dto)        │  ← Last (Publica evento)
│ └─ Chamará próximo:                     │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│ AuditDecorator.criar(dto)               │  ← 3º (Registra log)
│ └─ Chamará próximo:                     │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│ CacheDecorator.criar(dto)               │  ← 2º (Armazena cache)
│ └─ Chamará próximo:                     │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│ ValidationDecorator.criar(dto)          │  ← 1º (Valida entrada)
│ └─ Chamará próximo:                     │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│ ClienteService.criar(dto)               │  ← Core (Executa BD)
│ ├─ Valida dados                         │
│ ├─ repository.save()                    │
│ └─ Retorna cliente                      │
└──────────────┬──────────────────────────┘
               │
               ▼
         Response Saída
```

## 7. State Machine de Decorators

```
┌──────────────┐
│   Disabled   │  Sem nenhum decorator
│ (clienteOnly)│
└──────────────┘
       │
       │ Ativa: validation
       ▼
┌──────────────────────────────────────┐
│ validation                           │
│ ValidationDecorator wraps Service    │
└──────────────────────────────────────┘
       │
       │ Ativa: cache
       ▼
┌──────────────────────────────────────┐
│ validation + cache                   │
│ Validation > Cache > Service         │
└──────────────────────────────────────┘
       │
       │ Ativa: audit
       ▼
┌──────────────────────────────────────┐
│ validation + cache + audit           │
│ Validation > Cache > Audit > Service │
└──────────────────────────────────────┘
       │
       │ Ativa: notification
       ▼
┌──────────────────────────────────────┐
│ All Decorators Active                │
│ Notification > Audit > Cache > ...   │
└──────────────────────────────────────┘
       │
       │ Desativa: cache
       ▼
┌──────────────────────────────────────┐
│ validation + audit + notification    │
│ Notification > Audit > Validation... │
└──────────────────────────────────────┘
```

## Legenda

- **@Primary**: Bean que é injetado por padrão
- **@Async**: Executa em thread separada
- **ReadLock**: Múltiplos podem ler simultâneos
- **WriteLock**: Exclusivo, nenhum outro acesso
- **AtomicReference**: Visibilidade thread-safe
- **Decorator Pattern**: Adiciona comportamento dinamicamente
- **Factory Pattern**: Cria objetos com lógica complexa

---

Essa arquitetura garante:
✅ SOLID Principles  
✅ Zero Downtime  
✅ Thread-Safety  
✅ High Performance  
✅ Transactional Safety
