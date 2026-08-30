package com.clarituz.application.ui;

import com.clarituz.application.proposta.enums.CategoriaServico;
import com.clarituz.application.proposta.FaseProposta;
import com.clarituz.application.proposta.ItemProposta;
import com.clarituz.application.proposta.Proposta;
import com.clarituz.application.proposta.PropostaService;
import com.clarituz.application.proposta.enums.Recorrencia;
import com.clarituz.application.proposta.ServicoPadrao;
import com.clarituz.application.proposta.enums.StatusProposta;
import com.clarituz.application.proposta.ViaCepService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.streams.UploadHandler;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;

@Route("propostas")
@PageTitle("Editor de proposta · Clarituz")
public class PropostaEditorView extends HorizontalLayout implements HasUrlParameter<String> {

    private final PropostaService service;
    private final ViaCepService viaCep;

    private final Binder<Proposta> binder = new Binder<>(Proposta.class);
    private final PropostaPreview preview = new PropostaPreview();

    private final Image logoPreviewImg = new Image();
    private final Button btnRemoverLogo = new Button("Remover logo", VaadinIcon.TRASH.create());

    private final List<ItemProposta> itens = new ArrayList<>();
    private final List<FaseProposta> fases = new ArrayList<>();
    private final Div editorItens = new Div();
    private final Div editorFases = new Div();

    private final TextField titulo = new TextField("Título da proposta");
    private final TextField chamada = new TextField("Chamada de destaque");
    private final TextField cliente = new TextField("Contato principal");
    private final TextField empresa = new TextField("Empresa");
    private final EmailField email = new EmailField("E-mail do cliente");
    private final TextField contato = new TextField("Telefone do cliente");
    private final TextField segmento = new TextField("Segmento");
    
    private final TextField cep = new TextField("CEP");
    private final TextField logradouro = new TextField("Logradouro");
    private final TextField numero = new TextField("Número");
    private final TextField complemento = new TextField("Complemento");
    private final TextField bairro = new TextField("Bairro");
    private final TextField cidade = new TextField("Cidade");
    private final TextField estado = new TextField("Estado");
    private final DatePicker validaAte = new DatePicker("Válida até");
    private final TextField prazoExecucao = new TextField("Prazo de execução");
    private final BigDecimalField desconto = new BigDecimalField("Desconto (%)");
    private final ComboBox<Recorrencia> recorrencia = new ComboBox<>("Recorrência");
    private final ComboBox<StatusProposta> status = new ComboBox<>("Status");

    private final TextArea introducao = new TextArea("Contexto");
    private final TextArea desafio = new TextArea("Desafio");
    private final TextArea solucao = new TextArea("Nossa solução");
    private final TextArea entregaveis = new TextArea("Entregáveis");
    private final TextArea investimentoObs = new TextArea("Observações do investimento");
    private final TextArea condicoes = new TextArea("Condições comerciais");
    private final TextArea formaPagamento = new TextArea("Forma de pagamento");
    private final TextArea lgpdTermos = new TextArea("Termos de Proteção de Dados (LGPD) & Confidencialidade");

    private final TextField responsavel = new TextField("Responsável Clarituz");
    private final EmailField emailResponsavel = new EmailField("E-mail do responsável");
    private final TextField telefoneResponsavel = new TextField("Telefone do responsável");

    private Proposta proposta = new Proposta();

