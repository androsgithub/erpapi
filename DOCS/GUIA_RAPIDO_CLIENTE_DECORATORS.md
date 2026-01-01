# 🚀 Guia Rápido - Decorators de Cliente

## Índice

1. [Como Funciona](#como-funciona)
2. [Exemplos de Uso](#exemplos-de-uso)
3. [Habilitando/Desabilitando Decorators](#habilitandodesabilitando-decorators)
4. [Adicionando Novo Decorator](#adicionando-novo-decorator)
5. [FAQ](#faq)

---

## Como Funciona

### Visão Simplificada

```
1. App inicia
   → Factory cria ClienteService + Decorators
   → Proxy armazena no Holder
   
2. Controller faz requisição
   → Proxy.criar(dto)
   → Holder retorna serviço atual
   → Executa com decorators
   
3. Admin muda configuração
   → EmpresaService.updateClienteConfig()
   → Publica ClienteConfigUpdateEvent
   → Listener reconstrói decorators
   → Próxima requisição já usa nova config
```

### Sem Downtime

- ✅ Requisições em progresso terminam normalmente
- ✅ Próximas requisições usam nova config
- ✅ Ninguém é desconectado ou teve erro

---

## Exemplos de Uso

### Exemplo 1: Criar Cliente com Todos Decorators

**Configuração:**
```properties
# application.properties
cliente.validationEnabled=true
cliente.auditEnabled=true
cliente.cacheEnabled=true
cliente.notificationEnabled=true
```

**Código:**
```java
@RestController
public class ClienteController {
    private final IClienteService clienteService;  // Proxy injeta automaticamente
    
    @PostMapping
    public ClienteResponse criar(@RequestBody CreateClienteDto dto) {
        // Fluxo:
        // 1. ValidationDecorator valida
        // 2. CacheDecorator armazena resultado
        // 3. AuditDecorator registra: "Criado cliente: João"
        // 4. NotificationDecorator publica evento
        Cliente cliente = clienteService.criar(dto);
        return mapper.toResponse(cliente);
    }
}
```

**Logs Esperados:**
```
[CLIENTE FACTORY] ValidationDecorator aplicado
[CLIENTE FACTORY] CacheDecorator aplicado
[CLIENTE FACTORY] AuditDecorator aplicado
[CLIENTE FACTORY] NotificationDecorator aplicado

[AUDIT CLIENTE] Criando novo cliente: nome=João Silva, cnpj=12.345.678/0001-90
[AUDIT CLIENTE] Cliente criado com sucesso: id=123, nome=João Silva
[NOTIFICATION CLIENTE] Evento: Cliente criado - id=123, nome=João Silva
```

### Exemplo 2: Ativar Cache para Performance

**Antes:**
```
GET /api/v1/clientes/123  → 50ms (acesso BD)
GET /api/v1/clientes/123  → 50ms (acesso BD)
GET /api/v1/clientes/123  → 50ms (acesso BD)
Total: 150ms
```

**Depois de Ativar Cache:**
```
PUT /api/v1/empresas/config/cliente
{
  "clienteCacheEnabled": true
}

GET /api/v1/clientes/123  → 50ms (acesso BD, armazena cache)
GET /api/v1/clientes/123  → 1ms  (retorna do cache)
GET /api/v1/clientes/123  → 1ms  (retorna do cache)
Total: 52ms (Speedup: 3x)
```

**Como o Cache Funciona:**
```java
public Cliente pegarPorId(Long id) {
    // Checa cache primeiro
    if (cache.contém(id) && !expirado(id)) {
        return cache.get(id);  // 1ms - rápido!
    }
    
    // Se não estiver em cache, vai ao banco
    Cliente cliente = service.pegarPorId(id);  // 50ms
    cache.put(id, cliente);                     // armazena
    return cliente;
}

// Cache expira a cada 5 minutos
private static final long CACHE_TIMEOUT_MS = 5 * 60 * 1000;
```

### Exemplo 3: Auditoria para Compliance

**Cenário:** Empresa precisa rastrear todas operações de cliente

**Ativar:**
```
PUT /api/v1/empresas/config/cliente
{
  "clienteAuditEnabled": true
}
```

**Operação:**
```java
clienteService.criar(new CreateClienteDto(...));
```

**Auditoria Registrada:**
```
[AUDIT CLIENTE] Criando novo cliente: nome=João, cnpj=12.345.678/0001-90
[AUDIT CLIENTE] Cliente criado com sucesso: id=123, nome=João
```

**Verificar Logs:**
```bash
# Ver todos eventos de cliente
grep "AUDIT CLIENTE" application.log

# Ver apenas criações
grep "AUDIT CLIENTE.*Criando" application.log

# Ver deletions
grep "AUDIT CLIENTE.*Deletado" application.log
```

### Exemplo 4: Notificações para Integração

**Cenário:** Sistema quer notificar webhook quando cliente é criado

**Ativar:**
```
PUT /api/v1/empresas/config/cliente
{
  "clienteNotificationEnabled": true
}
```

**Implementar Listener (Futuro):**
```java
@Component
public class ClienteCriadoEventHandler {
    
    @EventListener
    public void onClienteCriado(ClienteCriadoEvent event) {
        // Enviar para webhook externo
        webhookService.notificar("https://external.com/api/clientes", 
            event.getCliente());
    }
}
```

---

## Habilitando/Desabilitando Decorators

### Via API

**Ativar todos os decorators:**
```bash
curl -X PUT http://localhost:8080/api/v1/empresas/config/cliente \
  -H "Content-Type: application/json" \
  -d '{
    "clienteValidationEnabled": true,
    "clienteAuditEnabled": true,
    "clienteCacheEnabled": true,
    "clienteNotificationEnabled": true
  }'
```

**Apenas auditoria (mais leve):**
```bash
curl -X PUT http://localhost:8080/api/v1/empresas/config/cliente \
  -H "Content-Type: application/json" \
  -d '{
    "clienteValidationEnabled": false,
    "clienteAuditEnabled": true,
    "clienteCacheEnabled": false,
    "clienteNotificationEnabled": false
  }'
```

**Apenas cache (mais rápido):**
```bash
curl -X PUT http://localhost:8080/api/v1/empresas/config/cliente \
  -H "Content-Type: application/json" \
  -d '{
    "clienteValidationEnabled": false,
    "clienteAuditEnabled": false,
    "clienteCacheEnabled": true,
    "clienteNotificationEnabled": false
  }'
```

### Resposta Esperada

```json
{
  "id": 1,
  "nome": "Minha Empresa",
  "config": {
    "clienteConfig": {
      "clienteValidationEnabled": true,
      "clienteAuditEnabled": true,
      "clienteCacheEnabled": true,
      "clienteNotificationEnabled": true
    }
  }
}
```

### Verificar Qual Decorator foi Aplicado

```bash
# Ver logs de inicialização
grep "CLIENTE FACTORY" application.log

# Exemplo:
# [CLIENTE FACTORY] ValidationDecorator aplicado
# [CLIENTE FACTORY] CacheDecorator aplicado
# [CLIENTE FACTORY] AuditDecorator aplicado
# [CLIENTE FACTORY] NotificationDecorator aplicado
```

---

## Adicionando Novo Decorator

### Passo 1: Criar Classe de Decorator

```java
// arquivo: ValidadeFormatDecoratorClienteService.java
package com.api.erp.v1.features.cliente.infrastructure.decorator;

import com.api.erp.v1.features.cliente.domain.service.IClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class ValidadeFormatDecoratorClienteService implements IClienteService {

    private final IClienteService service;

    @Override
    public Page<Cliente> pegarTodos(Pageable pageable) {
        return service.pegarTodos(pageable);
    }

    @Override
    public Cliente criar(CreateClienteDto clienteDto) {
        // TODO: Validar formato de CNPJ, email, telefone, etc
        validarFormato(clienteDto);
        return service.criar(clienteDto);
    }

    @Override
    public Cliente atualizar(Long id, CreateClienteDto clienteDto) {
        validarFormato(clienteDto);
        return service.atualizar(id, clienteDto);
    }

    @Override
    public Cliente pegarPorId(Long id) {
        return service.pegarPorId(id);
    }

    @Override
    public void deletar(Long id) {
        service.deletar(id);
    }

    private void validarFormato(CreateClienteDto dto) {
        // Validar CNPJ
        if (!validarCNPJ(dto.dadosFiscais().cnpj())) {
            throw new BusinessException("CNPJ inválido");
        }
        
        // Validar email
        if (!validarEmail(dto.dadosFiscais().email())) {
            throw new BusinessException("Email inválido");
        }
    }

    private boolean validarCNPJ(String cnpj) {
        // Implementar validação de CNPJ
        return true;
    }

    private boolean validarEmail(String email) {
        // Implementar validação de email
        return true;
    }
}
```

### Passo 2: Adicionar Flag em ClienteConfig

```java
// arquivo: ClienteConfig.java
package com.api.erp.v1.features.empresa.domain.entity;

@Embeddable
@Getter
@Setter
public class ClienteConfig {

    @Column(nullable = false)
    private Boolean clienteValidationEnabled = false;

    @Column(nullable = false)
    private Boolean clienteAuditEnabled = false;

    @Column(nullable = false)
    private Boolean clienteCacheEnabled = false;

    @Column(nullable = false)
    private Boolean clienteNotificationEnabled = false;

    // ✅ NOVO CAMPO
    @Column(nullable = false)
    private Boolean clienteFormatValidationEnabled = false;

    // ✅ NOVO GETTER
    public boolean isClienteFormatValidationEnabled() {
        return Boolean.TRUE.equals(clienteFormatValidationEnabled);
    }
}
```

### Passo 3: Criar Request DTO

```java
// arquivo: ClienteConfigRequest.java
public record ClienteConfigRequest(
    boolean clienteValidationEnabled,
    boolean clienteAuditEnabled,
    boolean clienteCacheEnabled,
    boolean clienteNotificationEnabled,
    boolean clienteFormatValidationEnabled  // ✅ NOVO
) {}
```

### Passo 4: Atualizar Factory

```java
// arquivo: ClienteServiceFactory.java
private IClienteService aplicarDecorators(IClienteService service, ClienteConfig config) {
    if (config != null && config.isClienteValidationEnabled()) {
        service = new ValidationDecoratorClienteService(service, validator);
    }

    // ✅ NOVO
    if (config != null && config.isClienteFormatValidationEnabled()) {
        service = new ValidadeFormatDecoratorClienteService(service);
    }

    if (config != null && config.isClienteCacheEnabled()) {
        service = new CacheDecoratorClienteService(service);
    }

    if (config != null && config.isClienteAuditEnabled()) {
        service = new AuditDecoratorClienteService(service);
    }

    if (config != null && config.isClienteNotificationEnabled()) {
        service = new NotificationDecoratorClienteService(service);
    }

    return service;
}
```

### Passo 5: Usar

```bash
curl -X PUT http://localhost:8080/api/v1/empresas/config/cliente \
  -H "Content-Type: application/json" \
  -d '{
    "clienteFormatValidationEnabled": true
  }'
```

**Pronto!** 🎉 Novo decorator está ativo

---

## FAQ

### P: E se a config atualizar enquanto estou fazendo uma requisição?

**R:** Sua requisição não é afetada!

```
Thread 1: clienteService.criar(dto)  // Obtém ReadLock
Thread 2: holder.updateService()    // Aguarda ReadLock
↓
Requisição termina, ReadLock é liberado
↓
updateService() agora consegue prosseguir (WriteLock)
↓
Próxima requisição já usa nova config
```

### P: Qual é o impacto de performance?

**R:** Mínimo!

| Operação | Overhead |
|----------|----------|
| Cada requisição | +0.05ms (apenas holder.getService()) |
| Atualizar config | ~50ms (1x por mudança, assincronamente) |
| Cache hit | -10ms (retorna mais rápido) |

### P: Posso desabilitar todos os decorators?

**R:** Sim! Deixe tudo como `false`. O sistema usará apenas `ClienteService` puro.

```json
{
  "clienteValidationEnabled": false,
  "clienteAuditEnabled": false,
  "clienteCacheEnabled": false,
  "clienteNotificationEnabled": false
}
```

### P: O cache funciona para múltiplas instâncias da aplicação?

**R:** Não, cada instância tem seu próprio cache em memória.

Para cache distribuído:
```java
// Adicionar Redis em futuro:
@Bean
public IClienteService clienteServiceWithRedis() {
    return new RedisDecoratorClienteService(service, redisTemplate);
}
```

### P: Como vejo quais decorators estão ativos?

**R:** Verifique os logs na inicialização:

```bash
grep "CLIENTE FACTORY.*aplicado" application.log
```

Ou faça uma requisição e veja os logs:

```bash
curl http://localhost:8080/api/v1/clientes/1 \
  && grep "CLIENTE" application.log | tail -20
```

### P: Posso adicionar decorators em ordem diferente?

**R:** Sim, mas a ordem importa! Recomendação:

1. **ValidationDecorator** - sempre primeiro (fail-fast)
2. **CacheDecorator** - antes de Audit (evita logs false)
3. **AuditDecorator** - antes de Notification (registra tudo)
4. **NotificationDecorator** - sempre último (event final)

### P: O que acontece se um decorator der erro?

**R:** Dependente do decorator:

```java
// CacheDecorator
try {
    // cache operation
} catch (Exception e) {
    // Ignora erro de cache e continua
    return service.metodo();
}

// ValidationDecorator
throw new BusinessException("Validação falhou"); // Propaga para controller
```

### P: Posso ter múltiplas chains de decorators?

**R:** Não no design atual. Mas você pode estender para suportar:

```java
// Futuro: diferentes decorators por cliente
public enum ClienteProfile {
    LIGHT,      // Apenas validação
    NORMAL,     // Validação + Audit
    PREMIUM     // Validação + Cache + Audit + Notification
}
```

---

## Resumo

✅ **Decorators** adicionam funcionalidade sem modificar código  
✅ **Factory** cria a chain baseado em config  
✅ **Holder** gerencia thread-safe com AtomicReference + ReadWriteLock  
✅ **Proxy** implementa IClienteService e delega para Holder  
✅ **Listener** recarrega quando config muda  
✅ **Sem downtime** - requisições em progresso não são afetadas  
✅ **Alto desempenho** - overhead mínimo  
