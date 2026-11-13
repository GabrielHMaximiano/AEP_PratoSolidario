package br.com.pratosolidario.dto;

import br.com.pratosolidario.model.Instituicao;

/** Dados de saída de uma instituição (nunca expõe o hash da senha). */
public record InstituicaoResponse(
        Integer id,
        String nome,
        String email,
        String areaAtuacao,
        Integer capacidadeAtendimento) {

    public static InstituicaoResponse de(Instituicao instituicao) {
        return new InstituicaoResponse(instituicao.getId(), instituicao.getNome(), instituicao.getEmail(),
                instituicao.getAreaAtuacao(), instituicao.getCapacidadeAtendimento());
    }
}
