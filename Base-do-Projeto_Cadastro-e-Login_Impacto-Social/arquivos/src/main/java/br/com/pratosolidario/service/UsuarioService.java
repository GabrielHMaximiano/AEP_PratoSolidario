package br.com.pratosolidario.service;

import br.com.pratosolidario.exception.ConflitoException;
import br.com.pratosolidario.exception.RecursoNaoEncontradoException;
import br.com.pratosolidario.model.Usuario;
import br.com.pratosolidario.repository.UsuarioJdbcRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço ABSTRATO com o fluxo de CRUD comum a qualquer usuário (RF07 / Sprint 1 —
 * Cadastro). As subclasses {@link DoadorService} e {@link InstituicaoService}
 * sobrescrevem apenas o que é específico de cada perfil.
 *
 * @param <T> Doador ou Instituicao
 */
public abstract class UsuarioService<T extends Usuario> {

    protected final UsuarioJdbcRepository<T> repositorio;

    protected UsuarioService(UsuarioJdbcRepository<T> repositorio) {
        this.repositorio = repositorio;
    }

    /** Nome usado nas mensagens de erro ("Doador", "Instituição"). */
    protected abstract String nomeRecurso();

    /** Copia os atributos próprios da subclasse na atualização. */
    protected abstract void copiarDadosEspecificos(T origem, T destino);

    /** Impede excluir usuários que já têm histórico (lotes ou reservas). */
    protected abstract void verificarPodeExcluir(T usuario);

    /** Regra comum: e-mail único. Subclasses podem acrescentar regras (super + extra). */
    protected void validarUnicidade(T usuario) {
        if (repositorio.emailEmUso(usuario.getEmail(), usuario.getId())) {
            throw new ConflitoException("Já existe um usuário cadastrado com o e-mail " + usuario.getEmail() + ".");
        }
    }

    @Transactional
    public T cadastrar(T novoUsuario, String senha) {
        novoUsuario.definirSenha(senha);
        novoUsuario.validar();          // chamada polimórfica: executa a validação da subclasse
        validarUnicidade(novoUsuario);
        return repositorio.salvar(novoUsuario);
    }

    public List<T> listar() {
        return repositorio.listarTodos();
    }

    public T buscar(Integer id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(nomeRecurso(), id));
    }

    @Transactional
    public T atualizar(Integer id, T novosDados, String novaSenha) {
        T existente = buscar(id);
        existente.setNome(novosDados.getNome());
        existente.setEmail(novosDados.getEmail());
        copiarDadosEspecificos(novosDados, existente);
        if (novaSenha != null && !novaSenha.isBlank()) {
            existente.definirSenha(novaSenha);
        }
        existente.validar();
        validarUnicidade(existente);
        return repositorio.atualizar(existente);
    }

    @Transactional
    public void excluir(Integer id) {
        T usuario = buscar(id);
        verificarPodeExcluir(usuario);
        repositorio.excluir(id);
    }
}
