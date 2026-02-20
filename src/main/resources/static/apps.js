// Variável para armazenar os filtros anteriores
let lastFilters = { preco: null, horario: null, categoria: null };

// Função executada ao clicar no botão "Bora roletar?!"
document.getElementById("new-activity-btn").addEventListener("click", function () {
    const preco = document.getElementById("preco").value;
    const horario = document.getElementById("horario").value;
    const categoria = document.getElementById("categoria").value;

    // Filtrar as atividades com base nos filtros selecionados
    const filteredActivities = activities.filter(activity =>
        activity.preco === preco && activity.horario.includes(horario) && activity.categoria === categoria
    );

    const resultsContainer = document.getElementById("results-container");
    resultsContainer.innerHTML = ""; // Limpar resultados anteriores

    // Verificar se há atividades filtradas
    if (filteredActivities.length === 0) {
        const card = document.createElement("div");
        card.classList.add("card");
        card.innerHTML = "<h3>Encontre o que fazer!</h3><p>Não encontramos atividades com esses filtros.</p>";
        resultsContainer.appendChild(card);
    } else {
        // Selecionar uma atividade aleatória dos resultados filtrados
        const randomActivity = filteredActivities[Math.floor(Math.random() * filteredActivities.length)];

        const card = document.createElement("div");
        card.classList.add("card");

        const title = document.createElement("h3");
        title.textContent = randomActivity.title;

        const description = document.createElement("p");
        description.textContent = randomActivity.description;

        const details = document.createElement("div");
        details.classList.add("details");
        details.innerHTML = `<strong>Custo do Rolê:</strong> ${randomActivity.preco}<br>
        <strong>Localização:</strong> <a href="${randomActivity.link}" target="_blank">Ver no mapa</a><br>
        <strong>Horario:</strong> ${randomActivity.horario}`;

        card.appendChild(title);
        card.appendChild(description);
        card.appendChild(details);
        resultsContainer.appendChild(card);
    }

    // Esconder a descrição inicial
    const descriptionCard = document.getElementById("description-card");
    if (descriptionCard) {
        descriptionCard.style.display = "none";
    }

    // Alterar o texto do botão com base nos filtros anteriores
    const button = document.getElementById("new-activity-btn");

    if (lastFilters.preco === preco && lastFilters.horario === horario && lastFilters.categoria === categoria) {
        button.textContent = "Não gostei. De novo!";
    } else {
        button.textContent = "Mais uma vez";
        lastFilters = { c, horario, categoria };
    }
});

// Função para mudar o gradiente com base no movimento do mouse
const gradient = document.getElementById('gradient');
document.addEventListener('mousemove', function (e) {
    const x = e.clientX / window.innerWidth;
    const y = e.clientY / window.innerHeight;

    gradient.style.background = `radial-gradient(circle at ${e.clientX}px ${e.clientY}px, 
    rgba(255, 255, 255, 0.1), #121212 50%)`;
});