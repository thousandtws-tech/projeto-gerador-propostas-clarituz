package com.clarituz.application.proposta;

import com.clarituz.application.proposta.enums.Recorrencia;
import com.clarituz.application.proposta.enums.StatusProposta;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Proposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false, length = 36)
    private String token = UUID.randomUUID().toString();

    @NotBlank
    @Column(nullable = false)
    private String titulo = "";

    @NotBlank
    @Column(nullable = false)
    private String cliente = "";

    @Email
    private String emailCliente;

    private String empresaCliente;

    private String contatoCliente;

    private String segmento;

    /** Endereço do cliente (preenchido via ViaCEP). */
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;

    /** Frase curta exibida em destaque no topo do preview. */
    private String chamada;

    /** Imagem da logo em Base64 Data URL. */
    @Column(columnDefinition = "TEXT")
    private String logoData;

    @Column(length = 4000)
    private String introducao;

    @Column(length = 4000)
    private String desafio;

    @Column(length = 4000)
    private String solucao;

    @Column(length = 4000)
    private String entregaveis;

    @Column(length = 4000)
    private String investimentoObservacao;

    @Column(length = 4000)
    private String condicoes;

    @Column(length = 2000)
    private String formaPagamento;

    @Column(length = 4000)
    private String lgpdTermos = "Esta proposta e as informações contidas neste documento são confidenciais e protegidas pela Lei Geral de Proteção de Dados (Lei nº 13.709/2018 - LGPD). Os dados fornecidos serão tratados exclusivamente para fins de análise comercial e execução dos serviços propostos, sendo vedada qualquer divulgação a terceiros sem autorização prévia.";

    private String prazoExecucao;

    private String responsavel;

    private String emailResponsavel;

    private String telefoneResponsavel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Recorrencia recorrencia = Recorrencia.UNICA;

    private LocalDate validaAte;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal descontoPercentual = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusProposta status = StatusProposta.RASCUNHO;

    @Column(nullable = false, updatable = false)
    private Instant criadaEm = Instant.now();

    @Column(nullable = false)
    private Instant atualizadaEm = Instant.now();

    private Instant visualizadaEm;

    // LAZY para evitar MultipleBagFetchException; o serviço inicializa dentro da transação.
    @OneToMany(mappedBy = "proposta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("posicao")
    private List<ItemProposta> itens = new ArrayList<>();

    @OneToMany(mappedBy = "proposta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("posicao")
    private List<FaseProposta> fases = new ArrayList<>();

    public BigDecimal getSubtotal() {
        return itens.stream()
                .map(ItemProposta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getValorDesconto() {
        BigDecimal percentual = descontoPercentual == null ? BigDecimal.ZERO : descontoPercentual;
        return getSubtotal()
                .multiply(percentual)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotal() {
        return getSubtotal().subtract(getValorDesconto()).setScale(2, RoundingMode.HALF_UP);
    }

    public void adicionarItem(ItemProposta item) {
        item.setProposta(this);
        item.setPosicao(itens.size());
        itens.add(item);
    }

    public void removerItem(ItemProposta item) {
        itens.remove(item);
        item.setProposta(null);
        reindexarItens();
    }

    public void substituirItens(List<ItemProposta> novosItens) {
        itens.forEach(i -> i.setProposta(null));
        itens.clear();
        novosItens.forEach(this::adicionarItem);
    }

    public void substituirFases(List<FaseProposta> novasFases) {
        fases.forEach(f -> f.setProposta(null));
        fases.clear();
        for (FaseProposta fase : novasFases) {
            fase.setProposta(this);
            fase.setPosicao(fases.size());
            fases.add(fase);
        }
    }

    private void reindexarItens() {
        for (int i = 0; i < itens.size(); i++) {
            itens.get(i).setPosicao(i);
        }
    }

    public List<FaseProposta> getFases() {
        return fases;
    }

    public String getEmpresaCliente() {
        return empresaCliente;
    }

    public void setEmpresaCliente(String empresaCliente) {
        this.empresaCliente = empresaCliente;
    }

    public String getContatoCliente() {
        return contatoCliente;
    }

    public void setContatoCliente(String contatoCliente) {
        this.contatoCliente = contatoCliente;
    }

    public String getSegmento() {
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    /** Endereço resumido em uma linha para o meta do preview. */
    public String getEnderecoFormatado() {
        StringBuilder sb = new StringBuilder();
        if (logradouro != null && !logradouro.isBlank()) {
            sb.append(logradouro);
        }
        if (numero != null && !numero.isBlank()) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(numero);
        }
        if (bairro != null && !bairro.isBlank()) {
            if (!sb.isEmpty()) sb.append(" — ");
            sb.append(bairro);
        }
        if (cidade != null && !cidade.isBlank()) {
            if (!sb.isEmpty()) sb.append(" · ");
            sb.append(cidade);
        }
        if (estado != null && !estado.isBlank()) {
            if (!sb.isEmpty()) sb.append("/").append(estado);
        }
        return sb.toString();
    }

    public String getChamada() {
        return chamada;
    }

    public void setChamada(String chamada) {
        this.chamada = chamada;
    }

    public String getLogoData() {
        return logoData;
    }

    public void setLogoData(String logoData) {
        this.logoData = logoData;
    }

    public String getDesafio() {
        return desafio;
    }

    public void setDesafio(String desafio) {
        this.desafio = desafio;
    }

    public String getSolucao() {
        return solucao;
    }

    public void setSolucao(String solucao) {
        this.solucao = solucao;
    }

    public String getEntregaveis() {
        return entregaveis;
    }

    public void setEntregaveis(String entregaveis) {
        this.entregaveis = entregaveis;
    }

    public String getInvestimentoObservacao() {
        return investimentoObservacao;
    }

    public void setInvestimentoObservacao(String investimentoObservacao) {
        this.investimentoObservacao = investimentoObservacao;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getLgpdTermos() {
        return lgpdTermos;
    }

    public void setLgpdTermos(String lgpdTermos) {
        this.lgpdTermos = lgpdTermos;
    }

    public String getPrazoExecucao() {
        return prazoExecucao;
    }

    public void setPrazoExecucao(String prazoExecucao) {
        this.prazoExecucao = prazoExecucao;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getEmailResponsavel() {
        return emailResponsavel;
    }

    public void setEmailResponsavel(String emailResponsavel) {
        this.emailResponsavel = emailResponsavel;
    }

    public String getTelefoneResponsavel() {
        return telefoneResponsavel;
    }

    public void setTelefoneResponsavel(String telefoneResponsavel) {
        this.telefoneResponsavel = telefoneResponsavel;
    }

    public Recorrencia getRecorrencia() {
        return recorrencia;
    }

    public void setRecorrencia(Recorrencia recorrencia) {
        this.recorrencia = recorrencia == null ? Recorrencia.UNICA : recorrencia;
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }

    public String getIntroducao() {
        return introducao;
    }

    public void setIntroducao(String introducao) {
        this.introducao = introducao;
    }

    public String getCondicoes() {
        return condicoes;
    }

    public void setCondicoes(String condicoes) {
        this.condicoes = condicoes;
    }

    public LocalDate getValidaAte() {
        return validaAte;
    }

    public void setValidaAte(LocalDate validaAte) {
        this.validaAte = validaAte;
    }

    public BigDecimal getDescontoPercentual() {
        return descontoPercentual;
    }

    public void setDescontoPercentual(BigDecimal descontoPercentual) {
        this.descontoPercentual = descontoPercentual == null ? BigDecimal.ZERO : descontoPercentual;
    }

    public StatusProposta getStatus() {
        return status;
    }

    public void setStatus(StatusProposta status) {
        this.status = status;
    }

    public Instant getCriadaEm() {
        return criadaEm;
    }

    public Instant getAtualizadaEm() {
        return atualizadaEm;
    }

    public Instant getVisualizadaEm() {
        return visualizadaEm;
    }

    public void setVisualizadaEm(Instant visualizadaEm) {
        this.visualizadaEm = visualizadaEm;
    }

    public void marcarAtualizada() {
        this.atualizadaEm = Instant.now();
    }

    public List<ItemProposta> getItens() {
        return itens;
    }
}
