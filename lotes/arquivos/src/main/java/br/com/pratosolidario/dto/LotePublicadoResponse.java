package br.com.pratosolidario.dto;

/** Resposta da publicação de um lote: o lote criado e quantas instituições foram notificadas (RF06). */
public record LotePublicadoResponse(LoteResponse lote, int instituicoesNotificadas) {
}
