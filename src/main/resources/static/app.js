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
        themeToggle.querySelector(".theme-icon").textContent = "🌞";
    } else {
        htmlElement.style.colorScheme = "dark";
        document.body.classList.remove("light-mode");
        themeToggle.querySelector(".theme-icon").textContent = "🌜";
    }

    // Toggle de tema ao clicar
    themeToggle.addEventListener("click", () => {
        const isLightMode = document.body.classList.contains("light-mode");

        if (isLightMode) {
            document.body.classList.remove("light-mode");
            htmlElement.style.colorScheme = "dark";
            localStorage.setItem("theme", "dark");
            themeToggle.querySelector(".theme-icon").textContent = "🌜";
        } else {
            document.body.classList.add("light-mode");
            htmlElement.style.colorScheme = "light";
            localStorage.setItem("theme", "light");
            themeToggle.querySelector(".theme-icon").textContent = "🌞";
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
        const { generos, duracoes, decadas } = estado;
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

        ultimosCriteria = { generos: [...generos], duracoes: [...duracoes], decadas: decadasFinais, anoExato };
        await sortearFilme(ultimosCriteria);
    });

    // Botão Listar
    document.getElementById("listar-btn").addEventListener("click", async () => {
        const { generos, duracoes, decadas } = estado;
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

        await listarFilmes({ generos: [...generos], duracoes: [...duracoes], decadas: decadasFinais, anoExato });
    });

    // Botão Sortear Outro (na tela de detalhes)
    document.getElementById("sortear-novamente-btn").addEventListener("click", async () => {
        if (ultimosCriteria) await sortearFilme(ultimosCriteria);
    });

    // Botão voltar
    document.getElementById("voltar-btn").addEventListener("click", () => {
        document.getElementById("tela-detalhes").classList.remove("fade-in");
        document.getElementById("tela-detalhes").style.display = "none";
        document.getElementById("tela-principal").style.display = "block";
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
            `radial-gradient(circle at ${e.clientX}px ${e.clientY}px, rgba(229,1,43,0.15),#121212 55%)`;
    });
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
function montarParams({ generos, duracoes, decadas }) {
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
                    <span class="nota-label"> Nota Divina(@obladepontokom)</span>
                    <span class="nota-valor">${escapeHtml(filme.notaDivina) || "-"}</span>
                </div>
                <div class="nota-box">
                    <span class="nota-label"> Nota do Público</span>
                    <span class="nota-valor">${escapeHtml(filme.notaPublico) || "-"}</span>
                </div>
                <div class="nota-box nota-media">
                    <span class="nota-label"> Média</span>
                    <span class="nota-valor">${escapeHtml(filme.mediaNotas) || "-"}</span>
                </div>
            </div>
        
            <div class="sinopse-container">
                <h4>Sinopse</h4>
                <p>${escapeHtml(filme.sinopse) || "Sinopse não disponível."}</p>
            </div>

            ${filme.motivoRecomendacao ? `
            <div class="motivo-container">
                <h4>Por que assistir? (@obladepontokom)</h4>
                <p>${escapeHtml(filme.motivoRecomendacao)}</p>
            </div>` : ""}

            <div class="plataformas-container">
                <h4>Onde assistir</h4>
                <div class="plataformas-lista">${plataformas}</div>
            </div>

            <div class="compartilhar-container">
                <h4>Compartilhar</h4>
                <div class="compartilhar-lista">
                    <a href="https://wa.me/text=${encodeURIComponent(`Olha esse filme que encontrei no Indica Filmes: ${filme.titulo} - ${window.location.href}`)}" target="_blank" rel="noopener noreferrer" class="compartilhar-link whatsapp" title="Compartilhar no WhatsApp">
                        <i class="fab fa-whatsapp"></i>
                        <span>WhatsApp</span>
                    </a>
                    <a href="https://twitter.com/intent/tweet?text=${encodeURIComponent(`Olha esse filme que encontrei no Indica Filmes: ${filme.titulo}')}&url=${encodeURIComponent(window.location.href)}" target="_blank" rel="noopener noreferrer" class="compartilhar-link twitter" title="Compartilhar no Twitter"></a>
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
    const miniPoster = filme.poster 
        ? `<img src="${posterUrl}" alt="${escapeHtml(filme.titulo)}" class="poster-mini" />`
        : "";

    const sinopseText = filme.sinopse ? escapeHtml(filme.sinopse.substring(0, 300)) + (filme.sinopse.length > 300 ? "..." : "") : "";

    card.innerHTML = `
        <div class="card-resumo-content">
            ${miniPoster}
            <div class="card-resumo-info">
                <h3>${escapeHtml(filme.titulo)} <span class="ano">(${escapeHtml(filme.anoLancamento) || "-"})</span></h3>
                <p class="generos-resumo">${filme.generos?.map(g => escapeHtml(g)).join(", ") || "-"} . ${filme.duracao ? escapeHtml(filme.duracao) + " min" : "-"}</p>
                <div class="notas-resumo">
                    <span> ${escapeHtml(filme.notaDivina) || "-"}</span>
                    <span> ${escapeHtml(filme.notaPublico) || "-"}</span>
                    <span> ${escapeHtml(filme.mediaNotas) || "-"}</span>
                </div>
                <p class="sinopse-resumo">${sinopseText}</p>
            </div>
        </div>
    `;
    card.addEventListener("click", () => abrirTelaDetalhes(filme, ultimosCriteria || { generos: [], duracoes: [], decadas: [] }));
    return card;
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
    document.getElementById("results-container").innerHTML = `
        <div class="card mensagem-card"><div class="spinner"></div><p>${escapeHtml(texto)}</p></div>`;
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