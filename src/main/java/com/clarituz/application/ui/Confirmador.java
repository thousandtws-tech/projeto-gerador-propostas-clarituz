package com.clarituz.application.ui;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import java.util.function.Consumer;

/**
 * Sistema centralizado de diálogos de confirmação baseado no ConfirmDialog do Vaadin.
 * <p>
 * Oferece atalhos estáticos para os cenários mais comuns: confirmar uma ação
 * destrutiva (excluir), confirmar uma ação importante (publicar) e avisar sobre
 * alterações não salvas.
 */
public final class Confirmador {

    private Confirmador() {
    }

    /**
     * Confirmação padrão com botões Confirmar e Cancelar.
     *
     * @param titulo   título do diálogo
     * @param mensagem texto explicativo
     * @param textoConfirmar rótulo do botão de confirmação
     * @param onConfirm callback executado ao confirmar
     */
    public static void confirmar(String titulo, String mensagem, String textoConfirmar, Runnable onConfirm) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader(titulo);
        dialog.setText(mensagem);
        dialog.setConfirmText(textoConfirmar);
        dialog.setCancelable(true);
        dialog.setCancelText("Cancelar");
        dialog.addConfirmListener(e -> onConfirm.run());
        dialog.open();
    }

    /**
     * Confirmação de ação destrutiva (excluir) com botão vermelho.
     *
     * @param titulo   título do diálogo
     * @param mensagem texto explicativo
     * @param onConfirm callback executado ao confirmar a exclusão
     */
    public static void excluir(String titulo, String mensagem, Runnable onConfirm) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader(titulo);
        dialog.setText(mensagem);
        dialog.setConfirmText("Excluir");
        dialog.setConfirmButtonTheme("error primary");
        dialog.setCancelable(true);
        dialog.setCancelText("Cancelar");
        dialog.addConfirmListener(e -> onConfirm.run());
        dialog.open();
    }

    /**
     * Confirmação com três opções: confirmar, rejeitar (descartar) e cancelar.
     *
     * @param titulo        título do diálogo
     * @param mensagem      texto explicativo
     * @param textoConfirmar rótulo do botão confirmar
     * @param textoRejeitar  rótulo do botão rejeitar/descartar
     * @param onConfirm     callback ao confirmar
     * @param onRejeitar    callback ao rejeitar
     */
    public static void escolher(String titulo, String mensagem, String textoConfirmar, String textoRejeitar,
            Runnable onConfirm, Runnable onRejeitar) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader(titulo);
        dialog.setText(mensagem);
        dialog.setConfirmText(textoConfirmar);
        dialog.setCancelable(true);
        dialog.setCancelText("Cancelar");
        dialog.setRejectable(true);
        dialog.setRejectText(textoRejeitar);
        dialog.addConfirmListener(e -> onConfirm.run());
        dialog.addRejectListener(e -> onRejeitar.run());
        dialog.open();
    }
}
