package br.com.pratosolidario.repository;

import br.com.pratosolidario.model.TipoUsuario;
import br.com.pratosolidario.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

/**
 * Repositório ABSTRATO para a hierarquia de usuários (herança table-per-subclass).
 * <p>
 * Implementa o que é comum a qualquer usuário (tabela {@code usuarios}) e deixa
 * para as subclasses ({@link DoadorRepository}, {@link InstituicaoRepository})
 * o SQL da tabela especializada — padrão Template Method.
 *
 * @param <T> Doador ou Instituicao
 */
public abstract class UsuarioJdbcRepository<T extends Usuario> implements RepositorioCrud<T> {

    protected final JdbcTemplate jdbc;

    protected UsuarioJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Tipo de usuário gravado em usuarios.tipo_usuario. */
    protected abstract TipoUsuario tipo();

    /** SELECT com o JOIN entre usuarios (alias u) e a tabela da subclasse. */
    protected abstract String sqlSelectBase();

    /** Converte uma linha do ResultSet no objeto concreto. */
    protected abstract RowMapper<T> rowMapper();

    /** INSERT na tabela especializada (doadores ou instituicoes). */
    protected abstract void inserirEspecializacao(T usuario);

    /** UPDATE na tabela especializada (doadores ou instituicoes). */
    protected abstract void atualizarEspecializacao(T usuario);

    @Override
    public T salvar(T usuario) {
        Integer id = jdbc.queryForObject(
                "INSERT INTO usuarios (nome, email, senha_hash, tipo_usuario) VALUES (?, ?, ?, ?) RETURNING id",
                Integer.class,
                usuario.getNome(), usuario.getEmail(), usuario.getSenhaHash(), tipo().name());
        usuario.setId(id);
        inserirEspecializacao(usuario);
        return usuario;
    }

    @Override
    public Optional<T> buscarPorId(Integer id) {
        List<T> resultado = jdbc.query(sqlSelectBase() + " WHERE u.id = ?", rowMapper(), id);
        return resultado.stream().findFirst();
    }

    public Optional<T> buscarPorEmail(String email) {
        List<T> resultado = jdbc.query(sqlSelectBase() + " WHERE u.email = ?", rowMapper(),
                email == null ? null : email.trim().toLowerCase());
        return resultado.stream().findFirst();
    }

    @Override
    public List<T> listarTodos() {
        return jdbc.query(sqlSelectBase() + " ORDER BY u.nome", rowMapper());
    }

    @Override
    public T atualizar(T usuario) {
        jdbc.update("UPDATE usuarios SET nome = ?, email = ?, senha_hash = ? WHERE id = ? AND tipo_usuario = ?",
                usuario.getNome(), usuario.getEmail(), usuario.getSenhaHash(), usuario.getId(), tipo().name());
        atualizarEspecializacao(usuario);
        return usuario;
    }

    /** Remove da tabela usuarios; o ON DELETE CASCADE remove a linha da subclasse. */
    @Override
    public boolean excluir(Integer id) {
        return jdbc.update("DELETE FROM usuarios WHERE id = ? AND tipo_usuario = ?", id, tipo().name()) > 0;
    }

    /** E-mail já usado por OUTRO usuário (de qualquer tipo)? */
    public boolean emailEmUso(String email, Integer idIgnorado) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM usuarios WHERE email = ? AND id <> ?",
                Integer.class, email, idIgnorado == null ? -1 : idIgnorado);
        return total != null && total > 0;
    }
}
