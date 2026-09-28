package br.edu.fernandotelheiro.continuumcontrol.repository;

import br.edu.fernandotelheiro.continuumcontrol.domain.Intervencao;
import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.ImpactoIntervencao;

import java.time.OffsetDateTime;

public interface IntervencaoRepository extends JpaRepository<Intervencao, Long> {
    long countByImpactoAndInstanteAfter(
            ImpactoIntervencao impacto,
            OffsetDateTime desde
    );
}