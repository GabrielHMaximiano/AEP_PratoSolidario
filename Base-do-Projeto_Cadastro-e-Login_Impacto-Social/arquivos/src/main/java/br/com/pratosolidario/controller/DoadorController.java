package br.com.pratosolidario.controller;

import br.com.pratosolidario.dto.DoadorRequest;
import br.com.pratosolidario.dto.DoadorResponse;
import br.com.pratosolidario.model.Doador;
import br.com.pratosolidario.service.DoadorService;
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

/** CRUD de doadores — /api/doadores */
@RestController
@RequestMapping("/api/doadores")
public class DoadorController {

    private final DoadorService doadorService;

    public DoadorController(DoadorService doadorService) {
        this.doadorService = doadorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoadorResponse cadastrar(@RequestBody DoadorRequest dados) {
        return DoadorResponse.de(doadorService.cadastrar(paraDominio(dados), dados.senha()));
    }

    @GetMapping
    public List<DoadorResponse> listar() {
        return doadorService.listar().stream().map(DoadorResponse::de).toList();
    }

    @GetMapping("/{id}")
    public DoadorResponse buscar(@PathVariable("id") Integer id) {
        return DoadorResponse.de(doadorService.buscar(id));
    }

    @PutMapping("/{id}")
    public DoadorResponse atualizar(@PathVariable("id") Integer id, @RequestBody DoadorRequest dados) {
        return DoadorResponse.de(doadorService.atualizar(id, paraDominio(dados), dados.senha()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable("id") Integer id) {
        doadorService.excluir(id);
    }

    private static Doador paraDominio(DoadorRequest dados) {
        return new Doador(dados.nome(), dados.email(), dados.documento(),
                dados.tipoEstabelecimento(), dados.regiao());
    }
}
