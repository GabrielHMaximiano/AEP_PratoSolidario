package br.com.pratosolidario.repository;

import br.com.pratosolidario.model.Doador;
import br.com.pratosolidario.model.LoteDoacao;
import br.com.pratosolidario.model.StatusLote;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Persistência de {@link LoteDoacao} (tabela lotes_doacao) — entidade principal do CRUD. */
@Repository
public class LoteDoacaoRepository implements RepositorioCrud<LoteDoacao> {

    private static final String SELECT_BASE = """
            SELECT l.id, l.doador_id, u.nome AS doador_nome, d.regiao AS doador_regiao,
                   l.tipo_alimento, l.quantidade_kg, l.data_validade,
                   l.retirada_inicio, l.retirada_fim, l.status, l.data_publicacao
              FROM lotes_doacao l
              JOIN doadores d ON d.usuario_id = l.doador_id
              JOIN usuarios u ON u.id = d.usuario_id""";

    private static final RowMapper<LoteDoacao> ROW_MAPPER = (rs, numeroLinha) -> {
        Doador doador = new Doador();
        doador.setId(rs.getInt("doador_id"));
        doador.setNome(rs.getString("doador_nome"));
        doador.setRegiao(rs.getString("doador_regiao"));

        LoteDoacao lote = new LoteDoacao();
        lote.setId(rs.getInt("id"));
        lote.setDoador(doador);
        lote.setTipoAlimento(rs.getString("tipo_alimento"));
        lote.setQuantidadeKg(rs.getDouble("quantidade_kg"));
        lote.setDataValidade(rs.getObject("data_validade", LocalDate.class));
        lote.definirJanelaRetirada(rs.getObject("retirada_inicio", LocalDateTime.class),
                rs.getObject("retirada_fim", LocalDateTime.class));
        lote.setStatus(StatusLote.valueOf(rs.getString("status")));
        lote.setDataPublicacao(rs.getObject("data_publicacao", LocalDateTime.class));
        return lote;
    };

    private final JdbcTemplate jdbc;

    public LoteDoacaoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public LoteDoacao salvar(LoteDoacao lote) {
        Integer id = jdbc.queryForObject("""
                        INSERT INTO lotes_doacao (doador_id, tipo_alimento, quantidade_kg, data_validade,
                                                  retirada_inicio, retirada_fim, status, data_publicacao)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                        RETURNING id""",
                Integer.class,
                lote.getDoador().getId(), lote.getTipoAlimento(), lote.getQuantidadeKg(), lote.getDataValidade(),
                lote.getRetiradaInicio(), lote.getRetiradaFim(), lote.getStatus().name(), lote.getDataPublicacao());
        lote.setId(id);
        return lote;
    }

    @Override
    public Optional<LoteDoacao> buscarPorId(Integer id) {
        return jdbc.query(SELECT_BASE + " WHERE l.id = ?", ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public List<LoteDoacao> listarTodos() {
        return jdbc.query(SELECT_BASE + " ORDER BY l.data_validade, l.id", ROW_MAPPER);
    }

    public List<LoteDoacao> listarPorDoador(Integer doadorId) {
        return jdbc.query(SELECT_BASE + " WHERE l.doador_id = ? ORDER BY l.data_validade, l.id", ROW_MAPPER, doadorId);
    }

    /**
     * RF02 — lotes que ainda podem ser reservados, do que vence primeiro para
     * o que vence por último (prioridade por urgência).
     */
    public List<LoteDoacao> listarDisponiveis(LocalDate hoje) {
        return jdbc.query(SELECT_BASE
                        + " WHERE l.status = 'DISPONIVEL' AND l.data_validade >= ?"
                        + " ORDER BY l.data_validade, l.retirada_inicio, l.id",
                ROW_MAPPER, hoje);
    }

    @Override
    public LoteDoacao atualizar(LoteDoacao lote) {
        jdbc.update("""
                        UPDATE lotes_doacao
                           SET tipo_alimento = ?, quantidade_kg = ?, data_validade = ?,
                               retirada_inicio = ?, retirada_fim = ?, status = ?
                         WHERE id = ?""",
                lote.getTipoAlimento(), lote.getQuantidadeKg(), lote.getDataValidade(),
                lote.getRetiradaInicio(), lote.getRetiradaFim(), lote.getStatus().name(), lote.getId());
        return lote;
    }

    public void atualizarStatus(Integer loteId, StatusLote novoStatus) {
        jdbc.update("UPDATE lotes_doacao SET status = ? WHERE id = ?", novoStatus.name(), loteId);
    }

    /**
     * Reserva atômica: só altera se o lote AINDA estiver disponível.
     * Se duas instituições tentarem ao mesmo tempo, apenas uma recebe "1 linha
     * alterada"; a outra recebe 0 e a reserva é recusada.
     */
    public boolean reservarSeDisponivel(Integer loteId) {
        return jdbc.update("UPDATE lotes_doacao SET status = 'RESERVADO' WHERE id = ? AND status = 'DISPONIVEL'",
                loteId) == 1;
    }

    @Override
    public boolean excluir(Integer id) {
        return jdbc.update("DELETE FROM lotes_doacao WHERE id = ?", id) > 0;
    }
}
