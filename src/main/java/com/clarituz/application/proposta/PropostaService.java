package com.clarituz.application.proposta;

import java.util.List;
import java.util.Optional;

import com.clarituz.application.proposta.enums.StatusProposta;
import com.clarituz.application.proposta.repository.PropostaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PropostaService {

    private final PropostaRepository repository;

    public PropostaService(PropostaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Proposta> listar(String filtro) {
        List<Proposta> resultado = (filtro == null || filtro.isBlank())
                ? repository.findAllByOrderByAtualizadaEmDesc()
                : repository.findByTituloContainingIgnoreCaseOrClienteContainingIgnoreCaseOrderByAtualizadaEmDesc(
                        filtro, filtro);
        resultado.forEach(PropostaService::inicializar);
        return resultado;
    }

    @Transactional(readOnly = true)
    public Optional<Proposta> buscarPorId(Long id) {
        return repository.findById(id).map(PropostaService::inicializar);
    }

    @Transactional(readOnly = true)
    public Optional<Proposta> buscarPorToken(String token) {
        return repository.findByToken(token).map(PropostaService::inicializar);
    }

    @Transactional
    public Proposta salvar(Proposta proposta) {
        proposta.marcarAtualizada();
        return inicializar(repository.save(proposta));
    }

    /** Carrega as coleções LAZY ainda dentro da transação (open-in-view desativado). */
    private static Proposta inicializar(Proposta proposta) {
        proposta.getItens().size();
        proposta.getFases().size();
        return proposta;
    }

    @Transactional
    public void excluir(Proposta proposta) {
        repository.delete(proposta);
    }

    @Transactional
    public Proposta alterarStatus(Long id, StatusProposta status) {
        Proposta proposta = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proposta não encontrada: " + id));
        proposta.setStatus(status);
        proposta.marcarAtualizada();
        return repository.save(proposta);
    }

    @Transactional
    public Optional<Proposta> responderPorToken(String token, boolean aceita) {
        return repository.findByToken(token).map(proposta -> {
            if (proposta.getStatus() == StatusProposta.PUBLICADA || proposta.getStatus() == StatusProposta.VISUALIZADA) {
                proposta.setStatus(aceita ? StatusProposta.ACEITA : StatusProposta.RECUSADA);
                proposta.marcarAtualizada();
            }
            return inicializar(proposta);
        });
    }

    @Transactional
    public Optional<Proposta> registrarVisualizacaoPorToken(String token) {
        return repository.findByToken(token).map(proposta -> {
            boolean alterou = false;
            if (proposta.getVisualizadaEm() == null) {
                proposta.setVisualizadaEm(java.time.Instant.now());
                alterou = true;
            }
            if (proposta.getStatus() == StatusProposta.PUBLICADA) {
                proposta.setStatus(StatusProposta.VISUALIZADA);
                alterou = true;
            }
            if (alterou) {
                proposta.marcarAtualizada();
                return inicializar(repository.save(proposta));
            }
            return inicializar(proposta);
        });
    }
}
