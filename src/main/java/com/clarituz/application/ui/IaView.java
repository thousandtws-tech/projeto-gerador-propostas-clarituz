package com.clarituz.application.ui;

import com.clarituz.application.ia.IaService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("ia")
@PageTitle("Assistente de IA · Clarituz")
public class IaView extends VerticalLayout {

    private final IaService iaService;

    private final TextArea resultado = new TextArea();
    private final ProgressBar loading = new ProgressBar();

    public IaView(IaService iaService) {
        this.iaService = iaService;
        setSizeFull();
        setPadding(true);
        addClassName("cz-shell");

        Span eyebrow = new Span("Inteligência Artificial");
        eyebrow.addClassName("cz-eyebrow");
        H2 titulo = new H2("Assistente Clarituz");
        titulo.getStyle().set("margin", "0.25rem 0 0");

        resultado.setReadOnly(true);
        resultado.setWidthFull();
        resultado.setMinHeight("300px");
        resultado.setLabel("Resposta da IA");

        loading.setVisible(false);
        loading.setWidthFull();

        TabSheet abas = new TabSheet();
        abas.setWidthFull();
        abas.add(new Tab("Análise do portfólio"), abaAnalise());
        abas.add(new Tab("Monitorar"), abaMonitorar());

        add(eyebrow, titulo, abas, loading, resultado);
        expand(resultado);
    }

    private VerticalLayout abaAnalise() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

        Button btn = new Button("Analisar propostas", VaadinIcon.CHART.create(),
                e -> executar(iaService::analisarPortifolio));
        btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        layout.add(new Span("Clique para obter insights sobre todas as propostas cadastradas."), btn);
        return layout;
    }

    private VerticalLayout abaMonitorar() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

        TextField pergunta = new TextField("Pergunta");
        pergunta.setPlaceholder("Ex: como melhorar a conversão de propostas?");
        pergunta.setWidthFull();

        Button btn = new Button("Perguntar à IA", VaadinIcon.BOLT.create(),
                e -> executar(() -> iaService.monitorar(pergunta.getValue())));
        btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout barra = new HorizontalLayout(pergunta, btn);
        barra.setWidthFull();
        barra.setAlignItems(Alignment.END);
        barra.expand(pergunta);

        layout.add(new Span("Use o monitor para tirar dúvidas e receber orientações."), barra);
        return layout;
    }

    private interface AcaoIa {
        String executar();
    }

    private void executar(AcaoIa acao) {
        resultado.clear();
        loading.setVisible(true);
        loading.setIndeterminate(true);

        new Thread(() -> {
            try {
                String resposta = acao.executar();
                getUI().ifPresent(ui -> ui.access(() -> {
                    resultado.setValue(resposta);
                    loading.setVisible(false);
                }));
            } catch (Exception ex) {
                getUI().ifPresent(ui -> ui.access(() -> {
                    resultado.setValue("Erro ao consultar a IA: " + ex.getMessage());
                    loading.setVisible(false);
                }));
            }
        }).start();
    }
}
