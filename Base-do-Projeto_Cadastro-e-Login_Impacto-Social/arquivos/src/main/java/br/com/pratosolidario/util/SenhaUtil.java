package br.com.pratosolidario.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Geração e conferência de hash de senha (SHA-256 com salt aleatório).
 * Formato armazenado na coluna usuarios.senha_hash: {@code salt$hash}.
 * A senha em texto puro nunca é gravada no banco.
 */
public final class SenhaUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final HexFormat HEX = HexFormat.of();
    private static final String SEPARADOR = "$";

    private SenhaUtil() {
        // classe utilitária: não deve ser instanciada
    }

    public static String gerarHash(String senha) {
        byte[] bytesSalt = new byte[16];
        RANDOM.nextBytes(bytesSalt);
        String salt = HEX.formatHex(bytesSalt);
        return salt + SEPARADOR + sha256(salt + senha);
    }

    public static boolean confere(String senhaInformada, String hashArmazenado) {
        if (senhaInformada == null || hashArmazenado == null) {
            return false;
        }
        int posicao = hashArmazenado.indexOf(SEPARADOR);
        if (posicao <= 0) {
            return false;
        }
        String salt = hashArmazenado.substring(0, posicao);
        String hashEsperado = hashArmazenado.substring(posicao + 1);
        String hashCalculado = sha256(salt + senhaInformada);
        return MessageDigest.isEqual(
                hashCalculado.getBytes(StandardCharsets.UTF_8),
                hashEsperado.getBytes(StandardCharsets.UTF_8));
    }

    private static String sha256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HEX.formatHex(digest.digest(texto.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponível na JVM", e);
        }
    }
}