    public PropostaEditorView(PropostaService service, ViaCepService viaCep) {
        this.service = service;
        this.viaCep = viaCep;
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        addClassName("cz-editor-root");

        configurarCampos();
        vincular();

        // Coluna esquerda — formulário
        VerticalLayout painelEsquerdo = new VerticalLayout(cabecalho(), abas());
        painelEsquerdo.setSizeFull();
        painelEsquerdo.addClassName("cz-editor-form");
        painelEsquerdo.setPadding(true);
        painelEsquerdo.getStyle().set("overflow", "auto");

        // Coluna direita — preview estilo PDF
        Div toolbar = new Div();
        toolbar.addClassName("cz-pdf-toolbar");
        Span docTitle = new Span("Proposta · Preview em tempo real");

        Button exportarPdfToolbar = new Button(VaadinIcon.PRINT.create(), e -> {
            getUI().ifPresent(ui -> ui.getPage().executeJs("window.print()"));
        });
        exportarPdfToolbar.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        exportarPdfToolbar.setTooltipText("Exportar / Imprimir PDF");

        Button whatsAppToolbar = new Button(VaadinIcon.CHAT.create(), e -> {
            if (proposta.getId() == null) {
                Notifier.aviso("Salve a proposta antes de enviar pelo WhatsApp");
                return;
            }
            getElement().executeJs(
                "const origin = window.location.origin; " +
                "const token = $0; " +
                "const title = $1; " +
                "const phone = $2; " +
                "const msg = encodeURIComponent('Olá! Segue a proposta comercial *' + (title || 'Proposta') + '*:\\n' + origin + '/p/' + token); " +
                "const cleanPhone = (phone || '').replace(/\\D/g, ''); " +
                "const num = (cleanPhone.length === 10 || cleanPhone.length === 11) ? ('55' + cleanPhone) : cleanPhone; " +
                "const url = num ? ('https://api.whatsapp.com/send?phone=' + num + '&text=' + msg) : ('https://api.whatsapp.com/send?text=' + msg); " +
                "window.open(url, '_blank');",
                proposta.getToken(),
                proposta.getTitulo(),
                proposta.getContatoCliente() != null && !proposta.getContatoCliente().isBlank() ? proposta.getContatoCliente() : proposta.getTelefoneResponsavel()
            );
        });
        whatsAppToolbar.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        whatsAppToolbar.addClassName("cz-btn-whatsapp");
        whatsAppToolbar.setTooltipText("Enviar pelo WhatsApp");

        Button abrirTab = new Button(VaadinIcon.EXTERNAL_LINK.create(), e -> {
            if (proposta.getId() == null) {
                Notifier.aviso("Salve a proposta antes de abrir o link");
                return;
            }
            getUI().ifPresent(ui -> ui.getPage().open("/" + LinkPreview.caminho(proposta.getToken()), "_blank"));
        });
        abrirTab.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        abrirTab.setTooltipText("Abrir em nova aba");

        Div acoesToolbar = new Div(whatsAppToolbar, exportarPdfToolbar, abrirTab);
        acoesToolbar.getStyle().set("display", "flex").set("gap", "0.5rem");

        toolbar.add(docTitle, acoesToolbar);

        Div paginaPdf = new Div(toolbar, preview);
        paginaPdf.addClassName("cz-pdf-page");

        Div bgPreview = new Div(paginaPdf);
        bgPreview.addClassName("cz-pdf-viewer");

        add(painelEsquerdo, bgPreview);

        carregar(new Proposta());
    }

    private void configurarCampos() {
        status.setItems(StatusProposta.values());
        status.setItemLabelGenerator(StatusProposta::getRotulo);
        recorrencia.setItems(Recorrencia.values());
        recorrencia.setItemLabelGenerator(Recorrencia::getRotulo);

        chamada.setHelperText("Frase curta exibida logo abaixo do título no preview");
        prazoExecucao.setPlaceholder("Ex.: 8 semanas");

        List.of(introducao, desafio, solucao, entregaveis, investimentoObs, condicoes, formaPagamento, lgpdTermos)
                .forEach(area -> {
                    area.setWidthFull();
                    area.setMaxLength(4000);
                    area.setMinHeight("120px");
                });

        // Endereço via ViaCEP
        cep.setPlaceholder("00000-000");
        cep.setHelperText("Digite o CEP para preenchimento automático");
        estado.setMaxLength(2);
        logradouro.setPlaceholder("Rua / Av.");
        numero.setPlaceholder("Nº");
        complemento.setPlaceholder("Apto, sala, bloco...");
        bairro.setPlaceholder("Bairro");
        cidade.setPlaceholder("Cidade");
        estado.setPlaceholder("UF");
    }

