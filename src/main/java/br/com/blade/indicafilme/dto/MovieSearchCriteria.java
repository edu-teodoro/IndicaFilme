package br.com.blade.indicafilme.dto;

import java.text.Normalizer;
import java.util.List;

public record MovieSearchCriteria(
                List<String> generos,
                List<String> duracoes,
                List<Integer> decadas
) {
        public MovieSearchCriteria {
                generos = generos == null ? null : 
                        generos.stream().map(MovieSearchCriteria::normalizar).toList();
                duracoes = duracoes == null ? null :
                        duracoes.stream().map(d -> d == null ? null : d.trim().toUpperCase()).toList();
                decadas = decadas == null ? null :
                        decadas.stream().map(ano -> ano == null ? null : (ano / 10) * 10).toList();
        }

        public static String normalizar(String valor) {
                if (valor == null || valor.isBlank()) return null;
                String semEspacos = valor.trim().toUpperCase();
                String decomposto = Normalizer.normalize(semEspacos, Normalizer.Form.NFD);
                return decomposto.replaceAll("\\p{M}", ""); // Remove acentos
        }
}