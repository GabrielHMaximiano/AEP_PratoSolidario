package br.com.pratosolidario.model;

import br.com.pratosolidario.exception.RegraNegocioException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Estabelecimento que publica excedentes de alimentos (ex.: padaria da persona
 * Dona Marta). Especialização de {@link Usuario} (HERANÇA).
 * <p>
 * Possui uma coleção de {@link LoteDoacao} (COMPOSIÇÃO 1 — 0..*): um lote só
 * existe porque um doador o publicou.
 */
public class Doador extends Usuario {

    private String documento;
    private String tipoEstabelecimento;
    private String regiao;
    private final List<LoteDoacao> lotes = new ArrayList<>();

    public Doador() {
        super();
    }

    public Doador(String nome, String email, String documento,
                  String tipoEstabelecimento, String regiao) {
        super(nome, email);
        this.documento = somenteDigitos(documento);
        this.tipoEstabelecimento = tipoEstabelecimento;
        this.regiao = regiao;
    }

    @Override
    public TipoUsuario getTipoUsuario() {
        return TipoUsuario.DOADOR;
    }

    /** Regras comuns (super) + regras específicas do doador. */
    @Override
    public void validar() {
        super.validar();
        if (documento == null || !(documento.length() == 11 || documento.length() == 14)) {
            throw new RegraNegocioException("O documento deve ser um CPF (11 dígitos) ou CNPJ (14 dígitos).");
        }
        if (tipoEstabelecimento == null || tipoEstabelecimento.isBlank()) {
            throw new RegraNegocioException("Informe o tipo de estabelecimento.");
        }
        if (regiao == null || regiao.isBlank()) {
            throw new RegraNegocioException("Informe a região do estabelecimento.");
        }
    }

    /**
     * RF01 — cria um novo lote vinculado a este doador, já validado e com
     * status DISPONIVEL.
     */
    public LoteDoacao publicarLote(String tipoAlimento, Double quantidadeKg, LocalDate dataValidade,
                                   LocalDateTime retiradaInicio, LocalDateTime retiradaFim) {
        LoteDoacao lote = new LoteDoacao(this, tipoAlimento, quantidadeKg,
                dataValidade, retiradaInicio, retiradaFim);
        lote.validar();
        lotes.add(lote);
        return lote;
    }

    /**
     * RF04 — impacto do DOADOR: soma dos quilos dos lotes que foram
     * efetivamente retirados por alguma instituição.
     */
    @Override
    public Double calcularImpacto() {
        return lotes.stream()
                .filter(lote -> lote.getStatus() == StatusLote.RETIRADO)
                .mapToDouble(LoteDoacao::getQuantidadeKg)
                .sum();
    }

    @Override
    public String getUnidadeImpacto() {
        return "kg de alimentos doados";
    }

    public void adicionarLote(LoteDoacao lote) {
        lotes.add(lote);
    }

    public List<LoteDoacao> getLotes() {
        return Collections.unmodifiableList(lotes);
    }

    public String getDocumento() {
        return documento;
    }

    /** Guarda só os dígitos (aceita "12.345.678/0001-90" ou "12345678000190"). */
    public void setDocumento(String documento) {
        this.documento = somenteDigitos(documento);
    }

    private static String somenteDigitos(String texto) {
        return texto == null ? null : texto.replaceAll("\\D", "");
    }

    public String getTipoEstabelecimento() {
        return tipoEstabelecimento;
    }

    public void setTipoEstabelecimento(String tipoEstabelecimento) {
        this.tipoEstabelecimento = tipoEstabelecimento;
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }
}
