package br.com.pratosolidario.model;

/** Ciclo de vida de um lote de doação (coluna lotes_doacao.status). */
public enum StatusLote {
    /** Publicado e aguardando uma instituição reservar. */
    DISPONIVEL,
    /** Reservado por uma instituição, aguardando a retirada. */
    RESERVADO,
    /** Retirado pela instituição: conta como alimento resgatado. */
    RETIRADO
}
