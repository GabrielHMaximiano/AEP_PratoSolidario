package br.com.pratosolidario.service;

import br.com.pratosolidario.dto.ReservaRequest;
import br.com.pratosolidario.exception.ConflitoException;
import br.com.pratosolidario.exception.RecursoNaoEncontradoException;
import br.com.pratosolidario.exception.RegraNegocioException;
import br.com.pratosolidario.model.Instituicao;
import br.com.pratosolidario.model.LoteDoacao;
import br.com.pratosolidario.model.Reserva;
import br.com.pratosolidario.model.StatusReserva;
import br.com.pratosolidario.repository.InstituicaoRepository;
import br.com.pratosolidario.repository.LoteDoacaoRepository;
import br.com.pratosolidario.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Reserva, conclusão da retirada e cancelamento (RF02 / Sprint 3). */
@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final LoteDoacaoRepository loteRepository;
    private final InstituicaoRepository instituicaoRepository;

    public ReservaService(ReservaRepository reservaRepository, LoteDoacaoRepository loteRepository,
                          InstituicaoRepository instituicaoRepository) {
        this.reservaRepository = reservaRepository;
        this.loteRepository = loteRepository;
        this.instituicaoRepository = instituicaoRepository;
    }

    @Transactional
    public Reserva reservar(ReservaRequest dados) {
        if (dados.loteId() == null || dados.instituicaoId() == null) {
            throw new RegraNegocioException("Informe o lote (loteId) e a instituição (instituicaoId).");
        }
        Instituicao instituicao = instituicaoRepository.buscarPorId(dados.instituicaoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Instituição", dados.instituicaoId()));
        LoteDoacao lote = loteRepository.buscarPorId(dados.loteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lote", dados.loteId()));

        // Regra de domínio: lote disponível e não vencido (lança exceção se não puder)
        Reserva reserva = instituicao.reservarLote(lote);

        // Garantia no banco contra reserva simultânea por duas instituições
        if (!loteRepository.reservarSeDisponivel(lote.getId())) {
            throw new ConflitoException("Este lote acabou de ser reservado por outra instituição.");
        }
        return reservaRepository.salvar(reserva);
    }

    public List<Reserva> listar(Integer instituicaoId) {
        return instituicaoId == null
                ? reservaRepository.listarTodos()
                : reservaRepository.listarPorInstituicao(instituicaoId);
    }

    public Reserva buscar(Integer id) {
        return reservaRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva", id));
    }

    /** Retirada realizada: lote vira RETIRADO e as famílias atendidas entram no impacto (RF04/RF05). */
    @Transactional
    public Reserva concluir(Integer id, Integer familiasAtendidas) {
        Reserva reserva = buscar(id);
        reserva.concluir(familiasAtendidas);
        reservaRepository.atualizar(reserva);
        loteRepository.atualizarStatus(reserva.getLote().getId(), reserva.getLote().getStatus());
        return reserva;
    }

    /** Cancelamento: o lote volta a ficar DISPONIVEL para outras instituições. */
    @Transactional
    public Reserva cancelar(Integer id) {
        Reserva reserva = buscar(id);
        reserva.cancelar();
        reservaRepository.atualizar(reserva);
        loteRepository.atualizarStatus(reserva.getLote().getId(), reserva.getLote().getStatus());
        return reserva;
    }

    /** Exclusão física permitida apenas para reservas já canceladas. */
    @Transactional
    public void excluir(Integer id) {
        Reserva reserva = buscar(id);
        if (reserva.getStatus() != StatusReserva.CANCELADA) {
            throw new ConflitoException("Somente reservas canceladas podem ser excluídas. Cancele a reserva primeiro.");
        }
        reservaRepository.excluir(id);
    }
}
