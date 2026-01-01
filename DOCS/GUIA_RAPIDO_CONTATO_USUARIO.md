# 🚀 Guia Rápido - Integração de Contatos por Usuário

## ⚡ Quick Start

### 1️⃣ Compilar o Projeto
```bash
mvn clean compile
```

### 2️⃣ Iniciar a Aplicação
```bash
mvn spring-boot:run
```

### 3️⃣ Acessar Swagger
```
http://localhost:8080/swagger-ui.html
```

---

## 📌 O Que Foi Implementado

✅ **Entidade UsuarioContato** - Relacionamento entre Usuário e Contatos
✅ **Repository** - Persistência de dados
✅ **Service Interface** - Contrato de serviço
✅ **Service Implementation** - Lógica de negócio
✅ **DTOs** - Objetos de transferência de dados
✅ **Controller Endpoints** - 7 novos endpoints REST
✅ **Database Migration** - Scripts SQL
✅ **Documentação Completa** - Guias e exemplos

---

## 🎯 Endpoints Disponíveis

### 📮 Associar Múltiplos Contatos
```
POST /api/v1/contatos/usuario/associar
```
Cadastra vários contatos para um usuário de uma vez.

### ➕ Adicionar Contato
```
POST /api/v1/contatos/usuario/{usuarioId}/contato
```
Adiciona um novo contato a um usuário existente.

### 📖 Listar Contatos
```
GET /api/v1/contatos/usuario/{usuarioId}
```
Retorna todos os contatos de um usuário.

### ⭐ Marcar Principal
```
PATCH /api/v1/contatos/usuario/{usuarioId}/contato/{contatoId}/principal
```
Define um contato como principal.

### ⚪ Desativar Contato
```
PATCH /api/v1/contatos/usuario/{usuarioId}/contato/{contatoId}/desativar
```
Desativa um contato (soft delete).

### 🟢 Ativar Contato
```
PATCH /api/v1/contatos/usuario/{usuarioId}/contato/{contatoId}/ativar
```
Reativa um contato desativado.

### 🗑️ Remover Contato
```
DELETE /api/v1/contatos/usuario/remover
```
Remove um contato específico de um usuário.

---

## 🔑 Exemplo Básico com cURL

### Associar Contatos
```bash
curl -X POST http://localhost:8080/api/v1/contatos/usuario/associar \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{
    "usuarioId": 1,
    "contatos": [
      {
        "tipo": "EMAIL",
        "valor": "usuario@example.com",
        "descricao": "Email principal",
        "principal": true
      },
      {
        "tipo": "CELULAR",
        "valor": "+5511987654321",
        "descricao": "Celular pessoal",
        "principal": false
      }
    ]
  }'
```

### Listar Contatos
```bash
curl -X GET http://localhost:8080/api/v1/contatos/usuario/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### Marcar como Principal
```bash
curl -X PATCH "http://localhost:8080/api/v1/contatos/usuario/1/contato/102/principal" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

## 🗄️ Estrutura de Banco de Dados

### Tabela: usuario_contato
```
id                    | BIGINT (PK)
usuario_id            | BIGINT (FK) - UNIQUE
data_criacao          | TIMESTAMP
data_atualizacao      | TIMESTAMP
```

### Alteração em contatos
```
Adicionada coluna: usuario_contato_id (FK)
```

---

## 🔐 Permissões Requeridas

Todos os endpoints usam permissões da feature **Contato**:

| Ação | Permissão |
|------|-----------|
| Criar/Adicionar | `ContatoPermissions.CRIAR` |
| Consultar | `ContatoPermissions.VISUALIZAR` |
| Atualizar/Ativar/Desativar | `ContatoPermissions.ATUALIZAR` |
| Remover | `ContatoPermissions.DELETAR` |

**Nota:** Garantir que o usuário autenticado tenha essas permissões.

---

## 📂 Arquivos Principais

| Arquivo | Descrição |
|---------|-----------|
| `UsuarioContato.java` | Entidade de relacionamento |
| `UsuarioContatoRepository.java` | Interface JPA |
| `GerenciamentoContatoServiceInterface.java` | Interface do serviço |
| `GerenciamentoContatoServiceImpl.java` | Implementação |
| `AssociarContatosRequest.java` | DTO de request |
| `UsuarioContatosResponse.java` | DTO de response |
| `ContatoController.java` | Endpoints REST |
| `V1_0__create_usuario_contato_tables.sql` | Migration |

