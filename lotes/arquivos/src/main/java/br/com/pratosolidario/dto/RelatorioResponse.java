package br.com.pratosolidario.dto;

import java.util.List;

/** Relatório consolidado de alimentos resgatados (RF05). */
public record RelatorioResponse(
        FiltroRelatorio filtro,
        int totalResgates,
        double totalKg,
        int totalFamilias,
        List<RelatorioLinha> resgates) {
}
