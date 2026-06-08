package br.com.blade.indicafilme.controller;

import br.com.blade.indicafilme.dto.DivinePatchDto;
import br.com.blade.indicafilme.dto.MovieDto;
import br.com.blade.indicafilme.dto.MovieRequestDto;
import br.com.blade.indicafilme.dto.StatusPatchDto;
import br.com.blade.indicafilme.exception.NotFoundException;
import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.model.StatusFilme;
import br.com.blade.indicafilme.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/movies")
@Tag(name = "Admin - Filmes", description = "Endpoints protegidos para gerenciamento de filmes (apenas admin)")
@SecurityRequirement(name = "AdminApiKey")
public class AdminMovieController {

        private static final Logger log = LoggerFactory.getLogger(AdminMovieController.class);
        private final MovieService service;

        @Autowired
        public AdminMovieController(MovieService service) {
                this.service = service;
        }

        @Operation(summary = "Criar filme")
        @PostMapping
        public ResponseEntity<MovieDto> criarFilme(@Valid @RequestBody MovieRequestDto dto) {
                log.info("POST /admin/movies criando '{}'", dto.getTitulo());
                Movie salvo = service.save(toMovie(dto));
                return ResponseEntity.status(HttpStatus.CREATED).body(MovieDto.fromMovie(salvo));
        }

        @Operation(summary = "Atualizar filme")
        @PutMapping("/{id}")
        public ResponseEntity<MovieDto> atualizarFilme(@PathVariable String id,@Valid @RequestBody MovieRequestDto dto) {
                log.info("PUT /admin/movies/{}", id);
                Movie existente = service.findById(id)
                                .orElseThrow(() -> new NotFoundException("Filme não encontrado: " + id));

                Movie atualizado = toMovie(dto);
                atualizado.setId(existente.getId());
                Movie salvo = service.save(atualizado);
                return ResponseEntity.ok(MovieDto.fromMovie(salvo));
        }

        @Operation(summary = "Atualizar campos divinos")
        @PatchMapping("/{id}/divine")
        public ResponseEntity<MovieDto> atualizarDivino(@PathVariable String id,@Valid @RequestBody DivinePatchDto dto) {
                log.info("PATCH /admin/movies/{}/divine", id);
                Movie filme = service.findById(id)
                                .orElseThrow(() -> new NotFoundException("Filme não encontrado: " + id));

                if (dto.getNotaDivina() != null)filme.setNotaDivina(dto.getNotaDivina());
                if (dto.getMotivoRecomendacao() != null)filme.setMotivoRecomendacao(dto.getMotivoRecomendacao());

                Movie salvo = service.save(filme);
                return ResponseEntity.ok(MovieDto.fromMovie(salvo));
        }

        @Operation(summary = "Atualizar status")
        @PatchMapping("/{id}/status")
        public ResponseEntity<MovieDto> atualizarStatus(@PathVariable String id,@Valid @RequestBody StatusPatchDto dto) {
                log.info("PATCH /admin/movies/{}/status -> {}", id, dto.getStatus());
                Movie salvo = service.atualizarStatus(id, dto.getStatus());
                return ResponseEntity.ok(MovieDto.fromMovie(salvo));
        }

        @Operation(summary = "Deletar filme")
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deletarFilme(@PathVariable String id) {
                log.info("DELETE /admin/movies/{}", id);
                if (service.findById(id).isEmpty()) {
                        throw new NotFoundException("Filme não encontrado: " + id);
                }
                service.deleteById(id);
                return ResponseEntity.noContent().build();
        }

        private Movie toMovie(MovieRequestDto dto) {
                Movie m = new Movie();
                m.setTitulo(dto.getTitulo());
                m.setAutor(dto.getAutor());
                m.setAnoLancamento(dto.getAnolancamento());
                m.setDuracao(dto.getDuracao());
                m.setGeneros(dto.getGeneros());
                m.setSinopse(dto.getSinopse());
                m.setNotaDivina(dto.getNotaDivina());
                m.setNotaPublico(dto.getNotaPublico());
                m.setMotivoRecomendacao(dto.getMotivoRecomendacao());
                m.setPoster(dto.getPoster());
                m.setPlataformas(dto.getPlataformas());
                m.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusFilme.AGUARDANDO);
                return m;
        }
}
