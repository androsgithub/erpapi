# Debug - Contato Decorators Não Atualizando

## ✅ Verificações Realizadas

### 1. Spring Boot Configuration
- [x] @EnableAsync adicionado em ErpApplication.java
- [x] @Import({ClienteServiceConfiguration.class, ContatoServiceConfiguration.class}) em ServiceConfiguration.java

### 2. ContatoServiceConfiguration
- [x] Factory bean criado
- [x] Holder bean criado (com AtomicReference)
- [x] Proxy bean criado com @Primary
- [x] ProxyInitializer bean criado

### 3. ContatoConfigUpdateListener
- [x] @Component anotação presente
- [x] @EventListener anotação presente
- [x] @Async anotação presente
- [x] Injeta factory e holder

### 4. EmpresaService
- [x] updateContatoConfig() método implementado
- [x] Publica ContatoConfigUpdateEvent
- [x] ApplicationEventPublisher injetado

### 5. ContatoServiceFactory
- [x] Recebe ContatoRepository e IEmpresaService
- [x] create() retorna IContatoService com decorators
- [x] obterConfiguracao() busca de EmpresaConfig.getContatoConfig()

## 🔍 Como Testar

### Passo 1: Inicializar a Aplicação
```
./mvnw spring-boot:run
```

### Passo 2: Verificar Logs Iniciais (deve conter):
```
[CONTATO CONFIG] Inicializando ContatoServiceConfiguration
[CONTATO CONFIG] Inicializando ContatoServiceFactory
[CONTATO CONFIG] Inicializando ContatoServiceHolder
[CONTATO CONFIG] Inicializando ContatoServiceProxy
[CONTATO CONFIG] Inicializando ContatoServiceProxy com decorators iniciais
[CONTATO CONFIG LISTENER] ...
```

### Passo 3: Atualizar Config via API
```
POST /api/v1/empresa/contato-config

{
  "contatoValidationEnabled": true,
  "contatoAuditEnabled": true,
  "contatoCacheEnabled": true,
  "contatoFormatValidationEnabled": true,
  "contatoNotificationEnabled": false
}
```

### Passo 4: Observar Logs de Atualização (deve conter):
```
[EMPRESA SERVICE] Atualizando configuração de Contato
[EMPRESA SERVICE] Publicando ContatoConfigUpdateEvent
[CONTATO CONFIG LISTENER] Detectada atualização de configuração
[CONTATO CONFIG LISTENER] Decorators recarregados com sucesso
[CONTATO SERVICE HOLDER] Serviço atualizado
```

### Passo 5: Testar Operação com Novo Decorator
```
POST /api/v1/contato

{
  "tipo": "EMAIL",
  "valor": "teste@example.com",
  "descricao": "Email de teste",
  "principal": true
}
```

Deve aparecer logs de validação (se validationEnabled=true)

## 🐛 Possíveis Problemas

### Problema 1: "ContatoConfigUpdateListener não recebe evento"
**Solução**: Verifique se @EnableAsync está em ErpApplication.java

### Problema 2: "ContatoServiceConfiguration não carrega"
**Solução**: Verifique se @Import está em ServiceConfiguration.java

### Problema 3: "ContatoService não inicializa com decorators"
**Solução**: Verifique se ContatoConfig está sendo retornado por EmpresaConfig.getContatoConfig()

### Problema 4: "Holder não atualiza após mudança de config"
**Solução**: Verifique logs do ContatoConfigUpdateListener - pode haver exceção não logada

## 📋 Checklist de Componentes

| Componente | Arquivo | Status |
|-----------|---------|--------|
| ErpApplication | ErpApplication.java | ✅ @EnableAsync adicionado |
| ServiceConfiguration | ServiceConfiguration.java | ✅ @Import adicionado |
| ContatoServiceConfiguration | ContatoServiceConfiguration.java | ✅ Todos beans registrados |
| ContatoServiceHolder | ContatoServiceHolder.java | ✅ ReadWriteLock + AtomicReference |
| ContatoServiceProxy | ContatoServiceProxy.java | ✅ 11 métodos implementados |
| ContatoServiceFactory | ContatoServiceFactory.java | ✅ 5 decorators aplicados |
| ContatoConfigUpdateListener | ContatoConfigUpdateListener.java | ✅ @Component @EventListener @Async |
| ContatoConfigUpdateEvent | ContatoConfigUpdateEvent.java | ✅ Extends ApplicationEvent |
| EmpresaService | EmpresaService.java | ✅ updateContatoConfig() implementado |
| ContatoConfigRequest | ContatoConfigRequest.java | ✅ 5 campos |

## 🎯 Próximas Verificações

Se ainda não está funcionando após aplicar estas mudanças:

1. **Limpar e recompilar**:
   ```
   ./mvnw clean compile
   ```

2. **Verificar ClassLoader**:
   ```
   - Todos os componentes estão no path de scan?
   - Não há conflitos de nomes?
   ```

3. **Ativar Debug de Eventos**:
   ```
   spring.events.publish.enabled=true
   logging.level.org.springframework.context.event=DEBUG
   ```

4. **Verificar EmpresaConfig**:
   ```
   - É persistida no banco?
   - Possui inicialização padrão de ContatoConfig?
   ```

5. **Testar Manual via Controller**:
   ```
   GET /api/v1/empresa/config
   - Verificar se contatoConfig está presente
   ```
