package br.com.pratosolidario.repository;

import br.com.pratosolidario.model.Doador;
import br.com.pratosolidario.model.Instituicao;
import br.com.pratosolidario.model.LoteDoacao;
import br.com.pratosolidario.model.Reserva;
import br.com.pratosolidario.model.StatusLote;
import br.com.pratosolidario.model.StatusReserva;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Persistência de {@link Reserva} (tabela associativa reservas). */
@Repository
public class ReservaRepository implements RepositorioCrud<Reserva> {

    private static final String SELECT_BASE = """
            SELECT r.id, r.data_reserva, r.data_retirada, r.familias_atendidas, r.status,
                   r.instituicao_id, ui.nome AS instituicao_nome, i.capacidade_atendimento,
                   l.id AS lote_id, l.tipo_alimento, l.quantidade_kg, l.data_validade,
                   l.status AS lote_status, l.doador_id, ud.nome AS doador_nome
              FROM reservas r
              JOIN lotes_doacao l  ON l.id = r.lote_id
              JOIN instituicoes i  ON i.usuario_id = r.instituicao_id
              JOIN usuarios ui     ON ui.id = i.usuario_id
              JOIN usuarios ud     ON ud.id = l.doador_id""";

    private static final RowMapper<Reserva> ROW_MAPPER = (rs, numeroLinha) -> {
        Doador doador = new Doador();
        doador.setId(rs.getInt("doador_id"));
        doador.setNome(rs.getString("doador_nome"));

        LoteDoacao lote = new LoteDoacao();
        lote.setId(rs.getInt("lote_id"));
        lote.setDoador(doador);
        lote.setTipoAlimento(rs.getString("tipo_alimento"));
        lote.setQuantidadeKg(rs.getDouble("quantidade_kg"));
        lote.setDataValidade(rs.getObject("data_validade", LocalDate.class));
        lote.setStatus(StatusLote.valueOf(rs.getString("lote_status")));

        Instituicao instituicao = new Instituicao();
        instituicao.setId(rs.getInt("instituicao_id"));
        instituicao.setNome(rs.getString("instituicao_nome"));
        instituicao.setCapacidadeAtendimento(rs.getInt("capacidade_atendimento"));

        Reserva reserva = new Reserva();
        reserva.setId(rs.getInt("id"));
        reserva.setLote(lote);
        reserva.setInstituicao(instituicao);
        reserva.setDataReserva(rs.getObject("data_reserva", LocalDateTime.class));
        reserva.setDataRetirada(rs.getObject("data_retirada", LocalDateTime.class));
        reserva.setFamiliasAtendidas(rs.getObject("familias_atendidas", Integer.class));
        reserva.setStatus(StatusReserva.valueOf(rs.getString("status")));
        return reserva;
    };

    private final JdbcTemplate jdbc;

    public ReservaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Reserva salvar(Reserva reserva) {
        Integer id = jdbc.queryForObject("""
                        INSERT INTO reservas (lote_id, instituicao_id, data_reserva, status)
                        VALUES (?, ?, ?, ?)
                        RETURNING id""",
                Integer.class,
                reserva.getLote().getId(), reserva.getInstituicao().getId(),
                reserva.getDataReserva(), reserva.getStatus().name());
        reserva.setId(id);
        return reserva;
    }

    @Override
    public Optional<Reserva> buscarPorId(Integer id) {
        return jdbc.query(SELECT_BASE + " WHERE r.id = ?", ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public List<Reserva> listarTodos() {
        return jdbc.query(SELECT_BASE + " ORDER BY r.data_reserva DESC, r.id DESC", ROW_MAPPER);
    }

    public List<Reserva> listarPorInstituicao(Integer instituicaoId) {
        return jdbc.query(SELECT_BASE + " WHERE r.instituicao_id = ? ORDER BY r.data_reserva DESC, r.id DESC",
                ROW_MAPPER, instituicaoId);
    }

    @Override
    public Reserva atualizar(Reserva reserva) {
        jdbc.update("UPDATE reservas SET data_retirada = ?, familias_atendidas = ?, status = ? WHERE id = ?",
                reserva.getDataRetirada(), reserva.getFamiliasAtendidas(), reserva.getStatus().name(), reserva.getId());
        return reserva;
    }

    @Override
    public boolean excluir(Integer id) {
        return jdbc.update("DELETE FROM reservas WHERE id = ?", id) > 0;
    }

    /** Usado antes de excluir um lote: remove o histórico de reservas canceladas dele. */
    public int excluirCanceladasDoLote(Integer loteId) {
        return jdbc.update("DELETE FROM reservas WHERE lote_id = ? AND status = 'CANCELADA'", loteId);
    }
}
