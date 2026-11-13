package br.com.pratosolidario.exception;

/** E-mail ou senha inválidos no login. Convertida em HTTP 401. */
public class AutenticacaoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AutenticacaoException() {
        super("E-mail ou senha inválidos.");
    }
}
