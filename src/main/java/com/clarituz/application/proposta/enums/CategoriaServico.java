package com.clarituz.application.proposta.enums;

public enum CategoriaServico {
    ESTRATEGIA("Estratégia"),
    BRANDING("Branding"),
    UX_UI("UX/UI Design"),
    DESENVOLVIMENTO("Desenvolvimento"),
    MARKETING("Marketing"),
    TRAFEGO_PAGO("Tráfego pago"),
    CONTEUDO("Conteúdo"),
    SUPORTE("Suporte e sustentação");

    private final String rotulo;

    CategoriaServico(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
