package br.com.pratosolidario.dto;

import br.com.pratosolidario.model.TipoUsuario;
import br.com.pratosolidario.model.Usuario;

/** Resposta do login: identifica o usuário e o seu perfil (Doador ou Instituição). */
public record UsuarioLogadoResponse(Integer id, String nome, String email, TipoUsuario tipoUsuario) {

    public static UsuarioLogadoResponse de(Usuario usuario) {
        return new UsuarioLogadoResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(),
                usuario.getTipoUsuario());
    }
}
