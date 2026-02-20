// Array de opções de passeio em Belo Horizonte
const activities = [
    // Comida
    {
        title: "Feira Hippie",
        description: "Funciona aos domingos, das 7 as 14h. A Feira Hippie é um convite à convivência, à troca e ao encontro cultural no espaço público, além de ser um grande centro comercial de produtos exclusivos e artesanais.",
        categoria: "comida",
        horario: "manha",
        preco: "barato",
        link: "https://maps.app.goo.gl/s4vT47F6X6YqCqN66"
    },
    {
        title: "Feira Hippie",
        description: "Funciona aos domingos, das 7 as 14h. A Feira Hippie é um convite à convivência, à troca e ao encontro cultural no espaço público, além de ser um grande centro comercial de produtos exclusivos e artesanais.",
        categoria: "comida",
        horario: "tarde",
        preco: "barato",
        link: "https://maps.app.goo.gl/s4vT47F6X6YqCqN66"
    },
    {
        title: "Restaurante Dona Lucinha",
        description: "Segunda a sabado das 11h as 22h e domingo das 11h as 17h.Comida típica mineira em ambiente rústico",
        categoria: "comida",
        horario: "tarde",
        preco: "regular",
        link: "https://maps.app.goo.gl/2gLBTuZX8yF7amRr6"
    },
    {
        title: "Topo do Mundo",
        description: "Restaurante com vista panorâmica da Serra da Moeda, comida refinada",
        categoria: "comida",
        horario: "noite",
        preco: "caro",
        link: "https://maps.app.goo.gl/VZumbEvhMZpqhvQTA"

    },

    // Cultura
    {
        title: "Praça da Liberdade",
        description: "Relaxe e aproveite a arquitetura e os jardins da praça",
        categoria: "cultura",
        horario: "manha",
        preco: "barato",
        link: "https://maps.app.goo.gl/aRHhgdDamQpNj3op7"
    },
    {
        title: "Mineirão",
        description: "O Estádio Governador Magalhães Pinto, mais conhecido como Mineirão, é um estádio de futebol",
        categoria: "cultura",
        horario: "manha",
        preco: "regular",
        link: "https://maps.app.goo.gl/2cjDBEaY9hKRUFDU8"
    },
    {
        title: "Museu de Ciências Naturais PUC Minas",
        description: "de Terça à Sabado, das 9h às 17h. O Museu de Ciências Naturais da Pontifícia Universidade Católica de Minas Gerais é um museu brasileiro de ciências naturais, que por meio de exposições, educação e pesquisa, preserva o patrimônio natural, histórico e cultural do Brasil.",
        categoria: "cultura",
        horario: "manha",
        preco: "barato",
        link: "https://maps.app.goo.gl/j1bTgZS6w7r821dg8"
    },
    {
        title: "Casa Fiat de Cultura",
        description: "Terça a sexta-feira, das 10h às 21h; sábados, domingos e feriados, das 10h às 18h. Famoso centro cultural com café, apresentações, exposições e workshops.",
        categoria: "cultura",
        horario: "manha",
        preco: "barato",
        link: "https://maps.app.goo.gl/W8PTGvekYvupLMMX6"
    },

    // Natureza
    {
        title: "Parque Municipal Américo Renné Giannetti",
        description: "É uma das áreas de preservação mais visitadas de Belo Horizonte devido a sua localização, beleza natural, monumentos históricos, equipamentos esportivos e opções de lazer como brinquedos, além de receber diversificados eventos.",
        categoria: "natureza",
        horario: "tarde",
        preco: "barato",
        link: "https://maps.app.goo.gl/UPcAU6k4PAmi7Quc6"
    },
    {
        title: "Lagoa da Pampulha",
        description: "Recentemente reconhecido como Patrimônio Cultural da Humanidade pela UNESCO, o Conjunto Arquitetônico da Pampulha localizado em BH – Minas Gerais, se destaca como a principal atração de Belo Horizonte.",
        categoria: "natureza",
        horario: "manha",
        preco: "barato",
        link: "https://maps.app.goo.gl/UCCJaQp3CMvAoiyn6"
    },
    /*{
        title: "Parque Estadual da Serra do Rola Moça",
        description: "Conecte-se com a natureza em um dos parques mais bonitos da região",
        categoria: "natureza",
        horario: "manha tarde",
        preco: "regular",
        link: "https://maps.app.goo.gl/aqHEx29D9aTRzMif9"
    },*/

    {
        title: "Zoobotânica (Jardins Zoológico e Botânico)",
        description: "Zoológico com jardins aquáticos em estilo japonês, gorilas, répteis, aves e um aquário.",
        categoria: "natureza",
        horario: "manha",
        preco: "barato",
        link: "https://maps.app.goo.gl/cXdzMRtBaN8QWBSPA"
    },

    // Passeio
    {
        title: "Avenida Afonso Pena",
        description: "Faça um passeio pela principal avenida da cidade e admire a arquitetura",
        categoria: "passeio",
        horario: "manha",
        preco: "barato",
        link: "https://maps.app.goo.gl/xcjNQBtiGMk61PG7A"
    },
    {
        title: "Diamond Mall",
        description: "Faça um passeio pela principal avenida da cidade e admire a arquitetura",
        categoria: "passeio",
        horario: "tarde",
        preco: "barato",
        link: "https://maps.app.goo.gl/rzMATwvmGebcr26h6"
    },
    /*{
        title: "Estação Camargos",
        description: "De Quarta à Sexta-feira está aberto das 18h às 23h. A Estação Camargos é uma experiência tematica, com atrações incriveis, com temas como 'Mundo dos Games' e livros.",
        categoria: "passeio",
        horario: "noite",
        preco: "barato",
        link: "https://maps.app.goo.gl/5S3yce69VZBTcgtx8"
    },*/

    //Musica
    {
        title: "3 Preto Bar",
        description: "A 3 Preto Bar é um bar bem movimentado e para quem gosta de muita musica e quer se sentir em casa.",
        categoria: "musica",
        horario: "noite",
        preco: "regular",
        link: "https://maps.app.goo.gl/mobjcv7Fm1HB7dvUA"
    },
    {
        title: "Circuito Savassi",
        description: "Divirta-se com a música ao vivo e um ambiente animado",
        categoria: "musica",
        horario: "noite",
        preco: "regular",
        link: "https://maps.app.goo.gl/iKoz3n4H7aFGmp5t6"
    },
    {
        title: "Raads",
        description: "Segunda a sexta, das 9 as 19h, sábado das 8 as 15h. Lanchonete com diferentes sanduíches e sucos",
        categoria: "comida",
        horario: "tarde",
        preco: "barato",
        link: "https://maps.app.goo.gl/tbfy4Be3X6fuqFRX9"
    },

    {
        title: "Feijão Maletta",
        description: "Funciona quarta a sábado, das 11 as 23h. 'Restaurante self service de comida caseira e afetiva.Comida simples feita com muito amor.Opções de carnes, vegetarianas e veganas.",
        categoria: "comida",
        horario: "tarde",
        preco: "barato",
        link: "https://maps.app.goo.gl/ZozvTwthmrzCsXFR9"
    },
    {
        title: "Restaurante Chen Chang Kee Noodle House - Galeria BH",
        description: "Funciona segunda a sábado, das 11 às 15h. Restaurantede comida chinesa, ambiente pequeno e lotado, comida caseira e de qualidade",
        categoria: "comida",
        horario: "tarde",
        preco: "barato",
        link: "https://maps.app.goo.gl/vzSAcd6gjfqDa2sF8"
    },
    {
        title: "Hotel Sul America   ",
        description: "Funciona segunda a sábado, das 11 às 15h. Restaurantede comida chinesa, ambiente pequeno e lotado, comida caseira e de qualidade",
        categoria: "comida",
        horario: "tarde",
        preco: "barato",
        link: "https://maps.app.goo.gl/6pCTre7gKBohbUcw8"
    },
    {
        title: "Mercado Central",
        description: "Animado mercado indoor com alimentos, artesanato e souvenirs, além de bares e restaurantes informais.",
        categoria: "comida",
        horario: "tarde",
        preco: "barato",
        link: "https://maps.app.goo.gl/oFHnQHb7KdFq3doK9"
    },

    // Variável para armazenar os filtros anteriores
];


