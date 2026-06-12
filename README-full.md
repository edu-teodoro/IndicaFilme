# Indica Filmes

Aplicação de recomendação e sorteio de filmes com filtros por gênero, duração e década.

## Visão geral

O sistema é uma aplicação Spring Boot 3.4.3 em Java 21 com front-end estático servido pelo próprio backend. O perfil `local` roda com repositório em memória (sem MongoDB) e dados de filme carregados de `FilmeData.java`. Com configuração de banco real, o app usa MongoDB e pode enriquecer filmes com informações de TMDB.

## Estrutura do projeto

- `pom.xml` - dependências Maven
- `Dockerfile` - build e runtime do container
- `src/main/java` - código-fonte Java
- `src/main/resources/static` - front-end estático (HTML, CSS, JS)
- `src/main/resources/application.yml` - configuração padrão
- `src/main/resources/application-local.yml` - perfil de desenvolvimento local

## Executar localmente

### Pré-requisitos

- Java 21
- Maven 3.x
- opcional: Docker, MongoDB, TMDB API Key

### Rodar com perfil local

O perfil local é voltado para desenvolvimento rápido e não exige MongoDB.

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

ou

```powershell
java -jar target/indica-filmes-1.0.0.jar --spring.profiles.active=local
```

A aplicação ficará disponível em `http://localhost:8086`.

### Rodar sem perfil local (MongoDB real)

Quando o perfil local não estiver ativo, o projeto tenta se conectar ao MongoDB pelo `spring.data.mongodb.uri`.

Defina as variáveis de ambiente:

- `MONGODB_URI` - URI de conexão MongoDB
- `TMDB_API_KEY` - chave para buscar dados de filmes no TMDB
- `ADMIN_API_KEY` - chave de API para proteger endpoints admin
- `CORS_ORIGINS` - origens permitidas para CORS, separadas por vírgula

Exemplo:

```powershell
$env:MONGODB_URI="mongodb://localhost:27017/indicafilmes"
$env:TMDB_API_KEY="sua_chave_tmdb"
$env:ADMIN_API_KEY="sua_chave_admin"
$env:CORS_ORIGINS="http://localhost:8080,http://localhost:3000"
mvn spring-boot:run
```

Por padrão, o servidor roda em `http://localhost:8080`.

## Configurações de perfil

### Perfil local

Ativa `application-local.yml` e desabilita a autoconfiguração de MongoDB, permitindo rodar sem dependências externas.

- porta: `8086`
- admin key padrão: `indica-filmes-admin-secret-2026` (pode ser sobrescrito por `ADMIN_API_KEY`)
- Swagger habilitado via `springdoc.swagger-ui.enabled=true`

### Perfil real / produção

Quando não usar `-Dspring-boot.run.profiles=local`, o aplicativo usa `application.yml` e tenta conectar ao MongoDB.

Os valores suportados são:

- `spring.data.mongodb.uri`
- `tmdb.api-key`
- `admin.api-key`
- `cors.allowed-origins`
- `server.port`

### Como usar o MongoDB verdadeiro

1. Instale o MongoDB local ou use um serviço em nuvem.
2. Crie o banco de dados e coleções automaticamente no primeiro startup.
3. Defina `MONGODB_URI` com a conexão apropriada.
4. Remova a opção `-Dspring-boot.run.profiles=local` ou use perfil vazio.

Exemplo de URI:

```powershell
$env:MONGODB_URI="mongodb://REDACTED"
```

## Endpoints principais

- `GET /api/v1/movies/random` - sortear filme com filtros
- `GET /api/v1/movies` - listar filmes com filtros
- `GET /api/v1/movies/{id}` - obter filme por ID
- `GET /api/v1/movies/titulo/{titulo}` - buscar filme por título

## Melhorias de UI/UX implementadas

- Para o filtro de duração, apenas uma opção pode ser escolhida por vez.
- Ao clicar em outra duração, a seleção anterior é desmarcada automaticamente.
- O formulário de década aceita ano e converte para década (ex: 1995 -> 1990).
- O botão `Sortear Filme` e `Ver todos os Filmes` exigem ao menos um filtro.
- Botão `Limpar Filtros` redefine as tags e o campo de busca.

## Melhorias de segurança e vulnerabilidades

- Spring Security protege rotas admin com `X-Admin-Key` e validação de timing-safe.
- Limite de taxa para endpoints admin restrito a 10 requisições por minuto por IP.
- CSP e cabeçalhos de segurança habilitados no `SecurityConfig`.
- CORS configurável por `CORS_ORIGINS`.
- `csrf` está desabilitado apenas porque a API é consumida por front-end estático; em produção avalie reativar para rotas de escrita.

### Recomendações de segurança adicionais

- Não deixe `ADMIN_API_KEY` em texto claro no repositório.
- Use variáveis de ambiente no ambiente de deploy.
- Habilite HTTPS no servidor ou no proxy reverso.
- Monitore logs e falhas de autenticação.

## Deploy em nuvem e hospedagem gratuita

### Docker

Build e execute:

```powershell
docker build -t indica-filmes .
docker run -p 8080:8080 -e MONGODB_URI="..." -e TMDB_API_KEY="..." -e ADMIN_API_KEY="..." indica-filmes
```

### Hospedagem gratuita recomendada

- Railway.app - suporte para container Docker e MongoDB gratuito no plano de desenvolvimento.
- Render.com - deploy de web services com Docker.
- Fly.io - deploy de aplicações Java pequenas.
- MongoDB Atlas - banco MongoDB gratuito com cluster free tier.

### O que configurar para subir na web

- `server.port` para 8080 ou variável de ambiente definida pelo provedor.
- `MONGODB_URI` apontando para MongoDB remoto.
- `TMDB_API_KEY` e `ADMIN_API_KEY` como secrets.
- `CORS_ORIGINS` para o domínio da aplicação hospedada.

## Observações

- O Dockerfile já monta o `jar` gerado pelo Maven.
- Em desenvolvimento local, o app usa `application-local.yml` para evitar dependência de MongoDB.
- Para produção, use `application.yml` com variáveis de ambiente.
