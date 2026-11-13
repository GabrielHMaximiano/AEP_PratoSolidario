package br.com.pratosolidario.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Demonstra o POLIMORFISMO do RF04: a mesma chamada calcularImpacto(), feita
 * sobre referências do tipo abstrato Usuario, executa lógicas diferentes.
 */
class ImpactoSocialPolimorfismoTest {

    @Test
    @DisplayName("RF04: Doador soma kg retirados e Instituição soma famílias atendidas")
    void calculoPolimorficoDoImpacto() {
        Doador padaria = new Doador("Padaria da Dona Marta", "marta@padaria.com",
                "12.345.678/0001-90", "Padaria", "Zona Norte");
        Instituicao ong = new Instituicao("ONG Mesa Solidária", "ong@mesa.org", "Zona Norte", 80);

        LocalDateTime inicio = LocalDateTime.now().plusHours(1);
        LoteDoacao paes = padaria.publicarLote("Pães", 12.5, LocalDate.now().plusDays(1), inicio, inicio.plusHours(2));
        LoteDoacao bolos = padaria.publicarLote("Bolos", 6.0, LocalDate.now().plusDays(2), inicio, inicio.plusHours(2));
        padaria.publicarLote("Doces", 3.0, LocalDate.now().plusDays(3), inicio, inicio.plusHours(2)); // não retirado

        ong.reservarLote(paes).concluir(20);
        ong.reservarLote(bolos).concluir(15);

        List<Usuario> usuarios = List.of(padaria, ong);   // tipo abstrato

        assertEquals(18.5, usuarios.get(0).calcularImpacto().doubleValue(), 0.001);   // kg (versão do Doador)
        assertEquals(35.0, usuarios.get(1).calcularImpacto().doubleValue(), 0.001);   // famílias (versão da Instituição)
        assertEquals("kg de alimentos doados", usuarios.get(0).getUnidadeImpacto());
        assertEquals("famílias atendidas", usuarios.get(1).getUnidadeImpacto());
        assertEquals(TipoUsuario.DOADOR, usuarios.get(0).getTipoUsuario());
        assertEquals(TipoUsuario.INSTITUICAO, usuarios.get(1).getTipoUsuario());
        assertInstanceOf(ImpactoSocial.class, usuarios.get(0));
        assertEquals("12345678000190", padaria.getDocumento());          // só dígitos
    }
}
