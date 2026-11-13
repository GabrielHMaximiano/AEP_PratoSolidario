package br.com.pratosolidario.repository;

import java.util.List;
import java.util.Optional;

/**
 * INTERFACE genérica com as quatro operações de CRUD.
 * Todos os repositórios do sistema a implementam (@Override), o que padroniza
 * o acesso ao banco e permite tratar qualquer repositório pelo mesmo contrato.
 *
 * @param <T> tipo da entidade de domínio
 */
public interface RepositorioCrud<T> {

    /** Create */
    T salvar(T entidade);

    /** Read (um) */
    Optional<T> buscarPorId(Integer id);

    /** Read (todos) */
    List<T> listarTodos();

    /** Update */
    T atualizar(T entidade);

    /** Delete — retorna true se algum registro foi removido. */
    boolean excluir(Integer id);
}
