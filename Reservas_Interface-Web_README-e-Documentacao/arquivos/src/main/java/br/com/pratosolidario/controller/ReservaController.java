package br.com.pratosolidario.controller;

import br.com.pratosolidario.dto.ConclusaoReservaRequest;
import br.com.pratosolidario.dto.ReservaRequest;
import br.com.pratosolidario.dto.ReservaResponse;
import br.com.pratosolidario.service.ReservaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Reservas de lotes — /api/reservas */
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    /** RF02 — {"loteId": 1, "instituicaoId": 3} */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaResponse reservar(@RequestBody ReservaRequest dados) {
        return ReservaResponse.de(reservaService.reservar(dados));
    }

    /** Todas, ou de uma instituição: /api/reservas?instituicaoId=3 */
    @GetMapping
    public List<ReservaResponse> listar(@RequestParam(name = "instituicaoId", required = false) Integer instituicaoId) {
        return reservaService.listar(instituicaoId).stream().map(ReservaResponse::de).toList();
    }

    @GetMapping("/{id}")
    public ReservaResponse buscar(@PathVariable("id") Integer id) {
        return ReservaResponse.de(reservaService.buscar(id));
    }

    /** Retirada concluída — {"familiasAtendidas": 15} */
    @PatchMapping("/{id}/concluir")
    public ReservaResponse concluir(@PathVariable("id") Integer id, @RequestBody ConclusaoReservaRequest dados) {
        return ReservaResponse.de(reservaService.concluir(id, dados.familiasAtendidas()));
    }

    @PatchMapping("/{id}/cancelar")
    public ReservaResponse cancelar(@PathVariable("id") Integer id) {
        return ReservaResponse.de(reservaService.cancelar(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable("id") Integer id) {
        reservaService.excluir(id);
    }
}
