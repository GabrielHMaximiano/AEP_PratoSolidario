package br.com.pratosolidario.model;

/**
 * Nível de urgência de um lote, calculado a partir dos dias restantes até a
 * data de validade (RF03).
 * <ul>
 *   <li>VENCIDO: validade já passou (não pode mais ser reservado);</li>
 *   <li>CRITICO: vence hoje ou amanhã (0 a 1 dia) — dispara notificação (RF06);</li>
 *   <li>ATENCAO: vence em 2 ou 3 dias;</li>
 *   <li>NORMAL: mais de 3 dias.</li>
 * </ul>
 */
public enum NivelUrgencia {

    VENCIDO("Vencido"),
    CRITICO("Crítico"),
    ATENCAO("Atenção"),
    NORMAL("Normal");

    public static final int LIMITE_DIAS_CRITICO = 1;
    public static final int LIMITE_DIAS_ATENCAO = 3;

    private final String descricao;

    NivelUrgencia(String descricao) {
        this.descricao = descricao;
    }

    public static NivelUrgencia porDiasRestantes(long diasRestantes) {
        if (diasRestantes < 0) {
            return VENCIDO;
        }
        if (diasRestantes <= LIMITE_DIAS_CRITICO) {
            return CRITICO;
        }
        if (diasRestantes <= LIMITE_DIAS_ATENCAO) {
            return ATENCAO;
        }
        return NORMAL;
    }

    /** Lotes "urgentes" no sentido do RF06 são os de nível crítico. */
    public boolean isUrgente() {
        return this == CRITICO;
    }

    public String getDescricao() {
        return descricao;
    }
}
