# 🏁 Primeiros Passos para Novos Desenvolvedores

## 👋 Bem-vindo!

Você foi adicionado ao projeto ERP API! Este guia vai ajudar você a começar a desenvolver em poucas horas.

## ⏱️ Tempo Estimado: 4 horas

1. **Setup (30 min)** - Instalar tudo
2. **Compreender (90 min)** - Ler documentação
3. **Explorar (60 min)** - Rodar a aplicação e explorar
4. **Praticar (60 min)** - Fazer primeira mudança

## 🎯 Checklist de Onboarding

- [ ] Java 17+ instalado
- [ ] Maven configurado
- [ ] Git funcionando
- [ ] Banco de dados criado
- [ ] Projeto clonado
- [ ] Build bem-sucedido
- [ ] Aplicação rodando
- [ ] Documentação lida
- [ ] Swagger acessível

## 📋 Passo a Passo

### 1️⃣ Setup Técnico (30 min)

**1.1 Verificar Java**

```bash
java -version
# Esperado: java version \"17\" ou superior
```

**1.2 Verificar Maven**

```bash
mvn -version
# Esperado: Apache Maven 3.6.x ou superior
```

**1.3 Verificar Git**

```bash
git --version
# Esperado: git version 2.x ou superior
```

**1.4 Instalar Banco de Dados**

Escolha uma opção:

- **MySQL**: [Download](https://dev.mysql.com/downloads/mysql/)
- **PostgreSQL**: [Download](https://www.postgresql.org/download/)

**1.5 Clonar Repositório**

```bash
git clone https://github.com/androsgithub/erpapi.git
cd erpapi
```

**1.6 Seguir Guia de Instalação**

Siga [GUIA_INSTALACAO.md](GUIA_INSTALACAO.md) completamente.

### 2️⃣ Compreender a Arquitetura (90 min)

Leia **nesta ordem**:

1. **[ESTRUTURA_PROJETO.md](ESTRUTURA_PROJETO.md)** (20 min)
   - Entender organização de pastas
   - Ver padrão de feature

2. **[ARQUITETURA_GERAL.md](ARQUITETURA_GERAL.md)** (20 min)
   - Entender camadas
   - Ver fluxo de requisição

3. **[CAMADA_DOMAIN.md](CAMADA_DOMAIN.md)** (20 min)
   - Aprender sobre Entities
   - Aprender sobre Value Objects

4. **[CAMADA_APPLICATION.md](CAMADA_APPLICATION.md)** (15 min)
   - Entender Application Services
   - Ver padrão de DTOs

5. **[CAMADA_PRESENTATION.md](CAMADA_PRESENTATION.md)** (15 min)
   - Entender Controllers
   - Ver padrão de endpoints

### 3️⃣ Rodar a Aplicação (30 min)

**3.1 Build**

```bash
./mvnw clean install
# Esperado: BUILD SUCCESS
```

**3.2 Executar**

```bash
./mvnw spring-boot:run
# Esperado: Application started
```

**3.3 Verificar**

```bash
curl http://localhost:8080/actuator/health
# Esperado: {\"status\":\"UP\"}
```

**3.4 Acessar Swagger**

Abra no navegador:

```
http://localhost:8080/swagger-ui.html
```

### 4️⃣ Explorar Features (30 min)

Agora que entende a arquitetura, explore uma feature.

**Leia nesta ordem**:

1. [FEATURE_PRODUTO.md](FEATURE_PRODUTO.md) - Feature de exemplo
   - Ver estrutura
   - Ver endpoints
   - Ver fluxos

2. Explore código-fonte de Produto:
   ```bash
   # Domain
   src/main/java/com/api/erp/v1/features/produto/domain/entity/Produto.java
   
   # Application
   src/main/java/com/api/erp/v1/features/produto/application/service/
   
   # Controller
   src/main/java/com/api/erp/v1/features/produto/presentation/controller/
   ```

## 💻 Primeira Tarefa: Fazer Uma Mudança Pequena

### Objetivo
Adicionar um novo campo a uma entidade existente.

### Passo a Passo

**1. Escolha uma feature** (ex: Produto)

**2. Abra a entidade**

```
src/main/java/com/api/erp/v1/features/produto/domain/entity/Produto.java
```

**3. Adicione um campo** (ex: sku)

```java
@Column(nullable = false, unique = true, length = 50)
private String sku;
```

**4. Adicione getter/setter**

```java
public String getSku() { return sku; }
public void setSku(String sku) { this.sku = sku; }
```

**5. Crie/atualize a migration** (banco de dados)

```sql
ALTER TABLE produto ADD COLUMN sku VARCHAR(50) UNIQUE NOT NULL;
```

**6. Atualize o DTO**

```
src/main/java/com/api/erp/v1/features/produto/application/dto/request/CriarProdutoRequest.java
```

```java
@NotBlank
private String sku;
```

**7. Compile e teste**

```bash
./mvnw clean install
./mvnw test -Dtest=ProdutoServiceTest
```

**8. Commit (Git)**

```bash
git add .
git commit -m \"feat: adicionar campo SKU em Produto\"
git push origin main
```

## 🔑 Conceitos-Chave para Memorizar

### 1. Domain-Driven Design (DDD)
- **Entity**: Objeto com identidade (Produto)
- **Value Object**: Objeto sem identidade (Email, CPF)
- **Repository**: Interface de acesso a dados
- **Service**: Lógica de domínio

### 2. Camadas
- **Domain**: Lógica pura de negócio
- **Application**: Orquestra casos de uso
- **Presentation**: Expõe API HTTP
- **Infrastructure**: Implementa detalhes técnicos

### 3. Padrões
- **DTO**: Transfer Object (request/response)
- **Mapper**: Converte DTO ↔ Entity
- **Validator**: Valida regras de negócio
- **Specification**: Query complexa reutilizável

## 📚 Documentação para Aprofundar

Leia conforme necessário:

- **Segurança**: [SEGURANCA.md](SEGURANCA.md)
- **Permissões**: [FEATURE_PERMISSAO.md](FEATURE_PERMISSAO.md)
- **Testes**: [TESTES.md](TESTES.md)
- **Padrões**: [PADROES_PROJETO.md](PADROES_PROJETO.md)

## 🚨 Problemas Comuns

### \"Build failed\"
```bash
./mvnw clean -U install
```

### \"Cannot connect to database\"
- Verifique BD está rodando
- Verifique credenciais em application.properties
- Execute script de criação do BD

### \"Port 8080 already in use\"
Mude a porta em application.properties:
```properties
server.port=8081
```

## 🤝 Como Pedir Ajuda

1. **Leia a documentação** - Provavelmente está aqui
2. **Veja exemplos** - Procure feature similar
3. **Execute testes** - Ver como funciona
4. **Peça para revisor** - Seu code reviewer
5. **Use Slack** - Canal #dev-erp-api

## ✅ Próximas Ações

Quando terminar este guia:

1. ✅ Faça sua primeira feature pequena
2. ✅ Passe code review
3. ✅ Faça merge para main
4. ✅ Celebre! 🎉

## 📞 Contato

- **Tech Lead**: [nome]
- **Code Reviewer**: [nome]
- **Slack**: #dev-erp-api
- **Documentação**: [DOCS/](./00_INDICE.md)

---

**Bem-vindo ao projeto! 🚀**

**Última atualização:** Dezembro de 2025
