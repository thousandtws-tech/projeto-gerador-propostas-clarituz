package com.clarituz.application.ui;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Formatos {

    private static final Locale BR = Locale.of("pt", "BR");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Formatos() {
    }

    public static String moeda(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance(BR).format(valor == null ? BigDecimal.ZERO : valor);
    }

    public static String data(LocalDate data) {
        return data == null ? "-" : DATA.format(data);
    }
}
