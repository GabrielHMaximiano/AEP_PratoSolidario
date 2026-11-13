package br.com.pratosolidario.controller;

import br.com.pratosolidario.dto.LoginRequest;
import br.com.pratosolidario.dto.UsuarioLogadoResponse;
import br.com.pratosolidario.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** POST /api/auth/login — {"email": "...", "senha": "..."} */
    @PostMapping("/login")
    public UsuarioLogadoResponse login(@RequestBody LoginRequest dados) {
        return UsuarioLogadoResponse.de(authService.autenticar(dados.email(), dados.senha()));
    }
}
