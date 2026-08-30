package com.clarituz.application.ia;

import com.clarituz.application.proposta.Proposta;
import com.clarituz.application.proposta.PropostaService;
import com.clarituz.application.proposta.enums.StatusProposta;
import dev.langchain4j.model.chat.ChatModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IaService {

    private final ChatModel model;
    private final PropostaService propostaService;

    public IaService(ChatModel model, PropostaService propostaService) {
        this.model = model;
        this.propostaService = propostaService;
    }

    public String otimizarTexto(String secao, String texto, String contexto) {
        String prompt = String.format("""
                Você é um especialista em redação comercial para uma agência de marketing.
                Reescreva o trecho abaixo da seção "%s" de uma proposta comercial.
                Mantenha tom profissional, objetivo e persuasivo em português do Brasil.
                Não invente dados fora do contexto fornecido. Preserve o mesmo tamanho aproximado.

                Contexto da proposta: %s

                Trecho:
                %s
                """, secao, contexto, texto);
        return generate(prompt);
    }

    public String sugerirChamada(Proposta proposta) {
        String prompt = String.format("""
                Crie uma frase curta e impactante (máximo 15 palavras) para ser a chamada de destaque
                de uma proposta comercial da Clarituz, uma agência de marketing.

                Cliente: %s
                Segmento: %s
                Título da proposta: %s
                """, proposta.getCliente(), proposta.getSegmento(), proposta.getTitulo());
        return generate(prompt);
    }

    public List<IdeiaItemDto> gerarIdeiasItens(String cliente, String segmento, String objetivo) {
        String prompt = String.format("""
                Você é um consultor de marketing. Sugira itens/serviços para uma proposta comercial.
                Cliente: %s
                Segmento: %s
                Objetivo: %s

                Responda apenas em formato de lista, uma linha por item, no seguinte formato:
                NOME | DESCRIÇÃO | QUANTIDADE | UNIDADE | VALOR UNITÁRIO

                Quantidade e valor devem ser numéricos. Sugira de 3 a 8 itens realistas.
                Não adicione cabeçalho, bullets ou explicações. Apenas as linhas.
                """, cliente, segmento, objetivo);
        String resposta = generate(prompt);
        return parseIdeias(resposta);
    }

    @Transactional(readOnly = true)
    public String analisarPortifolio() {
        List<Proposta> propostas = propostaService.listar(null);
        long total = propostas.size();
        long aceitas = propostas.stream().filter(p -> p.getStatus() == StatusProposta.ACEITA).count();
        long publicadas = propostas.stream().filter(p -> p.getStatus() == StatusProposta.PUBLICADA).count();
        BigDecimal somaTotal = propostas.stream()
                .map(Proposta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String prompt = String.format("""
                Você é um diretor comercial. Analise o seguinte cenário de propostas comerciais:
                - Total de propostas: %d
                - Aceitas: %d
                - Publicadas/em andamento: %d
                - Valor total em propostas: R$ %s

                Dê 3 insights objetivos e 3 recomendações práticas para aumentar conversão,
                em português. Use frases diretas e acionáveis.
                """, total, aceitas, publicadas, somaTotal);
        return generate(prompt);
    }

    public String monitorar(String pergunta) {
        String prompt = String.format("""
                Você é um assistente de monitoramento comercial. Responda com base em boas práticas.
                Pergunta: %s

                Dê uma resposta objetiva em português e indique se dados adicionais seriam necessários.
                """, pergunta);
        return generate(prompt);
    }

    private String generate(String prompt) {
        return model.chat(prompt);
    }

    private List<IdeiaItemDto> parseIdeias(String texto) {
        List<IdeiaItemDto> ideias = new ArrayList<>();
        for (String linha : texto.split("\n")) {
            String[] partes = linha.trim().split("\\|");
            if (partes.length >= 5) {
                try {
                    String nome = partes[0].trim();
                    String descricao = partes[1].trim();
                    double quantidade = Double.parseDouble(partes[2].trim().replace(",", "."));
                    String unidade = partes[3].trim();
                    BigDecimal valor = new BigDecimal(partes[4].trim().replace("R$", "").replace(".", "").replace(",", ".").trim());
                    ideias.add(new IdeiaItemDto(nome, descricao, quantidade, unidade, valor));
                } catch (Exception ignored) {
                    // ignora linhas mal formatadas
                }
            }
        }
        return ideias;
    }
}
