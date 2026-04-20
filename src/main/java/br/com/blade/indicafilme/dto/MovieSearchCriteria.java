package br.com.blade.indicafilme.dto;

import java.text.Normalizer;
import java.util.List;

/**
 * Critérios de busca de filmes informados pelo usuário.
 * 
 * <p>
 * Usa {@code record} porque os critérios são criados uma vez e não mudam.
 * o Java gera automaticamente o construtor, getter e {@code equals}.
 * </p>
 * 
 * <p>
 * Todos os textos são normalizados ao criar o record (via método estático
 * {@link #normalizar}), garantindo que "Ação, "acao" e "ACAO" sejam iguais.
 * </p>
 * 
 * <h3>Por que normalizar aqui e no MOVIE?</h3>
 * <p>
 * São dois lados de uma mesma comparação:
 * </p>
 * <ul>
 * <li><b>Aqui:</b> normaliza o que o <b>usuário digitou</b> no filtro.</li>
 * <li><b>No Movie:</b> (quando migrar para MongoDB) normalizaria o que foi
 * <b>cadastrado no banco</b>.</li>
 * <li>Para comparar dois valores, os <b>dois precisam estar no mesmo
 * formato</b>.</li>
 * </ul>
 * 
 * @param generos  lista de gêneros selecionados (ex: ["Ação", "Comédia"]).
 *                 Normalizados automaticamente.
 * @param duracoes lista de faixas de duração: "CURTA", "MEDIA" ou "LONGA".
 * @param decadas  lista de anos de início de décadas (ex: [1990, 2000]).
 *                 Se o usuário digitar 1995, o sistema converte para 1990
 */

public record MovieSearchCriteria(
                List<String> generos,
                List<String> duracoes,
                List<String> decadas) {

        /**
         * Construtor compacto que normaliza automaticamente os gêneros ao criar o
         * record.
         * Chamado toda vez que um novo {@code MovieSearchCriteria} é instanciado.
         */
        public MovieSearchCriteria {
                generos = generos == null ? null
                                : generos.stream()
                                                .map(MovieSearchCriteria::normalizar)
                                                .toList();

                duracoes = duracoes == null ? null
                                : duracoes.stream()
                                                .map(d -> d == null ? null : d.trim().toUpperCase())
                                                .toList();

                decadas = decadas == null ? null
                                : decadas.stream()
                                                .map(ano -> ano == null ? null : (ano / 10) * 10)
                                                .toList();
        }

        /**
         * Normaliza um texto para comparação uniforme:
         * 
         * @param valor
         * @return
         */

        public static String normalizar(String valor) {
                if (valor == null || valor.isBlank())
                        return null;
                String semEspacos = valor.trim().toUpperCase();
                String decomposto = Normalizer.normalize(semEspacos, Normalizer.Form.NFD);
                return decomposto.replaceAll("\\p{M}", ""); // Remove acentos
        }
}