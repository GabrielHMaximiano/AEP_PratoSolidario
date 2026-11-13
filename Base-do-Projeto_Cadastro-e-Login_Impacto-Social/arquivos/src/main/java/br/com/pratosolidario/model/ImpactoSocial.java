package br.com.pratosolidario.model;

/**
 * Contrato de cálculo do impacto social (RF04).
 * Cada tipo concreto de usuário implementa o cálculo à sua maneira
 * (polimorfismo via @Override):
 * <ul>
 *   <li>{@link Doador}: total de quilos de alimento efetivamente doados (retirados);</li>
 *   <li>{@link Instituicao}: total de famílias atendidas nas retiradas concluídas.</li>
 * </ul>
 */
public interface ImpactoSocial {

    Double calcularImpacto();

    String getUnidadeImpacto();
}
