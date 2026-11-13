package br.com.pratosolidario.repository;

import br.com.pratosolidario.model.Doador;
import br.com.pratosolidario.model.TipoUsuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/** Persistência de {@link Doador}: tabelas usuarios + doadores. */
@Repository
public class DoadorRepository extends UsuarioJdbcRepository<Doador> {

    private static final String SELECT_BASE = """
            SELECT u.id, u.nome, u.email, u.senha_hash,
                   d.documento, d.tipo_estabelecimento, d.regiao
              FROM usuarios u
              JOIN doadores d ON d.usuario_id = u.id""";

    public DoadorRepository(JdbcTemplate jdbc) {
        super(jdbc);
    }

    @Override
    protected TipoUsuario tipo() {
        return TipoUsuario.DOADOR;
    }

    @Override
    protected String sqlSelectBase() {
        return SELECT_BASE;
    }

    @Override
    protected RowMapper<Doador> rowMapper() {
        return (rs, numeroLinha) -> {
            Doador doador = new Doador();
            doador.setId(rs.getInt("id"));
            doador.setNome(rs.getString("nome"));
            doador.setEmail(rs.getString("email"));
            doador.setSenhaHash(rs.getString("senha_hash"));
            doador.setDocumento(rs.getString("documento"));
            doador.setTipoEstabelecimento(rs.getString("tipo_estabelecimento"));
            doador.setRegiao(rs.getString("regiao"));
            return doador;
        };
    }

    @Override
    protected void inserirEspecializacao(Doador doador) {
        jdbc.update("INSERT INTO doadores (usuario_id, documento, tipo_estabelecimento, regiao) VALUES (?, ?, ?, ?)",
                doador.getId(), doador.getDocumento(), doador.getTipoEstabelecimento(), doador.getRegiao());
    }

    @Override
    protected void atualizarEspecializacao(Doador doador) {
        jdbc.update("UPDATE doadores SET documento = ?, tipo_estabelecimento = ?, regiao = ? WHERE usuario_id = ?",
                doador.getDocumento(), doador.getTipoEstabelecimento(), doador.getRegiao(), doador.getId());
    }

    public boolean documentoEmUso(String documento, Integer idIgnorado) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM doadores WHERE documento = ? AND usuario_id <> ?",
                Integer.class, documento, idIgnorado == null ? -1 : idIgnorado);
        return total != null && total > 0;
    }

    public int contarLotes(Integer doadorId) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM lotes_doacao WHERE doador_id = ?", Integer.class, doadorId);
        return total == null ? 0 : total;
    }
}
