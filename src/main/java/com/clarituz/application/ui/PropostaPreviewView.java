package com.clarituz.application.ui;

import com.clarituz.application.proposta.Proposta;
import com.clarituz.application.proposta.PropostaService;
import com.clarituz.application.proposta.enums.StatusProposta;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.Optional;

@Route(LinkPreview.ROTA)
@PageTitle("Proposta · Clarituz")
public class PropostaPreviewView extends VerticalLayout implements HasUrlParameter<String> {

    private final PropostaService service;
    private final PropostaPreview preview = new PropostaPreview();

    public PropostaPreviewView(PropostaService service) {
        this.service = service;
        addClassName("cz-client-preview");
        setWidthFull();
        setMaxWidth("1100px");
        getStyle().set("margin", "0 auto");
        setPadding(true);
        getStyle().set("box-sizing", "border-box");
    }

    @Override
    public void setParameter(BeforeEvent event, String token) {
        removeAll();
        Optional<Proposta> encontrada = token == null ? Optional.empty() : service.registrarVisualizacaoPorToken(token);
        encontrada.filter(p -> p.getStatus() != StatusProposta.RASCUNHO)
                .ifPresentOrElse(this::renderizar, this::renderizarNaoEncontrada);
    }

    private void renderizarNaoEncontrada() {
        Div card = new Div();
        card.addClassName("cz-card");
        Span eyebrow = new Span("Link indisponível");
        eyebrow.addClassName("cz-eyebrow");
        Paragraph texto = new Paragraph("O link informado é inválido ou a proposta ainda não foi publicada.");
        texto.addClassName("cz-text");
        card.add(new Marca(), eyebrow, new H1("Proposta indisponível"), texto);
        add(card);
    }

    private void renderizar(Proposta p) {
        preview.render(p);
        preview.getRodape().removeAll();
        add(preview);
    }
}
