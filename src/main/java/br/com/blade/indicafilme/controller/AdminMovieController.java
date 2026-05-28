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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de adm para gerenciamento de filmes
 *
 * Todos os endpoints requerem o header {@code X-Admin-Key} com a chave de API
 * configurada em {@code admin.api-key}.
 *
 * Rotas disponíveis:
 * {@code POST /api/v1/admin/movies} - cria um novo filme
 * {@code PUT /api/v1/admin/movies/{id}} - atualiza todos os dados de um filme
 * {@code PATCH /api/v1/admin/movies/{id}/divine} - atualiza campos divinos
 * (nota e motivo)
 * {@code PATCH /api/v1/admin/movies/{id}/status} - atualiza apenas o status do
 * filme
 * {@code DELETE /api/v1/admin/movies/{id}} - remove um filme
 *
 * Somente o admin tem acesso a estes endpoints. Users comuns
 * acessam apenas os endpoints publicos {@code /api/v1/movies}
 */

@RestController
@RequestMapping("/api/v1/admin/movies")
@Tag(name = "Admin - Filmes", description = "Endpoints protegidos para gerenciamento de filmes (apenas admin)")
@SecurityRequirement(name = "AdminApiKey")
public class AdminMovieController {

        private static final Logger log = LoggerFactory.getLogger(AdminMovieController.class);

        private final MovieService service;

        /**
         * Construtor com injeção do serviço de filmes.
         *
         * @param service serviço de regras de negócio de filmes
         */
        @Autowired
        public AdminMovieController(MovieService service) {
                this.service = service;
        }