    private void vincular() {
        binder.forField(titulo).asRequired("Informe o título").bind(Proposta::getTitulo, Proposta::setTitulo);
        binder.forField(cliente).asRequired("Informe o cliente").bind(Proposta::getCliente, Proposta::setCliente);
        binder.forField(chamada).bind(Proposta::getChamada, Proposta::setChamada);
        binder.forField(empresa).bind(Proposta::getEmpresaCliente, Proposta::setEmpresaCliente);
        binder.forField(email).bind(Proposta::getEmailCliente, Proposta::setEmailCliente);
        binder.forField(contato).bind(Proposta::getContatoCliente, Proposta::setContatoCliente);
        binder.forField(segmento).bind(Proposta::getSegmento, Proposta::setSegmento);
        binder.forField(cep).bind(Proposta::getCep, Proposta::setCep);
        binder.forField(logradouro).bind(Proposta::getLogradouro, Proposta::setLogradouro);
        binder.forField(numero).bind(Proposta::getNumero, Proposta::setNumero);
        binder.forField(complemento).bind(Proposta::getComplemento, Proposta::setComplemento);
        binder.forField(bairro).bind(Proposta::getBairro, Proposta::setBairro);
        binder.forField(cidade).bind(Proposta::getCidade, Proposta::setCidade);
        binder.forField(estado).bind(Proposta::getEstado, Proposta::setEstado);
        binder.forField(validaAte).bind(Proposta::getValidaAte, Proposta::setValidaAte);
        binder.forField(prazoExecucao).bind(Proposta::getPrazoExecucao, Proposta::setPrazoExecucao);
        binder.forField(desconto)
                .withValidator(v -> v == null || (v.signum() >= 0 && v.compareTo(BigDecimal.valueOf(100)) <= 0),
                        "Desconto deve estar entre 0 e 100")
                .bind(Proposta::getDescontoPercentual, Proposta::setDescontoPercentual);
        binder.forField(recorrencia).bind(Proposta::getRecorrencia, Proposta::setRecorrencia);
        binder.forField(status).bind(Proposta::getStatus, Proposta::setStatus);
        binder.forField(introducao).bind(Proposta::getIntroducao, Proposta::setIntroducao);
        binder.forField(desafio).bind(Proposta::getDesafio, Proposta::setDesafio);
        binder.forField(solucao).bind(Proposta::getSolucao, Proposta::setSolucao);
        binder.forField(entregaveis).bind(Proposta::getEntregaveis, Proposta::setEntregaveis);
        binder.forField(investimentoObs).bind(Proposta::getInvestimentoObservacao,
                Proposta::setInvestimentoObservacao);
        binder.forField(condicoes).bind(Proposta::getCondicoes, Proposta::setCondicoes);
        binder.forField(formaPagamento).bind(Proposta::getFormaPagamento, Proposta::setFormaPagamento);
        binder.forField(lgpdTermos).bind(Proposta::getLgpdTermos, Proposta::setLgpdTermos);
        binder.forField(responsavel).bind(Proposta::getResponsavel, Proposta::setResponsavel);
        binder.forField(emailResponsavel).bind(Proposta::getEmailResponsavel, Proposta::setEmailResponsavel);
        binder.forField(telefoneResponsavel).bind(Proposta::getTelefoneResponsavel,
                Proposta::setTelefoneResponsavel);

        // Preview em tempo real a cada tecla digitada.
        List.of(titulo, chamada, cliente, empresa, contato, segmento, prazoExecucao, responsavel,
                telefoneResponsavel, cep, logradouro, numero, complemento, bairro, cidade, estado)
                .forEach(f -> f.setValueChangeMode(ValueChangeMode.EAGER));
        List.of(email, emailResponsavel).forEach(f -> f.setValueChangeMode(ValueChangeMode.EAGER));
        List.of(introducao, desafio, solucao, entregaveis, investimentoObs, condicoes, formaPagamento, lgpdTermos)
                .forEach(f -> f.setValueChangeMode(ValueChangeMode.EAGER));
        desconto.setValueChangeMode(ValueChangeMode.EAGER);

        // Consulta ViaCEP ao digitar 8 dígitos
        cep.addValueChangeListener(e -> {
            String valor = e.getValue();
            if (valor == null) return;
            String limpo = valor.replaceAll("\\D", "");
            if (limpo.length() == 8) {
                viaCep.consultar(limpo).ifPresentOrElse(resp -> {
                    logradouro.setValue(nz(resp.logradouro()));
                    complemento.setValue(nz(resp.complemento()));
                    bairro.setValue(nz(resp.bairro()));
                    cidade.setValue(nz(resp.localidade()));
                    estado.setValue(nz(resp.uf()));
                    atualizarPreview();
                    Notifier.sucesso("Endereço preenchido pelo CEP");
                }, () -> Notifier.erro("CEP não encontrado"));
            }
        });

        binder.addValueChangeListener(e -> atualizarPreview());
    }

