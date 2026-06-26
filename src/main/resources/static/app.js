/*
    Indica Filmes - app.js (v1)

    Link para formulário Google de sugestão de filmes.
*/
const SUGERIR_FILME_LINK = "https://forms.gle/B1cRGsWP3ENVe6kE9";

// Estado dos filtros selecionados (até 3 por tipo)
const estado = {
    generos: [],
    duracoes: [],
    decadas: []
};

// Filtros ativos no momento do ultimo sorteio (para "Sortear Outro")
let ultimosCriteria = null;

document.addEventListener("DOMContentLoaded", () => {

    // Theme Toogle (Dark/Light Mode)
    const themeToggle = document.getElementById("theme-toggle");
    const htmlElement = document.documentElement;
    const savedTheme = localStorage.getItem("theme") || "dark";

    // Aplica tema salvo
    if (savedTheme === "light") {
        htmlElement.style.colorScheme = "light";
        document.body.classList.add("light-mode");
        themeToggle.querySelector(".theme-icon").textContent = "🦇";
    } else {
        htmlElement.style.colorScheme = "dark";
        document.body.classList.remove("light-mode");
        themeToggle.querySelector(".theme-icon").textContent = "🧛🏽";
    }

    // Toggle de tema ao clicar
    themeToggle.addEventListener("click", () => {
        const isLightMode = document.body.classList.contains("light-mode");

        if (isLightMode) {
            document.body.classList.remove("light-mode");
            htmlElement.style.colorScheme = "dark";
            localStorage.setItem("theme", "dark");
            themeToggle.querySelector(".theme-icon").textContent = "🧛🏽";
        } else {
            document.body.classList.add("light-mode");
            htmlElement.style.colorScheme = "light";
            localStorage.setItem("theme", "light");
            themeToggle.querySelector(".theme-icon").textContent = "🦇";
        }
    });

    // Configura cliques nas tags
    configurarTags("generos-tags", estado.generos, 3, false);
    configurarTags("duracoes-tags", estado.duracoes, 1, false);
    configurarTags("decadas-tags", estado.decadas, 3, true);

    // Input de ano digitado -> converte para inicio da década
    document.getElementById("decada-custom").addEventListener("keydown", (e) => {
        if (e.key === "Enter") {
            const ano = parseInt(e.target.value);
            if (!isNaN(ano) && ano >= 1900 && ano <= new Date().getFullYear()) {
                const decada = Math.floor(ano / 10) * 10;
                if (estado.decadas.length < 3 && !estado.decadas.includes(decada)) {
                    estado.decadas.push(decada);
                    document.querySelectorAll("#decadas-tags .tag").forEach(tag => {
                        if (parseInt(tag.dataset.valor) === decada) tag.classList.add("ativa");
                    });
                }
                e.target.value = "";
            }
        }
    });

    // Botão de sortear
    document.getElementById("sortear-btn").addEventListener("click", async () => {
        const {
            generos,
            duracoes,
            decadas
        } = estado;
        const anoDigitado = parseInt(document.getElementById("decada-custom").value);
        let decadasFinais = [...decadas];
        let anoExato = null;

        // Se digitou um ano válido e não pressionou Enter, converte para decada
        if (!isNaN(anoDigitado) && anoDigitado >= 1900 && anoDigitado <= new Date().getFullYear()) {
            anoExato = anoDigitado;
            const decada = (Math.floor(anoDigitado / 10) * 10);
            if (!decadasFinais.includes(decada)) {
                decadasFinais.push(decada);
            }
        }

        // Valida se tem ao menos um filtro
        if (!generos.length && !duracoes.length && !decadasFinais.length) {
            mostrarMensagem("Por favor, selecione pelo menos um filtro ou digite um ano.");
            return;
        }

        ultimosCriteria = {
            generos: [...generos],
            duracoes: [...duracoes],
            decadas: decadasFinais,
            anoExato
        };
        await sortearFilme(ultimosCriteria);
    });

    // Botão Listar
    document.getElementById("listar-btn").addEventListener("click", async () => {
        const {
            generos,
            duracoes,
            decadas
        } = estado;
        const anoDigitado = parseInt(document.getElementById("decada-custom").value);
        let decadasFinais = [...decadas];
        let anoExato = null;

        // Se digitou um ano válido e não pressionou Enter, converte para década
        if (!isNaN(anoDigitado) && anoDigitado >= 1900 && anoDigitado <= new Date().getFullYear()) {
            anoExato = anoDigitado;
            const decada = (Math.floor(anoDigitado / 10) * 10);
            if (!decadasFinais.includes(decada)) {
                decadasFinais.push(decada);
            }
        }

        // Valida se tem ao menos um filtro
        if (!generos.length && !duracoes.length && !decadasFinais.length) {
            mostrarMensagem("Por favor, selecione pelo menos um filtro ou digite um ano.");
            return;
        }

        await listarFilmes({
            generos: [...generos],
            duracoes: [...duracoes],
            decadas: decadasFinais,
            anoExato
        });
    });

    // Botão Sortear Outro (na tela de detalhes)
    document.getElementById("sortear-novamente-btn").addEventListener("click", async () => {
        if (ultimosCriteria) await sortearFilme(ultimosCriteria);
    });

    // Botão voltar
    document.getElementById("voltar-btn").addEventListener("click", () => {
        document.getElementById("tela-detalhes").classList.remove("fade-in");
        document.getElementById("tela-detalhes").style.display = "none";
        document.getElementById("tela-principal").classList.remove("fade-in");
        document.getElementById("tela-principal").style.display = "block";
        // Força reflow para sincronizar com CSS animation
        document.getElementById("tela-principal").offsetHeight;
        document.getElementById("tela-principal").classList.add("fade-in");
    });

    // Botão limpar filtros
    document.getElementById("limpar-btn").addEventListener("click", () => {
        estado.generos.length = 0;
        estado.duracoes.length = 0;
        estado.decadas.length = 0;
        document.querySelectorAll(".tag.ativa").forEach(tag => tag.classList.remove("ativa"));
        document.getElementById("decada-custom").value = "";
        document.getElementById("results-container").innerHTML =
            `<div class="card mensagem-card" id="description-card">
                <h3>Seu próximo filme está aqui 🎬</h3>
                <p>Selecione ao menos um filtro e clique em <strong>Sortear Filme</strong> para descobrir o que assistir hoje!</p>
            </div>`;
    });

    // Footer com ano dinâmico
    const anoAtual = new Date().getFullYear();
    const footerYear = document.getElementById("footer-year");
    if (footerYear) footerYear.textContent = anoAtual;

    // Limite máximo do input de década = ano atual
    document.getElementById("decada-custom").max = anoAtual;

    // Gradiente que segue o mouse
    const gradient = document.getElementById("gradient");
    document.addEventListener("mousemove", (e) => {
        gradient.style.background =
            `radial-gradient(circle at ${e.clientX}px ${e.clientY}px, rgba(229,1,43,0.15), #121212 55%)`;
    });

    // Carrega e popula os rolos de filmes nas laterais
    preencherRolos().catch(err => console.error("Erro ao inicializar rolos:", err));
});

