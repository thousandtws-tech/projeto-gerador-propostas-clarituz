package com.clarituz.application.proposta.api;

import com.clarituz.application.proposta.PropostaService;
import com.clarituz.application.proposta.enums.StatusProposta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/propostas")
public class PropostaPublicaController {

    private final PropostaService service;

    public PropostaPublicaController(PropostaService service) {
        this.service = service;
    }

    @GetMapping("/{token}")
    public ResponseEntity<PropostaDto> obter(@PathVariable String token) {
        return service.buscarPorToken(token)
                .filter(p -> p.getStatus() != StatusProposta.RASCUNHO)
                .map(PropostaDto::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{token}/aceitar")
    public ResponseEntity<PropostaDto> aceitar(@PathVariable String token) {
        return responder(token, true);
    }

    @PostMapping("/{token}/recusar")
    public ResponseEntity<PropostaDto> recusar(@PathVariable String token) {
        return responder(token, false);
    }

    private ResponseEntity<PropostaDto> responder(String token, boolean aceita) {
        return service.responderPorToken(token, aceita)
                .filter(p -> p.getStatus() != StatusProposta.RASCUNHO)
                .map(PropostaDto::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
