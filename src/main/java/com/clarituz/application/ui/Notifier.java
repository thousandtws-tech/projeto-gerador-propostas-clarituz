package com.clarituz.application.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * Sistema centralizado de notificações com tipos visuais (sucesso, erro, aviso, info).
 * <p>
 * Cada tipo define ícone, cor, duração e posição automáticos. As notificações
 * aparecem no topo central da tela e desaparecem sozinhas.
 */
public final class Notifier {

    public enum Tipo {
        SUCESSO(VaadinIcon.CHECK_CIRCLE, NotificationVariant.LUMO_SUCCESS, 3000),
        ERRO(VaadinIcon.EXCLAMATION_CIRCLE, NotificationVariant.LUMO_ERROR, 5000),
        AVISO(VaadinIcon.WARNING, NotificationVariant.LUMO_CONTRAST, 4000),
        INFO(VaadinIcon.INFO_CIRCLE, null, 3000);

        private final VaadinIcon icone;
        private final NotificationVariant variante;
        private final int duracaoMs;

        Tipo(VaadinIcon icone, NotificationVariant variante, int duracaoMs) {
            this.icone = icone;
            this.variante = variante;
            this.duracaoMs = duracaoMs;
        }
    }

    private Notifier() {
    }

    // ---- Atalhos estáticos ----

    public static void sucesso(String mensagem) {
        mostrar(mensagem, Tipo.SUCESSO);
    }

    public static void erro(String mensagem) {
        mostrar(mensagem, Tipo.ERRO);
    }

    public static void aviso(String mensagem) {
        mostrar(mensagem, Tipo.AVISO);
    }

    public static void info(String mensagem) {
        mostrar(mensagem, Tipo.INFO);
    }

    // ---- Implementação ----

    public static void mostrar(String mensagem, Tipo tipo) {
        UI ui = UI.getCurrent();
        if (ui == null) {
            return;
        }
        ui.access(() -> render(mensagem, tipo));
    }

    private static void render(String mensagem, Tipo tipo) {
        Icon icone = tipo.icone.create();
        icone.addClassName("cz-notif-icon");

        Span texto = new Span(mensagem);
        texto.addClassName("cz-notif-text");

        Div conteudo = new Div(icone, texto);
        conteudo.addClassName("cz-notif");

        Notification notification = new Notification(conteudo);
        notification.setPosition(Notification.Position.TOP_END);
        notification.setDuration(tipo.duracaoMs);
        if (tipo.variante != null) {
            notification.addThemeVariants(tipo.variante);
        }
        notification.open();
    }
}
