package br.com.pratosolidario.service;

import br.com.pratosolidario.exception.AutenticacaoException;
import br.com.pratosolidario.model.Usuario;
import br.com.pratosolidario.repository.DoadorRepository;
import br.com.pratosolidario.repository.InstituicaoRepository;
import br.com.pratosolidario.repository.UsuarioJdbcRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/** RF07 / Sprint 1 — login: identifica se o e-mail pertence a um Doador ou a uma Instituição. */
@Service
public class AuthService {

    private final List<UsuarioJdbcRepository<? extends Usuario>> repositorios;

    public AuthService(DoadorRepository doadorRepository, InstituicaoRepository instituicaoRepository) {
        // Os dois repositórios são tratados pelo mesmo tipo abstrato (polimorfismo)
        this.repositorios = List.of(doadorRepository, instituicaoRepository);
    }

    public Usuario autenticar(String email, String senha) {
        for (UsuarioJdbcRepository<? extends Usuario> repositorio : repositorios) {
            Optional<? extends Usuario> encontrado = repositorio.buscarPorEmail(email);
            if (encontrado.isPresent()) {
                Usuario usuario = encontrado.get();
                if (Boolean.TRUE.equals(usuario.autenticar(senha))) {
                    return usuario;
                }
                throw new AutenticacaoException();
            }
        }
        throw new AutenticacaoException();
    }
}
