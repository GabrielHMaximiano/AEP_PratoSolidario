package br.com.pratosolidario.dto;

import br.com.pratosolidario.model.Notificacao;

import java.time.LocalDateTime;

public record NotificacaoResponse(
        Integer id,
        Integer instituicaoId,
        Integer loteId,
        String mensagem,
        LocalDateTime dataCriacao,
        Boolean lida) {

    public static NotificacaoResponse de(Notificacao notificacao) {
        return new NotificacaoResponse(notificacao.getId(), notificacao.getInstituicao().getId(),
                notificacao.getLote().getId(), notificacao.getMensagem(),
                notificacao.getDataCriacao(), notificacao.getLida());
    }
}
