# 🎯 Decorators Baseados em EmpresaConfig

## 📌 Arquitetura

Os decorators do serviço de Contato agora são **configurados via `EmpresaConfig`**, não via `application.properties`.

Cada empresa tem sua própria configuração de quais decorators devem estar ativos.

```
EmpresaConfig
    ├─ contatoValidationEnabled = true/false
    ├─ contatoAuditEnabled = true/false
    ├─ contatoCacheEnabled = true/false
    └─ contatoFormatValidationEnabled = true/false
         ↓
ContatoDecoratorFactory.criar(repository, empresaConfig)
    ├─ Se validation enabled → ValidationDecorator
    ├─ Se audit enabled → AuditDecorator
    ├─ Se cache enabled → CacheDecorator
    └─ Se formatValidation enabled → FormatValidationDecorator
         ↓
ContatoService (base)
```

## 🔧 Como Usar

### Opção 1: No Controller

```java
@RestController
@RequestMapping("/api/v1/contatos")
public class ContatoController {
    
    private final ContatoServiceInterface service;
    
    public ContatoController(
            ContatoRepository repository,
            EmpresaConfig empresaConfig) {
        // Factory cria o serviço com decorators conforme empresa
        this.service = ContatoDecoratorFactory.criar(repository, empresaConfig);
    }
}
```

### Opção 2: Como Bean Spring

```java
@Configuration
public class ContatoConfig {
    
    @Bean
    public ContatoServiceInterface contatoService(
            ContatoRepository repository,
            EmpresaConfig empresaConfig) {
        return ContatoDecoratorFactory.criar(repository, empresaConfig);
    }
}
```

### Opção 3: Em Testes

```java
@Test
void testComValoracaoHabilitada() {
    // Criar config customizada
    EmpresaConfig config = new EmpresaConfig();
    config.setContatoValidationEnabled(true);
    config.setContatoAuditEnabled(false);
    
    ContatoRepository repo = mock(ContatoRepository.class);
    
    // Factory cria service conforme config
    ContatoServiceInterface service = 
        ContatoDecoratorFactory.criar(repo, config);
    
    // Usar service
}
```

## 📝 Configurar Empresa

### No Banco de Dados

```sql
UPDATE empresa 
SET contato_validation_enabled = true,
    contato_audit_enabled = true,
    contato_cache_enabled = false,
    contato_format_validation_enabled = true
WHERE id = 1;
```

### Via API (Criar/Atualizar)

```json
POST /api/v1/empresas

{
  "nome": "Empresa A",
  "config": {
    "requerAprovacaoGestor": false,
    "requerEmailCorporativo": true,
    "dominiosPermitidos": ["empresa.com"],
    
    // ← Decorators de Contato
    "contatoValidationEnabled": true,
    "contatoAuditEnabled": true,
    "contatoCacheEnabled": false,
    "contatoFormatValidationEnabled": true
  }
}
```

## 🎯 Por Empresa

### Empresa A (Startup)
```java
// Config: Apenas validação (rápido, sem logs)
config.setContatoValidationEnabled(true);
config.setContatoAuditEnabled(false);
config.setContatoCacheEnabled(false);
```

### Empresa B (Grande Corporação)
```java
// Config: Tudo habilitado (segurança + performance)
config.setContatoValidationEnabled(true);
config.setContatoAuditEnabled(true);
config.setContatoCacheEnabled(true);
config.setContatoFormatValidationEnabled(true);
```

### Empresa C (Compliance Pesado)
```java
// Config: Validação + Audit (rastreabilidade total)
config.setContatoValidationEnabled(true);
config.setContatoAuditEnabled(true);
config.setContatoCacheEnabled(false); // Sem cache por compliance
config.setContatoFormatValidationEnabled(true);
```

## 📦 Classe EmpresaConfig

