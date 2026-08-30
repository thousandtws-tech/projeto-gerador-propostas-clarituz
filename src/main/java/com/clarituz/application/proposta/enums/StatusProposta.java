package com.clarituz.application.proposta.enums;

public enum StatusProposta {
    RASCUNHO("Rascunho"),
    PUBLICADA("Publicada"),
    VISUALIZADA("Visualizada"),
    ACEITA("Aceita"),
    RECUSADA("Recusada");

    private final String rotulo;

    StatusProposta(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