/**
Configura o comportamento de clique nas tags de filtro
*/
function configurarTags(containerId, lista, max, isNumerico) {
    document.querySelectorAll(`#${containerId} .tag`).forEach(tag => {
        tag.addEventListener("click", () => {
            const raw = tag.dataset.valor;
            const valor = isNumerico ? parseInt(raw) : raw;

            if (tag.classList.contains("ativa")) {
                tag.classList.remove("ativa");
                const idx = lista.indexOf(valor);
                if (idx !== -1) lista.splice(idx, 1);
            } else {
                if (containerId === "duracoes-tags") {
                    document.querySelectorAll(`#${containerId} .tag`).forEach(t => t.classList.remove("ativa"));
                    lista.length = 0;
                } else if (lista.length >= max) {
                    return;
                }
                tag.classList.add("ativa");
                lista.push(valor);
            }
        });
    });
}

/**
    Mostra os parametros da URL com multiplos valores por chave
*/
function montarParams({
    generos,
    duracoes,
    decadas
}) {
    const params = new URLSearchParams();
    generos.forEach(g => params.append("generos", g));
    duracoes.forEach(d => params.append("duracoes", d));
    decadas.forEach(d => params.append("decadas", d));
    return params.toString();
}

/**
    Sorteia UM filme e abre a tela de detalhes
*/
async function sortearFilme(criteria) {
    mostrarLoading("Sorteando um filme...");
    setBotoesDisabled(true);
    try {
        const response = await fetch(`/api/v1/movies/random?${montarParams(criteria)}`);
        const dados = await response.json();

        if (!response.ok) {
            mostrarMensagem(escapeHtml(dados.message || "Erro ao sortear filme."));
            return;
        }

        // Se a resposta for "Desculpe, não encontramos nenhum filme que corresponda aos filtros selecionados. Que tal tentar outros?" (Lista vazia no backend).
        if (dados === "Desculpe, não encontramos nenhum filme que corresponda aos filtros selecionados. Que tal tentar outros?") {
            mostrarMensagem("Desculpe, não encontramos nenhum filme que corresponda aos filtros selecionados. Que tal tentar outros?");
            return;
        }

        abrirTelaDetalhes(dados, criteria);
    } catch (err) {
        console.error(err);
        mostrarMensagem("Erro ao conectar com servidor. Por favor, tente novamente.");
    } finally {
        setBotoesDisabled(false);
    }
}

