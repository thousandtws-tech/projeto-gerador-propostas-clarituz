package com.clarituz.application.proposta.enums;

public enum Recorrencia {
    UNICA("Pagamento único"),
    MENSAL("Mensal"),
    TRIMESTRAL("Trimestral"),
    ANUAL("Anual");

    private final String rotulo;

    Recorrencia(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
