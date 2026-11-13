package br.com.pratosolidario.dto;

import br.com.pratosolidario.model.Reserva;
import br.com.pratosolidario.model.StatusReserva;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservaResponse(
        Integer id,
        Integer loteId,
        String tipoAlimento,
        Double quantidadeKg,
        LocalDate dataValidade,
        String doadorNome,
        Integer instituicaoId,
        String instituicaoNome,
        LocalDateTime dataReserva,
        LocalDateTime dataRetirada,
        Integer familiasAtendidas,
        StatusReserva status) {

    public static ReservaResponse de(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getLote().getId(),
                reserva.getLote().getTipoAlimento(),
                reserva.getLote().getQuantidadeKg(),
                reserva.getLote().getDataValidade(),
                reserva.getLote().getDoador() == null ? null : reserva.getLote().getDoador().getNome(),
                reserva.getInstituicao().getId(),
                reserva.getInstituicao().getNome(),
                reserva.getDataReserva(),
                reserva.getDataRetirada(),
                reserva.getFamiliasAtendidas(),
                reserva.getStatus());
    }
}