    private Component cabecalho() {
        Div identidade = new Div(new Marca());
        identidade.getStyle().set("display", "flex").set("align-items", "center").set("gap", "1.25rem");

        HorizontalLayout barra = new HorizontalLayout(identidade, acoes());
        barra.setWidthFull();
        barra.setAlignItems(Alignment.CENTER);
        barra.setJustifyContentMode(JustifyContentMode.BETWEEN);
        return barra;
    }

    private Component acoes() {
        Button salvar = new Button("Salvar", VaadinIcon.CHECK.create(), e -> salvar());
        salvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button publicar = new Button("Publicar e copiar link", VaadinIcon.SHARE.create(), e -> publicar());

        Button abrirPreview = new Button("Abrir link", VaadinIcon.EXTERNAL_LINK.create(), e -> {
            if (proposta.getId() == null) {
                Notifier.aviso("Salve a proposta antes de abrir o link");
                return;
            }
            getUI().ifPresent(ui -> ui.getPage().open("/" + LinkPreview.caminho(proposta.getToken()), "_blank"));
        });

        Button voltar = new Button(VaadinIcon.ARROW_LEFT.create(),
                e -> getUI().ifPresent(ui -> ui.navigate(PropostasView.class)));
        voltar.setTooltipText("Voltar para a lista");

        return new HorizontalLayout(voltar, abrirPreview, publicar, salvar);
    }

    private Component abas() {
        TabSheet abas = new TabSheet();
        abas.setSizeFull();
        abas.add(new Tab("Cliente"), aba(secaoLogo(), formCliente()));
        abas.add(new Tab("Narrativa"), aba(introducao, desafio, solucao, entregaveis));
        abas.add(new Tab("Escopo"), aba(barraItens(), editorItens));
        abas.add(new Tab("Cronograma"), aba(barraFases(), editorFases));
        abas.add(new Tab("Investimento"), aba(formInvestimento(), investimentoObs, condicoes, formaPagamento));
        return abas;
    }

    private Component aba(Component... conteudo) {
        VerticalLayout layout = new VerticalLayout(conteudo);
        layout.setPadding(false);
        layout.setWidthFull();
        return layout;
    }

