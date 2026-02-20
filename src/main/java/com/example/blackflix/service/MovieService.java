package com.example.blackflix.service;

import com.example.blackflix.dto.MovieSearchCriteria;
import com.example.blackflix.model.Movie;
import com.example.blackflix.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final MongoTemplate mongoTemplate;
    private final SecureRandom random = new SecureRandom();

    @Autowired
    public MovieService(MovieRepository movieRepository, MongoTemplate mongoTemplate) {
        this.movieRepository = movieRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Movie> filterMovies(MovieSearchCriteria criteria) {
        if ((criteria.getGenero() == null || criteria.getGenero().isBlank())
                && (criteria.getDuracao() == null || criteria.getDuracao().isBlank())
                && (criteria.getDecada() == null)) {
            throw new IllegalArgumentException("Ao menos um filtro deve ser fornecido");
        }

        Query query = new Query();

        if (criteria.getGenero() != null && !criteria.getGenero().isBlank()) {
            query.addCriteria(Criteria.where("generos").regex("^" + criteria.getGenero() + "$", "i"));
        }

        if (criteria.getDuracao() != null && !criteria.getDuracao().isBlank()) {
            // suportar formatos simples: "<=90", "90-120", ">=120"
            String d = criteria.getDuracao().trim();
            try {
                if (d.startsWith("<=")) {
                    int v = Integer.parseInt(d.substring(2));
                    query.addCriteria(Criteria.where("duracao").lte(v));
                } else if (d.startsWith(">=")) {
                    int v = Integer.parseInt(d.substring(2));
                    query.addCriteria(Criteria.where("duracao").gte(v));
                } else if (d.contains("-")) {
                    String[] parts = d.split("-");
                    int a = Integer.parseInt(parts[0]);
                    int b = Integer.parseInt(parts[1]);
                    query.addCriteria(Criteria.where("duracao").gte(a).lte(b));
                } else {
                    int v = Integer.parseInt(d);
                    query.addCriteria(Criteria.where("duracao").is(v));
                }
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Formato inválido para duração");
            }
        }

        if (criteria.getDecada() != null) {
            int start = criteria.getDecada();
            int end = start + 9;
            query.addCriteria(Criteria.where("anoLancamento").gte(start).lte(end));
        }

        return mongoTemplate.find(query, Movie.class);
    }

    public Optional<Movie> pickRandom(List<Movie> candidates) {
        if (candidates == null || candidates.isEmpty()) return Optional.empty();
        int idx = random.nextInt(candidates.size());
        return Optional.of(candidates.get(idx));
    }

    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }

    public Optional<Movie> findById(String id) {
        return movieRepository.findById(id);
    }
}
