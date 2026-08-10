# 💰 Controle Financeiro Pessoal — API REST

API REST desenvolvida em **Java + Spring Boot** para gerenciamento de finanças pessoais: cadastro de receitas e despesas, categorização de gastos e geração de relatórios mensais (saldo e gastos por categoria).

Projeto criado como parte do meu portfólio, aplicando conceitos de arquitetura em camadas, persistência de dados e boas práticas de API REST.

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3** (Web, Data JPA, Validation)
- **H2 Database** (desenvolvimento local) / **PostgreSQL** (produção)
- **Swagger / OpenAPI** (documentação interativa)
- **Maven**
- **JUnit 5 + Mockito** (testes unitários)
- **Lombok**

## 🌐 Demo

- **API:** `https://SEU-LINK-AQUI.onrender.com`
- **Documentação interativa (Swagger):** `https://SEU-LINK-AQUI.onrender.com/swagger-ui.html`

> Atualize este link depois de fazer o deploy.

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas, separando responsabilidades:

```
Controller  → recebe requisições HTTP e devolve respostas
Service     → contém as regras de negócio
Repository  → conversa com o banco de dados (Spring Data JPA)
Model       → entidades que representam as tabelas
Exception   → tratamento centralizado de erros
```

## 📋 Funcionalidades

- [x] CRUD de Categorias
- [x] CRUD de Transações (receitas e despesas)
- [x] Relatório de saldo mensal
- [x] Relatório de gastos por categoria
- [x] Validação de dados de entrada
- [x] Tratamento centralizado de exceções
- [x] Testes unitários da camada de serviço
- [x] Documentação interativa com Swagger
- [x] Configuração para deploy em nuvem (PostgreSQL + variáveis de ambiente)
- [x] Autenticação com Spring Security + JWT
- [x] Isolamento de dados por usuário (cada um só vê os próprios registros)
- [ ] Deploy publicado (Render/Railway)

## ▶️ Como executar

**Pré-requisitos:** Java 17+ e Maven instalados.

```bash
# Clonar o repositório
git clone https://github.com/SEU-USUARIO/controle-financeiro.git
cd controle-financeiro

# Rodar a aplicação
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`.
O console do banco H2 fica disponível em `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:financas_db`, usuário `sa`, senha em branco).

## 🔐 Autenticação

A API usa **JWT (JSON Web Token)**. Praticamente todas as rotas exigem um token válido, exceto cadastro e login.

**1. Cadastre-se:**
```bash
curl -X POST http://localhost:8080/api/auth/registrar \
  -H "Content-Type: application/json" \
  -d '{"nome": "Maria Silva", "email": "maria@email.com", "senha": "senha123"}'
```

**2. Faça login e guarde o token retornado:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "maria@email.com", "senha": "senha123"}'
```

**3. Use o token em todas as próximas requisições:**
```bash
curl http://localhost:8080/api/categorias \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

> No Swagger, clique no botão **Authorize** (cadeado, no topo da página), cole `Bearer SEU_TOKEN_AQUI` e todas as chamadas feitas por lá já vão autenticadas.

Cada usuário só enxerga suas próprias categorias e transações — isso é validado tanto na consulta quanto na criação/edição.

## 📡 Endpoints principais

| Método | Rota | Autenticação | Descrição |
|--------|------|:---:|-----------|
| POST   | `/api/auth/registrar` | ❌ | Cria uma conta |
| POST   | `/api/auth/login` | ❌ | Faz login e retorna o token |
| GET    | `/api/categorias` | ✅ | Lista as categorias do usuário logado |
| POST   | `/api/categorias` | ✅ | Cria uma categoria |
| GET    | `/api/transacoes` | ✅ | Lista as transações do usuário logado |
| POST   | `/api/transacoes` | ✅ | Cria uma transação |
| PUT    | `/api/transacoes/{id}` | ✅ | Atualiza uma transação |
| DELETE | `/api/transacoes/{id}` | ✅ | Remove uma transação |
| GET    | `/api/transacoes/relatorios/saldo?mes=2026-08` | ✅ | Saldo do mês |
| GET    | `/api/transacoes/relatorios/gastos-por-categoria?mes=2026-08` | ✅ | Total gasto por categoria no mês |

### Exemplo de requisição

```bash
# Criar categoria (autenticado)
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -d '{"nome": "Alimentação", "descricao": "Supermercado e restaurantes"}'

# Criar transação (autenticado)
curl -X POST http://localhost:8080/api/transacoes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -d '{
    "descricao": "Compra do mês",
    "valor": 450.90,
    "data": "2026-08-05",
    "tipo": "DESPESA",
    "categoria": {"id": 1}
  }'
```

## 🧪 Rodando os testes

```bash
mvn test
```

## ☁️ Deploy em produção

O projeto já está preparado para deploy em plataformas como **Render** ou **Railway**:

1. Crie um banco PostgreSQL gratuito na plataforma escolhida
2. Configure as variáveis de ambiente no serviço:
   - `SPRING_PROFILES_ACTIVE=prod`
   - `DATABASE_URL` (URL JDBC do banco, ex: `jdbc:postgresql://host:5432/nome_do_banco`)
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `JWT_SECRET` (uma string longa e aleatória — **nunca** use o valor padrão do código em produção)
3. A plataforma injeta automaticamente a variável `PORT`, que a aplicação já usa
4. Build command: `mvn clean package -DskipTests`
5. Start command: `java -jar target/controle-financeiro-0.0.1-SNAPSHOT.jar`

Localmente, sem definir `SPRING_PROFILES_ACTIVE`, a aplicação continua usando H2 normalmente.

## 📌 Próximos passos

Este projeto está em desenvolvimento ativo como parte do meu aprendizado em Java/Spring Boot. As próximas features (autenticação, Swagger, Docker) estão listadas na seção de funcionalidades acima.

---

Desenvolvido por [Seu Nome](https://github.com/SEU-USUARIO) 👋
