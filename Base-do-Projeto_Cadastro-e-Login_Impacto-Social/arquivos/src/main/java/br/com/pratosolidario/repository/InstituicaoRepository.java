package br.com.pratosolidario.repository;

import br.com.pratosolidario.model.Instituicao;
import br.com.pratosolidario.model.TipoUsuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Persistência de {@link Instituicao}: tabelas usuarios + instituicoes. */
@Repository
public class InstituicaoRepository extends UsuarioJdbcRepository<Instituicao> {

    private static final String SELECT_BASE = """
            SELECT u.id, u.nome, u.email, u.senha_hash,
                   i.area_atuacao, i.capacidade_atendimento
              FROM usuarios u
              JOIN instituicoes i ON i.usuario_id = u.id""";

    public InstituicaoRepository(JdbcTemplate jdbc) {
        super(jdbc);
    }

    @Override
    protected TipoUsuario tipo() {
        return TipoUsuario.INSTITUICAO;
    }

    @Override
    protected String sqlSelectBase() {
        return SELECT_BASE;
    }

    @Override
    protected RowMapper<Instituicao> rowMapper() {
        return (rs, numeroLinha) -> {
            Instituicao instituicao = new Instituicao();
            instituicao.setId(rs.getInt("id"));
            instituicao.setNome(rs.getString("nome"));
            instituicao.setEmail(rs.getString("email"));
            instituicao.setSenhaHash(rs.getString("senha_hash"));
            instituicao.setAreaAtuacao(rs.getString("area_atuacao"));
            instituicao.setCapacidadeAtendimento(rs.getInt("capacidade_atendimento"));
            return instituicao;
        };
    }

    @Override
    protected void inserirEspecializacao(Instituicao instituicao) {
        jdbc.update("INSERT INTO instituicoes (usuario_id, area_atuacao, capacidade_atendimento) VALUES (?, ?, ?)",
                instituicao.getId(), instituicao.getAreaAtuacao(), instituicao.getCapacidadeAtendimento());
    }

    @Override
    protected void atualizarEspecializacao(Instituicao instituicao) {
        jdbc.update("UPDATE instituicoes SET area_atuacao = ?, capacidade_atendimento = ? WHERE usuario_id = ?",
                instituicao.getAreaAtuacao(), instituicao.getCapacidadeAtendimento(), instituicao.getId());
    }

    /** RF06 — instituições que atuam em determinada região (sem diferenciar maiúsculas). */
    public List<Instituicao> listarPorAreaAtuacao(String regiao) {
        return jdbc.query(SELECT_BASE + " WHERE LOWER(TRIM(i.area_atuacao)) = LOWER(TRIM(?)) ORDER BY u.nome",
                rowMapper(), regiao);
    }

    public int contarReservas(Integer instituicaoId) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM reservas WHERE instituicao_id = ?", Integer.class, instituicaoId);
        return total == null ? 0 : total;
    }
}
