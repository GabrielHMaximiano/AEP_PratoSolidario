package br.com.pratosolidario.model;

import br.com.pratosolidario.exception.RegraNegocioException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Instituição assistencial que recebe os alimentos (ex.: ONG Mesa Solidária).
 * Especialização de {@link Usuario} (HERANÇA).
 */
public class Instituicao extends Usuario {

    private String areaAtuacao;
    private Integer capacidadeAtendimento;
    private final List<Reserva> reservas = new ArrayList<>();

    public Instituicao() {
        super();
    }

    public Instituicao(String nome, String email, String areaAtuacao, Integer capacidadeAtendimento) {
        super(nome, email);
        this.areaAtuacao = areaAtuacao;
        this.capacidadeAtendimento = capacidadeAtendimento;
    }

    @Override
    public TipoUsuario getTipoUsuario() {
        return TipoUsuario.INSTITUICAO;
    }

    /** Regras comuns (super) + regras específicas da instituição. */
    @Override
    public void validar() {
        super.validar();
        if (areaAtuacao == null || areaAtuacao.isBlank()) {
            throw new RegraNegocioException("Informe a área (região) de atuação da instituição.");
        }
        if (capacidadeAtendimento == null || capacidadeAtendimento <= 0) {
            throw new RegraNegocioException("A capacidade de atendimento deve ser maior que zero.");
        }
    }

    /**
     * RF02 — reserva um lote para retirada. A regra de disponibilidade fica no
     * próprio lote ({@link LoteDoacao#reservar()}).
     */
    public Reserva reservarLote(LoteDoacao lote) {
        lote.reservar();
        Reserva reserva = new Reserva(lote, this);
        reservas.add(reserva);
        return reserva;
    }

    /** RF06 — a instituição atende a região em que o lote foi publicado? */
    public boolean atendeRegiao(String regiao) {
        return regiao != null && areaAtuacao != null
                && areaAtuacao.trim().equalsIgnoreCase(regiao.trim());
    }

    /**
     * RF04 — impacto da INSTITUIÇÃO: total de famílias atendidas nas
     * retiradas concluídas.
     */
    @Override
    public Double calcularImpacto() {
        return reservas.stream()
                .filter(reserva -> reserva.getStatus() == StatusReserva.CONCLUIDA)
                .mapToDouble(reserva -> reserva.getFamiliasAtendidas() == null ? 0 : reserva.getFamiliasAtendidas())
                .sum();
    }

    @Override
    public String getUnidadeImpacto() {
        return "famílias atendidas";
    }

    public void adicionarReserva(Reserva reserva) {
        reservas.add(reserva);
    }

    public List<Reserva> getReservas() {
        return Collections.unmodifiableList(reservas);
    }

    public String getAreaAtuacao() {
        return areaAtuacao;
    }

    public void setAreaAtuacao(String areaAtuacao) {
        this.areaAtuacao = areaAtuacao;
    }

    public Integer getCapacidadeAtendimento() {
        return capacidadeAtendimento;
    }

    public void setCapacidadeAtendimento(Integer capacidadeAtendimento) {
        this.capacidadeAtendimento = capacidadeAtendimento;
    }
}
