package br.com.pratosolidario.model;

import br.com.pratosolidario.exception.RegraNegocioException;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Período em que um lote pode ser retirado no estabelecimento (RF01).
 * <p>
 * É uma parte de {@link LoteDoacao} (COMPOSIÇÃO): a janela é criada pelo
 * próprio lote, não existe fora dele e é substituída por inteiro quando o
 * lote é alterado. Por isso é imutável (atributos {@code final}, sem setters).
 * No banco, seus atributos ficam nas colunas retirada_inicio e retirada_fim
 * da tabela lotes_doacao.
 */
public final class JanelaRetirada {

    private final LocalDateTime inicio;
    private final LocalDateTime fim;

    public JanelaRetirada(LocalDateTime inicio, LocalDateTime fim) {
        this.inicio = inicio;
        this.fim = fim;
    }

    /** A janela precisa ter início e fim, e o fim deve ser depois do início. */
    public void validar() {
        if (inicio == null || fim == null) {
            throw new RegraNegocioException("Informe o início e o fim da janela de retirada.");
        }
        if (!fim.isAfter(inicio)) {
            throw new RegraNegocioException("O fim da janela de retirada deve ser depois do início.");
        }
    }

    /** O horário informado está dentro da janela? */
    public boolean contem(LocalDateTime momento) {
        return momento != null && !momento.isBefore(inicio) && !momento.isAfter(fim);
    }

    /** A janela já terminou em relação ao momento informado? */
    public boolean encerradaEm(LocalDateTime momento) {
        return momento != null && fim != null && momento.isAfter(fim);
    }

    public Duration getDuracao() {
        return Duration.between(inicio, fim);
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }

    @Override
    public String toString() {
        return "JanelaRetirada{" + inicio + " até " + fim + "}";
    }
}
