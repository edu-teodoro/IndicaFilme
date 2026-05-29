package br.com.blade.indicafilme.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class MovieCacheService {

    private final Cache<String, String> ultimoFilmePorSessao = Caffeine.newBuilder()
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .maximumSize(2_000)
            .build();

    public void registrarUltimoFilme(String sessionId, String filmeId){
        ultimoFilmePorSessao.put(sessionId, filmeId);
    }

    public String getUltimoFilmeId(String sessionId){
        return ultimoFilmePorSessao.getIfPresent(sessionId);
    }

    public boolean isUltimoFilme(String sessionId, String filmeId) {
        return filmeId != null && filmeId.equals(ultimoFilmePorSessao.getIfPresent(sessionId));
    }

    public void limparSessao(String sessionId){
        ultimoFilmePorSessao.invalidate(sessionId);
    }
}
