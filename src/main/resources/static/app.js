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
const MOBILE_BREAKPOINT = 768;
let wasMobileView = null;

document.addEventListener("DOMContentLoaded", () => {
    // CSS Layout Fix
    const titleContainer = document.querySelector('.title-container');
    if (titleContainer) titleContainer.style.textAlign = 'center';
    // Removed height: 100% - containers shoould use CSS height: auto and grow naturally
    // const filtersContainer = document.querySelector('.filters-container');
    // if (filtersContainer) filtersContainer.style.height = '100%';
    // const resultsContainer = document.getElementById('results-container');
    // if (resultsContainer) resultsContainer.style.height = '100%';

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
        const telaDetalhes = document.getElementById("tela-detalhes");
        const telaPrincipal = document.getElementById("tela-principal");
        const overlay = document.getElementById("tv-static-overlay");

        // Reset completo do overlay
        if (overlay) {
            overlay.style.display = "none";
            overlay.classList.remove("fade-out");
        }

        telaDetalhes.classList.remove("fade-in");
        telaDetalhes.style.display = "none";
        telaPrincipal.classList.remove("fade-in");
        // Restaura o display original definido no CSS (#tela-principal = grid)
        telaPrincipal.style.removeProperty("display");
        // Força reflow para sincronizar com CSS animation
        telaPrincipal.offsetHeight;
        telaPrincipal.classList.add("fade-in");

        // Remove a classe de modo lista do container de resultados
        const container = document.getElementById("results-container");
        if (container) container.classList.remove("modo-lista");
        mostrarMensagemPadrao();
        if (isMobileView()) {
            setContainerExpanded("filters", true);
            setContainerExpanded("results", false);
        }
    });

    // Botão limpar filtros
    document.getElementById("limpar-btn").addEventListener("click", () => {
        estado.generos.length = 0;
        estado.duracoes.length = 0;
        estado.decadas.length = 0;
        document.querySelectorAll(".tag.ativa").forEach(tag => tag.classList.remove("ativa"));
        document.getElementById("decada-custom").value = "";
        mostrarMensagemPadrao();
        if (isMobileView()) {
            setContainerExpanded("filters", true);
            setContainerExpanded("results", false);
        }
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

    const container = document.getElementById("results-container");
    if (container) container.classList.remove("modo-lista");

    initMobilePanels();
    initFilterGroupPanels();
    applyMobilePanelState();
    window.addEventListener("resize", applyMobilePanelState);
});

function isMobileView() {
    return window.matchMedia(`(max-width: ${MOBILE_BREAKPOINT}px)`).matches;
}

function getResultsContent() {
    return document.getElementById("results-content") || document.getElementById("results-container");
}

function getPanelRefs(panel) {
    if (panel === "filters") {
        return {
            container: document.getElementById("filters-container"),
            toggle: document.getElementById ("toggle-filters-btn")
        };
    }

    if (panel === "results") {
        return {
            container: document.getElementById("results-container"),
            toggle: document.getElementById ("toggle-results-btn")
        };
    }

    return {
        container: null,
        toggle: null
    };
}

function setContainerExpanded(panel, expanded) {
    const { container, toggle } = getPanelRefs(panel);
    if (!container || !toggle) return;

    container.classList.toggle("is-collapsed", !expanded);
    toggle.setAttribute("aria-expanded", String(expanded));
}

function toggleContainer(panel) {
    const { container, toggle } = getPanelRefs(panel);
    if (!container || !toggle) return;
    
    const currentlyExpanded = toggle.getAttribute("aria-expanded") === "true";
    const nextExpanded = !currentlyExpanded;
    setContainerExpanded(panel, nextExpanded);

    // Mobile UX: ao recolher resultados manualmente, reabre filtros para facilitar nova busca.
    if (panel === "results" && !nextExpanded && isMobileView()) {
        setContainerExpanded("filters", true);
    }
}

