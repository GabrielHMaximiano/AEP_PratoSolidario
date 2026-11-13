package br.com.pratosolidario.dto;

import java.time.LocalDateTime;

/** Corpo padrão das respostas de erro da API. */
public record ErroResponse(int status, String erro, String mensagem, LocalDateTime dataHora) {

    public ErroResponse(int status, String erro, String mensagem) {
        this(status, erro, mensagem, LocalDateTime.now());
    }
}
