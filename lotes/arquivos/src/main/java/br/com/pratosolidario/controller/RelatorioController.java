package br.com.pratosolidario.controller;

import br.com.pratosolidario.dto.FiltroRelatorio;
import br.com.pratosolidario.dto.RelatorioResponse;
import br.com.pratosolidario.service.RelatorioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * RF05 — relatórios de resgate, filtráveis por período, doador e instituição.
 * Exemplos:
 *   /api/relatorios/resgates?inicio=2026-11-01&fim=2026-11-30
 *   /api/relatorios/resgates/csv?doadorId=1
 */
@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/resgates")
    public RelatorioResponse resgates(
            @RequestParam(name = "inicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(name = "fim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(name = "doadorId", required = false) Integer doadorId,
            @RequestParam(name = "instituicaoId", required = false) Integer instituicaoId) {
        return relatorioService.gerar(new FiltroRelatorio(inicio, fim, doadorId, instituicaoId));
    }

    /** Exportação dos dados (RF05) em CSV. */
    @GetMapping("/resgates/csv")
    public ResponseEntity<byte[]> exportarCsv(
            @RequestParam(name = "inicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(name = "fim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(name = "doadorId", required = false) Integer doadorId,
            @RequestParam(name = "instituicaoId", required = false) Integer instituicaoId) {
        String csv = relatorioService.exportarCsv(new FiltroRelatorio(inicio, fim, doadorId, instituicaoId));
        // BOM (﻿) faz o Excel reconhecer os acentos em UTF-8
        byte[] conteudo = ("﻿" + csv).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"relatorio-resgates.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(conteudo);
    }
}
