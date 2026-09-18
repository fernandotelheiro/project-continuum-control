package br.edu.fernandotelheiro.continuumcontrol.repository;

import br.edu.fernandotelheiro.continuumcontrol.domain.EventoHistorico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoHistoricoRepository extends JpaRepository<EventoHistorico, Long> {
}