```java
@Embeddable
@Getter
@Setter
public class EmpresaConfig {
    
    // Configs existentes
    private boolean requerAprovacaoGestor;
    private boolean requerEmailCorporativo;
    private List<String> dominiosPermitidos;
    
    // ← NOVO: Decorators de Contato
    private boolean contatoValidationEnabled = true;
    private boolean contatoAuditEnabled = true;
    private boolean contatoCacheEnabled = false;
    private boolean contatoFormatValidationEnabled = true;
}
```

## 🏗️ Factory Pattern

```java
public class ContatoDecoratorFactory {
    
    public static ContatoServiceInterface criar(
            ContatoRepository repository,
            EmpresaConfig empresaConfig) {
        
        // 1. Serviço base
        ContatoServiceInterface service = new ContatoService(repository);
        
        // 2. Adicionar decorators conforme config
        if (empresaConfig.isContatoValidationEnabled()) {
            service = new ValidationDecorator(service);
        }
        if (empresaConfig.isContatoAuditEnabled()) {
            service = new AuditDecorator(service);
        }
        // ... etc
        
        return service;
    }
}
```

## 🔄 Fluxo Completo

```
1. Controller recebe requisição
   ↓
2. Injetar: ContatoRepository + EmpresaConfig
   ↓
3. ContatoDecoratorFactory.criar(repo, config)
   ├─ Ler config.contatoValidationEnabled
   ├─ Ler config.contatoAuditEnabled
   ├─ Ler config.contatoCacheEnabled
   └─ Compor decorators necessários
   ↓
4. Usar service
   ├─ Validação automática (se enabled)
   ├─ Audit automático (se enabled)
   ├─ Cache automático (se enabled)
   └─ Lógica de negócio
   ↓
5. Resposta
```

## ✅ Vantagens

✅ **Por Empresa** - Cada empresa suas próprias regras
✅ **Sem Recompilação** - Muda no banco de dados
✅ **Rastreável** - Log de configuração ao iniciar
✅ **Testável** - Fácil criar configs customizadas
✅ **Extensível** - Novos decorators em EmpresaConfig + Factory
✅ **Padrão** - Segue padrão do projeto

## 🚀 Adicionar Novo Decorator

### 1. Adicionar Campo em EmpresaConfig

```java
@Embeddable
public class EmpresaConfig {
    // ... campos existentes
    
    // ← NOVO
    private boolean contatoMeuDecoratorEnabled = false;
}
```

### 2. Criar Decorator

```java
public class MeuDecoratorContatoService implements ContatoServiceInterface {
    private final ContatoServiceInterface service;
    
    public ContatoResponse criar(CreateContatoRequest request) {
        // Sua lógica
        return service.criar(request);
    }
    // ... implementar outros métodos
}
```

### 3. Adicionar à Factory

```java
public class ContatoDecoratorFactory {
    
    if (empresaConfig.isContatoMeuDecoratorEnabled()) {
        service = new MeuDecoratorContatoService(service);
    }
}
```

### 4. Configurar Empresa

```sql
UPDATE empresa 
SET contato_meu_decorator_enabled = true
WHERE id = 1;
```

## 📋 Comparação: Antes vs Depois

### ❌ Antes
```java
// Hardcoded na factory
service = new ValidationDecorator(...);
service = new AuditDecorator(...);
service = new CacheDecorator(...);
// Recompilação para mudar
```

### ✅ Depois
```java
// Conforme EmpresaConfig
if (config.isContatoValidationEnabled()) {
    service = new ValidationDecorator(...);
}
// Muda no banco, sem recompilação
```

## 📚 Arquivos Relacionados

- `EmpresaConfig.java` - Configurações da empresa (inclui decorators)
- `ContatoDecoratorFactory.java` - Factory que lê config e cria service
- `ContatoController.java` - Usa factory para criar service
- `ContatoServiceInterface.java` - Interface do serviço
- Decorators:
  - `ValidationDecoratorContatoService.java`
  - `AuditDecoratorContatoService.java`
  - `CacheDecoratorContatoService.java`
  - `FormatValidationDecoratorContatoService.java`

---

**Data**: Dezembro de 2025
**Status**: Pronto para produção
**Padrão**: Decorator Pattern + EmpresaConfig
