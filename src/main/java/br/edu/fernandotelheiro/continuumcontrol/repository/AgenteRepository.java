package br.edu.fernandotelheiro.continuumcontrol.repository;

import br.edu.fernandotelheiro.continuumcontrol.domain.Agente;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusAgente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgenteRepository extends JpaRepository<Agente, Long> {

    Page<Agente> findByStatus(
            StatusAgente status,
            Pageable pageable
    );
    long countByStatus(StatusAgente status);
}