/**
    Lista TODOS os filmes no painel de resultados
*/
async function listarFilmes(criteria) {
    mostrarLoading("Buscando filmes...");
    setBotoesDisabled(true);
    try {
        const response = await fetch(`/api/v1/movies?${montarParams(criteria)}`);
        const dados = await response.json();

        if (!response.ok) {
            mostrarMensagem(escapeHtml(dados.message || "Erro ao buscar filmes."));
            return;
        }

        // Se a resposta for "Desculpe, não encontramos nenhum filme que corresponda aos filtros selecionados. Que tal tentar outros?" (Lista vazia no backend).
        if (dados === "Desculpe, não encontramos nenhum filme que corresponda aos filtros selecionados. Que tal tentar outros?") {
            mostrarMensagem("Desculpe, não encontramos nenhum filme que corresponda aos filtros selecionados. Que tal tentar outros?");
            return;
        }

        const container = document.getElementById("results-container");
        container.innerHTML = `<h3 class="lista-titulo">${dados.length} filme(s) encontrado(s):</h3>`;
        dados.forEach(filme => container.appendChild(criarCardResumo(filme)));
    } catch (err) {
        console.error(err);
        mostrarMensagem("Erro ao conectar com servidor. Por favor, tente novamente.");
    } finally {
        setBotoesDisabled(false);
    }
}

