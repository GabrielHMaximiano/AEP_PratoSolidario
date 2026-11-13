package br.com.pratosolidario.model;

import br.com.pratosolidario.exception.ConflitoException;
import br.com.pratosolidario.exception.RegraNegocioException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Lote de alimento publicado por um {@link Doador} — entidade principal do
 * CRUD do sistema.
 * <p>
 * COMPOSIÇÃO: o lote é composto por uma {@link JanelaRetirada}, criada e
 * substituída somente pelo próprio lote. Por sua vez, o lote faz parte da
 * coleção de lotes do {@link Doador} que o publicou.
 */
public class LoteDoacao {

    private Integer id;
    private Doador doador;
    private String tipoAlimento;
    private Double quantidadeKg;
    private LocalDate dataValidade;
    private JanelaRetirada janelaRetirada;
    private StatusLote status = StatusLote.DISPONIVEL;
    private LocalDateTime dataPublicacao;

    public LoteDoacao() {
    }

    public LoteDoacao(Doador doador, String tipoAlimento, Double quantidadeKg, LocalDate dataValidade,
                      LocalDateTime retiradaInicio, LocalDateTime retiradaFim) {
        this.doador = doador;
        this.tipoAlimento = tipoAlimento;
        this.quantidadeKg = quantidadeKg;
        this.dataValidade = dataValidade;
        this.janelaRetirada = new JanelaRetirada(retiradaInicio, retiradaFim);
        this.status = StatusLote.DISPONIVEL;
        this.dataPublicacao = LocalDateTime.now();
    }

    /** RF03 — nível de urgência considerando a data de hoje. */
    public NivelUrgencia calcularUrgencia() {
        return calcularUrgencia(LocalDate.now());
    }

    /** RF03 — versão que recebe a data de referência (facilita os testes). */
    public NivelUrgencia calcularUrgencia(LocalDate hoje) {
        return NivelUrgencia.porDiasRestantes(diasParaVencer(hoje));
    }

    public long diasParaVencer(LocalDate hoje) {
        return ChronoUnit.DAYS.between(hoje, dataValidade);
    }

    /** Valida os dados de cadastro/alteração do lote (RF01). */
    public void validar() {
        if (doador == null) {
            throw new RegraNegocioException("O lote precisa estar vinculado a um doador.");
        }
        if (tipoAlimento == null || tipoAlimento.isBlank()) {
            throw new RegraNegocioException("Informe o tipo de alimento.");
        }
        if (quantidadeKg == null || quantidadeKg <= 0) {
            throw new RegraNegocioException("A quantidade (kg) deve ser maior que zero.");
        }
        if (dataValidade == null) {
            throw new RegraNegocioException("Informe a data de validade.");
        }
        if (dataValidade.isBefore(LocalDate.now())) {
            throw new RegraNegocioException("Não é possível publicar um lote já vencido.");
        }
        if (janelaRetirada == null) {
            throw new RegraNegocioException("Informe o início e o fim da janela de retirada.");
        }
        janelaRetirada.validar();   // a parte valida a si mesma
    }

    /** Só lotes ainda disponíveis podem ser editados ou excluídos. */
    public boolean isEditavel() {
        return status == StatusLote.DISPONIVEL;
    }

    /** Transição DISPONIVEL -> RESERVADO, com as regras de negócio do RF02. */
    public void reservar() {
        if (status != StatusLote.DISPONIVEL) {
            throw new ConflitoException("O lote " + id + " não está mais disponível (status: " + status + ").");
        }
        if (calcularUrgencia() == NivelUrgencia.VENCIDO) {
            throw new RegraNegocioException("O lote " + id + " está vencido e não pode ser reservado.");
        }
        status = StatusLote.RESERVADO;
    }

    /** Transição RESERVADO -> RETIRADO (retirada concluída). */
    public void marcarRetirado() {
        if (status != StatusLote.RESERVADO) {
            throw new ConflitoException("Somente lotes reservados podem ser marcados como retirados.");
        }
        status = StatusLote.RETIRADO;
    }

    /** Transição RESERVADO -> DISPONIVEL (reserva cancelada). */
    public void liberar() {
        if (status != StatusLote.RESERVADO) {
            throw new ConflitoException("Somente lotes reservados podem ser liberados.");
        }
        status = StatusLote.DISPONIVEL;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Doador getDoador() {
        return doador;
    }

    public void setDoador(Doador doador) {
        this.doador = doador;
    }

    public String getTipoAlimento() {
        return tipoAlimento;
    }

    public void setTipoAlimento(String tipoAlimento) {
        this.tipoAlimento = tipoAlimento;
    }

    public Double getQuantidadeKg() {
        return quantidadeKg;
    }

    public void setQuantidadeKg(Double quantidadeKg) {
        this.quantidadeKg = quantidadeKg;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public JanelaRetirada getJanelaRetirada() {
        return janelaRetirada;
    }

    /** Substitui a janela de retirada inteira (a parte é recriada pelo próprio lote). */
    public void definirJanelaRetirada(LocalDateTime inicio, LocalDateTime fim) {
        this.janelaRetirada = new JanelaRetirada(inicio, fim);
    }

    public LocalDateTime getRetiradaInicio() {
        return janelaRetirada == null ? null : janelaRetirada.getInicio();
    }

    public LocalDateTime getRetiradaFim() {
        return janelaRetirada == null ? null : janelaRetirada.getFim();
    }

    public StatusLote getStatus() {
        return status;
    }

    public void setStatus(StatusLote status) {
        this.status = status;
    }

    public LocalDateTime getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(LocalDateTime dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    @Override
    public String toString() {
        return "LoteDoacao{id=" + id + ", tipoAlimento='" + tipoAlimento + "', quantidadeKg=" + quantidadeKg
                + ", dataValidade=" + dataValidade + ", status=" + status + "}";
    }
}
