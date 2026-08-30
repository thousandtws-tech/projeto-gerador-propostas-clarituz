package com.clarituz.application.proposta;

import com.clarituz.application.proposta.enums.CategoriaServico;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
public class ItemProposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "proposta_id")
    private Proposta proposta;

    @Column(nullable = false)
    private int posicao;

    @Column(nullable = false)
    private String descricao = "";

    @Column(length = 1000)
    private String detalhe = "";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategoriaServico categoria = CategoriaServico.DESENVOLVIMENTO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantidade = BigDecimal.ONE;

    @Column(nullable = false)
    private String unidade = "un";

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valorUnitario = BigDecimal.ZERO;

    public ItemProposta() {
    }

    public ItemProposta(String descricao, BigDecimal quantidade, String unidade, BigDecimal valorUnitario) {
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.unidade = unidade;
        this.valorUnitario = valorUnitario;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public void setDetalhe(String detalhe) {
        this.detalhe = detalhe;
    }

    public CategoriaServico getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaServico categoria) {
        this.categoria = categoria == null ? CategoriaServico.DESENVOLVIMENTO : categoria;
    }

    public BigDecimal getTotal() {
        BigDecimal qtd = quantidade == null ? BigDecimal.ZERO : quantidade;
        BigDecimal valor = valorUnitario == null ? BigDecimal.ZERO : valorUnitario;
        return qtd.multiply(valor).setScale(2, RoundingMode.HALF_UP);
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade == null ? BigDecimal.ZERO : quantidade;
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
        this.valorUnitario = valorUnitario == null ? BigDecimal.ZERO : valorUnitario;
    }
}
