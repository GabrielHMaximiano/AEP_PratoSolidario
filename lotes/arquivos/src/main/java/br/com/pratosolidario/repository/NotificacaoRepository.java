package br.com.pratosolidario.repository;

import br.com.pratosolidario.model.Instituicao;
import br.com.pratosolidario.model.LoteDoacao;
import br.com.pratosolidario.model.Notificacao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Persistência de {@link Notificacao} (tabela notificacoes) — RF06. */
@Repository
public class NotificacaoRepository implements RepositorioCrud<Notificacao> {

    private static final String SELECT_BASE = """
            SELECT n.id, n.instituicao_id, n.lote_id, n.mensagem, n.data_criacao, n.lida
              FROM notificacoes n""";

    private static final RowMapper<Notificacao> ROW_MAPPER = (rs, numeroLinha) -> {
        Instituicao instituicao = new Instituicao();
        instituicao.setId(rs.getInt("instituicao_id"));
        LoteDoacao lote = new LoteDoacao();
        lote.setId(rs.getInt("lote_id"));

        Notificacao notificacao = new Notificacao();
        notificacao.setId(rs.getInt("id"));
        notificacao.setInstituicao(instituicao);
        notificacao.setLote(lote);
        notificacao.setMensagem(rs.getString("mensagem"));
        notificacao.setDataCriacao(rs.getObject("data_criacao", LocalDateTime.class));
        notificacao.setLida(rs.getBoolean("lida"));
        return notificacao;
    };

    private final JdbcTemplate jdbc;

    public NotificacaoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Notificacao salvar(Notificacao notificacao) {
        Integer id = jdbc.queryForObject("""
                        INSERT INTO notificacoes (instituicao_id, lote_id, mensagem, data_criacao, lida)
                        VALUES (?, ?, ?, ?, ?)
                        RETURNING id""",
                Integer.class,
                notificacao.getInstituicao().getId(), notificacao.getLote().getId(),
                notificacao.getMensagem(), notificacao.getDataCriacao(), notificacao.getLida());
        notificacao.setId(id);
        return notificacao;
    }

    @Override
    public Optional<Notificacao> buscarPorId(Integer id) {
        return jdbc.query(SELECT_BASE + " WHERE n.id = ?", ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public List<Notificacao> listarTodos() {
        return jdbc.query(SELECT_BASE + " ORDER BY n.data_criacao DESC, n.id DESC", ROW_MAPPER);
    }

    public List<Notificacao> listarPorInstituicao(Integer instituicaoId) {
        return jdbc.query(SELECT_BASE + " WHERE n.instituicao_id = ? ORDER BY n.lida, n.data_criacao DESC, n.id DESC",
                ROW_MAPPER, instituicaoId);
    }

    @Override
    public Notificacao atualizar(Notificacao notificacao) {
        jdbc.update("UPDATE notificacoes SET mensagem = ?, lida = ? WHERE id = ?",
                notificacao.getMensagem(), notificacao.getLida(), notificacao.getId());
        return notificacao;
    }

    @Override
    public boolean excluir(Integer id) {
        return jdbc.update("DELETE FROM notificacoes WHERE id = ?", id) > 0;
    }
}
