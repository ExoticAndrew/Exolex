package exolex.exotic.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ClienteNotFoundException.class)
    public ResponseEntity<ErroResponse> handlerClienteNotFound(
            ClienteNotFoundException ex, WebRequest request) {
        return construirResposta(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(ProcessoNotFoundException.class)
    public ResponseEntity<ErroResponse> handlerProcessoNotFound(
            ProcessoNotFoundException ex, WebRequest request) {
        return construirResposta(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<ErroResponse> handlerAcessoNegado(
            AcessoNegadoException ex, WebRequest request) {
        return construirResposta(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), request);
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ErroResponse> handlerEmailJaCadastrado(
            EmailJaCadastradoException ex, WebRequest request) {
        return construirResposta(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErroResponse> handlerCredenciaisInvalidas(
            CredenciaisInvalidasException ex, WebRequest request) {
        return construirResposta(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handlerValidacaoCampos(
            MethodArgumentNotValidException ex, WebRequest request) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return construirResposta(HttpStatus.BAD_REQUEST, "Bad Request", mensagem, request);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErroResponse> handlerValidacaoHandlerMethod(
            HandlerMethodValidationException ex, WebRequest request) {
        String mensagem = ex.getAllErrors().stream()
                .map(erro -> erro.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return construirResposta(HttpStatus.BAD_REQUEST, "Bad Request", mensagem, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGenericException(
            Exception ex, WebRequest request) {
        logger.error("Erro inesperado na requisição {}: {}",
                request.getDescription(false), ex.getMessage(), ex);
        return construirResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                request
        );
    }

    private ResponseEntity<ErroResponse> construirResposta(
            HttpStatus status, String error, String message, WebRequest request) {
        ErroResponse erro = new ErroResponse(
                LocalDateTime.now(),
                status.value(),
                error,
                message,
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(status).body(erro);
    }
    @ExceptionHandler(PrazoNotFoundException.class)
    public ResponseEntity<ErroResponse> handlerPrazoNotFound(
            PrazoNotFoundException ex, WebRequest request) {
        return construirResposta(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }
}