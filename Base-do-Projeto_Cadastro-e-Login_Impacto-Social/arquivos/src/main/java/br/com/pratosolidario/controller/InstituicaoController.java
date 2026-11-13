package br.com.pratosolidario.controller;

import br.com.pratosolidario.dto.InstituicaoRequest;
import br.com.pratosolidario.dto.InstituicaoResponse;
import br.com.pratosolidario.dto.NotificacaoResponse;
import br.com.pratosolidario.model.Instituicao;
import br.com.pratosolidario.service.InstituicaoService;
import br.com.pratosolidario.service.NotificacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** CRUD de instituições — /api/instituicoes */
@RestController
@RequestMapping("/api/instituicoes")
public class InstituicaoController {

    private final InstituicaoService instituicaoService;
    private final NotificacaoService notificacaoService;

    public InstituicaoController(InstituicaoService instituicaoService, NotificacaoService notificacaoService) {
        this.instituicaoService = instituicaoService;
        this.notificacaoService = notificacaoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstituicaoResponse cadastrar(@RequestBody InstituicaoRequest dados) {
        return InstituicaoResponse.de(instituicaoService.cadastrar(paraDominio(dados), dados.senha()));
    }

    @GetMapping
    public List<InstituicaoResponse> listar() {
        return instituicaoService.listar().stream().map(InstituicaoResponse::de).toList();
    }

    @GetMapping("/{id}")
    public InstituicaoResponse buscar(@PathVariable("id") Integer id) {
        return InstituicaoResponse.de(instituicaoService.buscar(id));
    }

    @PutMapping("/{id}")
    public InstituicaoResponse atualizar(@PathVariable("id") Integer id, @RequestBody InstituicaoRequest dados) {
        return InstituicaoResponse.de(instituicaoService.atualizar(id, paraDominio(dados), dados.senha()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable("id") Integer id) {
        instituicaoService.excluir(id);
    }

    /** RF06 — notificações de lotes críticos recebidas pela instituição. */
    @GetMapping("/{id}/notificacoes")
    public List<NotificacaoResponse> notificacoes(@PathVariable("id") Integer id) {
        return notificacaoService.listarPorInstituicao(id).stream().map(NotificacaoResponse::de).toList();
    }

    private static Instituicao paraDominio(InstituicaoRequest dados) {
        return new Instituicao(dados.nome(), dados.email(), dados.areaAtuacao(), dados.capacidadeAtendimento());
    }
}
