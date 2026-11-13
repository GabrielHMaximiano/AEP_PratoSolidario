package br.com.pratosolidario.dto;

/** Dados de entrada para cadastrar/alterar um doador. Na alteração, senha vazia mantém a atual. */
public record DoadorRequest(
        String nome,
        String email,
        String senha,
        String documento,
        String tipoEstabelecimento,
        String regiao) {
}
