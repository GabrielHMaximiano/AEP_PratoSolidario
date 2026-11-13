package br.com.pratosolidario.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Aviso enviado a uma instituição quando um lote CRÍTICO é publicado na
 * região em que ela atua (RF06).
 */
public class Notificacao {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Integer id;
    private Instituicao instituicao;
    private LoteDoacao lote;
    private String mensagem;
    private LocalDateTime dataCriacao;
    private Boolean lida = Boolean.FALSE;

    public Notificacao() {
    }

    public Notificacao(Instituicao instituicao, LoteDoacao lote, String mensagem) {
        this.instituicao = instituicao;
        this.lote = lote;
        this.mensagem = mensagem;
        this.dataCriacao = LocalDateTime.now();
        this.lida = Boolean.FALSE;
    }

    /** Fábrica: monta a mensagem padrão de alerta de lote urgente. */
    public static Notificacao paraLoteUrgente(Instituicao instituicao, LoteDoacao lote) {
        String mensagem = String.format("Lote CRÍTICO na região %s: %s (%.1f kg) de %s. Validade: %s.",
                lote.getDoador().getRegiao(),
                lote.getTipoAlimento(),
                lote.getQuantidadeKg(),
                lote.getDoador().getNome(),
                lote.getDataValidade().format(FORMATO_DATA));
        return new Notificacao(instituicao, lote, mensagem);
    }

    public void marcarComoLida() {
        this.lida = Boolean.TRUE;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Instituicao getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(Instituicao instituicao) {
        this.instituicao = instituicao;
    }

    public LoteDoacao getLote() {
        return lote;
    }

    public void setLote(LoteDoacao lote) {
        this.lote = lote;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Boolean getLida() {
        return lida;
    }

    public void setLida(Boolean lida) {
        this.lida = lida;
    }
}
