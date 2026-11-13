package br.com.pratosolidario.model;

import br.com.pratosolidario.exception.ConflitoException;
import br.com.pratosolidario.exception.RegraNegocioException;

import java.time.LocalDateTime;

/**
 * Classe associativa entre {@link LoteDoacao} e {@link Instituicao}: registra
 * quem reservou o lote, quando retirou e quantas famílias foram atendidas.
 */
public class Reserva {

    private Integer id;
    private LoteDoacao lote;
    private Instituicao instituicao;
    private LocalDateTime dataReserva;
    private LocalDateTime dataRetirada;
    private Integer familiasAtendidas;
    private StatusReserva status = StatusReserva.ATIVA;

    public Reserva() {
    }

    public Reserva(LoteDoacao lote, Instituicao instituicao) {
        this.lote = lote;
        this.instituicao = instituicao;
        this.dataReserva = LocalDateTime.now();
        this.status = StatusReserva.ATIVA;
    }

    /**
     * Conclui a retirada: registra data/hora e número de famílias atendidas e
     * marca o lote como RETIRADO. Esses dados alimentam o RF04 e o RF05.
     */
    public void concluir(Integer familias) {
        if (status != StatusReserva.ATIVA) {
            throw new ConflitoException("Somente reservas ativas podem ser concluídas (status atual: " + status + ").");
        }
        if (familias == null || familias <= 0) {
            throw new RegraNegocioException("Informe quantas famílias foram atendidas (maior que zero).");
        }
        if (instituicao != null && instituicao.getCapacidadeAtendimento() != null
                && familias > instituicao.getCapacidadeAtendimento()) {
            throw new RegraNegocioException("O número de famílias (" + familias
                    + ") excede a capacidade de atendimento da instituição ("
                    + instituicao.getCapacidadeAtendimento() + ").");
        }
        lote.marcarRetirado();
        this.familiasAtendidas = familias;
        this.dataRetirada = LocalDateTime.now();
        this.status = StatusReserva.CONCLUIDA;
    }

    /** Cancela a reserva e devolve o lote para a vitrine de disponíveis. */
    public void cancelar() {
        if (status != StatusReserva.ATIVA) {
            throw new ConflitoException("Somente reservas ativas podem ser canceladas (status atual: " + status + ").");
        }
        lote.liberar();
        this.status = StatusReserva.CANCELADA;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LoteDoacao getLote() {
        return lote;
    }

    public void setLote(LoteDoacao lote) {
        this.lote = lote;
    }

    public Instituicao getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(Instituicao instituicao) {
        this.instituicao = instituicao;
    }

    public LocalDateTime getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(LocalDateTime dataReserva) {
        this.dataReserva = dataReserva;
    }

    public LocalDateTime getDataRetirada() {
        return dataRetirada;
    }

    public void setDataRetirada(LocalDateTime dataRetirada) {
        this.dataRetirada = dataRetirada;
    }

    public Integer getFamiliasAtendidas() {
        return familiasAtendidas;
    }

    public void setFamiliasAtendidas(Integer familiasAtendidas) {
        this.familiasAtendidas = familiasAtendidas;
    }

    public StatusReserva getStatus() {
        return status;
    }

    public void setStatus(StatusReserva status) {
        this.status = status;
    }
}
