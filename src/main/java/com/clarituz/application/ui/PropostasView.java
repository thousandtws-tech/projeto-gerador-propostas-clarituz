package com.clarituz.application.ui;

import com.clarituz.application.proposta.Proposta;
import com.clarituz.application.proposta.PropostaService;
import com.clarituz.application.proposta.enums.StatusProposta;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Propostas")
public class PropostasView extends VerticalLayout {

    private final PropostaService service;
    private final Grid<Proposta> grid = new Grid<>(Proposta.class, false);
    private final TextField filtro = new TextField();

    public PropostasView(PropostaService service) {
        this.service = service;
        setSizeFull();
        setPadding(true);
        addClassName("cz-shell");

        filtro.setPlaceholder("Buscar por título ou cliente");
        filtro.setClearButtonVisible(true);
        filtro.setValueChangeMode(ValueChangeMode.LAZY);
        filtro.addValueChangeListener(e -> atualizar());

        Button nova = new Button("Nova proposta", VaadinIcon.PLUS.create(),
                e -> getUI().ifPresent(ui -> ui.navigate(PropostaEditorView.class, "novo")));
        nova.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout barra = new HorizontalLayout(filtro, nova);
        barra.setWidthFull();
        barra.setAlignItems(Alignment.END);
        barra.expand(filtro);
        barra.addClassName("cz-lista-barra");

        configurarGrid();

        Span eyebrow = new Span("Gerador de propostas");
        eyebrow.addClassName("cz-eyebrow");
        H2 titulo = new H2("Suas propostas");
        titulo.getStyle().set("margin", "0.25rem 0 0");

        HorizontalLayout topo = new HorizontalLayout(new Marca());
        topo.setWidthFull();
        topo.setAlignItems(Alignment.CENTER);

        add(topo, eyebrow, titulo, barra, grid);
        expand(grid);
        atualizar();
    }

    private void configurarGrid() {
        grid.setSizeFull();
        grid.addColumn(Proposta::getTitulo).setHeader("Título").setAutoWidth(true).setFlexGrow(1)
                .setKey("titulo");
        grid.addColumn(Proposta::getCliente).setHeader("Cliente").setAutoWidth(true)
                .setKey("cliente");
        grid.addComponentColumn(this::renderizarStatus).setHeader("Status").setAutoWidth(true)
                .setKey("status");
        grid.addColumn(p -> Formatos.data(p.getValidaAte())).setHeader("Válida até").setAutoWidth(true)
                .setKey("valida");
        grid.addColumn(p -> Formatos.moeda(p.getTotal())).setHeader("Total").setAutoWidth(true)
                .setKey("total");

        grid.addComponentColumn(this::acoes).setHeader("Ações").setAutoWidth(true).setFlexGrow(0)
                .setKey("acoes");

        grid.addItemDoubleClickListener(e -> abrir(e.getItem()));
    }

    private Component renderizarStatus(Proposta p) {
        if (p.getStatus() == StatusProposta.VISUALIZADA) {
            Span dot = new Span();
            dot.addClassName("cz-pulse-green");
            Span txt = new Span("Visualizada");
            Span chip = new Span(dot, txt);
            chip.addClassName("cz-chip");
            chip.addClassName("cz-chip-success");
            return chip;
        }
        Span chip = new Span(p.getStatus().getRotulo());
        chip.addClassName("cz-chip");
        if (p.getStatus() == StatusProposta.PUBLICADA) {
            chip.addClassName("cz-chip-accent");
        }
        return chip;
    }

    private HorizontalLayout acoes(Proposta proposta) {
        Button editar = new Button(VaadinIcon.EDIT.create(), e -> abrir(proposta));
        editar.setTooltipText("Editar");

        Button link = new Button(VaadinIcon.LINK.create(), e -> copiarLink(proposta));
        link.setTooltipText("Copiar link de preview");
        link.setEnabled(proposta.getStatus() != StatusProposta.RASCUNHO);

        Button excluir = new Button(VaadinIcon.TRASH.create(), e -> {
            Confirmador.excluir(
                    "Excluir proposta",
                    "Tem certeza que deseja excluir \"" + proposta.getTitulo() + "\"? Esta ação não pode ser desfeita.",
                    () -> {
                        service.excluir(proposta);
                        atualizar();
                        Notifier.sucesso("Proposta excluída");
                    });
        });
        excluir.addThemeVariants(ButtonVariant.LUMO_ERROR);
        excluir.setTooltipText("Excluir");

        HorizontalLayout layout = new HorizontalLayout(editar, link, excluir);
        layout.setSpacing(false);
        return layout;
    }

    private void copiarLink(Proposta proposta) {
        getElement().executeJs("const u = location.origin + '/' + $0; navigator.clipboard.writeText(u); return u;",
                LinkPreview.caminho(proposta.getToken()))
                .then(String.class, url -> Notifier.info("Link copiado: " + url));
    }

    private void abrir(Proposta proposta) {
        getUI().ifPresent(ui -> ui.navigate(PropostaEditorView.class, String.valueOf(proposta.getId())));
    }

    private void atualizar() {
        grid.setItems(service.listar(filtro.getValue()));
    }
}