function initMobilePanels() {
    const filtersToggle = document.getElementById("toggle-filters-btn");
    const resultsToggle = document.getElementById("toggle-results-btn");

    if (filtersToggle) {
        filtersToggle.addEventListener("click", () => toggleContainer("filters"));
    }

    if (resultsToggle) {
        resultsToggle.addEventListener("click", () => toggleContainer("results"));
    }
}

function getFilterGroupRefs(group) {
    if (group === "generos") {
        return {
            container: document.getElementById("group-generos"),
            toggle: document.getElementById ("toggle-generos-btn")
        };
    }

    if (group === "duracoes") {
        return {
            container: document.getElementById("group-duracoes"),
            toggle: document.getElementById ("toggle-duracoes-btn")
        };
    }

    if (group === "decadas") {
        return {
            container: document.getElementById("group-decadas"),
            toggle: document.getElementById ("toggle-decadas-btn")
        };
    }

    return {
        container: null,
        toggle: null
    };
}

function setFilterGroupExpanded(group, expanded) {
    const { container, toggle } = getFilterGroupRefs(group);
    if (!container || !toggle) return;

    container.classList.toggle("is-collapsed", !expanded);
    toggle.setAttribute("aria-expanded", String(expanded));
}

function toggleFilterGroup(group) {
    const { toggle } = getFilterGroupRefs(group);
    if (!toggle) return;

    const currentlyExpanded = toggle.getAttribute("aria-expanded") === "true";
    setFilterGroupExpanded(group, !currentlyExpanded);
}

function setAllFilterGroupsExpanded(expanded) {
    setFilterGroupExpanded("generos", expanded);
    setFilterGroupExpanded("duracoes", expanded);
    setFilterGroupExpanded("decadas", expanded);
}

function initFilterGroupPanels() {
    const groups = ["generos","duracoes","decadas"];

    groups.forEach(group => {
        const { toggle } = getFilterGroupRefs(group);
        if (!toggle) return;

        toggle.addEventListener("click", () => toggleFilterGroup(group));
    });
}

