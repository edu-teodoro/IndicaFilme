package br.com.blade.indicafilme.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Serviço de cache de sessão para controle de filmes exibidos recentemente.
 *
 * Responsavel por registrar o último filme exibido por sessão do user,
 * evitando qe=ue o mesmo filme seja sorteado consecutivamente.
 *
 * Funcionamento
 *      User sorteira um filme -> o ID é registrado no cache desta sessão.
 *      User sorteia novamente -> o sistema exclui o ultimo ID da lista de candidatos
 *      Se todos os candidatos forem o mesmo filme, o cache é ignorado (evita loop infinito).
 *
 * Escopo: cache por sessão HTTP identificada pelo ID da sessão.
 * Não presiste entre reinicializações do servidor.
 *
 * Usa {@link Caffeine} com TTL de 30 minutos após o últimmo acesso,
 * evitando memory leak em produção com muitos users simultaneos.
 */
@Service
public class MovieCacheService {
    private static final Logger log = LoggerFactory.getLogger(MovieCacheService.class);

    /**
     * Cache de sessionId -> ID do último filme exibido nesta sessão.
     * Expira automaticamente 15 minutos após o último acesso.
     * Limitado a 2.000 entradas simultâneas
     */
    private final Cache<String, String> ultimoFilmePorSessao = Caffeine.newBuilder()
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .maximumSize(2_000)
            .build();

    /**
     * Registra o ID do último filme exibido para uma determinada sessão
     *
     * @param sessionId identificador da sessão HTTP do user
     * @param filmeId   ID do filme que foi exibido
     */
    public void registrarUltimoFilme(String sessionId, String filmeId){
        log.debug("Cache: sessão={} -> último filme registrado={}", sessionId, filmeId);
        ultimoFilmePorSessao.put(sessionId, filmeId);
    }

    /**
     * Retorna o ID do último filme exibido para uma sessão, ou {@code null} se não houver registro.
     *
     * @param sessionId identificador da sessão HTTP do user.
     * @return ID do último filme exibido, ou {@code null} se a sessão ainda não tem histórico.
     */
    public String getUltimoFilmeId(String sessionId){
        return ultimoFilmePorSessao.getIfPresent(sessionId);
    }

    /**
     * Verifica se um determinado filme é o mesmo que o ultimo exibido nesta sessão.
     *
     * @param sessionId identificador da sessão HTTP do user
     * @param filmeId ID do filme a verificar.
     * @return {@code true} se este filme foi o ultimo exibido nesta sessão.
     */
    public boolean isUltimoFilme(String sessionId, String filmeId) {
        return filmeId != null && filmeId.equals(ultimoFilmePorSessao.getIfPresent(sessionId));
    }

    /**
     * Remove o registro do cache para uma sessão especifica.
     * Útil para limpar o histórico quando o user retorna à tela inicial.
     *
     * @param sessionId identificador da sessão HTTP do user.
     */
    public void limparSessao(String sessionId){
        log.debug("Cache: limpando sessão={}", sessionId);
        ultimoFilmePorSessao.invalidate(sessionId);
    }
}