/**
    Abre a tela de detalhes completos de um filme sorteado
*/
function abrirTelaDetalhes(filme, criteria) {
    document.getElementById("tela-principal").style.display = "none";
    const telaDetalhes = document.getElementById("tela-detalhes");
    telaDetalhes.style.display = "block";
    telaDetalhes.classList.add("fade-in");

    // Mostra os filtros usados no topo
    const resumo = document.getElementById("filtros-resumo");
    const partes = [];
    if (criteria.generos.length) partes.push(criteria.generos.join(", "));
    if (criteria.duracoes.length) partes.push(criteria.duracoes.join(", "));
    if (criteria.decadas.length) partes.push(criteria.decadas.join(", "));
    resumo.textContent = "Filtros: " + partes.join(" . ");

    // Mostrar o conteúdo de detalhes do filme (com escapeHtml para prevenir XSS)
    const content = document.getElementById("filme-detalhes-content");
    const generos = filme.generos?.map(g => escapeHtml(g)).join(", ") || "Não informado";
    const plataformas = filme.plataformas?.length ?
        filme.plataformas.map(p =>
            `<a href="${escapeUrl(p.url)}" target="_blank" rel="noopener noreferrer" class="plataforma-link">${escapeHtml(p.nome)}</a>`
        ).join(" ") :
        "Não disponível";

    const posterUrl = escapeUrl(filme.poster);
    const posterHtml = filme.poster ?
        `<div class="poster-container"><img src="${posterUrl}" alt="Poster de ${escapeHtml(filme.titulo)}" class="poster-img" /></div>` :
        "";

    content.innerHTML = `
        <div class="card card-detalhes">

            ${posterHtml}

            <h1 class="filme-titulo">${escapeHtml(filme.titulo)}</h1>

            <div class="filme-meta">
                <span> ${escapeHtml(filme.anoLancamento) || "-"}</span>
                <span> ${generos}</span>
                <span> ${filme.duracao ? escapeHtml(filme.duracao + " min") : "-"}</span>
                <span> ${escapeHtml(filme.autor) || "-"}</span>
            </div>

            <div class="notas-container">
                <div class="nota-box">
                    <span class="nota-label"> Nota Divina(@obladepontokom) </span>
                    <span class="nota-valor">${escapeHtml(filme.notaDivina) || "-"}</span>
                </div>
                <div class="nota-box">
                    <span class="nota-label"> Nota do Público </span>
                    <span class="nota-valor">${escapeHtml(filme.notaPublico) || "-"}</span>
                </div>
                <div class="nota-box nota-media">
                    <span class="nota-label"> Média </span>
                    <span class="nota-valor">${escapeHtml(filme.mediaNotas) || "-"}</span>
                </div>
            </div>
        
            <div class="sinopse-container">
                <h4>Sinopse</h4>
                <p>${escapeHtml(filme.sinopse) || "Sinopse não disponível."}</p>
            </div>

            ${filme.motivoRecomendacao ? `
            <div class="motivo-container">
                <h4>Por que assistir? (@obladepontokom) </h4>
                <p>${escapeHtml(filme.motivoRecomendacao)}</p>
            </div>` : ""}

            <div class="plataformas-container">
                <h4>Onde assistir?</h4>
                <div class="plataformas-lista">${plataformas}</div>
            </div>

            <div class="compartilhar-container">
                <h4>Compartilhar</h4>
                <div class="compartilhar-lista">
                    <a href="https://wa.me/?text=${encodeURIComponent(`Olha esse filme que encontrei no Indica Filmes: ${filme.titulo} - ${window.location.href}`)}" target="_blank" rel="noopener noreferrer" class="compartilhar-link whatsapp" title="Compartilhar no WhatsApp">
                        <span class="compartilhar-icon>📱</span>
                    <i class="fab fa-whatsapp"></i>
                        <span>WhatsApp</span>
                    </a>
                    <a href="https://twitter.com/intent/tweet?text=${encodeURIComponent(`Olha esse filme que encontrei no Indica Filmes: ${filme.titulo}`)}&url=${encodeURIComponent(window.location.href)}" target="_blank" rel="noopener noreferrer" class="compartilhar-link twitter" title="Compartilhar no Twitter">
                        <i class="fab fa-twitter"></i>
                        <span>Twitter</span>
                    </a>
                    <a href="https://instagram.com" target="_blank" rel="noopener noreferrer" class="compartilhar-link instagram" title="Compartilhar no Instagram" onclick="alert('Copie o link do filme e compartilhe no Instagram: ' + window.location.href + ' - ' + '${escapeHtml(filme.titulo)}'); return false;">
                        <i class="fab fa-instagram"></i>
                        <span>Instagram</span>
                    </a>
                    <a href="https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(window.location.href)}" target="_blank" rel="noopener noreferrer" class="compartilhar-link facebook" title="Compartilhar no Facebook">
                        <i class="fab fa-facebook"></i>
                        <span>Facebook</span>
                    </a>
                </div>
            </div>

            <div class="sugerir-container">
                <p>Conhece um filme incrível?
                    <a href="${SUGERIR_FILME_LINK}" target="_blank" rel="noopener noreferrer" class="sugerir-link"><u>Sugerir Filme</u></a>
                </p>
            </div>
        </div>
    `;

    window.scrollTo(0, 0);
}

/*
    Cria um card resumido (para lista de filmes)
*/
function criarCardResumo(filme) {
    const card = document.createElement("div");
    card.classList.add("card", "card-resumo");

    const posterUrl = escapeUrl(filme.poster);
    const miniPoster = filme.poster ?
        `<img src="${posterUrl}" alt="${escapeHtml(filme.titulo)}" class="poster-mini" />` : "";

    const sinopseText = filme.sinopse ? escapeHtml(filme.sinopse.substring(0, 300)) + (filme.sinopse.length > 300 ? "..." : "") : "";

    card.innerHTML = ` 
        <div class="card-resumo-content">
            ${miniPoster} 
            <div class="card-resumo-info">
                <h3>${escapeHtml(filme.titulo)}<span class="ano">(${escapeHtml(filme.anoLancamento) || "-"})</span></h3><p class="generos-resumo">${filme.generos?.map(g => escapeHtml(g)).join(", ") || "-"} . ${filme.duracao ? escapeHtml(filme.duracao) + " min" : "-"}</p> 
         <div class="notas-resumo">
            <span>${escapeHtml(filme.notaDivina) || "-"}</span> 
            <span>${escapeHtml(filme.notaPublico) || "-"}</span>
            <span>${escapeHtml(filme.mediaNotas) || "-"}</span>
        </div> 
        <p class="sinopse-resumo">${sinopseText}</p>
        </div>
        </div>
    `;
    card.addEventListener("click", () => abrirTelaDetalhes(filme, ultimosCriteria || {
        generos: [],
        duracoes: [],
        decadas: []
    }));
    return card;
}

