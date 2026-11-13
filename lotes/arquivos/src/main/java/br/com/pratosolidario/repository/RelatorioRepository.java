package br.com.pratosolidario.repository;

import br.com.pratosolidario.dto.FiltroRelatorio;
import br.com.pratosolidario.dto.RelatorioLinha;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Consultas do relatório de alimentos resgatados (RF05).
 * Considera apenas reservas CONCLUÍDAS (alimento efetivamente retirado).
 */
@Repository
public class RelatorioRepository {

    private final JdbcTemplate jdbc;

    public RelatorioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<RelatorioLinha> buscarResgates(FiltroRelatorio filtro) {
        StringBuilder sql = new StringBuilder("""
                SELECT r.id AS reserva_id, r.data_retirada,
                       l.doador_id, ud.nome AS doador_nome,
                       r.instituicao_id, ui.nome AS instituicao_nome,
                       l.tipo_alimento, l.quantidade_kg, r.familias_atendidas
                  FROM reservas r
                  JOIN lotes_doacao l ON l.id = r.lote_id
                  JOIN usuarios ud    ON ud.id = l.doador_id
                  JOIN usuarios ui    ON ui.id = r.instituicao_id
                 WHERE r.status = 'CONCLUIDA'""");
        List<Object> parametros = new ArrayList<>();

        // Os filtros só entram no SQL quando foram informados
        if (filtro.inicio() != null) {
            sql.append(" AND r.data_retirada >= ?");
            parametros.add(filtro.inicio().atStartOfDay());
        }
        if (filtro.fim() != null) {
            sql.append(" AND r.data_retirada < ?");
            parametros.add(filtro.fim().plusDays(1).atStartOfDay());
        }
        if (filtro.doadorId() != null) {
            sql.append(" AND l.doador_id = ?");
            parametros.add(filtro.doadorId());
        }
        if (filtro.instituicaoId() != null) {
            sql.append(" AND r.instituicao_id = ?");
            parametros.add(filtro.instituicaoId());
        }
        sql.append(" ORDER BY r.data_retirada, r.id");

        return jdbc.query(sql.toString(), (rs, numeroLinha) -> new RelatorioLinha(
                rs.getInt("reserva_id"),
                rs.getObject("data_retirada", LocalDateTime.class),
                rs.getInt("doador_id"),
                rs.getString("doador_nome"),
                rs.getInt("instituicao_id"),
                rs.getString("instituicao_nome"),
                rs.getString("tipo_alimento"),
                rs.getDouble("quantidade_kg"),
                rs.getInt("familias_atendidas")
        ), parametros.toArray());
    }
}
