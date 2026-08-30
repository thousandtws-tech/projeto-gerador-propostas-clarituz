package com.clarituz.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Inline;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.lumo.Lumo;

@SpringBootApplication
@PWA(name = "Clarituz · Gerador de Propostas", shortName = "Clarituz")
@StyleSheet(Lumo.STYLESHEET)
@StyleSheet("styles.css")
public class Application implements AppShellConfigurator {

    @Override
    public void configurePage(AppShellSettings settings) {
        settings.addInlineWithContents(
            "<link rel='preconnect' href='https://fonts.googleapis.com'>" +
            "<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>" +
            "<link rel='stylesheet' href='https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap'>" +
            "<style>" +
            "html, body, :root, :host, *, *::before, *::after { " +
            "  scrollbar-width: none !important; " +
            "  -ms-overflow-style: none !important; " +
            "} " +
            "::-webkit-scrollbar, *::-webkit-scrollbar, html::-webkit-scrollbar, body::-webkit-scrollbar, :host::-webkit-scrollbar, ::-webkit-scrollbar-track, ::-webkit-scrollbar-thumb, ::-webkit-scrollbar-corner { " +
            "  display: none !important; " +
            "  width: 0 !important; " +
            "  height: 0 !important; " +
            "  background: transparent !important; " +
            "  opacity: 0 !important; " +
            "  visibility: hidden !important; " +
            "} " +
            "</style>",
            Inline.Wrapping.AUTOMATIC
        );
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
