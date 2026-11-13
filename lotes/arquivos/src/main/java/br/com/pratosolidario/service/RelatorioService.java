package br.com.pratosolidario.service;

import br.com.pratosolidario.dto.FiltroRelatorio;
import br.com.pratosolidario.dto.RelatorioLinha;
import br.com.pratosolidario.dto.RelatorioResponse;
import br.com.pratosolidario.exception.RegraNegocioException;
import br.com.pratosolidario.repository.RelatorioRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** RF05 — relatório consolidado de alimentos resgatados, com exportação em CSV. */
@Service
public class RelatorioService {

    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final String SEPARADOR = ";";

    private final RelatorioRepository relatorioRepository;

    public RelatorioService(RelatorioRepository relatorioRepository) {
        this.relatorioRepository = relatorioRepository;
    }

    public RelatorioResponse gerar(FiltroRelatorio filtro) {
        if (filtro.inicio() != null && filtro.fim() != null && filtro.fim().isBefore(filtro.inicio())) {
            throw new RegraNegocioException("A data final do período não pode ser anterior à data inicial.");
        }
        List<RelatorioLinha> linhas = relatorioRepository.buscarResgates(filtro);
        double totalKg = linhas.stream().mapToDouble(RelatorioLinha::quantidadeKg).sum();
        int totalFamilias = linhas.stream().mapToInt(RelatorioLinha::familiasAtendidas).sum();
        return new RelatorioResponse(filtro, linhas.size(), Math.round(totalKg * 100) / 100.0, totalFamilias, linhas);
    }

    /** CSV separado por ";" (abre direto no Excel/LibreOffice em português). */
    public String exportarCsv(FiltroRelatorio filtro) {
        RelatorioResponse relatorio = gerar(filtro);
        StringBuilder csv = new StringBuilder();
        csv.append(String.join(SEPARADOR, "Reserva", "Data da retirada", "Doador", "Instituição",
                "Alimento", "Quantidade (kg)", "Famílias atendidas")).append('\n');
        for (RelatorioLinha linha : relatorio.resgates()) {
            csv.append(String.join(SEPARADOR,
                    String.valueOf(linha.reservaId()),
                    linha.dataRetirada() == null ? "" : linha.dataRetirada().format(FORMATO_DATA_HORA),
                    campo(linha.doadorNome()),
                    campo(linha.instituicaoNome()),
                    campo(linha.tipoAlimento()),
                    String.format(PT_BR, "%.2f", linha.quantidadeKg()),
                    String.valueOf(linha.familiasAtendidas()))).append('\n');
        }
        csv.append(String.join(SEPARADOR, "TOTAL", String.valueOf(relatorio.totalResgates()) + " resgate(s)",
                "", "", "", String.format(PT_BR, "%.2f", relatorio.totalKg()),
                String.valueOf(relatorio.totalFamilias()))).append('\n');
        return csv.toString();
    }

    /** Coloca o texto entre aspas se ele tiver separador, aspas ou quebra de linha. */
    private static String campo(String valor) {
        if (valor == null) {
            return "";
        }
        if (valor.contains(SEPARADOR) || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}
