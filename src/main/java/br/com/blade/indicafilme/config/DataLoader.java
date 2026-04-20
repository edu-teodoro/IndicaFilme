package br.com.blade.indicafilme.config;

import br.com.blade.indicafilme.repository.InMemoryMovieRepository;
import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.repository.FilmeData;
import br.com.blade.indicafilme.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Config de dados iniciais da aplicação
 *
 * Quando a aplicação sobe pela primeira vez com o banco vazio,
 * este loader insere automaticamente os filmes do {@link FilmeData}
 * na coleção {@code movies} do MongoDB.
 *
 * Se o banco já tiver filmes, ele não faz nada - evita duplicações.
 *
 * Se o MongoDB estiver indisponível, o loader ignora silenciosamente
 * a carga e a aplicação continua usando o repo em memória ({@link InMemoryMovieRepository}).
 *
 * Não é executado no perfil {@code test} - testes usam o repositório em memória diretamente.
 */
@Configuration
@Profile("!test")
public class DataLoader{
    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    /**
     * Bean executado automaticamente quando a aplicação termina de subir.
     *
     * {@link CommandLineRunner} é uma interface do Spring Boot que roda
     * um bloco de código uma única vez logo após a inicialização.
     *
     * Envolve as chamadas ao MongoDB em {@code try/catch} para tolerar
     * falhas de conexão sem derrubar a aplicação
     *
     * @param repository repositório de filmes (MongoDB em produção, memória como fallback).
     * @return runner que popula o banco se estiver vazio e acessível
     */
    @Bean
    public CommandLineRunner carregarDadosIniciais(MovieRepository repository){
        return args -> {
            try{
                long total = repository.count();
                if(total == 0){
                    log.info("Banco vazio - inserindo filmes iniciais do FilmeData...");
                    for (Movie filme : FilmeData.todos()) {
                        // Limpa o ID para deixar o MongoDB gerar um novo ID automaticamente
                        filme.setId(null);
                        repository.save(filme);
                    }
                    log.info("{} filme(s) inserido(s) com sucesso!", FilmeData.todos().size());
                } else {
                    log.info("Banco já contém {} filme(s) - carga inicial ignorada.", total);
                }
            } catch (Exception e) {
                log.warn ("Não foi possivel conectar ao MongoDB para carga inicial: {}", e.getMessage());
                log.warn ("A aplicação continuará usando a base de filmes em memoria (FilmeData).");
            }
        };
    }
}

