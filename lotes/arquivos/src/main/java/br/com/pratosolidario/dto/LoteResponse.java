package br.com.pratosolidario.dto;

import br.com.pratosolidario.model.LoteDoacao;
import br.com.pratosolidario.model.NivelUrgencia;
import br.com.pratosolidario.model.StatusLote;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Dados de saída de um lote, já com o nível de urgência calculado (RF03). */
public record LoteResponse(
        Integer id,
        Integer doadorId,
        String doadorNome,
        String regiao,
        String tipoAlimento,
        Double quantidadeKg,
        LocalDate dataValidade,
        LocalDateTime retiradaInicio,
        LocalDateTime retiradaFim,
        StatusLote status,
        NivelUrgencia urgencia,
        String urgenciaDescricao,
        long diasParaVencer) {

    public static LoteResponse de(LoteDoacao lote) {
        LocalDate hoje = LocalDate.now();
        NivelUrgencia urgencia = lote.calcularUrgencia(hoje);
        return new LoteResponse(
                lote.getId(),
                lote.getDoador().getId(),
                lote.getDoador().getNome(),
                lote.getDoador().getRegiao(),
                lote.getTipoAlimento(),
                lote.getQuantidadeKg(),
                lote.getDataValidade(),
                lote.getRetiradaInicio(),
                lote.getRetiradaFim(),
                lote.getStatus(),
                urgencia,
                urgencia.getDescricao(),
                lote.diasParaVencer(hoje));
    }
}
