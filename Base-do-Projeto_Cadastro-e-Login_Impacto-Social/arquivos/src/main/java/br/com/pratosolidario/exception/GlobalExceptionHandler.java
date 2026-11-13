package br.com.pratosolidario.exception;

import br.com.pratosolidario.dto.ErroResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Converte as exceções da aplicação em respostas HTTP com JSON padronizado. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(RecursoNaoEncontradoException e) {
        return resposta(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResponse> conflito(ConflitoException e) {
        return resposta(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> regraNegocio(RegraNegocioException e) {
        return resposta(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(AutenticacaoException.class)
    public ResponseEntity<ErroResponse> autenticacao(AutenticacaoException e) {
        return resposta(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    /** Ex.: dois cadastros simultâneos com o mesmo e-mail ou duas reservas do mesmo lote. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> integridade(DataIntegrityViolationException e) {
        LOG.warn("Violação de integridade no banco: {}", e.getMostSpecificCause().getMessage());
        return resposta(HttpStatus.CONFLICT,
                "A operação viola uma regra de integridade do banco de dados (registro duplicado ou em uso).");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> jsonInvalido(HttpMessageNotReadableException e) {
        return resposta(HttpStatus.BAD_REQUEST,
                "JSON inválido. Confira os campos e os formatos (datas: 2026-11-20 e 2026-11-20T18:00).");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> parametroInvalido(MethodArgumentTypeMismatchException e) {
        return resposta(HttpStatus.BAD_REQUEST, "Valor inválido para o parâmetro '" + e.getName() + "'.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> erroInesperado(Exception e) {
        // Exceções padrão do Spring MVC (rota inexistente, método HTTP errado, parâmetro faltando...)
        // já carregam o status HTTP correto: devolve esse status em vez de 500.
        if (e instanceof org.springframework.web.ErrorResponse erroSpring) {
            HttpStatus status = HttpStatus.resolve(erroSpring.getStatusCode().value());
            return resposta(status == null ? HttpStatus.BAD_REQUEST : status, e.getMessage());
        }
        LOG.error("Erro inesperado", e);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor. Consulte o log da aplicação.");
    }

    private static ResponseEntity<ErroResponse> resposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(new ErroResponse(status.value(), status.getReasonPhrase(), mensagem));
    }
}
