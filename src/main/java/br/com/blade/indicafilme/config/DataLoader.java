package br.com.blade.indicafilme.config;

import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.repository.FilmeData;
import br.com.blade.indicafilme.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class DataLoader{

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    public CommandLineRunner carregarDadosIniciais(MovieRepository repository){
        return args -> {
            try{
                long total = repository.count();
                if(total == 0){
                    log.info("Banco vazio - inserindo filmes iniciais...");
                    for (Movie filme : FilmeData.todos()) {
                        filme.setId(null);
                        repository.save(filme);
                    }
                    log.info("{} filme(s) inserido(s) com sucesso!", FilmeData.todos().size());
                } else {
                    log.info("Banco já contém {} filme(s) - carga inicial ignorada.", total);
                }
            } catch (Exception e) {
                log.warn ("Não foi possivel conectar ao MongoDB: {}", e.getMessage());
            }
        };
    }
}

