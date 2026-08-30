package com.clarituz.application.proposta;

import com.clarituz.application.proposta.enums.CategoriaServico;

import java.util.List;

public record ServicoPadrao(
    String numero,
    String titulo,
    CategoriaServico categoria,
    String detalhe,
    String url
) {
    public static final List<ServicoPadrao> CATALOGO = List.of(
        new ServicoPadrao("01", "Consultoria de marketing digital", CategoriaServico.MARKETING,
            "Analisamos posicionamento, presença digital, aquisição e operação comercial para transformar desafios dispersos em prioridades objetivas e um plano possível de executar.",
            "https://www.clarituz.com.br/servicos/consultoria-marketing-digital/"),
        new ServicoPadrao("02", "Design gráfico", CategoriaServico.BRANDING,
            "Criamos materiais digitais e impressos que organizam a mensagem, valorizam a marca e tornam sua comunicação mais profissional.",
            "https://www.clarituz.com.br/servicos/design-grafico/"),
        new ServicoPadrao("03", "Fotografia e vídeo", CategoriaServico.CONTEUDO,
            "Planejamos, dirigimos e produzimos fotografias e vídeos para apresentar produtos, pessoas, espaços e histórias com autenticidade.",
            "https://www.clarituz.com.br/servicos/fotografia-e-video/"),
        new ServicoPadrao("04", "Hospedagem, e-mails e suporte", CategoriaServico.SUPORTE,
            "Cuidamos da base técnica que mantém site, contas profissionais e serviços digitais disponíveis, seguros e organizados.",
            "https://www.clarituz.com.br/servicos/hospedagem-email-suporte/"),
        new ServicoPadrao("05", "Identidade visual", CategoriaServico.BRANDING,
            "Transformamos posicionamento em um sistema visual próprio, capaz de transmitir personalidade e gerar reconhecimento.",
            "https://www.clarituz.com.br/servicos/identidade-visual/"),
        new ServicoPadrao("06", "Plataformas e sistemas", CategoriaServico.DESENVOLVIMENTO,
            "Desenvolvemos plataformas, painéis e integrações adaptados à realidade da sua operação.",
            "https://www.clarituz.com.br/servicos/plataformas-e-sistemas/"),
        new ServicoPadrao("07", "Social media", CategoriaServico.CONTEUDO,
            "Planejamos e produzimos conteúdo alinhado ao posicionamento e aos objetivos do negócio.",
            "https://www.clarituz.com.br/servicos/social-media/"),
        new ServicoPadrao("08", "Web design", CategoriaServico.UX_UI,
            "Unimos estratégia, conteúdo, experiência e tecnologia para criar sites e landing pages que facilitam a decisão.",
            "https://www.clarituz.com.br/servicos/web-design/"),
        new ServicoPadrao("09", "Tráfego pago", CategoriaServico.TRAFEGO_PAGO,
            "Planejamos, ativamos e otimizamos campanhas em Google Ads e Meta Ads com foco em intenção e qualidade.",
            "https://www.clarituz.com.br/servicos/trafego-pago/")
    );
}
