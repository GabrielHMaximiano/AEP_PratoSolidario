package br.com.pratosolidario.exception;

/**
 * Violação de uma regra de negócio do domínio (ex.: lote vencido, quantidade inválida).
 * Convertida em HTTP 400 pelo {@link GlobalExceptionHandler}.
 */
public class RegraNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
