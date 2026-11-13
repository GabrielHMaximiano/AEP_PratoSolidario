package br.com.pratosolidario.dto;

/** Dados de entrada para cadastrar/alterar uma instituição. Na alteração, senha vazia mantém a atual. */
public record InstituicaoRequest(
        String nome,
        String email,
        String senha,
        String areaAtuacao,
        Integer capacidadeAtendimento) {
}