---

## 🧪 Testar via Swagger

1. Acesse: `http://localhost:8080/swagger-ui.html`
2. Expanda a seção **Contatos**
3. Clique em "Try it out" para qualquer endpoint
4. Preencha os dados conforme exemplos
5. Clique em "Execute"

---

## ⚙️ Configuração do Banco de Dados

O projeto usa **Hibernate DDL** auto. As tabelas são criadas automaticamente na primeira execução.

Para usar **Flyway** (alternativa):
1. Adicione a dependência Flyway no `pom.xml`
2. Configure em `application.properties`:
   ```properties
   spring.flyway.enabled=true
   spring.flyway.locations=classpath:db/migration
   ```

---

## 🐛 Troubleshooting

### Erro: "Usuário não encontrado"
- Certifique-se de que o `usuarioId` existe no banco
- Verifique se está passando o ID correto no request

### Erro: "Permissão negada"
- Valide se o token JWT é válido
- Confirme se o usuário autenticado tem as permissões necessárias

### Erro: "Contato não encontrado"
- Verifique se o `contatoId` é válido
- Confirme que o contato pertence ao usuário

### Erro: "Compilação falhou"
- Execute `mvn clean install`
- Verifique se todos os imports estão corretos
- Limpe o cache: `mvn clean`

---

## 📚 Documentação Relacionada

Para mais detalhes:
- [FEATURE_CONTATO_USUARIO.md](FEATURE_CONTATO_USUARIO.md) - Documentação completa
- [EXEMPLOS_CONTATO_USUARIO.md](EXEMPLOS_CONTATO_USUARIO.md) - Exemplos práticos
- [SUMARIO_MUDANCAS.md](SUMARIO_MUDANCAS.md) - Resumo de mudanças

---

## 💡 Dicas de Implementação

### 1. Sempre ter um contato principal
```java
// Ao criar o primeiro contato, marque como principal
contatos.add(new CreateContatoRequest("EMAIL", "user@example.com", "Principal", true));
```

### 2. Validação personalizada
```java
// Adicione validação no serviço se necessário
if (contatoRequest.valor().isEmpty()) {
    throw new ValidationException("Valor do contato não pode estar vazio");
}
```

### 3. Cache para consultas frequentes
```java
// Implementar cache Spring @Cacheable no serviço
@Cacheable(value = "usuarioContatos", key = "#usuarioId")
public UsuarioContatosResponse buscarContatosUsuario(Long usuarioId) {
    // ...
}
```

### 4. Auditoria
```java
// Todos os serviços já possuem dataCriacao e dataAtualizacao
// Isso permite rastrear alterações
```

---

## 🔄 Fluxo Típico de Uso

```
1. Usuário se registra no sistema
   ↓
2. Sistema chama: POST /usuario/associar
   Com lista de contatos do usuário
   ↓
3. Contatos são salvos e associados
   ↓
4. Sistema pode:
   - Listar contatos: GET /usuario/{id}
   - Adicionar novo: POST /usuario/{id}/contato
   - Marcar principal: PATCH /usuario/{id}/contato/{id}/principal
   - Remover: DELETE /usuario/remover
```

---

## 🚢 Deploy

### Ambiente de Desenvolvimento
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Ambiente de Produção
```bash
mvn clean package -Pprod
java -jar target/erp-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

---

## ✨ Próximos Passos Recomendados

1. **Testes Unitários**
   - Adicionar testes para GerenciamentoContatoServiceImpl

2. **Testes de Integração**
   - Adicionar testes para ContatoController

3. **Validações Avançadas**
   - Validar formato de email
   - Validar formato de telefone
   - Validar URLs (LinkedIn, Website)

4. **Performance**
   - Implementar paginação
   - Adicionar índices no banco
   - Implementar cache

5. **Notificações**
   - Enviar notificação quando contato principal muda
   - Log de auditoria de alterações

---

## 📞 Suporte

Para dúvidas ou problemas:
1. Consulte a documentação em [FEATURE_CONTATO_USUARIO.md](FEATURE_CONTATO_USUARIO.md)
2. Veja exemplos em [EXEMPLOS_CONTATO_USUARIO.md](EXEMPLOS_CONTATO_USUARIO.md)
3. Revise o código-fonte comentado

---

**Status:** ✅ Pronto para uso
**Última atualização:** 25 de Dezembro de 2025
**Versão:** 1.0
