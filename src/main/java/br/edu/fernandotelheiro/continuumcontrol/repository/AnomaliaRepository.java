package br.edu.fernandotelheiro.continuumcontrol.repository;

import br.edu.fernandotelheiro.continuumcontrol.domain.Anomalia;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.RiscoAnomalia;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusAnomalia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;

public interface AnomaliaRepository
        extends JpaRepository<Anomalia, Long> {

    Page<Anomalia> findByRisco(
            RiscoAnomalia risco,
            Pageable pageable
    );

    Page<Anomalia> findByStatus(
            StatusAnomalia status,
            Pageable pageable
    );

    Page<Anomalia> findByRiscoAndStatus(
            RiscoAnomalia risco,
            StatusAnomalia status,
            Pageable pageable
    );
    long countByStatusNotIn(
            Collection<StatusAnomalia> status
    );

    long countByRiscoAndStatusNotIn(
            RiscoAnomalia risco,
            Collection<StatusAnomalia> status
    );
}