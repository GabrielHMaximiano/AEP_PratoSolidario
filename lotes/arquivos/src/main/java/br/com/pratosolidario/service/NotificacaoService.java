package br.com.pratosolidario.service;

import br.com.pratosolidario.exception.RecursoNaoEncontradoException;
import br.com.pratosolidario.model.Instituicao;
import br.com.pratosolidario.model.LoteDoacao;
import br.com.pratosolidario.model.Notificacao;
import br.com.pratosolidario.repository.InstituicaoRepository;
import br.com.pratosolidario.repository.NotificacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** RF06 — notificação das instituições da região quando um lote crítico é publicado. */
@Service
public class NotificacaoService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificacaoService.class);

    private final NotificacaoRepository notificacaoRepository;
    private final InstituicaoRepository instituicaoRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository,
                              InstituicaoRepository instituicaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
        this.instituicaoRepository = instituicaoRepository;
    }

    /** Cria uma notificação para cada instituição que atua na região do doador. */
    @Transactional
    public int notificarInstituicoesDaRegiao(LoteDoacao lote) {
        String regiao = lote.getDoador().getRegiao();
        int total = 0;
        for (Instituicao instituicao : instituicaoRepository.listarPorAreaAtuacao(regiao)) {
            if (instituicao.atendeRegiao(regiao)) {
                notificacaoRepository.salvar(Notificacao.paraLoteUrgente(instituicao, lote));
                total++;
            }
        }
        LOG.info("Lote {} (crítico) publicado na região '{}': {} instituição(ões) notificada(s).",
                lote.getId(), regiao, total);
        return total;
    }

    public List<Notificacao> listarPorInstituicao(Integer instituicaoId) {
        instituicaoRepository.buscarPorId(instituicaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Instituição", instituicaoId));
        return notificacaoRepository.listarPorInstituicao(instituicaoId);
    }

    @Transactional
    public Notificacao marcarComoLida(Integer id) {
        Notificacao notificacao = notificacaoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Notificação", id));
        notificacao.marcarComoLida();
        return notificacaoRepository.atualizar(notificacao);
    }
}
