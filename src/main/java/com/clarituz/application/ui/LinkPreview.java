package com.clarituz.application.ui;

import com.clarituz.application.proposta.Proposta;

public final class LinkPreview {

    public static final String ROTA = "p";

    private LinkPreview() {
    }

    public static String caminho(String token) {
        return ROTA + "/" + token;
    }

    public static String gerarLinkWhatsApp(Proposta p, String origin) {
        String urlProposta = (origin != null ? origin : "") + "/" + caminho(p.getToken());
        String mensagem = "Olá! Segue a proposta comercial *" + (p.getTitulo() != null && !p.getTitulo().isBlank() ? p.getTitulo() : "Proposta") + "*:\n" + urlProposta;
        String encodedMsg = java.net.URLEncoder.encode(mensagem, java.nio.charset.StandardCharsets.UTF_8);

        String fone = p.getContatoCliente();
        if (fone == null || fone.isBlank()) {
            fone = p.getTelefoneResponsavel();
        }

        if (fone != null) {
            String numOnly = fone.replaceAll("\\D", "");
            if (!numOnly.isBlank()) {
                if (numOnly.length() == 10 || numOnly.length() == 11) {
                    numOnly = "55" + numOnly;
                }
                return "https://api.whatsapp.com/send?phone=" + numOnly + "&text=" + encodedMsg;
            }
        }
        return "https://api.whatsapp.com/send?text=" + encodedMsg;
    }
}
