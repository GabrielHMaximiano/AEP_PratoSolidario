package br.com.pratosolidario.service;

import br.com.pratosolidario.exception.ConflitoException;
import br.com.pratosolidario.model.Instituicao;
import br.com.pratosolidario.repository.InstituicaoRepository;
import org.springframework.stereotype.Service;

@Service
public class InstituicaoService extends UsuarioService<Instituicao> {

    private final InstituicaoRepository instituicaoRepository;

    public InstituicaoService(InstituicaoRepository instituicaoRepository) {
        super(instituicaoRepository);
        this.instituicaoRepository = instituicaoRepository;
    }

    @Override
    protected String nomeRecurso() {
        return "Instituição";
    }

    @Override
    protected void copiarDadosEspecificos(Instituicao origem, Instituicao destino) {
        destino.setAreaAtuacao(origem.getAreaAtuacao());
        destino.setCapacidadeAtendimento(origem.getCapacidadeAtendimento());
    }

    @Override
    protected void verificarPodeExcluir(Instituicao instituicao) {
        int reservas = instituicaoRepository.contarReservas(instituicao.getId());
        if (reservas > 0) {
            throw new ConflitoException("A instituição possui " + reservas
                    + " reserva(s) registrada(s) e não pode ser excluída, pois esses dados compõem o histórico de impacto.");
        }
    }
}
