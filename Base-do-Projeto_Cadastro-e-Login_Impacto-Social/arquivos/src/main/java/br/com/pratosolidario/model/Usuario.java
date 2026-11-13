package br.com.pratosolidario.model;

import br.com.pratosolidario.exception.RegraNegocioException;
import br.com.pratosolidario.util.SenhaUtil;

/**
 * Superclasse ABSTRATA de todos os usuários do sistema.
 * <p>
 * Concentra os atributos e comportamentos comuns (identificação, credenciais e
 * autenticação). Não pode ser instanciada diretamente: todo usuário é, de fato,
 * um {@link Doador} ou uma {@link Instituicao} (HERANÇA).
 * <p>
 * Implementa {@link ImpactoSocial} sem definir o cálculo: cada subclasse é
 * obrigada a sobrescrever {@code calcularImpacto()} (POLIMORFISMO).
 * Também deixa abstratos {@link #getTipoUsuario()} e estende a validação em
 * {@link #validar()}, que as subclasses complementam com {@code super.validar()}.
 */
public abstract class Usuario implements ImpactoSocial {

    private static final int TAMANHO_MINIMO_SENHA = 6;

    private Integer id;
    private String nome;
    private String email;
    private String senhaHash;

    protected Usuario() {
    }

    protected Usuario(String nome, String email) {
        this.nome = nome;
        this.email = normalizarEmail(email);
    }

    private static String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    /** Perfil concreto do usuário; cada subclasse informa o seu. */
    public abstract TipoUsuario getTipoUsuario();

    /** Confere a senha informada no login com o hash armazenado. */
    public Boolean autenticar(String senhaInformada) {
        return SenhaUtil.confere(senhaInformada, senhaHash);
    }

    /** Define uma nova senha, guardando apenas o hash. */
    public void definirSenha(String senhaPura) {
        if (senhaPura == null || senhaPura.length() < TAMANHO_MINIMO_SENHA) {
            throw new RegraNegocioException(
                    "A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
        this.senhaHash = SenhaUtil.gerarHash(senhaPura);
    }

    /**
     * Regras de validação comuns a qualquer usuário. As subclasses sobrescrevem
     * este método, chamam {@code super.validar()} e acrescentam as suas regras.
     */
    public void validar() {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("O nome é obrigatório.");
        }
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new RegraNegocioException("Informe um e-mail válido.");
        }
        if (senhaHash == null || senhaHash.isBlank()) {
            throw new RegraNegocioException("A senha é obrigatória.");
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = normalizarEmail(email);
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    @Override
    public String toString() {
        return getTipoUsuario() + "{id=" + id + ", nome='" + nome + "'}";
    }
}
