package br.com.pratosolidario.model;

import br.com.pratosolidario.exception.RegraNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** validar() é sobrescrito: regras comuns (super) + regras de cada subclasse. */
class UsuarioValidacaoTest {

    @Test
    @DisplayName("Doador com documento inválido é rejeitado (regra da subclasse)")
    void doadorDocumentoInvalido() {
        Doador doador = new Doador("Padaria", "padaria@email.com", "123", "Padaria", "Centro");
        doador.definirSenha("123456");
        assertThrows(RegraNegocioException.class, doador::validar);
    }

    @Test
    @DisplayName("E-mail inválido é rejeitado (regra herdada de Usuario)")
    void emailInvalido() {
        Instituicao ong = new Instituicao("ONG", "email-invalido", "Centro", 10);
        ong.definirSenha("123456");
        assertThrows(RegraNegocioException.class, ong::validar);
    }

    @Test
    @DisplayName("Instituição precisa de capacidade de atendimento positiva")
    void instituicaoCapacidadeInvalida() {
        Instituicao ong = new Instituicao("ONG", "ong@email.com", "Centro", 0);
        ong.definirSenha("123456");
        assertThrows(RegraNegocioException.class, ong::validar);
    }

    @Test
    @DisplayName("Senha é guardada como hash e conferida no login")
    void autenticacao() {
        Instituicao ong = new Instituicao("ONG", "ong@email.com", "Centro", 10);
        ong.definirSenha("segredo123");
        assertFalse(ong.getSenhaHash().contains("segredo123"));
        assertTrue(ong.autenticar("segredo123"));
        assertFalse(ong.autenticar("outra"));
        assertDoesNotThrow(ong::validar);
    }

    @Test
    @DisplayName("Senha curta é rejeitada")
    void senhaCurta() {
        Doador doador = new Doador("Padaria", "padaria@email.com", "12345678000190", "Padaria", "Centro");
        assertThrows(RegraNegocioException.class, () -> doador.definirSenha("123"));
    }

    @Test
    @DisplayName("Hash gravado no script database/02_seed.sql confere com a senha 123456")
    void hashDoSeedConfere() {
        String hashDoSeed = "a1b2c3d4e5f60718293a4b5c6d7e8f90$457a2ff654a76ac5a19ce5a4e04e5c54ad717e94712db4dc28d54c03d13c452b";
        assertTrue(br.com.pratosolidario.util.SenhaUtil.confere("123456", hashDoSeed));
        assertFalse(br.com.pratosolidario.util.SenhaUtil.confere("654321", hashDoSeed));
    }
}
