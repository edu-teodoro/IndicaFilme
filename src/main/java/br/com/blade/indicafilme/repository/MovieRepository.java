package br.com.blade.indicafilme.repository;

import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.model.StatusFilme;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório de acesso aos filmes no MongoDB.
 * 
 * Estende {@link MongoRepository} - o Spring Data gera automaticamente toda a implementação de {@code findAll}, {@code findById}, {@code save}, {@code delete}, etc. Você não precisa escrever nenhuma query SQL.
 * 
 * Analogia: é como contratar um funcionário que já sabe fazer tudo - você só diz o que quer e ele faz.
 * 
 * Para testes sem MongoDB: use o {@link InMemoryMovieRepository} anotado com {@code @Primary} e o perfil {@code test}.
 */

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {

    /**
     * Busca todos os filmes com um determinado status.
     * 
     * O Spring Data gera a query automaticamente pelo nome do método: {@code findBy} + {@code Status} -> {@code WHERE status = ?}
     * 
     * @param status status desejado (ex: {@link StatusFilme#ATIVO}).
     * @return lista de filmes com o status especificado. 
     */
    
    List<Movie> findByStatus(StatusFilme status);
    
    Optional<Movie> findByTituloIgnoreCase(String titulo);    
}
