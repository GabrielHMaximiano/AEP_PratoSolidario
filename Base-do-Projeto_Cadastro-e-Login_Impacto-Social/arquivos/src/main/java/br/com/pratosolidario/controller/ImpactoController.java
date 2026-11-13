package br.com.pratosolidario.controller;

import br.com.pratosolidario.dto.ImpactoResponse;
import br.com.pratosolidario.service.ImpactoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** RF04 — impacto social calculado de forma polimórfica — /api/impacto */
@RestController
@RequestMapping("/api/impacto")
public class ImpactoController {

    private final ImpactoService impactoService;

    public ImpactoController(ImpactoService impactoService) {
        this.impactoService = impactoService;
    }

    @GetMapping
    public List<ImpactoResponse> todos() {
        return impactoService.calcularTodos();
    }

    @GetMapping("/{usuarioId}")
    public ImpactoResponse doUsuario(@PathVariable("usuarioId") Integer usuarioId) {
        return impactoService.calcular(usuarioId);
    }
}
