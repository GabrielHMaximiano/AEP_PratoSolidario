package br.com.pratosolidario.model;

import br.com.pratosolidario.exception.ConflitoException;
import br.com.pratosolidario.exception.RegraNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoteDoacaoTest {

    private static final LocalDate HOJE = LocalDate.of(2026, 11, 10);

    private LoteDoacao loteComValidade(LocalDate validade) {
        Doador doador = new Doador("Padaria", "p@p.com", "12345678000190", "Padaria", "Zona Norte");
        LocalDateTime inicio = LocalDateTime.now().plusHours(1);
        return new LoteDoacao(doador, "Pães", 10.0, validade, inicio, inicio.plusHours(2));
    }

    @Test
    @DisplayName("RF03: urgência calculada pelos dias até o vencimento")
    void calculaNiveisDeUrgencia() {
        assertEquals(NivelUrgencia.VENCIDO, loteComValidade(HOJE.minusDays(1)).calcularUrgencia(HOJE));
        assertEquals(NivelUrgencia.CRITICO, loteComValidade(HOJE).calcularUrgencia(HOJE));
        assertEquals(NivelUrgencia.CRITICO, loteComValidade(HOJE.plusDays(1)).calcularUrgencia(HOJE));
        assertEquals(NivelUrgencia.ATENCAO, loteComValidade(HOJE.plusDays(2)).calcularUrgencia(HOJE));
        assertEquals(NivelUrgencia.ATENCAO, loteComValidade(HOJE.plusDays(3)).calcularUrgencia(HOJE));
        assertEquals(NivelUrgencia.NORMAL, loteComValidade(HOJE.plusDays(4)).calcularUrgencia(HOJE));
        assertTrue(NivelUrgencia.CRITICO.isUrgente());
    }

    @Test
    @DisplayName("RF02: um lote não pode ser reservado duas vezes")
    void naoPermiteReservaDupla() {
        LoteDoacao lote = loteComValidade(LocalDate.now().plusDays(2));
        lote.reservar();
        assertEquals(StatusLote.RESERVADO, lote.getStatus());
        assertThrows(ConflitoException.class, lote::reservar);
    }

    @Test
    @DisplayName("RF01: janela de retirada precisa terminar depois de começar")
    void validaJanelaDeRetirada() {
        LoteDoacao lote = loteComValidade(LocalDate.now().plusDays(2));
        lote.definirJanelaRetirada(lote.getRetiradaInicio(), lote.getRetiradaInicio().minusMinutes(30));
        assertThrows(RegraNegocioException.class, lote::validar);
    }

    @Test
    @DisplayName("RF01: quantidade precisa ser positiva")
    void validaQuantidade() {
        LoteDoacao lote = loteComValidade(LocalDate.now().plusDays(2));
        lote.setQuantidadeKg(0.0);
        assertThrows(RegraNegocioException.class, lote::validar);
    }

    @Test
    @DisplayName("Composição: o lote cria e substitui a sua própria JanelaRetirada")
    void composicaoComJanelaDeRetirada() {
        LoteDoacao lote = loteComValidade(LocalDate.now().plusDays(2));
        JanelaRetirada original = lote.getJanelaRetirada();
        assertTrue(original.contem(original.getInicio().plusMinutes(30)));
        assertEquals(120, original.getDuracao().toMinutes());

        lote.definirJanelaRetirada(original.getInicio(), original.getInicio().plusHours(1));
        assertTrue(lote.getJanelaRetirada() != original);              // a parte foi recriada pelo todo
        assertEquals(60, lote.getJanelaRetirada().getDuracao().toMinutes());
    }
}
