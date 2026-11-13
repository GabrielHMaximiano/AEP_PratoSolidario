package br.com.pratosolidario.service;

import br.com.pratosolidario.dto.LotePublicadoResponse;
import br.com.pratosolidario.dto.LoteRequest;
import br.com.pratosolidario.dto.LoteResponse;
import br.com.pratosolidario.exception.ConflitoException;
import br.com.pratosolidario.exception.RecursoNaoEncontradoException;
import br.com.pratosolidario.exception.RegraNegocioException;
import br.com.pratosolidario.model.Doador;
import br.com.pratosolidario.model.LoteDoacao;
import br.com.pratosolidario.repository.DoadorRepository;
import br.com.pratosolidario.repository.LoteDoacaoRepository;
import br.com.pratosolidario.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/** CRUD da entidade principal LoteDoacao (RF01, RF02, RF03) + disparo do RF06. */
@Service
public class LoteDoacaoService {

    private final LoteDoacaoRepository loteRepository;
    private final DoadorRepository doadorRepository;
    private final ReservaRepository reservaRepository;
    private final NotificacaoService notificacaoService;

    public LoteDoacaoService(LoteDoacaoRepository loteRepository, DoadorRepository doadorRepository,
                             ReservaRepository reservaRepository, NotificacaoService notificacaoService) {
        this.loteRepository = loteRepository;
        this.doadorRepository = doadorRepository;
        this.reservaRepository = reservaRepository;
        this.notificacaoService = notificacaoService;
    }

    /** CREATE — RF01; se o lote nascer CRÍTICO, notifica as instituições da região (RF06). */
    @Transactional
    public LotePublicadoResponse publicar(LoteRequest dados) {
        if (dados.doadorId() == null) {
            throw new RegraNegocioException("Informe o doador responsável pelo lote (doadorId).");
        }
        Doador doador = doadorRepository.buscarPorId(dados.doadorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Doador", dados.doadorId()));

        LoteDoacao lote = doador.publicarLote(dados.tipoAlimento(), dados.quantidadeKg(), dados.dataValidade(),
                dados.retiradaInicio(), dados.retiradaFim());
        loteRepository.salvar(lote);

        int notificadas = 0;
        if (lote.calcularUrgencia().isUrgente()) {
            notificadas = notificacaoService.notificarInstituicoesDaRegiao(lote);
        }
        return new LotePublicadoResponse(LoteResponse.de(lote), notificadas);
    }

    /** READ — todos os lotes ou apenas os de um doador. */
    public List<LoteDoacao> listar(Integer doadorId) {
        return doadorId == null ? loteRepository.listarTodos() : loteRepository.listarPorDoador(doadorId);
    }

    /** READ — RF02: disponíveis, ordenados pela validade mais próxima. */
    public List<LoteDoacao> listarDisponiveis() {
        return loteRepository.listarDisponiveis(LocalDate.now());
    }

    /** READ — um lote. */
    public LoteDoacao buscar(Integer id) {
        return loteRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lote", id));
    }

    /** UPDATE — somente enquanto o lote estiver DISPONIVEL. */
    @Transactional
    public LoteDoacao atualizar(Integer id, LoteRequest dados) {
        LoteDoacao lote = buscar(id);
        if (!lote.isEditavel()) {
            throw new ConflitoException("O lote " + id + " já foi " + lote.getStatus().name().toLowerCase()
                    + " e não pode mais ser alterado.");
        }
        lote.setTipoAlimento(dados.tipoAlimento());
        lote.setQuantidadeKg(dados.quantidadeKg());
        lote.setDataValidade(dados.dataValidade());
        lote.definirJanelaRetirada(dados.retiradaInicio(), dados.retiradaFim());
        lote.validar();
        return loteRepository.atualizar(lote);
    }

    /** DELETE — somente enquanto o lote estiver DISPONIVEL (lotes reservados/retirados são histórico). */
    @Transactional
    public void excluir(Integer id) {
        LoteDoacao lote = buscar(id);
        if (!lote.isEditavel()) {
            throw new ConflitoException("Lotes reservados ou já retirados não podem ser excluídos, "
                    + "pois fazem parte do histórico de impacto social.");
        }
        reservaRepository.excluirCanceladasDoLote(id);
        loteRepository.excluir(id);
    }
}
