package br.com.pratosolidario.dto;

import br.com.pratosolidario.model.TipoUsuario;
import br.com.pratosolidario.model.Usuario;

/** Resultado do cálculo polimórfico de impacto social (RF04). */
public record ImpactoResponse(
        Integer usuarioId,
        String nome,
        TipoUsuario tipoUsuario,
        Double impacto,
        String unidade) {

    /** A mesma chamada calcularImpacto() executa lógicas diferentes conforme o tipo concreto. */
    public static ImpactoResponse de(Usuario usuario) {
        return new ImpactoResponse(usuario.getId(), usuario.getNome(), usuario.getTipoUsuario(),
                usuario.calcularImpacto(), usuario.getUnidadeImpacto());
    }
}