/* Carrega todos os filmes disponiveis para popular os rolos */
async function carregarFilmesParaRolos() {
    try {
        const response = await fetch("/api/v1/movies");
        if (!response.ok) {
            console.warn("API /api/v1/movies retornou erro, usando fallback de FilmeData");
            return obterFilmesPadraoFilmeData();            
        }
        const filmes = await response.json();

        // Se a API retorna array vazio, usar fallback
        if (!Array.isArray(filmes) || filmes.length === 0) {
            console.warn("Nenhum filme no banco de dados, usando FilmeData padrão");
            return obterFilmesPadraoFilmeData();
        }

        return filmes;
    } catch (err) {
        console.error("Erro ao carregar filmes para rolos:", err);
        return obterFilmesPadraoFilmeData();
    }
}

/*  Retorna dados padrão do FilmeData.java como fallback
    Usados quando a API retorna lista vazia ou há erro de conexão
*/
function obterFilmesPadraoFilme() {
    const filmesPadrao = [
        {
            id:"1",
            titulo: "Senhor dos Anéis: A Comunidade do Anel", 
            autor: "Peter Jackson",
            generos: ["Fantasia", "Aventura", "Drama"], 
            duracao: 208,
            anoLancamento: 2001,
            sinopse: "Um hobbit despretensiosos encontra um anel invisível e aprende que deve lutar contra o poderoso senhor dos anéis.",
            notaDivina: 9.0,
            notaPublico: 8.8,
            motivoRecomendacao: "Uma épica visualmente deslumbrante que adapta brilhantemente um clássico da literatura, com uma narrativa envolvente e efeitos especiais revolucionários.",
            poster: "https://br.web.img3.acsta.net/medias/nmedia/18/92/91/32/20224832.jpg"
        },
        {
           id: "2",
           titulo: "Interestelar",
           autor: "Christopher Nolan",
           generos: ["Ficção Científica", "Drama", "Aventura"],
           duracao: 169,
           anoLancamento: 2014,
           sinopse: "Um grupo de astronautas viaja através de um buraco de minhoca perto de Júpiter para encontrar um novo lar para a humanidade.",
           notaDivina: 8.6,
           notaPublico: 8.6,
           motivoRecomendacao: "Uma obra-prima de ficção científica que combina visão ambiciosa com emoção humana, apresentando conceitos complexos de forma acessível.",
           poster: "https://acdn-us.mitiendanube.com/stores/004/687/740/products/pos-01876-4c8ebd420e08f8359717181254801917-1024-1024.webp"                      
        },
        {
           id: "3", 
           titulo: "Parasita",
           autor: "Bong Joon-ho",
           generos: ["Drama", "Thriller"],
           duracao: 132,
           anoLancamento: 2019,
           sinopse: "A família Kim, de baixa renda, planeja uma operação para se infiltrar na residência de uma família rica.",
           notaDivina: 8.6,
           notaPublico: 8.5,
           motivoRecomendacao: "Um filme brilhantemente executado que mistura humor, drama e comentário social, com uma narrativa que mantém você na beira do assento.",
           poster: "https://image.tmdb.org/t/p/w600_and_h900_bestv2/7IiTTgloJzvGI1TAYymCfbfl3vT.jpg"                                                
        },
        {
          id:  "4",
          titulo: "De Volta para o Futuro",
          autor: "Robert Zemeckis",
          generos: ["Ficção Científica", "Comédia", "Aventura"],
          duracao: 116,
          anoLancamento: 1985,
          sinopse: "Um adolescente é acidentalmente enviado ao passado em uma máquina do tempo e deve garantir que seus pais se apaixonem para que ele exista.",
          notaDivina: 8.5,
          notaPublico: 8.5,
          motivoRecomendacao: "Um clássico atemporal que combina humor, ação e uma história de viagem no tempo perfeitamente executada, com personagens memoráveis.",
          poster: "https://m.media-amazon.com/images/M/MV5BZDcyNmYxN2QtMmViZS00NTQ4LTlhZTAtZjc4MzY2Yjg0M2ZmXkEyXkFqcGc@._V1_.jpg"
        },
        {
          id: "5",
          titulo: "Clube da Luta",
          autor:  "David Fincher",
          generos:["Drama", "Thriller"],
          duracao: 139,
          anoLancamento: 1999,
          sinopse: "Um insomne contador conhece um vendedor de sabão carismático, e ambos formam um clube secreto de luta que evolui em algo muito mais sombrio.",
          notaDivina: 8.8,
          notaPublico: 8.8,
          motivoRecomendacao: "Um thriller psicológico visceral e inovador que permanece relevante e impactante, com uma reviravolta final memorável.",
          poster: "https://m.media-amazon.com/images/I/61vKJHwfCUL._AC_UF894,1000_QL80_.jpg"
        },
        {
          id: "6",
          titulo: "Whiplash",
          autor: "Damien Chazelle",
          generos: ["Drama", "Música"],
          duracao: 107,
          anoLancamento: 2014,
          sinpose: "Um baterista ambicioso num colégio de artes recebe a atenção de um regente abusivo que o coloca em um jogo perturbador de desempenho.",
          notaDivina: 8.5,
          notaPublico: 8.5,
          motivoRecomendacao: "Um drama intenso e angustiante sobre obsessão e excelência, com cenas de músicas incríveis e uma tensão praticamente insuportável.",
          poster: "https://rollingstone.com.br/wp-content/uploads/legacy/2014/img-1026474-whiplash-poster.jpg"
        },
        {
          id: "7",
          titulo: "Oldboy",
          autor: "Park Chan-wook",
          generos: ["Ação", "Thriller", "Crime"],
          duracao: 120,
          anoLancamento: 2003,
          sinopse: "Um homem é preso em um quarto anônimo por 15 anos, depois é libertado e deve descobrir por que foi sequestrado e quem foi o responsável.",
          notaDivina: 8.4,
          notaPublico: 8.4,
          motivoRecomendacao: "Um thriller de vingança visualmente impressionante com uma reviravolta perturbadora, abordando temas de justiça e redenção.",
          poster: "https://image.tmdb.org/t/p/original/pWDtjs568ZfOTMbURQBYuT4Qxka.jpg"
        },
        {
          id: "8",
          titulo:  "A Viagem de Chihiro",
          autor:  "Hayao Miyazaki",
          generos: ["Animação", "Aventura"],
          duracao: 125,
          anoLancamento: 2001,
          sinposte: "Uma jovem garota chamada Chihiro se encontra em um mundo mágico e misterioso, onde deve encontrar uma maneira de salvar seus pais e retornar ao mundo real.",
          notaDivina: 10.0,
          notaPublico: 8.6,
          motivoRecomendacao: "Uma obra-prima da animação japonesa que combina uma narrativa encantadora, personagens memoráveis e uma estética visual deslumbrante para criar uma experiência cinematográfica única e inesquecível.",
          poster: "https://m.media-amazon.com/images/M/MV5BYmZmMmM4OTYtMDkyNi00ZDI5LThiODItNzhlZGI3ZDJmZDZiXkEyXkFqcGc@._V1_FMjpg_UX1000_.jpg"
        },
        {
          id: "9",
          titulo: "Seven",
          autor: "David Fincher",
          generos: ["Thriller", "Crime", "Drama"],
          duracao: 127,
          anoLancamento: 1995,
          sinpose: "Dois detetives enfrentam um serial killer que usa os sete pecados capitais como tema para seus assassinatos brutais.",
          notaDivina: 8.6,
          notaPublico: 8.6,
          motivoRecomendacao: "Um thriller sombrio e perturbador que estabeleceu o tom para muitos filmes de crime posteriores, com uma atmosfera opressiva e uma reviravolta devastadora.",
          poster: "https://image.tmdb.org/t/p/w500/zgB9sNxR5G43qyI9KZjCv8smpO8.jpg"
        },
        { 
          id: "10",
          titulo: "As Crônicas de Spiderwick",
          autor: "Mark Waters",
          generos: ["Fantasia", "Aventura", "Comédia"],
          duracao: 112,
          anoLancamento: 2008,
          sinopse: "Uma família se vê envolvida em um mundo de criaturas mágicas ocultas quando descobrem um manual de criaturas fantásticas.",
          notaDivina: 8.0,
          notaPublico: 7.8,
          motivoRecomendacao: "Um filme envolvente e visualmente impressionante que combina elementos de fantasia e aventura para criar uma experiência cinematográfica única e inesquecível.",
          poster: "https://image.tmdb.org/t/p/w500/fP2lGsqVwPpgHqqd4huwKNdg2Tz.jpg"
         },
         {
          id: "11",
          titulo: "O Pálido Olho Azul",
          autor: "S. Darko",
          generos: ["Horror", "Drama"],
          duracao: 90,
          anoLancamento: 2007,
          sinopse: "Uma exploradora de cavernas descobre uma criatura antiga que coloca em risco a sua vida e a de sua equipe.",
          notaDivina: 6.5,
          notaPublico: 6.2,
          motivoRecomendacao: "Um filme de horror atmosférico que cria tensão através de isolamento e o desconhecido.",
          poster: "https://image.tmdb.org/t/p/w500/nAU4dVvkZgQvR0M7tKAv9qgtcGL.jpg",
         }, 
         { 
          id: "12",
          titulo: "Paprika",
          autor: "Satoshi Kon",
          generos: ["Animação", "Ficção Científica"],
          duracao: 104,
          anoLancamento: 2006,
          sinopse: "Uma psicóloga que trabalha com um dispositivo que permite entrar nos sonhos dos pacientes tem sua vida virada de cabeça para baixo quando o dispositivo é roubado e usado para causar caos.",
          notaDivina: 10.0,
          notaPublico: 7.7,
          motivoRecomendacao: "Uma obra-prima da animação japonesa que mistura elementos de ficção científica e surrealismo, explorando temas de identidade, realidade e o poder dos sonhos de maneira visualmente deslumbrante e narrativamente complexa.",
          poster: "https://m.media-amazon.com/images/S/pv-target-images/7863540cdfeb19c162bb351209a5c5c13505f7354f789578c504740000f0e4ad.jpg",
         },
         { 
          id: "13",
          titulo: "Castelo Animado",
          autor: "Hayao Miyazaki",
          generos: ["Animação", "Fantasia", "Romance"],
          duracao: 119,
          anoLancamento: 2004,
          sinopse: "Uma jovem rapariga maldita recebe ajuda de um misterioso mago e é levada a um castelo animado, onde descobre que há mais do que parece.",
          notaDivina: 8.4,
          notaPublico: 8.2,
          motivoRecomendacao: "Uma animação deslumbrante com uma história de fantasia romantizada, personagens encantadores e uma trilha sonora memorável.",
          poster: "https://image.tmdb.org/t/p/w500/3cyjYtLWCW7A4s5rT2gj2jqVzlR.jpg",
         },
         {
           id: "14",
           titulo: "Túmulo dos Vagalumes",
           autor: "Isao Takahata",
           generos: ["Animação", "Drama", "Guerra"],
           duracao: 89,
           anoLancamento: 1988,
           sinopse: "Dois órfãos enfrentam a pobreza e o bombardeio durante a Segunda Guerra Mundial, com um menino mais velho protegendo sua irmã mais jovem.",
           notaDivina: 8.5,
           notaPublico: 8.3,
           motivoRecomendacao: "Um filme de animação profundamente comovente que oferece uma perspectiva humanista sobre os horrores da guerra através dos olhos de crianças.",
           poster: "https://image.tmdb.org/t/p/w500/xZvqsfXamQgNAeq4eEL1WEeMsQG.jpg",
         },
         {
           id: "15",
           titulo: "O Homem Invisível",
           autor: "Leigh Whannell",
           generos: ["Horror", "Thriller", "Ficção Científica"],
           duracao: 125,
           anoLancamento: 2020,
           sinopse: "Uma mulher é aterrorizada por seu ex abusivo que encontra um meio de se tornar invisível, permitindo-lhe perseguir-la impunemente.",
           notaDivina: 7.4,
           notaPublico: 7.5,
           motivoRecomendacao: "Um thriller de horror moderno que usa o conceito de invisibilidade de forma criativa, gerando tensão através da ameaça invisível e do gaslighting psicológico.",
           poster: "https://image.tmdb.org/t/p/w500/djbD2pi3J5wiM5G8eaEXKVjrVB.jpg",
         },
         {
           id: "16",
           titulo: "10 Coisas Que Eu Odeio em Você",
           autor: "Gil Junger",
           generos: ["Comédia", "Romance", "Drama"],
           duracao: 97,
           anoLancamento: 1999,
           sinopse: "Uma tentativa de conquistar uma jovem mulher que odeia romantismo se transforma em uma história de romance genuíno quando dois meninos fazem uma aposta.",
           notaDivina: 7.6,
           notaPublico: 7.7,
           motivoRecomendacao:"Uma comédia romântica leve e divertida baseada em Shakespeare que encanta com seu humor e química entre os protagonistas.",
           poster: "https://image.tmdb.org/t/p/w500/uxDMWYVz9HY85UQndhQ33r5P9mE.jpg"
        }
    ];

    console.log("Usando dados padrão de FilmeData como fallback para rolos");
    return filmesPadrao;
}

