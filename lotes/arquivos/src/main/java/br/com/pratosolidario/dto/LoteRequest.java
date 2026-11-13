package br.com.pratosolidario.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Dados de entrada para publicar/alterar um lote (RF01).
 * Datas no formato ISO: dataValidade "2026-11-20"; retirada "2026-11-19T18:00".
 */
public record LoteRequest(
        Integer doadorId,
        String tipoAlimento,
        Double quantidadeKg,
        LocalDate dataValidade,
        LocalDateTime retiradaInicio,
        LocalDateTime retiradaFim) {
}
