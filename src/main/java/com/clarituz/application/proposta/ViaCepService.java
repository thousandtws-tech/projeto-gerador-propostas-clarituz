package com.clarituz.application.proposta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Optional;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ViaCepService {

    private static final Logger log = LoggerFactory.getLogger(ViaCepService.class);
    private static final Pattern CEP_VALIDO = Pattern.compile("\\d{8}");
    private static final String URL = "https://viacep.com.br/ws/{cep}/json/";

    private final RestClient client;

    public ViaCepService() {
        this.client = RestClient.builder().baseUrl("https://viacep.com.br").build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ViaCepResponse(String cep, String logradouro, String complemento, String bairro,
            String localidade, String uf, String erro) {
    }

    /**
     * Consulta o endereço pelo CEP.
     *
     * @param cep CEP contendo apenas dígitos (8 caracteres).
     * @return Optional com o endereço encontrado, ou vazio se inválido/não encontrado.
     */
    public Optional<ViaCepResponse> consultar(String cep) {
        if (cep == null) {
            return Optional.empty();
        }
        String limpo = cep.replaceAll("\\D", "");
        if (!CEP_VALIDO.matcher(limpo).matches()) {
            return Optional.empty();
        }
        try {
            ViaCepResponse resp = client.get().uri("/ws/{cep}/json/", limpo).retrieve().body(ViaCepResponse.class);
            if (resp == null || resp.erro() != null) {
                return Optional.empty();
            }
            return Optional.of(resp);
        } catch (Exception e) {
            log.warn("Falha ao consultar ViaCEP para {}: {}", limpo, e.getMessage());
            return Optional.empty();
        }
    }
}
