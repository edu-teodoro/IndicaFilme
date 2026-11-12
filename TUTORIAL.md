IndicaFilme — Guia Completo de Configuração, Deploy e Boas Práticas

Este tutorial transforma este repositório em um site profissional com baixo custo. Abrange: o que configurar na IDE, o que NÃO commitar, ajustes no .gitignore, deploy gratuito (Render), e melhorias recomendadas.

1. Variáveis de Ambiente para configurar na IDE (IntelliJ)

- MONGODB_URI: string de conexão do MongoDB Atlas (com username e senha). Exemplo:
  mongodb+srv://USERNAME:PASSWORD@CLUSTER.mongodb.net/DATABASE
- ADMIN_API_KEY: chave administrativa (use a chave gerada, não compartilhe).
- TMDB_API_KEY: (opcional) chave da API TheMovieDB para enriquecer dados.
- FALLBACK_LOCAL_ENABLED: true/false (use `false` em produção)
- SERVER_PORT: porta do servidor (padrão 1533 / override para Docker)

Como configurar no IntelliJ:
- Run > Edit Configurations > escolha a configuração Spring Boot > Environment variables
- Adicione: MONGODB_URI=...;ADMIN_API_KEY=...;TMDB_API_KEY=...;FALLBACK_LOCAL_ENABLED=false
- Não salve estas variáveis em arquivos versionados.

2. O que NÃO commitar (regras essenciais)

- Arquivos de ambiente com segredos: `.env`, `.env.*`
- IDE configs pessoais: `.idea/` (exceto arquivos de projeto que não contenham segredos)
- Arquivos de run/debug locais: `.run/`
- Arquivos com dados sensíveis: chaves, certificados, arquivos de backup com dados reais

3. Ajuste recomendado para .gitignore

Certifique-se de incluir as linhas abaixo no `.gitignore` na raiz do repositório:

```
# Build
target/

# IDE
.idea/
*.iml

# Variables and secrets
.env
.env.*
!.env.example
.run/

# Logs and temp
*.log
logs/

# OS
.DS_Store
Thumbs.db
```

4. Passo-a-passo para deploy gratuito (Render.com, Docker)

Por que Render?
- Suporta serviços Docker, fácil integração com GitHub, plano gratuito com deploy automático.

Passos:
1. Criar conta em https://render.com (usando sua conta GitHub facilita)
2. New -> Web Service -> Connect a repository -> selecione `IndicaFilme`
3. Environment: Docker
4. Branch: main
5. Build Command: (deixe em branco se usar Dockerfile)
6. Start Command: deixe em branco (usará Dockerfile)
7. Health Check Path: /actuator/health
8. Environment Variables: adicionar `MONGODB_URI`, `ADMIN_API_KEY`, `TMDB_API_KEY` (se necessário)
9. Deploy

Alternativa grátis: Railway (similar) ou Fly.io (para apps Docker). Render é mais simples para iniciantes.

5. Como atualizar secrets em serviços (Render / CI / Docker Compose)

- Render: Dashboard > Service > Environment > Edit
- Docker Compose (local): use `.env` local (não versionar). Exemplo do serviço `app` no docker-compose.yml:
  ADMIN_API_KEY: "${ADMIN_API_KEY:}"

6. Passo-a-passo para limpeza de histórico (resumo)

Se precisar remover segredos do histórico (já realizado neste repositório), as etapas são:
- Fazer backup do repositório
- Regenerar credenciais
- Usar `git-filter-repo` (recomendado) ou `git filter-branch`
- Forçar push para o repositório remoto
- Notificar equipe para re-clonar

7. Melhorias aplicáveis (priorizadas)

- Ativar GitHub Secret Scanning e Push Protection (evita commits de secrets)
- Adicionar pre-commit com `git-secrets` ou `truffleHog` e Husky hooks
- Mover segredos para GitHub Actions Secrets ou Render/Platform secrets
- Automatizar deploy com GitHub Actions e secrets gerenciados
- Adotar um gerenciador de segredos (Vault, AWS Secrets Manager) quando a escala aumentar

8. Checklist final antes do deploy

- [ ] `.gitignore` atualizado
- [ ] `.env` não versionado
- [ ] MongoDB Atlas IP permitido para a instância
- [ ] Novas credenciais geradas e testadas localmente
- [ ] Deploy em Render com variáveis configuradas


Se quiser, posso gerar um arquivo `docs/DEPLOY.md` mais elegante com imagens e comandos prontos para copiar. Quer que eu gere agora?