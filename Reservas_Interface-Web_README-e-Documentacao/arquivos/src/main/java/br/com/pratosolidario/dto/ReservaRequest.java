package br.com.pratosolidario.dto;

/** Pedido de reserva de um lote por uma instituição (RF02). */
public record ReservaRequest(Integer loteId, Integer instituicaoId) {
}
