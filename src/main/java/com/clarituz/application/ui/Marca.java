package com.clarituz.application.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;

public class Marca extends Div {

    public Marca() {
        addClassName("cz-brand");

        Image logoImg = new Image("Clarituz-V4%201.png", "Clarituz Agência");
        logoImg.addClassName("cz-brand-logo-img");
        logoImg.setMaxHeight("10rem");
        logoImg.getStyle().set("object-fit", "contain").set("display", "block");

        add(logoImg);
    }
}
