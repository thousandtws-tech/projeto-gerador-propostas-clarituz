package com.clarituz.application.proposta.api;

import com.clarituz.application.proposta.Proposta;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PropostaDto(
        String token,
        String titulo,
        String chamada,
        String cliente,
        String empresaCliente,
        String segmento,
        String introducao,
        String desafio,
        String solucao,
        String entregaveis,
        String condicoes,
        String formaPagamento,
        String investimentoObservacao,
        String prazoExecucao,
        String responsavel,
        LocalDate validaAte,
        String status,
        String recorrencia,
        BigDecimal subtotal,
        BigDecimal descontoPercentual,
        BigDecimal valorDesconto,
        BigDecimal total,
        List<ItemDto> itens,
        List<FaseDto> fases) {

    public record ItemDto(String descricao, String detalhe, String categoria, BigDecimal quantidade, String unidade,
            BigDecimal valorUnitario, BigDecimal total) {
    }

    public record FaseDto(String nome, String descricao, String prazo) {
    }

    public static PropostaDto from(Proposta p) {
        List<ItemDto> itens = p.getItens().stream()
                .map(i -> new ItemDto(i.getDescricao(), i.getDetalhe(), i.getCategoria().getRotulo(),
                        i.getQuantidade(), i.getUnidade(), i.getValorUnitario(), i.getTotal()))
                .toList();
        List<FaseDto> fases = p.getFases().stream()
                .map(f -> new FaseDto(f.getNome(), f.getDescricao(), f.getPrazo()))
                .toList();
        return new PropostaDto(p.getToken(), p.getTitulo(), p.getChamada(), p.getCliente(), p.getEmpresaCliente(),
                p.getSegmento(), p.getIntroducao(), p.getDesafio(), p.getSolucao(), p.getEntregaveis(),
                p.getCondicoes(), p.getFormaPagamento(), p.getInvestimentoObservacao(), p.getPrazoExecucao(),
                p.getResponsavel(), p.getValidaAte(), p.getStatus().name(), p.getRecorrencia().getRotulo(),
                p.getSubtotal(), p.getDescontoPercentual(), p.getValorDesconto(), p.getTotal(), itens, fases);
    }
}
