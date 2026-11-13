package br.com.pratosolidario.service;

import br.com.pratosolidario.exception.ConflitoException;
import br.com.pratosolidario.model.Doador;
import br.com.pratosolidario.repository.DoadorRepository;
import org.springframework.stereotype.Service;

@Service
public class DoadorService extends UsuarioService<Doador> {

    private final DoadorRepository doadorRepository;

    public DoadorService(DoadorRepository doadorRepository) {
        super(doadorRepository);
        this.doadorRepository = doadorRepository;
    }

    @Override
    protected String nomeRecurso() {
        return "Doador";
    }

    @Override
    protected void copiarDadosEspecificos(Doador origem, Doador destino) {
        destino.setDocumento(origem.getDocumento());
        destino.setTipoEstabelecimento(origem.getTipoEstabelecimento());
        destino.setRegiao(origem.getRegiao());
    }

    /** E-mail único (regra herdada) + documento (CPF/CNPJ) único. */
    @Override
    protected void validarUnicidade(Doador doador) {
        super.validarUnicidade(doador);
        if (doadorRepository.documentoEmUso(doador.getDocumento(), doador.getId())) {
            throw new ConflitoException("Já existe um doador cadastrado com o documento " + doador.getDocumento() + ".");
        }
    }

    @Override
    protected void verificarPodeExcluir(Doador doador) {
        int lotes = doadorRepository.contarLotes(doador.getId());
        if (lotes > 0) {
            throw new ConflitoException("O doador possui " + lotes
                    + " lote(s) publicado(s) e não pode ser excluído, pois esses dados compõem o histórico de impacto.");
        }
    }
}
