package br.com.pratosolidario.exception;

/**
 * Regra de negócio que conflita com o estado atual dos dados
 * (ex.: lote já reservado, e-mail já cadastrado). Convertida em HTTP 409.
 */
public class ConflitoException extends RegraNegocioException {

    private static final long serialVersionUID = 1L;

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