/* Embaralha aleatoriamente um array (Fisher-Yates shuffle) */
function shuffleArray(array) {
    const shuffled = [...array];
    for (let i = shuffled.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
    }
    return shuffled;
}

/* Preenche os rolos de filmes (direito e esquerdo) com frames animados */
async function preencherRolos() {
    let filmes = await carregarFilmesParaRolos();
    if (filmes.length === 0) return;

    // Embaralha filmes para ordem aleatoria (cada reload diferente)
    filmes = shuffleArray(filmes);
    
    const filmstripLeft = document.getElementById("filmstrip-left");
    const filmstripRight = document.getElementById("filmstrip-right");

    if (!filmstripLeft || !filmstripRight) return;

    // Divide os filmes em dois grupos (para rolos diferentes)
    const meio = Math.ceil(filmes.length / 2);
    const filmesEsquerda = filmes.slice(0, meio);
    const filmesDireita = filmes.slice(meio);

    // Preenche rolo esquerdo (ordem aleatoria com duplicação para loop continuo)
    filmesEsquerda.forEach(filme => {
        criarFrameFilmstrip(filmstripLeft, filme);
    });
    // Duplica 100% dos filmes no final para efeito loop infinito suave e imperceptivel
    filmesEsquerda.forEach(filme => {
        criarFrameFilmstrip(filmstripLeft, filme);
    });
    // Preenche rolo direito (ordem inversa para movimento oposto)
    filmesDireita.reverse().forEach(filme => {
        criarFrameFilmstrip(filmstripRight, filme);
    });
    // Duplica 100% dos filmes no final para efeito loop infinito suave e imperceptivel
    filmesDireita.forEach(filme => {
        criarFrameFilmstrip(filmstripRight, filme);
    });
}

