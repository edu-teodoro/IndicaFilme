package br.com.blade.indicafilme.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Tratador global de exceções da aplicação Indica Filmes.
 *
 * Intercepta todas as exceções lançadas pelos controllers e as transforma
 * em respostas HTTP padronizadas usando o objeto {@link ApiError}.
 *
 * Funciona como um "guarda-chuva" - qualquer erro que aconteça em qualquer
 * parte do sistema passa por aqui antes de chegar ao user
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Trata erros de recurso não encontrado (404).
     * Ex: buscar um filme que não existe no banco.
     *
     * @param ex        a exceção lançada.
     * @param request   a requisição HTTP que originou o erro.
     * @return          resposta 404 com detalhes do erro.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException ex, HttpServletRequest request){
        log.warn("Recurso não encontrado: {} | path: {}", ex.getMessage(), request.getRequestURI());
        ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Trata erros de requisição inválida (400).
     * Ex: evitar uma busca sem filtro.
     *
     * @param ex        a exceção lançada.
     * @param request   a requisição HTTP que originou o erro.
     * @return          resposta 400 com detalhes do erro.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException ex, HttpServletRequest request){
        log.warn("Requisição inválida: {} | path: {}", ex.getMessage(), request.getRequestURI());
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata erros de validação de campos (400).
     * Ex: tentar criar um filme sem informar o título obrigatório.
     *
     * @param ex        a exceção de validação com detalhes de cada campo inválido.
     * @param request   a requisição HTTP que originou o erro.
     * @return          resposta 400 com a lista de campos inválidos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request){
        String mensagem = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Erro de validação: {} | path: {}", mensagem, request.getRequestURI());
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), mensagem, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata qualquer outro erro inesperado do sistema (500)
     * Garante que o usuário nunca veja um stack trace exposta
     *
     * @param ex        a exceção genérica capturada.
     * @param request   a requisição HTTP que originou o erro.
     * @return          resposta 500 com mensagem genérica.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request){
        log.error("Erro interno inesperado: {} | path: {}", ex.getMessage(), request.getRequestURI(), ex);
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um erro interno. Tente novamente mais tarde.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}