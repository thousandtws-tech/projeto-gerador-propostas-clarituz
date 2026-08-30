package com.clarituz.application.ia;

import java.math.BigDecimal;

public class IdeiaItemDto {

    private String nome;
    private String descricao;
    private double quantidade;
    private String unidade;
    private BigDecimal valorUnitario;

    public IdeiaItemDto(String nome, String descricao, double quantidade, String unidade, BigDecimal valorUnitario) {
        this.nome = nome;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.unidade = unidade;
        this.valorUnitario = valorUnitario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }
}