        /**
         * POST /api/v1/admin/movies - cria um novo filme
         *
         * Cria um novo filme no sistema
         *
         * O status padrão é {@code AGUARDANDO} - o filme não aparece para
         * users até ser marcado como {@code ATIVO}
         *
         * @param dto dados do filme a ser criado (validados automaticamente pelo
         *            Spring)
         * @return {@code 201 Created} com o filme criado e seu ID gerado
         */
        @Operation(summary = "Criar filme", description = "Cria um novo filme no banco de dados. Status padrão: AGUARDANDO.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Filme criado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado - header X-Admin-Key ausente ou inválido")
        })
        @PostMapping
        public ResponseEntity<MovieDto> criarFilme(@Valid @RequestBody MovieRequestDto dto) {
                log.info("POST /admin/movies -> criando filme '{}'", dto.getTitulo());

                Movie filme = toMovie(dto);
                Movie salvo = service.save(filme);

                log.info("Filme criado: '{}' (id={})", salvo.getTitulo(), salvo.getId());
                return ResponseEntity.status(HttpStatus.CREATED).body(MovieDto.fromMovie(salvo));
        }

        /**
         * PUT /api/v1/admin/movies/{id} - atualiza um filme completo
         *
         * Atualiza todos os dados de um filme existente.
         *
         * Substitui todos os campos do filme pelo conteudo do DTO fornecido.
         * Para atualizar apenas campos divinos, use o endpoint {@code PATCH /divine}.
         *
         * @param id  ID do filme a atualizar
         * @param dto novos dados completos do filme
         * @return {@code 200 OK} com o filme atualizado
         */
        @Operation(summary = "Atualizar filme", description = "Substitui todos os dados de um filme pelo conteúdo fornecido.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Filme atualizado com sucesso com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado"),
                        @ApiResponse(responseCode = "404", description = "Filme não encontrado")
        })
        @PutMapping("/{id}")
        public ResponseEntity<MovieDto> atualizarFilme(
                        @Parameter(description = "ID do filme no MongoDB") @PathVariable String id,
                        @Valid @RequestBody MovieRequestDto dto) {

                log.info("PUT /admin/movies/{} -> atualizando filme", id);

                Movie existente = service.findById(id)
                                .orElseThrow(() -> new NotFoundException("Filme não encontrado: " + id));

                // Mantém o mesmo ID e atualiza os demais campos
                Movie atualizado = toMovie(dto);
                atualizado.setId(existente.getId());

                Movie salvo = service.save(atualizado);
                log.info("Filme atualizado: '{}' (id={})", salvo.getTitulo(), salvo.getId());
                return ResponseEntity.ok(MovieDto.fromMovie(salvo));
        }

        /**
         * PATCH /api/v1/admin/movies/{id}/divine - atualiza campos divinos
         * 
         * Atualiza apenas os campos 'divinos' de um filme: nota e motivo da
         * recomendação
         * 
         * os campos nao informados (nulos no DTO) são mantidos sem alterações
         * 
         * @param id  ID do filme a atualizar
         * @param dto campos divinos a atualizar (ambos opcionais)
         *            // * @return {@code 200 OK} com o filme com campos divinos
         *            atualizados.
         */
        @Operation(summary = "Atualizar campos divinos", description = "Atualiza apenas notaDivina e/ou motivoRecomendacao de um filme. "
                        +
                        "Campos nulos no body são ignorados (mantidos sem alteração).")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Campos divinos atualizados com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado"),
                        @ApiResponse(responseCode = "404", description = "Filme não encontrado")
        })
        @PatchMapping("/{id}/divine")
        public ResponseEntity<MovieDto> atualizarDivino(
                        @Parameter(description = "ID do filme no MongoDB") @PathVariable String id,
                        @Valid @RequestBody DivinePatchDto dto) {

                log.info("PATCH /admin/movies/{}/divine", id);

                Movie filme = service.findById(id)
                                .orElseThrow(() -> new NotFoundException("Filme não encontrado: " + id));

                // Aplica apenas os campos informados (não-nulos).
                if (dto.getNotaDivina() != null) {
                        filme.setNotaDivina(dto.getNotaDivina());
                }
                if (dto.getMotivoRecomendacao() != null) {
                        filme.setMotivoRecomendacao(dto.getMotivoRecomendacao());
                }

                Movie salvo = service.save(filme);
                log.info("Campos divinos atualizados para filme '{}' (id={})", salvo.getTitulo(), salvo.getId());
                return ResponseEntity.ok(MovieDto.fromMovie(salvo));
        }

        /**
         * PATCH /api/v1/admin/movies/{id}/status - atualiza o status do filme
         *
         * Atualiza apenas o status de curadoria de um filme.
         *
         * Controla a visibilidade do filme para os usuarios:
         *
         * {@code ATIVO} - filme aparece nos endpoints públicos
         * {@code AGUARDANDO} - filme oculto, aguardando revisão
         * {@code ERRO} - filme oculto, marcado com problema
         *
         * @param id  ID do filme a atualizar
         * @param dto novo status a aplicar
         * @return {@code 200 OK} com o filme atualizado
         */
        @Operation(summary = "Atualizar status do filme", description = "Altera apenas o status de curadoria. " +
                        "ATIVO = filme visivel para users. AGUARDANDO/ERRO = filme oculto.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Status inválido ou ausente"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado"),
                        @ApiResponse(responseCode = "404", description = "Filme não encontrado")
        })
        @PatchMapping("/{id}/status")
        public ResponseEntity<MovieDto> atualizarStatus(
                        @Parameter(description = "ID do filme no MongoDB") @PathVariable String id,
                        @Valid @RequestBody StatusPatchDto dto) {

                log.info("PATCH /admin/movies/{}/status -> novo status: {}", id, dto.getStatus());

                Movie salvo = service.atualizarStatus(id, dto.getStatus());

                log.info("Status do filme '{}' (id={}) atualizado para {}", salvo.getTitulo(), salvo.getId(),
                                dto.getStatus());
                return ResponseEntity.ok(MovieDto.fromMovie(salvo));
        }

        /**
         * DELETE /api/v1/admin/movies/{id} - remove um filme
         *
         * Remove um filme do sistema pelo ID.
         *
         * @param id ID do filme a remover.
         * @return {@code 204 No Content} se removido com sucesso, {@code 404} se não
         *         existir
         */
        @Operation(summary = "Deletar filme", description = "Remove permanentemente um filme do banco de dados.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Filme removido com sucesso"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado"),
                        @ApiResponse(responseCode = "404", description = "Filme não encontrado"),
        })
        @PatchMapping("/{id}")
        public ResponseEntity<Void> deletarFilme(
                        @Parameter(description = "ID do filme no MongoDB") @PathVariable String id) {

                log.info("DELETE /admin/movies/{}", id);

                if (service.findById(id).isEmpty()) {
                        throw new NotFoundException("Filme não encontrado: " + id);
                }

                service.deleteById(id);
                log.info("Filme id={} deletado com sucesso", id);
                return ResponseEntity.noContent().build();
        }

        /**
         * Conversão privado DTO -> entidade
         *
         * Converte um {@link MovieRequestDto} em uma entidade {@link Movie}.
         *
         * @param dto DTO recebido na requisição.
         * @return entidade pronta para salvar.
         */
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
