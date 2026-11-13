package br.com.pratosolidario.controller;

import br.com.pratosolidario.dto.LotePublicadoResponse;
import br.com.pratosolidario.dto.LoteRequest;
import br.com.pratosolidario.dto.LoteResponse;
import br.com.pratosolidario.service.LoteDoacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** CRUD da entidade principal: lotes de doação — /api/lotes */
@RestController
@RequestMapping("/api/lotes")
public class LoteDoacaoController {

    private final LoteDoacaoService loteService;

    public LoteDoacaoController(LoteDoacaoService loteService) {
        this.loteService = loteService;
    }

    /** CREATE (RF01) */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LotePublicadoResponse publicar(@RequestBody LoteRequest dados) {
        return loteService.publicar(dados);
    }

    /** READ — todos, ou de um doador: /api/lotes?doadorId=1 */
    @GetMapping
    public List<LoteResponse> listar(@RequestParam(name = "doadorId", required = false) Integer doadorId) {
        return loteService.listar(doadorId).stream().map(LoteResponse::de).toList();
    }

    /** READ — RF02: disponíveis ordenados pela validade mais próxima, com urgência (RF03). */
    @GetMapping("/disponiveis")
    public List<LoteResponse> listarDisponiveis() {
        return loteService.listarDisponiveis().stream().map(LoteResponse::de).toList();
    }

    /** READ — um lote */
    @GetMapping("/{id}")
    public LoteResponse buscar(@PathVariable("id") Integer id) {
        return LoteResponse.de(loteService.buscar(id));
    }

    /** UPDATE */
    @PutMapping("/{id}")
    public LoteResponse atualizar(@PathVariable("id") Integer id, @RequestBody LoteRequest dados) {
        return LoteResponse.de(loteService.atualizar(id, dados));
    }

    /** DELETE */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable("id") Integer id) {
        loteService.excluir(id);
    }
}
