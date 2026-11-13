package br.com.pratosolidario.service;

import br.com.pratosolidario.dto.ImpactoResponse;
import br.com.pratosolidario.exception.RecursoNaoEncontradoException;
import br.com.pratosolidario.model.Doador;
import br.com.pratosolidario.model.Instituicao;
import br.com.pratosolidario.model.Usuario;
import br.com.pratosolidario.repository.DoadorRepository;
import br.com.pratosolidario.repository.InstituicaoRepository;
import br.com.pratosolidario.repository.LoteDoacaoRepository;
import br.com.pratosolidario.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * RF04 — impacto social. Carrega o histórico de cada usuário e chama
 * {@code calcularImpacto()} sobre a referência do tipo abstrato {@link Usuario}:
 * o Java decide em tempo de execução se executa a versão do Doador (kg) ou a
 * da Instituição (famílias) — POLIMORFISMO DINÂMICO.
 */
@Service
public class ImpactoService {

    private final DoadorRepository doadorRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final LoteDoacaoRepository loteRepository;
    private final ReservaRepository reservaRepository;

    public ImpactoService(DoadorRepository doadorRepository, InstituicaoRepository instituicaoRepository,
                          LoteDoacaoRepository loteRepository, ReservaRepository reservaRepository) {
        this.doadorRepository = doadorRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.loteRepository = loteRepository;
        this.reservaRepository = reservaRepository;
    }

    public ImpactoResponse calcular(Integer usuarioId) {
        return ImpactoResponse.de(carregarComHistorico(usuarioId));
    }

    /** Impacto de todos os usuários: uma única lista de Usuario com Doadores e Instituições misturados. */
    public List<ImpactoResponse> calcularTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        doadorRepository.listarTodos().forEach(doador -> usuarios.add(comHistorico(doador)));
        instituicaoRepository.listarTodos().forEach(instituicao -> usuarios.add(comHistorico(instituicao)));
        return usuarios.stream().map(ImpactoResponse::de).toList();
    }

    private Usuario carregarComHistorico(Integer usuarioId) {
        Optional<Doador> doador = doadorRepository.buscarPorId(usuarioId);
        if (doador.isPresent()) {
            return comHistorico(doador.get());
        }
        Optional<Instituicao> instituicao = instituicaoRepository.buscarPorId(usuarioId);
        if (instituicao.isPresent()) {
            return comHistorico(instituicao.get());
        }
        throw new RecursoNaoEncontradoException("Usuário", usuarioId);
    }

    private Doador comHistorico(Doador doador) {
        loteRepository.listarPorDoador(doador.getId()).forEach(doador::adicionarLote);
        return doador;
    }

    private Instituicao comHistorico(Instituicao instituicao) {
        reservaRepository.listarPorInstituicao(instituicao.getId()).forEach(instituicao::adicionarReserva);
        return instituicao;
    }
}
