package br.com.blade.indicafilme.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório de acesso aos filmes no MongoDB.
 * 
 * <p>Estende {@link MongoRepository} - o Spring Data gera automaticamente toda a implementação de {@code findAll}, {@code findById}, {@code save}, {@code delete}, etc. Você não precisa escrever nenhuma query SQL.</p>
 * 
 * <p>Analogia: é como contratar um funcionário que já sabe fazer tudo - você só diz o que quer e ele faz.</p>
 * 
 * <p><b>Para testes sem MongoDB:</b> use o {@link InMemoryMovieRepository} anotado com {@code @Primary} e o perfil {@code test}.</p>
 */

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {

    /**
     * Busca todos os filmes com um determinado status.
     * 
     * <p>O Spring Data gera a query automaticamente pelo nome do método: {@code findBy} + {@code Status} -> {@code WHERE status = ?}</p>
     * 
     * @param status status desejado (ex: {@link StatusFilme#ATIVO}).
     * @return lista de filmes com o status especificado. 
     */
    
    List<Movie> findByStatus(StatusFilme status);
    
    Optional<Movie> findByTituloIgnoreCase(String titulo);    
}