    private Component secaoLogo() {
        Upload upload = new Upload(UploadHandler.inMemory((metadata, data) -> {
            String base64 = Base64.getEncoder().encodeToString(data);
            String dataUrl = "data:" + metadata.contentType() + ";base64," + base64;
            proposta.setLogoData(dataUrl);
            logoPreviewImg.setSrc(dataUrl);
            logoPreviewImg.setVisible(true);
            btnRemoverLogo.setVisible(true);
            atualizarPreview();
            Notifier.sucesso("Logo marca carregada!");
        }));
        upload.setAcceptedFileTypes("image/png", "image/jpeg", "image/jpg", "image/svg+xml", "image/webp");
        upload.setMaxFileSize(2 * 1024 * 1024);
        upload.setDropLabel(new Span("Arraste a logo marca ou clique para selecionar"));

        logoPreviewImg.setMaxHeight("50px");
        logoPreviewImg.setMaxWidth("180px");
        logoPreviewImg.getStyle().set("object-fit", "contain");

        btnRemoverLogo.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        btnRemoverLogo.addClickListener(e -> {
            Confirmador.confirmar(
                    "Remover logo",
                    "Deseja remover a logo marca da proposta?",
                    "Remover",
                    () -> {
                        proposta.setLogoData(null);
                        logoPreviewImg.setVisible(false);
                        logoPreviewImg.setSrc("");
                        btnRemoverLogo.setVisible(false);
                        upload.getElement().executeJs("this.files = []");
                        atualizarPreview();
                    });
        });

        Span labelLogo = new Span("Logo Marca da Empresa");
        labelLogo.addClassName("cz-eyebrow");

        HorizontalLayout prevLayout = new HorizontalLayout(logoPreviewImg, btnRemoverLogo);
        prevLayout.setAlignItems(Alignment.CENTER);

        VerticalLayout wrapper = new VerticalLayout(labelLogo, upload, prevLayout);
        wrapper.setPadding(false);
        wrapper.setSpacing(true);

        Div card = new Div(wrapper);
        card.addClassName("cz-card");
        card.getStyle().set("margin-bottom", "1.25rem");
        return card;
    }

