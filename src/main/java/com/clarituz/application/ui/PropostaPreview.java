package com.clarituz.application.ui;

import com.clarituz.application.proposta.FaseProposta;
import com.clarituz.application.proposta.ItemProposta;
import com.clarituz.application.proposta.Proposta;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PropostaPreview extends Div {

    private final Div hero = new Div();
    private final Div corpo = new Div();
    private final Div rodape = new Div();

    public PropostaPreview() {
        addClassName("cz-preview");
        setWidthFull();
        corpo.addClassName("cz-body");
        add(hero, corpo, rodape);
    }

    public void render(Proposta p) {
        renderHero(p);
        renderCorpo(p);
    }

    public Div getRodape() {
        return rodape;
    }

    private void renderHero(Proposta p) {
        hero.removeAll();
        hero.addClassName("cz-hero");

        Component elementoMarca;
        if (preenchido(p.getLogoData())) {
            Image customLogo = new Image(p.getLogoData(), "Logo");
            customLogo.addClassName("cz-custom-logo");
            elementoMarca = customLogo;
        } else {
            elementoMarca = new Marca();
        }

        Div topo = new Div(elementoMarca);
        topo.addClassName("cz-hero-topo");
        topo.getStyle().set("display", "flex").set("justify-content", "space-between")
                .set("align-items", "center").set("gap", "1rem").set("flex-wrap", "wrap");

        Span eyebrow = new Span("Proposta comercial");
        eyebrow.addClassName("cz-eyebrow");

        H1 titulo = new H1(vazioOu(p.getTitulo(), "Título da proposta"));
        titulo.addClassName("cz-hero-title");

        hero.add(topo, new Div(eyebrow), titulo);

        if (preenchido(p.getChamada())) {
            Paragraph claim = new Paragraph(p.getChamada());
            claim.addClassName("cz-hero-claim");
            hero.add(claim);
        }

        Div meta = new Div();
        meta.addClassName("cz-meta");
        meta.add(metaItem("Cliente", vazioOu(p.getCliente(), "—")));
        if (preenchido(p.getEmpresaCliente())) {
            meta.add(metaItem("Empresa", p.getEmpresaCliente()));
        }
        if (preenchido(p.getSegmento())) {
            meta.add(metaItem("Segmento", p.getSegmento()));
        }
        if (preenchido(p.getEnderecoFormatado())) {
            meta.add(metaItemLargo("Endereço", p.getEnderecoFormatado()));
        }
        if (preenchido(p.getPrazoExecucao())) {
            meta.add(metaItem("Prazo", p.getPrazoExecucao()));
        }
        meta.add(metaItem("Válida até", Formatos.data(p.getValidaAte())));
        hero.add(meta);
    }

    private void renderCorpo(Proposta p) {
        corpo.removeAll();

        secaoTexto("Contexto", p.getIntroducao());
        secaoTexto("Desafio", p.getDesafio());
        secaoTexto("Nossa solução", p.getSolucao());
        secaoTexto("Entregáveis", p.getEntregaveis());

        renderFases(p.getFases());
        renderEscopo(p);
        renderInvestimento(p);

        secaoTexto("Condições comerciais", p.getCondicoes());
        secaoTexto("Forma de pagamento", p.getFormaPagamento());
        renderLgpd(p.getLgpdTermos());

        renderContato(p);
    }

    private static final String LGPD_PADRAO =
            "Esta proposta e as informações contidas neste documento são confidenciais e protegidas pela Lei Geral de "
            + "Proteção de Dados (Lei nº 13.709/2018 - LGPD). Os dados fornecidos serão tratados exclusivamente para "
            + "fins de análise comercial e execução dos serviços propostos, sendo vedada qualquer divulgação a "
            + "terceiros sem autorização prévia.";

    private void renderLgpd(String texto) {
        String conteudo = preenchido(texto) ? texto : LGPD_PADRAO;
        Div caixa = new Div();
        caixa.addClassName("cz-lgpd-box");

        Paragraph paragrafo = new Paragraph(conteudo);
        paragrafo.addClassName("cz-lgpd-text");

        caixa.add(paragrafo);
        corpo.add(bloco("Proteção de Dados & Privacidade (LGPD)", caixa));
    }

    private void secaoTexto(String titulo, String texto) {
        if (!preenchido(texto)) {
            return;
        }
        Paragraph paragrafo = new Paragraph(texto);
        paragrafo.addClassName("cz-text");
        corpo.add(bloco(titulo, paragrafo));
    }

    private void renderFases(List<FaseProposta> fases) {
        if (fases.isEmpty()) {
            return;
        }
        Div lista = new Div();
        for (FaseProposta fase : fases) {
            Div item = new Div();
            item.addClassName("cz-phase");

            Div cabecalho = new Div();
            cabecalho.getStyle().set("display", "flex").set("gap", "0.75rem")
                    .set("align-items", "center").set("flex-wrap", "wrap");
            Span nome = new Span(vazioOu(fase.getNome(), "Fase"));
            nome.addClassName("cz-phase-name");
            cabecalho.add(nome);
            if (preenchido(fase.getPrazo())) {
                cabecalho.add(chip(fase.getPrazo(), false));
            }
            item.add(cabecalho);

            if (preenchido(fase.getDescricao())) {
                Paragraph descricao = new Paragraph(fase.getDescricao());
                descricao.addClassName("cz-text");
                item.add(descricao);
            }
            lista.add(item);
        }
        corpo.add(bloco("Como vamos executar", lista));
    }

    private void renderEscopo(Proposta p) {
        if (p.getItens().isEmpty()) {
            return;
        }
        Map<String, List<ItemProposta>> porCategoria = p.getItens().stream()
                .collect(Collectors.groupingBy(i -> i.getCategoria().getRotulo(), java.util.LinkedHashMap::new,
                        Collectors.toList()));

        Div lista = new Div();
        porCategoria.forEach((categoria, itens) -> {
            Div grupo = new Div();
            grupo.getStyle().set("margin-bottom", "1.25rem");
            grupo.add(chip(categoria, true));

            for (ItemProposta item : itens) {
                Div esquerda = new Div();
                Span nome = new Span(vazioOu(item.getDescricao(), "Item"));
                nome.addClassName("cz-item-name");
                esquerda.add(nome);
                if (preenchido(item.getDetalhe())) {
                    Div detalhe = new Div(new Span(item.getDetalhe()));
                    detalhe.addClassName("cz-item-detail");
                    esquerda.add(detalhe);
                }
                Div qtd = new Div(new Span(item.getQuantidade().stripTrailingZeros().toPlainString()
                        + " " + item.getUnidade() + " × " + Formatos.moeda(item.getValorUnitario())));
                qtd.addClassName("cz-item-detail");
                esquerda.add(qtd);

                Span valor = new Span(Formatos.moeda(item.getTotal()));
                valor.addClassName("cz-item-value");

                Div linha = new Div(esquerda, valor);
                linha.addClassName("cz-item-row");
                grupo.add(linha);
            }
            lista.add(grupo);
        });

        corpo.add(bloco("Escopo e serviços", lista));
    }

    private void renderInvestimento(Proposta p) {
        Div caixa = new Div();
        caixa.addClassName("cz-total-box");

        caixa.add(linhaTotal("Subtotal", Formatos.moeda(p.getSubtotal())));
        if (p.getValorDesconto().signum() > 0) {
            caixa.add(linhaTotal("Desconto (" + p.getDescontoPercentual().stripTrailingZeros().toPlainString() + "%)",
                    "- " + Formatos.moeda(p.getValorDesconto())));
        }

        Span rotulo = new Span("Investimento total");
        rotulo.addClassName("cz-eyebrow");
        Div valor = new Div(new Span(Formatos.moeda(p.getTotal())));
        valor.addClassName("cz-total-value");

        Div destaque = new Div(rotulo, valor, chip(p.getRecorrencia().getRotulo(), false));
        destaque.getStyle().set("margin-top", "0.75rem");
        caixa.add(destaque);

        Div conteudo = new Div(caixa);
        if (preenchido(p.getInvestimentoObservacao())) {
            Paragraph obs = new Paragraph(p.getInvestimentoObservacao());
            obs.addClassName("cz-text");
            conteudo.add(obs);
        }
        corpo.add(bloco("Investimento", conteudo));
    }

    private void renderContato(Proposta p) {
        if (!preenchido(p.getResponsavel()) && !preenchido(p.getEmailResponsavel())
                && !preenchido(p.getTelefoneResponsavel())) {
            return;
        }
        Div dados = new Div();
        dados.addClassName("cz-contato");
        if (preenchido(p.getResponsavel())) {
            dados.add(metaItem("Responsável", p.getResponsavel()));
        }
        if (preenchido(p.getEmailResponsavel())) {
            dados.add(metaItem("E-mail", p.getEmailResponsavel()));
        }
        if (preenchido(p.getTelefoneResponsavel())) {
            dados.add(metaItem("Telefone", p.getTelefoneResponsavel()));
        }
        corpo.add(bloco("Fale com a gente", dados));
    }

    private Div bloco(String titulo, Component conteudo) {
        Div wrapper = new Div();
        wrapper.addClassName("cz-block");
        wrapper.getStyle().set("margin-bottom", "1.75rem");
        Div h = new Div(new Span(titulo));
        h.addClassName("cz-section-title");
        wrapper.add(h, conteudo);
        return wrapper;
    }

    private Div linhaTotal(String rotulo, String valor) {
        Div linha = new Div(new Span(rotulo), new Span(valor));
        linha.addClassName("cz-total-line");
        return linha;
    }

    private Div metaItem(String rotulo, String valor) {
        Div item = new Div();
        Span label = new Span(rotulo);
        label.addClassName("cz-meta-label");
        Span value = new Span(valor);
        value.addClassName("cz-meta-value");
        item.add(label, value);
        return item;
    }

    private Div metaItemLargo(String rotulo, String valor) {
        Div item = metaItem(rotulo, valor);
        item.addClassName("cz-meta-wide");
        return item;
    }

    private Span chip(String texto, boolean destaque) {
        Span chip = new Span(texto);
        chip.addClassName("cz-chip");
        if (destaque) {
            chip.addClassName("cz-chip-accent");
        }
        return chip;
    }

    private static boolean preenchido(String valor) {
        return valor != null && !valor.isBlank();
    }

    private static String vazioOu(String valor, String padrao) {
        return preenchido(valor) ? valor : padrao;
    }
}
