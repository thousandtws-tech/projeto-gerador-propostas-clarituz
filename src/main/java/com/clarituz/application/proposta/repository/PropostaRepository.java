package com.clarituz.application.proposta.repository;

import java.util.List;
import java.util.Optional;

import com.clarituz.application.proposta.Proposta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropostaRepository extends JpaRepository<Proposta, Long> {

    Optional<Proposta> findByToken(String token);

    List<Proposta> findAllByOrderByAtualizadaEmDesc();

    List<Proposta> findByTituloContainingIgnoreCaseOrClienteContainingIgnoreCaseOrderByAtualizadaEmDesc(
            String titulo, String cliente);
}
