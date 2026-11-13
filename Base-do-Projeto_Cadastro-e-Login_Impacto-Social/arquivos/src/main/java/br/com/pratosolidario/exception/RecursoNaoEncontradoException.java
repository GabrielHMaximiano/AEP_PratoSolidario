package br.com.pratosolidario.exception;

/** Registro procurado não existe no banco. Convertida em HTTP 404. */
public class RecursoNaoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RecursoNaoEncontradoException(String recurso, Integer id) {
        super(recurso + " com id " + id + " não encontrado(a).");
    }
}
