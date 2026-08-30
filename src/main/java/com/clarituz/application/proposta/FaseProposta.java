package com.clarituz.application.proposta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class FaseProposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "proposta_id")
    private Proposta proposta;

    @Column(nullable = false)
    private int posicao;

    @Column(nullable = false)
    private String nome = "";

    @Column(length = 1000)
    private String descricao = "";

    @Column(nullable = false)
    private String prazo = "";


    public FaseProposta() {
    }

    public FaseProposta(String nome, String descricao, String prazo) {
        this.nome = nome;
        this.descricao = descricao;
        this.prazo = prazo;
    }

    public Long getId() {
        return id;
    }

    public Proposta getProposta() {
        return proposta;
    }

    void setProposta(Proposta proposta) {
        this.proposta = proposta;
    }

    void setPosicao(int posicao) {
        this.posicao = posicao;
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

    public String getPrazo() {
        return prazo;
    }

    public void setPrazo(String prazo) {
        this.prazo = prazo;
    }
}
