package br.com.pratosolidario.dto;

import br.com.pratosolidario.model.Doador;

/** Dados de saída de um doador (nunca expõe o hash da senha). */
public record DoadorResponse(
        Integer id,
        String nome,
        String email,
        String documento,
        String tipoEstabelecimento,
        String regiao) {

    public static DoadorResponse de(Doador doador) {
        return new DoadorResponse(doador.getId(), doador.getNome(), doador.getEmail(),
                doador.getDocumento(), doador.getTipoEstabelecimento(), doador.getRegiao());
    }
}
