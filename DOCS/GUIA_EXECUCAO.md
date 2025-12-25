# ▶️ Guia de Execução

## 🚀 Executar a Aplicação

### Opção 1: Executar via Maven

```bash
# Terminal 1: Executar aplicação
cd erpapi
./mvnw spring-boot:run

# Log de sucesso esperado:
# [main] c.api.erp.ErpApplication : Started ErpApplication in X.XXX seconds
```

### Opção 2: Executar via JAR

```bash
# Build
./mvnw clean package

# Executar JAR
java -jar target/erp-0.0.1-SNAPSHOT.jar

# Com variáveis de ambiente
java -Dspring.profiles.active=prod \\
     -Dspring.datasource.url=jdbc:mysql://host:3306/db \\
     -Dspring.datasource.username=user \\
     -Dspring.datasource.password=pass \\
     -jar target/erp-0.0.1-SNAPSHOT.jar
```

### Opção 3: Executar em IDE

**IntelliJ IDEA**:
1. Clique em Run → Run 'ErpApplication'
2. Ou use Shift + F10 (Windows) / Control + R (Mac)

**VS Code** (com Extension Java):
1. Abra arquivo ErpApplication.java
2. Clique no botão \"Run\" acima da classe
3. Ou use Ctrl + F5

## ✅ Verificar se Está Rodando

```bash
# Teste de conectividade
curl http://localhost:8080/api/v1/health

# Esperado:
{
  \"status\": \"UP\"
}
```

## 📊 Acessar Documentação da API

A documentação Swagger fica disponível em:

```
http://localhost:8080/swagger-ui.html
```

## 🧪 Executar Testes

### Rodar Todos os Testes

```bash
./mvnw test
```

### Rodar Teste Específico

```bash
./mvnw test -Dtest=ProdutoServiceTest
./mvnw test -Dtest=ProdutoServiceTest#deveCriarProduto
```

### Rodar com Cobertura

```bash
./mvnw test jacoco:report
# Resultado: target/site/jacoco/index.html
```

## 📝 Logs

### Ver Logs em Tempo Real

Os logs são exibidos no console quando a aplicação está rodando.

### Nível de Logging

Configure em `application.properties`:

```properties
# Geral
logging.level.root=INFO

# Aplicação
logging.level.com.api.erp=DEBUG

# Spring Framework
logging.level.org.springframework=INFO

# Hibernate/JPA
logging.level.org.hibernate=INFO

# Requests HTTP
logging.level.org.springframework.web=DEBUG
```

## 🔌 Endpoints Básicos

### Autenticação

```bash
# Login
curl -X POST http://localhost:8080/api/v1/auth/login \\
  -H \"Content-Type: application/json\" \\
  -d '{
    \"email\": \"admin@empresa.com\",
    \"senha\": \"senha123\"
  }'
```

### Produtos

```bash
# Listar produtos
curl http://localhost:8080/api/v1/produtos \\
  -H \"Authorization: Bearer {token}\"

# Criar produto
curl -X POST http://localhost:8080/api/v1/produtos \\
  -H \"Content-Type: application/json\" \\
  -H \"Authorization: Bearer {token}\" \\
  -d '{
    \"codigo\": \"PROD001\",
    \"descricao\": \"Produto Teste\",
    \"unidadeMedidaId\": 1,
    \"tipo\": \"COMPRADO\"
  }'
```

## 🌐 WebSocket (Tempo Real)

Para testar WebSocket, acesse:

```
http://localhost:8080/sockjs.html
```

## 🐛 Modo Debug

### Debug em IDE

**IntelliJ IDEA**:
1. Coloque breakpoints (clique na linha)
2. Run → Debug 'ErpApplication'
3. Use F8 para step over, F7 para step into

**VS Code**:
1. Instale \"Debugger for Java\" extension
2. Coloque breakpoints
3. F5 para iniciar debug

### Debug Remote

```bash
# Iniciar com opções de debug
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \\
     -jar target/erp-0.0.1-SNAPSHOT.jar

# Conectar IDE na porta 5005
```

## 🗄️ Acessar Banco de Dados

### MySQL

```bash
mysql -h localhost -u erp_user -p erpapi

# Comandos úteis
SELECT * FROM produto;
SELECT * FROM usuario;
SHOW TABLES;
DESC produto;
```

### Ferramenta Visual

Use **MySQL Workbench**, **DBeaver** ou **PhpMyAdmin**:
- Host: localhost
- Port: 3306
- User: erp_user
- Password: (sua senha)
- Database: erpapi

## 📊 Monitoramento

### Health Check

```bash
curl http://localhost:8080/actuator/health

# Response
{
  \"status\": \"UP\",
  \"components\": {
    \"db\": {
      \"status\": \"UP\"
    }
  }
}
```

### Métricas

```bash
curl http://localhost:8080/actuator/metrics
```

## 🔧 Variáveis de Ambiente

### Executar com Variáveis

```bash
# Linux/Mac
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/erpapi
export SPRING_DATASOURCE_USERNAME=erp_user
export SPRING_DATASOURCE_PASSWORD=senha123
./mvnw spring-boot:run

# Windows (PowerShell)
$env:SPRING_DATASOURCE_URL=\"jdbc:mysql://localhost:3306/erpapi\"
$env:SPRING_DATASOURCE_USERNAME=\"erp_user\"
$env:SPRING_DATASOURCE_PASSWORD=\"senha123\"
.\\mvnw spring-boot:run
```

## 🛑 Parar a Aplicação

```bash
# Ctrl + C (qualquer SO)
# Ou feche a janela do terminal
```

## 📚 Próximos Passos

1. Leia [PRIMEIROS_PASSOS.md](PRIMEIROS_PASSOS.md)
2. Explore a documentação Swagger
3. Faça seu primeiro request à API
4. Comece a desenvolver!

---

**Última atualização:** Dezembro de 2025