function applyMobilePanelState() {
    const mobile = isMobileView();
    if (wasMobileView === mobile) return;

    wasMobileView = mobile;

    if (mobile) {
        setContainerExpanded("filters", true);
        setContainerExpanded("results", false);
        setAllFilterGroupsExpanded(true);
        return;
    }

    setContainerExpanded("filters", true);
    setContainerExpanded("results", true);
    setAllFilterGroupsExpanded(true);
}

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
            const content = getResultsContent();
            if (!container || !content) return;

            // Detecta se é mobile para renderizar em carrossel
            const isMobile = isMobileView();

            if (isMobile) {
                renderCarrossel(dados, criteria);
            } else {
                // Desktop: renderizar lista normal
                // Limpa cards antigos
                Array.from(container.children).forEach(child => {
                        if (!child.classList.contains("mobile-panel-header") &&
                            !child.classList.contains("mobile-panel-content")) {
                            child.remove();
                        }
                    });


                    content.innerHTML = `<h3 class="lista-titulo">${dados.length} filme(s) encontrado(s):</h3>`; dados.forEach(filme => container.appendChild(criarCardResumo(filme))); container.classList.add("modo-lista");
                }

                if (isMobileView()) {
                    setContainerExpanded("filters", false);
                    setContainerExpanded("results", true);
                }
            } catch (err) {
                console.error(err);
                mostrarMensagem("Erro ao conectar com servidor. Por favor, tente novamente.");
            } finally {
                setBotoesDisabled(false);
            }
        }

        /* Renderizar carrossel mobile de filmes */
        function renderCarrossel(filmes, criteria) {
            const container = document.getElementById("results-container");
            const content = getResultsContent();
            if (!container || !content) return;

            // Reconstrói a estrutura do carrossel (em caso de ter sido removido por mostrarLoading)
            content.innerHTML = `<div class="carousel-container" id="carousel-container" style="display: flex;">
    <div class="carousel-wrapper">
        <div class="carousel-track" id="carousel-track">
            <!-- Filme por vez via JS -->
        </div>
    </div>
    <div class="carousel-nav">
        <button class="carousel-btn carousel-btn-prev" id="carousel-prev"
            aria-label="Filme anterior">«</button>
        <div class="carousel-indicator" id="carousel-indicator">1 de 1</div>
        <button class="carousel-btn carousel-btn-next" id="carousel-next"
            aria-label="Próximo filme">»</button>
    </div>
</div>`;

            const carouselContainer = document.getElementById("carousel-container");
            const carouselTrack = document.getElementById("carousel-track");

            // Limpa carrossel anterior
            carouselTrack.innerHTML = "";

            // Renderiza slides
            filmes.forEach((filme, index) => {
                const slide = document.createElement("div");
                slide.className = "carousel-slide";

                const posterHtml = filme.poster ?
                    `<img src"${escapeUrl(filme.poster)}" alt="Poster de ${escapeHtml(filme.titulo)}"
        class="carousel-poster" />` :
                    `<div class="carousel-poster no-image">🎬</div>`;

                const generos = filme.generos?.map(g => escapeHtml(g)).join(", ") || "-";
                const duracao = filme.duracao ? `${escapeHtml(filme.duracao)} min` : "-";

                slide.innerHTML = `
        ${posterHtml}
        <div class="carousel-content">
        <h2 class="carousel-title">${escapeHtml(filme.titulo)}</h2>
        <div class="carousel-year">${escapeHtml(filme.anoLancamento) || "-"}</div>
        <div class="carousel-genres"${generos}</div>
        <div class="carousel-duration">${duracao}</div>
        </div>
        `;

                // Clica para abrir detalhes
                slide.addEventListener("click", () => {
                    abrirTelaDetalhes(filme, criteria);
                });

                carouselTrack.appendChild(slide);
            });

            // Mostra carrossel
            carouselContainer.style.display = "flex";

            // Configura navegação
            setupCarouselNav(filmes.length);

            container.classList.remove("modo-lista");
        }

        /* Configura navegação do carrossel (botões próximo/anterior) */
        function setupCarouselNav(totalFilmes) {
            const track = document.getElementById("carousel-track");
            const prevBtn = document.getElementById("carousel-prev");
            const nextBtn = document.getElementById("carousel-next");
            const indicator = document.getElementById("carousel-indicator");

            let currentIndex = 0;

            const updateIndicator = () => {
                indicator.textContent = `${currentIndex + 1} de ${totalFilmes}`;
            };

            const updateButtons = () => {
                prevBtn.disabled = currentIndex === 0;
                nextBtn.disabled = currentIndex === totalFilmes - 1;
            };

            const scrollToSlide = (index) => {
                const slideWidth = track.children[0].offsetWidth + 0;
                const scrollPosition = index * slideWidth;
                track.scrollLeft = scrollPosition;
                currentIndex = index;
                updateIndicator();
                updateButtons();
            };

            // Botão anterior
            prevBtn.addEventListener("click", () => {
                if (currentIndex > 0) scrollToSlide(currentIndex - 1);
            });

            // Botão próximo
            nextBtn.addEventListener("click", () => {
                if (currentIndex < totalFilmes - 1) scrollToSlide(currentIndex + 1);
            });

            // Suporte a swipe/drag (mobile friendly)
            let startX = 0;
            let currentScroll = 0;
            let isDragging = false;

            track.addEventListener("mousedown", (e) => {
                isDragging = true;
                startX = e.clientX;
                currentScroll = track.scrollLeft;
            });

            track.addEventListener("touchstart", (e) => {
                isDragging = true;
                startX = e.touches[0].clientX;
                currentScroll = track.scrollLeft;
            });

            const handleDragMove = (clientX) => {
                if (!isDragging) return;
                const diff = startX - clienteX;
                track.scrollLeft = currentScroll + diff;
            };

            track.addEventListener("mousemove", (e) => handleDragMove(e.clientX));
            track.addEventListener("touchmove", (e) => handleDragMove(e.touches[0].clientX), {
                passive: true
            });

            const handleDragEnd = () => {
                if (!isDragging) return;
                isDragging = false;

                // Snap ao slide mais próximo
                const slideWidth = track.children[0].offsetWidth;
                const newIndex = Math.round(track.scrollLeft / slideWidth);
                const clampedIndex = Math.min(newIndex, totalFilmes - 1);
                scrollToSlide(clampedIndex);
            };

            track.addEventListener("mouseup", handleDragEnd);
            track.addEventListener("mouseleave", handleDragEnd);
            track.addEventListener("touchend", handleDragEnd);

            // Inicializa
            updateIndicator();
            updateButtons();
        }

        /**
            Abre a tela de detalhes completos de um filme sorteado
        */
        function abrirTelaDetalhes(filme, criteria) {
            const telaPrincipal = document.getElementById("tela-principal");
            const telaDetalhes = document.getElementById("tela-detalhes");

            telaPrincipal.style.display = "none";
            // Remove inline display para usar comportamento default do elemento
            telaDetalhes.style.removeProperty("display");
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

            // Mostrar TV com estática antes de revelar o filme
            mostrarTVComEstatica();

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
            const filmesPadrao = [{
                    id: "1",
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
                    id: "4",
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
                    autor: "David Fincher",
                    generos: ["Drama", "Thriller"],
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
                    titulo: "A Viagem de Chihiro",
                    autor: "Hayao Miyazaki",
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
                    motivoRecomendacao: "Uma comédia romântica leve e divertida baseada em Shakespeare que encanta com seu humor e química entre os protagonistas.",
                    poster: "https://image.tmdb.org/t/p/w500/uxDMWYVz9HY85UQndhQ33r5P9mE.jpg"
                },
                {
                    id: "17",
                    titulo: "Tokyo Godfathers",
                    autor: "Satoshi Kon",
                    generos: ["Animação", "Comédia", "Drama"],
                    duracao: 92,
                    anoLancamento: 2003,
                    sinopse: "Três sem-teto encontram um bebê abandonado na véspera de Natal e embarcam em uma jornada para encontrar seus pais, enfrentando desafios e descobrindo segredos do passado.",
                    notaDivina: 10.0,
                    notaPublico: 8.0,
                    motivoRecomendacao: "Uma animação comovente e divertida que mistura humor, drama e crítica social, explorando temas de família, redenção e a importância da empatia.",
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
                abrirTelaDetalhes(filme, ultimosCriteria || {
                    generos: [],
                    duracoes: [],
                    decadas: []
                });
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
            const content = getResultsContent();
            if (!content) return;

            container.classList.remove("modo-lista");
            const card = document.createElement("div");
            card.classList.add("card", "mensagem-card");
            const p = document.createElement("p");
            p.textContent = texto;
            card.appendChild(p);
            content.innerHTML = "";
            content.appendChild(card);

            if (isMobileView()) {
                setContainerExpanded("results", true);
            }
        }

        /* Restaura a mensagem padrão do painel de resultados */
        function mostrarMensagemPadrao() {
            const container = document.getElementById("results-container");
            const content = getResultsContent();
            if (!container || !content) return;

            container.classList.remove("modo-lista");

            // Limpa completamente o conteúdo (remove cards de filmes adicionados)
            content.innerHTML =
                `<div class="card mensagem-card" id="description-card">
        <h3>Seu próximo filme está aqui 🎬</h3>
        <p>Selecione ao menos um filtro e clique em <strong>Sortear Filme</strong> para descobrir o que assistir hoje!</p>
    </div>`;

            // Remove todos os cards de filmes adicionados ao container
            Array.from(container.children).forEach(child => {
                if (child.id !== "toggle-results-btn" && child.classList && !child.classList.contains("mobile-panel-header") && !child.classList.contains("mobile-panel-content")) {
                    child.remove();
                }
            });
        }

        /*
            Mostra mensagem com spinner de loading
        */
        function mostrarLoading(texto) {
            const container = document.getElementById("results-container");
            const content = getResultsContent();
            if (!content) return;

            container.classList.remove("modo-lista");
            content.innerHTML = `<div class="card mensagem-card"><div class="spinner"></div><p>${escapeHtml(texto)}</p></div>`;

            if (isMobileView()) {
                setContainerExpanded("results", true);
            }
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

        /* Gera e anima a estatica da tv */
        function gerarEstatica() {
            const canvas = document.getElementById("static-canvas");
            if (!canvas) return;

            const ctx = canvas.getContext("2d");
            const width = canvas.width = canvas.offsetWidth;
            const height = canvas.height = canvas.offsetHeight;

            function desenharEstatica() {
                const imageData = ctx.createImageData(width, height);
                const data = imageData.data;

                for (let i = 0; i < data.length; i += 4) {
                    const pixel = Math.random() * 255;

                    // Gera ruido com cores azul e verde (para efeito de TV retro)
                    const r = pixel < 127 ? Math.floor(Math.random() * 100) : 0;
                    const g = Math.random() * 200;
                    const b = Math.random() * 255;
                    const a = 255;

                    data[i] = r;
                    data[i + 1] = g;
                    data[i + 2] = b;
                    data[i + 3] = a;
                }

                ctx.putImageData(imageData, 0, 0);
            }

            const animacaoId = setInterval(desenharEstatica, 50);
            return animacaoId;

        }

        /* Mostra a TV com esstatica e depois a revela o filme sorteado */
        function mostrarTVComEstatica() {
            const overlay = document.getElementById("tv-static-overlay");
            const tvBezel = document.querySelector(".tv-bezel");
            const tvStand = document.querySelector(".tv-stand");
            const tvMessage = document.getElementById("tv-message");

            if (!overlay || !tvBezel || !tvMessage || !tvStand) return;

            // Reset completo
            // Remove todas as classes de animação
            tvBezel.classList.remove("grow-and-fade", "reset-animation");
            tvStand.classList.remove("grow-and-fade", "reset-animation");
            tvMessage.classList.remove("reset-animation");
            overlay.classList.remove("fade-out");

            // Reset animaçoes inline
            tvBezel.style.aniamation = "none";
            tvBezel.style.transform = "";
            tvBezel.style.opacity = "";
            tvBezel.style.removeProperty("animation");
            tvBezel.style.removeProperty("transform");
            tvBezel.style.removeProperty("opacity");

            tvStand.style.aniamation = "none";
            tvStand.style.transform = "";
            tvStand.style.opacity = "";
            tvStand.style.removeProperty("animation");
            tvStand.style.removeProperty("transform");
            tvStand.style.removeProperty("opacity");

            tvMessage.style.aniamation = "none";
            tvMessage.style.transform = "";
            tvMessage.style.opacity = "";
            tvMessage.style.removeProperty("animation");
            tvMessage.style.removeProperty("transform");
            tvMessage.style.removeProperty("opacity");

            overlay.style.opacity = "";
            overlay.style.display = "flex";

            // Force reflow para resetar o layout
            void tvBezel.offsetHeight;
            void tvStand.offsetHeight;
            void tvMessage.offsetHeight;
            void overlay.offsetHeight;

            // Iniciar Animação
            // Aguarda um frame (requestAnimationFrame) para garantir que o reset foi processado
            requestAnimationFrame(() => {
                // Inicia a animação de estatica
                const animacaoId = gerarEstatica();

                // Em 1.8s: TV e pé começam a crescer e desaparecer
                setTimeout(() => {
                    tvBezel.classList.add("grow-and-fade");
                    tvStand.classList.add("grow-and-fade");
                    clearInterval(animacaoId);
                }, 2500);

                // Em 3.5s (quando TV terminar de desaparecer): Começar fade-out do overlay
                setTimeout(() => {
                    overlay.classList.add("fade-out");

                    // Após a transição do overlay (1s), remover do DOM e revelar contepudo
                    setTimeout(() => {
                        overlay.style.display = "none";
                    }, 1000);
                }, 3500);

            });

        }