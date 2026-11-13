package br.com.pratosolidario.controller;

import br.com.pratosolidario.dto.NotificacaoResponse;
import br.com.pratosolidario.service.NotificacaoService;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** RF06 — /api/notificacoes (a listagem fica em /api/instituicoes/{id}/notificacoes) */
@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @PatchMapping("/{id}/lida")
    public NotificacaoResponse marcarComoLida(@PathVariable("id") Integer id) {
        return NotificacaoResponse.de(notificacaoService.marcarComoLida(id));
    }
}