/* Cria um frame individual para o rolo de filmes */
function criarFrameFilmstrip(container, filme) {
    const frame = document.createElement("div");
    frame.classList.add("filmstrip-frame");
    frame.setAttribute("data-title", escapeHtml(filme.titulo || "Sem título"));

    if (filme.poster) {
        const img = document.createElement("img");
        img.src = escapeUrl(filme.poster);
        img.alt = escapeHtml(filme.titulo || "Sem título");
        img.onerror = () => {
            img.style.display = "none";
            frame.classList.add("no-image");
            frame.innerHTML = "📽️";
        };
        frame.appendChild(img);
    } else {
        frame.classList.add("no-image");
        frame.innerHTML = "📽️";
    }

    // Clique no frame abre os detalhes do filme
    frame.addEventListener("click", () => {
        abrirTelaDetalhes(filme, ultimosCriteria || { generos: [], duracoes: [], decadas: [] });
    });

    container.appendChild(frame);
}

/*
    Proteção contra Xss - escapa caracteres HTML perigosos
*/
function escapeHtml(text) {
    if (text == null) return "";
    const div = document.createElement("div");
    div.textContent = String(text);
    return div.innerHTML;
}

// Escapa uma URL para uso seguro em atributos href/src
function escapeUrl(url) {
    if (!url) return "#";
    try {
        const parsed = new URL(url);
        if (parsed.protocol === "http:" || parsed.protocol === "https:") return url;
    } catch (_) {}
    return "#";
}

/*
    Mostra mensagem simples no painel de resultados (seguro contra XSS)
*/
function mostrarMensagem(texto) {
    const container = document.getElementById("results-container");
    const card = document.createElement("div");
    card.classList.add("card", "mensagem-card");
    const p = document.createElement("p");
    p.textContent = texto;
    card.appendChild(p);
    container.innerHTML = "";
    container.appendChild(card);
}

/*
    Mostra mensagem com spinner de loading
*/
function mostrarLoading(texto) {
    document.getElementById("results-container").innerHTML = `<div class="card mensagem-card"><div class="spinner"></div><p>${escapeHtml(texto)}</p></div>`;
}

/*
    Desabilita/habilita os botões durante requisições
*/
function setBotoesDisabled(disabled) {
    ["sortear-btn", "listar-btn", "sortear-novamente-btn"].forEach(id => {
        const btn = document.getElementById(id);
        if (btn) btn.disabled = disabled;
    });
}