package br.com.pratosolidario.dto;

import java.time.LocalDate;

/** Filtros opcionais do relatório de resgates (RF05): período, doador e instituição. */
public record FiltroRelatorio(
        LocalDate inicio,
        LocalDate fim,
        Integer doadorId,
        Integer instituicaoId) {
}
