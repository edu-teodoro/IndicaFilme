**Contexto:** Você é um desenvolvedor Fullstack sênior e está trabalhando na promoção do projeto **Indica Filmes** do ambiente de desenvolvimento (dev) para o ambiente de homologação (homol) e posteriormente de produção. O projeto consiste em uma página de indicação de filmes por sorteio aleatório, com backend em **Java 21 + Spring Boot** e frontend em **JavaScript, HTML e CSS**.

**Repositório:** https://github.com/esturrardx15/IndicaFilme

**Relevantes:** No MongoDB Atlas, a organization é `Est.Teodoro`, o project  é `IndicaFilmes3lade`' e o cluster é `BladeIndica`. A aplicação utiliza a coleção `movies`. A API do TMDB é utilizada para enriquecer os filmes com informações adicionais.

**Sua tarefa é:** finalizar todas as configurações necessárias para que o projeto funcione corretamente em homologação, incluindo:

1.  **Comunicação com o banco de dados real (MongoDB Atlas):** Garantir que a aplicação se conecte ao MongoDB Atlas de forma estável, utilizando a URI correta e resolvendo problemas de timeout e SSL.
2.  **Integração com a API do TMDB:** Verificar se a chave da API (`TMDB_API_KEY`) está configurada e se os filmes estão sendo enriquecidos com dados (pôsteres, sinopse, etc.).
3.  **Publicação do site na web:** Ajustar o projeto para deploy em uma plataforma como Railway, Render ou Fly.io, utilizando o `Dockerfile` já existente.
4.  **Ajustes de SEO:** Configurar o `robots.txt` e o `sitemap.xml` para melhorar a indexação no Google.
5.  **Correção de bugs:** Diagnosticar e corrigir os problemas que surgirem durante esse processo.

**Regra fundamental:** Antes de dar qualquer resposta, revise todo o repositório para verificar exatamente onde estão os problemas. Não tente adivinhar nada; baseie-se estritamente nos fatos encontrados no código e na documentação. Se houver alguma dúvida, questione-me antes de prosseguir.

---

**Problemas já identificados (para sua referência):**

- **Conexão com MongoDB:** A aplicação está utilizando um repositório em memória (`InMemoryMovieRepository`) como fallback, pois o `MongoTemplate` não está sendo criado devido a falhas de conexão (timeouts, `UnknownHostException`, erros de SSL).
- **Variáveis de Ambiente:** As seguintes variáveis são obrigatórias e precisam estar configuradas corretamente:
    - `MONGODB_URI` (`mongodb+srv://Blade:basquete15@bladeindica.lhazjyp.mongodb.net/?appName=BladeIndica`)
    - `TMDB_API_KEY`
    - `ADMIN_API_KEY=1234566`
    - `CORS_ORIGINS`
- **Configuração de Perfis:** O perfil `local` deve ser desativado em homologação para que a aplicação tente conectar ao MongoDB real.
- **Endpoints Administrativos:** Requerem o header `X-Admin-Key` com o valor da `ADMIN_API_KEY`.
- **Estrutura do Modelo `Movie`:** Os campos são: `id`, `titulo`, `autor`, `sinopse`, `motivoRecomendacao`, `poster`, `status`, `generos`, `duracao`, `anoLancamento`, `notaDivina`, `notaPublico`, `plataformas`.

**Passos sugeridos para a correção:**

1.  Analise o arquivo `application.yml` e a classe `MongoFallbackConfig` para entender a lógica de fallback.
2.  Verifique se a URI do MongoDB está correta e se o IP da máquina de homologação está liberado no Atlas (Network Access).
3.  Aumente os timeouts na string de conexão, conforme indicado acima.
4.  Ative logs detalhados do driver MongoDB (`logging.level.org.mongodb.driver=DEBUG`) para diagnosticar falhas de conexão.
5.  Após estabilizar a conexão, valide se os dados estão sendo lidos e escritos no banco real (e não no fallback em memória).
6.  Configure as variáveis de ambiente no ambiente de homologação e no pipeline de deploy.
7.  Atualize o `sitemap.xml` com o domínio final de homologação.
8.  Teste todos os endpoints e a funcionalidade de sorteio.

**Entregáveis esperados:**
- Relatório detalhado dos problemas encontrados e suas causas.
- Instruções claras e objetivas para cada correção necessária (comandos, alterações em arquivos, configurações).
- Validação de que a aplicação está funcionando com o banco real e a API do TMDB.

**Inicie a análise agora. Qual é o primeiro passo?**