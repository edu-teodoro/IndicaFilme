# Indica Filmes

Sistema de recomendação e sorteio de filmes com filtros por gênero, duração e década.

## Como executar

### Rodar localmente com perfil local

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

A aplicação ficará disponível em `http://localhost:8086`.

### Rodar usando MongoDB real

Defina as variáveis de ambiente:

- `MONGODB_URI`
- `TMDB_API_KEY`
- `ADMIN_API_KEY`
- `CORS_ORIGINS`

Execute sem `-Dspring-boot.run.profiles=local`:

```powershell
mvn spring-boot:run
```

## Notas rápidas

- Perfil `local` usa dados em memória e não exige MongoDB.
- Ao usar o filtro de duração, apenas uma opção pode ser selecionada por vez.
- O Dockerfile já está pronto para build e execução em container.
- Para deploy gratuito, considere Railway, Render ou Fly.io com MongoDB Atlas.