    private FormLayout formCliente() {
        FormLayout form = new FormLayout(titulo, chamada, cliente, empresa, email, contato, segmento,
                cep, logradouro, numero, complemento, bairro, cidade, estado,
                validaAte, prazoExecucao, status, responsavel, emailResponsavel, telefoneResponsavel);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("520px", 2));
        form.setColspan(titulo, 2);
        form.setColspan(chamada, 2);
        form.setColspan(logradouro, 2);
        form.setColspan(complemento, 2);
        return form;
    }

    private FormLayout formInvestimento() {
        FormLayout form = new FormLayout(desconto, recorrencia);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("520px", 2));
        return form;
    }

    /* ---------------------------------------------------------------- itens */

    private Component barraItens() {
        Button adicionar = new Button("Serviço customizado", VaadinIcon.PLUS.create(), e -> {
            ItemProposta item = new ItemProposta("Novo serviço", BigDecimal.ONE, "un", BigDecimal.ZERO);
            itens.add(item);
            redesenharItens();
            atualizarPreview();
        });
        adicionar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        ComboBox<ServicoPadrao> selectCatalogo = new ComboBox<>();
        selectCatalogo.setPlaceholder("Adicionar do Catálogo Clarituz...");
        selectCatalogo.setItems(ServicoPadrao.CATALOGO);
        selectCatalogo.setItemLabelGenerator(s -> s.numero() + ". " + s.titulo());
        selectCatalogo.setWidth("320px");
        selectCatalogo.addValueChangeListener(e -> {
            ServicoPadrao s = e.getValue();
            if (s != null) {
                ItemProposta item = new ItemProposta(s.titulo(), BigDecimal.ONE, "un", BigDecimal.ZERO);
                item.setCategoria(s.categoria());
                item.setDetalhe(s.detalhe());
                itens.add(item);
                redesenharItens();
                atualizarPreview();
                selectCatalogo.clear();
                Notifier.sucesso("Serviço '" + s.titulo() + "' adicionado!");
            }
        });

        Button addTodos = new Button("Importar 9 Serviços Clarituz", VaadinIcon.MAGIC.create(), e -> {
            for (ServicoPadrao s : ServicoPadrao.CATALOGO) {
                ItemProposta item = new ItemProposta(s.titulo(), BigDecimal.ONE, "un", BigDecimal.ZERO);
                item.setCategoria(s.categoria());
                item.setDetalhe(s.detalhe());
                itens.add(item);
            }
            redesenharItens();
            atualizarPreview();
            Notifier.sucesso("Os 9 serviços da Clarituz foram importados!");
        });
        addTodos.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout barra = new HorizontalLayout(adicionar, selectCatalogo, addTodos);
        barra.setAlignItems(Alignment.CENTER);
        barra.setWidthFull();
        return barra;
    }

    private void redesenharItens() {
        editorItens.removeAll();
        editorItens.setWidthFull();
        for (ItemProposta item : itens) {
            editorItens.add(cardItem(item));
        }
    }

    private Component cardItem(ItemProposta item) {
        TextField descricao = new TextField("Serviço");
        descricao.setValue(nz(item.getDescricao()));
        ligar(descricao, v -> item.setDescricao(v));

        ComboBox<CategoriaServico> categoria = new ComboBox<>("Categoria");
        categoria.setItems(CategoriaServico.values());
        categoria.setItemLabelGenerator(CategoriaServico::getRotulo);
        categoria.setValue(item.getCategoria());
        categoria.addValueChangeListener(e -> {
            item.setCategoria(e.getValue());
            atualizarPreview();
        });

        TextArea detalhe = new TextArea("Detalhamento");
        detalhe.setValue(nz(item.getDetalhe()));
        detalhe.setWidthFull();
        ligar(detalhe, v -> item.setDetalhe(v));

        BigDecimalField quantidade = new BigDecimalField("Qtd");
        quantidade.setValue(item.getQuantidade());
        ligar(quantidade, v -> item.setQuantidade(v));

        TextField unidade = new TextField("Unidade");
        unidade.setValue(nz(item.getUnidade()));
        ligar(unidade, v -> item.setUnidade(v));

        BigDecimalField valor = new BigDecimalField("Valor unitário");
        valor.setValue(item.getValorUnitario());
        ligar(valor, v -> item.setValorUnitario(v));

        Button remover = new Button(VaadinIcon.TRASH.create(), e -> {
            Confirmador.excluir(
                    "Remover serviço",
                    "Remover \"" + nz(item.getDescricao()) + "\" da proposta?",
                    () -> {
                        itens.remove(item);
                        redesenharItens();
                        atualizarPreview();
                    });
        });
        remover.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

        FormLayout form = new FormLayout(descricao, categoria, quantidade, unidade, valor);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("520px", 3));
        form.setColspan(descricao, 2);

        Div card = new Div(cabecalhoCard("Serviço", remover), form, detalhe);
        card.addClassName("cz-card");
        card.getStyle().set("margin-bottom", "0.85rem");
        return card;
    }

    /* ---------------------------------------------------------------- fases */

    private Component barraFases() {
        Button adicionar = new Button("Adicionar fase", VaadinIcon.PLUS.create(), e -> {
            fases.add(new FaseProposta("Nova fase", "", ""));
            redesenharFases();
            atualizarPreview();
        });
        adicionar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return adicionar;
    }

    private void redesenharFases() {
        editorFases.removeAll();
        editorFases.setWidthFull();
        for (FaseProposta fase : fases) {
            editorFases.add(cardFase(fase));
        }
    }

    private Component cardFase(FaseProposta fase) {
        TextField nome = new TextField("Fase");
        nome.setValue(nz(fase.getNome()));
        ligar(nome, v -> fase.setNome(v));

        TextField prazo = new TextField("Prazo");
        prazo.setPlaceholder("Ex.: Semanas 1-2");
        prazo.setValue(nz(fase.getPrazo()));
        ligar(prazo, v -> fase.setPrazo(v));

        TextArea descricao = new TextArea("Descrição");
        descricao.setWidthFull();
        descricao.setValue(nz(fase.getDescricao()));
        ligar(descricao, v -> fase.setDescricao(v));

        Button remover = new Button(VaadinIcon.TRASH.create(), e -> {
            Confirmador.excluir(
                    "Remover fase",
                    "Remover \"" + nz(fase.getNome()) + "\" do cronograma?",
                    () -> {
                        fases.remove(fase);
                        redesenharFases();
                        atualizarPreview();
                    });
        });
        remover.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

        FormLayout form = new FormLayout(nome, prazo);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("520px", 2));

        Div card = new Div(cabecalhoCard("Fase", remover), form, descricao);
        card.addClassName("cz-card");
        card.getStyle().set("margin-bottom", "0.85rem");
        return card;
    }

    private Component cabecalhoCard(String rotulo, Button acao) {
        Span titulo = new Span(rotulo);
        titulo.addClassName("cz-eyebrow");
        Div cabecalho = new Div(titulo, acao);
        cabecalho.getStyle().set("display", "flex").set("justify-content", "space-between")
                .set("align-items", "center");
        return cabecalho;
    }

    /** Aplica o valor no modelo a cada tecla e reflete no preview. */
    private <V> void ligar(HasValue<?, V> campo, Consumer<V> setter) {
        if (campo instanceof HasValueChangeMode modo) {
            modo.setValueChangeMode(ValueChangeMode.EAGER);
        }
        campo.addValueChangeListener(e -> {
            setter.accept(e.getValue());
            atualizarPreview();
        });
    }

    /* -------------------------------------------------------------- preview */

    private void atualizarPreview() {
        Proposta snapshot = new Proposta();
        binder.writeBeanAsDraft(snapshot, true);
        snapshot.setLogoData(proposta.getLogoData());
        snapshot.substituirItens(new ArrayList<>(itens));
        snapshot.substituirFases(new ArrayList<>(fases));
        preview.render(snapshot);
    }

    /* -------------------------------------------------------- persistência */

    private boolean salvar() {
        try {
            binder.writeBean(proposta);
        } catch (ValidationException ex) {
            Notifier.erro("Verifique os campos obrigatórios");
            return false;
        }
        proposta.substituirItens(new ArrayList<>(itens));
        proposta.substituirFases(new ArrayList<>(fases));
        proposta = service.salvar(proposta);
        carregar(proposta);
        Notifier.sucesso("Proposta salva");
        return true;
    }

    private void publicar() {
        Confirmador.confirmar(
                "Publicar proposta",
                "Ao publicar, a proposta ficará acessível pelo link público e o cliente poderá visualizá-la. Deseja continuar?",
                "Publicar e copiar link",
                () -> {
                    status.setValue(StatusProposta.PUBLICADA);
                    if (!salvar()) {
                        return;
                    }
                    getElement().executeJs(
                            "const u = location.origin + '/' + $0; navigator.clipboard.writeText(u); return u;",
                            LinkPreview.caminho(proposta.getToken()))
                            .then(String.class, url -> Notifier.info("Link copiado: " + url));
                });
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        if (parameter == null || "novo".equals(parameter)) {
            carregar(new Proposta());
            return;
        }
        try {
            service.buscarPorId(Long.valueOf(parameter)).ifPresentOrElse(this::carregar, () -> {
                Notifier.erro("Proposta não encontrada");
                event.forwardTo(PropostasView.class);
            });
        } catch (NumberFormatException ex) {
            event.forwardTo(PropostasView.class);
        }
    }

    private void carregar(Proposta p) {
        this.proposta = p;
        binder.readBean(p);
        itens.clear();
        itens.addAll(p.getItens());
        fases.clear();
        fases.addAll(p.getFases());

        if (p.getLogoData() != null && !p.getLogoData().isBlank()) {
            logoPreviewImg.setSrc(p.getLogoData());
            logoPreviewImg.setVisible(true);
            btnRemoverLogo.setVisible(true);
        } else {
            logoPreviewImg.setVisible(false);
            logoPreviewImg.setSrc("");
            btnRemoverLogo.setVisible(false);
        }

        redesenharItens();
        redesenharFases();
        atualizarPreview();
    }

    private static String nz(String valor) {
        return valor == null ? "" : valor;
    }
}
