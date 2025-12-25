# 🚀 Guia de Instalação

## 📋 Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- **Java 17+**: [Download](https://www.oracle.com/java/technologies/downloads/#java17)
- **Maven 3.6+**: [Download](https://maven.apache.org/download.cgi)
- **Git**: [Download](https://git-scm.com/downloads)
- **Banco de Dados**: MySQL 8.0+ ou PostgreSQL 13+
- **Git Bash** (Windows): Para executar scripts bash

## 🔧 Configuração do Ambiente

### 1. Clone o Repositório

```bash
git clone https://github.com/androsgithub/erpapi.git
cd erpapi
```

### 2. Banco de Dados

**MySQL**

```sql
CREATE DATABASE erpapi CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'erp_user'@'localhost' IDENTIFIED BY 'senha_segura_123';
GRANT ALL PRIVILEGES ON erpapi.* TO 'erp_user'@'localhost';
FLUSH PRIVILEGES;
```

**PostgreSQL**

```sql
CREATE DATABASE erpapi;
CREATE USER erp_user WITH PASSWORD 'senha_segura_123';
ALTER ROLE erp_user SET client_encoding TO 'utf8';
GRANT ALL PRIVILEGES ON DATABASE erpapi TO erp_user;
```

### 3. Configuração da Aplicação

Crie o arquivo `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/erpapi
spring.datasource.username=erp_user
spring.datasource.password=senha_segura_123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# Application
server.port=8080
spring.application.name=erp-api
spring.profiles.active=dev

# JWT
jwt.secret=sua-chave-secreta-muito-longa-com-caracteres-especiais-123456
jwt.expiration=86400000

# Logging
logging.level.root=INFO
logging.level.com.api.erp=DEBUG

# Jackson
spring.jackson.default-property-inclusion=non_null
spring.jackson.serialization.write-dates-as-timestamps=false
```

### 4. Variáveis de Ambiente (Segurança)

Crie um arquivo `.env` na raiz do projeto (não commitar):

```bash
# Database
DB_HOST=localhost
DB_PORT=3306
DB_NAME=erpapi
DB_USER=erp_user
DB_PASSWORD=senha_segura_123

# JWT
JWT_SECRET=sua-chave-secreta-muito-longa-com-caracteres-especiais-123456

# Application
APP_PORT=8080
APP_PROFILE=dev
```

## 📦 Build do Projeto

### Build Maven

```bash
# Limpar e compilar
./mvnw clean install

# Apenas compilar
./mvnw compile

# Pular testes
./mvnw clean install -DskipTests
```

### Verificar Build

```bash
./mvnw clean package
# Procure por: BUILD SUCCESS
# Arquivo gerado: target/erp-0.0.1-SNAPSHOT.jar
```

## 🗄️ Migrations do Banco de Dados

Se usar Flyway ou Liquibase:

```bash
# Aplicar migrations
./mvnw flyway:migrate

# Ver status
./mvnw flyway:info
```

Se usar Hibernate (automático):

```properties
spring.jpa.hibernate.ddl-auto=update  # DEV ONLY
spring.jpa.hibernate.ddl-auto=validate # PROD
```

## ✅ Verificações Pós-Instalação

### 1. Testar Compilação

```bash
./mvnw clean test
```

### 2. Testar Conectividade BD

```bash
mysql -h localhost -u erp_user -p erpapi -e \"SELECT VERSION();\"
```

### 3. Verificar Dependências

```bash
./mvnw dependency:tree
```

## 🐛 Troubleshooting

### Erro: Java versão incompatível

```bash
java -version
# Deve retornar Java 17 ou superior
```

**Solução**: Instale Java 17+ ou configure JAVA_HOME

### Erro: Conexão com Banco de Dados

```
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
```

**Verificar**:
1. Banco de dados está rodando?
2. Credenciais estão corretas?
3. Porta está correta? (3306 MySQL, 5432 PostgreSQL)
4. Firewall está bloqueando?

### Erro: Porta 8080 em uso

```bash
# Mudar porta no application.properties
server.port=8081

# Ou matar processo usando a porta
# Windows
netstat -ano | findstr :8080
taskkill /PID {PID} /F

# Linux
lsof -i :8080
kill -9 {PID}
```

### Erro: Build fails

Limpe e reconfigure:

```bash
./mvnw clean -U install
rm -rf ~/.m2/repository/com/api
```

## 📚 Próximos Passos

1. Leia [GUIA_EXECUCAO.md](GUIA_EXECUCAO.md) para executar a aplicação
2. Leia [PRIMEIROS_PASSOS.md](PRIMEIROS_PASSOS.md) para começar a desenvolver
3. Leia [ESTRUTURA_PROJETO.md](ESTRUTURA_PROJETO.md) para entender a organização

---

**Última atualização:** Dezembro de 2025
