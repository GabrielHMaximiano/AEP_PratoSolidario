package br.com.pratosolidario.dto;

import java.time.LocalDateTime;

/** Uma retirada concluída no relatório de resgates (RF05). */
public record RelatorioLinha(
        Integer reservaId,
        LocalDateTime dataRetirada,
        Integer doadorId,
        String doadorNome,
        Integer instituicaoId,
        String instituicaoNome,
        String tipoAlimento,
        Double quantidadeKg,
        Integer familiasAtendidas) {
}
